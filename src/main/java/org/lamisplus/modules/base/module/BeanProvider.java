package org.lamisplus.modules.base.module;

import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.core.context.info.AcrossContextInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BeanProvider {
    private final ChildContextsHolder contextsHolder;

    public <T> List<T> getBeansOfType(Class<T> type) {
        List<T> beans = new ArrayList<>();
        contextsHolder.getContexts().values().forEach(context -> {
            AcrossContextInfo contextInfo = AcrossContextUtils.getContextInfo(context);
            beans.addAll(contextInfo.getApplicationContext().getBeansOfType(type).values());
        });
        return beans;
    }
}
