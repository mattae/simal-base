package org.lamisplus.modules.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
public class ExceptionHandlerExceptionHandler {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModules(
            new ProblemModule().withStackTraces(false),
            new ConstraintViolationProblemModule());
    }

    @Bean(name = DispatcherServlet.HANDLER_EXCEPTION_RESOLVER_BEAN_NAME)
    CompositeHandlerExceptionResolver compositeHandlerExceptionResolver() {
        return new CompositeHandlerExceptionResolver();
    }

    static class CompositeHandlerExceptionResolver implements HandlerExceptionResolver {

        @Autowired
        private ListableBeanFactory beanFactory;

        private List<HandlerExceptionResolver> resolvers;

        @Override
        public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                             Exception ex) {
            if (this.resolvers == null) {
                this.resolvers = extractResolvers();
            }
            return this.resolvers.stream().map((resolver) -> resolver.resolveException(request, response, handler, ex))
                .filter(Objects::nonNull).findFirst().orElse(null);
        }

        private List<HandlerExceptionResolver> extractResolvers() {
            List<HandlerExceptionResolver> list = new ArrayList<>(
                this.beanFactory.getBeansOfType(HandlerExceptionResolver.class).values());
            list.remove(this);
            AnnotationAwareOrderComparator.sort(list);
            if (list.isEmpty()) {
                list.add(new DefaultErrorAttributes());
                list.add(new DefaultHandlerExceptionResolver());
            }
            return list;
        }

    }

}
