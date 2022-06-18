package com.mattae.simal.modules.base.services;

import bsh.EvalError;
import bsh.Interpreter;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.events.AcrossContextBootstrappedEvent;
import com.mattae.simal.modules.base.config.ContextProvider;
import com.mattae.simal.modules.base.domain.entities.Extension;
import com.mattae.simal.modules.base.domain.repositories.ExtensionRepository;
import com.mattae.simal.modules.base.extensions.ExtensionPoint;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExtensionService {
    private final ExtensionRepository extensionRepository;
    private final AcrossContext acrossContext;
    protected Map<Class<? extends ExtensionPoint>, Map<String, ExtensionPoint>> staticExtensionsByClassByName = new HashMap<>();
    protected Map<Class<? extends ExtensionPoint>, Map<String, ExtensionPoint>> extensionsByClassByName;

    public synchronized void refresh() {
        extensionsByClassByName = new HashMap<>();

        if (acrossContext != null) {
            Map<String, ExtensionPoint> extensionPointMap =
                new TreeMap<>(BeanFactoryUtils.beansOfTypeIncludingAncestors(acrossContext.getParentApplicationContext(), ExtensionPoint.class));
            if (acrossContext.getParentApplicationContext().getParentBeanFactory() != null
                && acrossContext.getParentApplicationContext().getParentBeanFactory() instanceof ListableBeanFactory) {
                extensionPointMap.putAll(((ListableBeanFactory) acrossContext.getParentApplicationContext().getParentBeanFactory())
                    .getBeansOfType(ExtensionPoint.class));
            }

            LOG.info("Found {} extension points from spring that will be registered", extensionPointMap.size());
            for (String name : extensionPointMap.keySet()) {
                registerExtension(name, extensionPointMap.get(name));
            }
        }

        for (Class<? extends ExtensionPoint> extensionClass : staticExtensionsByClassByName.keySet()) {
            Map<String, ExtensionPoint> byNameMap = staticExtensionsByClassByName.get(extensionClass);
            for (String name : byNameMap.keySet()) {
                ExtensionPoint ext = byNameMap.get(name);
                getExtensionsByNameMap(extensionClass).put(name, ext);
            }
        }

        List<Extension> extensionList = extensionRepository.findAll();
        LOG.info("Found {} extension points from the database that will be registered", extensionList.size());

        for (Extension extension : extensionList) {
            registerExtension(extension);
        }
    }

    public void registerExtension(Extension extension) {
        if (extension.getExtensionText() != null) {
            if (extension.getExtensionType().equals(Extension.ExtensionType.GROOVY)) {
                try {
                    GroovyShell shell = new GroovyShell();
                    shell.setProperty("ctx", ContextProvider.class);
                    shell.setProperty("log", LOG);
                    String script = extension.getExtensionText();
                    script += " this";
                    final Object e = shell.evaluate(script);
                    Object ext = Proxy.newProxyInstance(this.getClass().getClassLoader(),
                        new Class[]{ExtensionPoint.class},
                        (proxy, method, args) -> {
                            Method m = e.getClass().getMethod(method.getName());
                            return m.invoke(e, args);
                        });
                    registerExtension(extension.getName(), (ExtensionPoint) ext);
                } catch (CompilationFailedException e) {
                    LOG.error("Error while compiling Groovy extension " + extension.getName(), e);
                } catch (ClassCastException e) {
                    LOG.error("Interface class not found for Groovy extension " + extension.getName(), e);
                }
            } else if (extension.getExtensionType().equals(Extension.ExtensionType.BSH)) {
                try {
                    Interpreter interpreter = new Interpreter();
                    interpreter.eval(extension.getExtensionText());
                    interpreter.set("ctx", ContextProvider.class);
                    interpreter.set("log", LOG);
                    Object ext = interpreter.getInterface(Class.forName(extension.getInterfaceName()));
                    registerExtension(extension.getName(), (ExtensionPoint) ext);
                } catch (EvalError e) {
                    LOG.error("Error while parsing BSH extension " + extension.getName(), e);
                } catch (ClassNotFoundException e) {
                    LOG.error("Interface class not found for BSH extension " + extension.getName(), e);
                }
            } else {
                LOG.error("Skipping extension " + extension.getName() + ", unknown extension type " + extension.getExtensionType());
            }
        }
    }

    protected boolean registerExtension(String name, ExtensionPoint ext) {
        if (ext == null) {
            LOG.error("Missing ExtensionPoint interface for extension " + name);
            return false;
        }
        boolean installed = false;
        for (Class<? extends ExtensionPoint> extensionClass : getExtensionClassList(ext)) {
            installed = true;
            getExtensionsByNameMap(extensionClass).put(name, ext);
        }
        return installed;
    }

    public void unRegisterExtension(String name, ExtensionPoint ext) {
        for (Class<? extends ExtensionPoint> extensionClass : getExtensionClassList(ext)) {
            getExtensionsByNameMap(extensionClass).remove(name);
        }
    }

    protected List<Class<? extends ExtensionPoint>> getExtensionClassList(ExtensionPoint ext) {
        List<Class<? extends ExtensionPoint>> classList = new ArrayList<>();
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(ext.getClass());
        for (Class clazz : interfaces) {
            if (ExtensionPoint.class.isAssignableFrom(clazz) && !clazz.getName().equals(ExtensionPoint.class.getName())) {
                classList.add(clazz);
            }
        }
        return classList;
    }

    public synchronized <T extends ExtensionPoint> T getExtensionPoint(Class<T> extensionClass) {
        List<T> availableExtensions = getExtensionPointList(extensionClass);
        for (T extension : availableExtensions) {
            return extension;
        }
        return null;
    }

    public synchronized <T extends ExtensionPoint> List<T> getExtensionPointList(Class<T> extensionClass) {
        List<T> extensions = new ArrayList<T>(getExtensionPointMap(extensionClass).values());
        Collections.sort(extensions);
        return extensions;
    }

    public synchronized <T extends ExtensionPoint> Map<String, T> getExtensionPointMap(Class<T> extensionClass) {
        return (Map<String, T>) getExtensionsByNameMap(extensionClass);
    }

    public synchronized void addExtensionPoint(ExtensionPoint extension) {
        for (Class<? extends ExtensionPoint> extensionClass : getExtensionClassList(extension)) {
            getStaticExtensionsByNameMap(extensionClass).put(extension.getClass().getCanonicalName(), extension);
        }
        registerExtension(extension.getClass().getCanonicalName(), extension);
    }

    public synchronized void addExtensionPoint(String name, ExtensionPoint extension) {
        for (Class<? extends ExtensionPoint> extensionClass : getExtensionClassList(extension)) {
            getStaticExtensionsByNameMap(extensionClass).put(name, extension);
        }
        registerExtension(name, extension);
    }

    public synchronized void removeExtensionPoint(ExtensionPoint extension) {
        for (Class<? extends ExtensionPoint> extensionClass : getExtensionClassList(extension)) {
            getStaticExtensionsByNameMap(extensionClass).remove(extension.getClass().getCanonicalName());
        }
        unRegisterExtension(extension.getClass().getCanonicalName(), extension);
    }

    private Map<String, ExtensionPoint> getStaticExtensionsByNameMap(Class<? extends ExtensionPoint> extensionClass) {
        return getExtensionsByNameMap(staticExtensionsByClassByName, extensionClass);
    }

    private Map<String, ExtensionPoint> getExtensionsByNameMap(Class<? extends ExtensionPoint> extensionClass) {
        return getExtensionsByNameMap(extensionsByClassByName, extensionClass);
    }

    private Map<String, ExtensionPoint> getExtensionsByNameMap(Map<Class<? extends ExtensionPoint>, Map<String,
        ExtensionPoint>> byClassByNameMap, Class<? extends ExtensionPoint> extensionClass) {
        return byClassByNameMap.computeIfAbsent(extensionClass, k -> new HashMap<>());
    }

    public List<Extension> getExtensions() {
        return extensionRepository.findAll();
    }

    public void saveExtension(Extension extension) {
        if (extension.getEnabled()) {
            registerExtension(extension);
        }
        refresh();
    }

    public void deleteExtension(String extensionId) {
        refresh();
    }

    @EventListener
    public void onApplicationEvent(AcrossContextBootstrappedEvent event) {
        refresh();
    }
}
