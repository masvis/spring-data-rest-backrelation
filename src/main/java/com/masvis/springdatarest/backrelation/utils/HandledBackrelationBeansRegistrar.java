package com.masvis.springdatarest.backrelation.utils;

import com.masvis.springdatarest.backrelation.BackrelationsEventHandler;
import com.masvis.springdatarest.backrelation.annotations.HandledBackrelation;
import com.masvis.springdatarest.backrelation.annotations.EnableHandledBackrelations;
import com.masvis.springdatarest.backrelation.utils.ClassPathScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.MultiValueMap;

import javax.persistence.Entity;
import java.lang.reflect.Field;

public class HandledBackrelationBeansRegistrar implements ImportBeanDefinitionRegistrar {
    private ClassPathScanner classpathScanner;

    public HandledBackrelationBeansRegistrar() {
        classpathScanner = new ClassPathScanner(false);
        classpathScanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!(importingClassMetadata instanceof StandardAnnotationMetadata))
            return;
        String[] basePackages = getBasePackages((StandardAnnotationMetadata) importingClassMetadata);
        for (String basePackage : basePackages) {
            createHandledBackrelationsProxies(basePackage, registry);
        }
    }

    private String[] getBasePackages(StandardAnnotationMetadata standardAnnotationMetadata) {
        MultiValueMap<String, Object> allAnnotationAttributes =
                standardAnnotationMetadata.getAllAnnotationAttributes(EnableHandledBackrelations.class.getName());

        String[] basePackages = (String[]) allAnnotationAttributes.getFirst("basePackages");
        String[] returnedBasePackages;
        if (basePackages.length == 0) {
            returnedBasePackages = new String[1];
            returnedBasePackages[0] = standardAnnotationMetadata.getIntrospectedClass().getPackage().getName();
        } else returnedBasePackages = basePackages;

        return returnedBasePackages;
    }

    private void createHandledBackrelationsProxies(String basePackage, BeanDefinitionRegistry registry) {
        try {
            for (BeanDefinition beanDefinition : classpathScanner.findCandidateComponents(basePackage)) {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(HandledBackrelation.class)) {
                        HandledBackrelation handledBackrelation = field.getAnnotation(HandledBackrelation.class);
                        BeanDefinition definition = BeanDefinitionBuilder
                                .genericBeanDefinition(BackrelationsEventHandler.class)
                                .addConstructorArgValue(clazz)
                                .addConstructorArgValue(field)
                                .addConstructorArgValue(handledBackrelation.value())
                                .setScope(BeanDefinition.SCOPE_SINGLETON)
                                .getBeanDefinition();

                        registry.registerBeanDefinition(clazz.getSimpleName() + "BackrelationsEventHandler", definition);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception while createing proxy");
            e.printStackTrace();
        }
    }
}