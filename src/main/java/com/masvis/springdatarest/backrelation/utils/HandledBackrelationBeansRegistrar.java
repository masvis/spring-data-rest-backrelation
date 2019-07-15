package com.masvis.springdatarest.backrelation.utils;

import com.masvis.springdatarest.backrelation.BackrelationsEventHandler;
import com.masvis.springdatarest.backrelation.annotations.EnableHandledBackrelations;
import com.masvis.springdatarest.backrelation.annotations.HandledBackrelation;
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
import java.util.Objects;

/**
 * This registrar scans the {@link EnableHandledBackrelations#value()} packages in order to find one or more
 * {@link Entity} annotated objects. If the value of the annotation is empty, it scans the packages and the sub-packages
 * of the annotated class.
 * Once collected the requested resources, the registrar scans each field of the entities, filters
 * {@link HandledBackrelation} annotated fields, and creates a {@link BackrelationsEventHandler} bean to handle the
 * backrelation.
 *
 * @author Ruggero Russo
 * @author Sante Stanisci
 */
public class HandledBackrelationBeansRegistrar implements ImportBeanDefinitionRegistrar {
    /**
     * A classes scanner
     */
    private ClassPathScanner classpathScanner;

    /**
     * Generate a new {@link ClassPathScanner}, filtering for {@code @Entity} annotated classes.
     */
    public HandledBackrelationBeansRegistrar() {
        classpathScanner = new ClassPathScanner(false);
        classpathScanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!(importingClassMetadata instanceof StandardAnnotationMetadata))
            return;
        String[] basePackages = getBasePackages((StandardAnnotationMetadata) importingClassMetadata);
        for (String basePackage : basePackages) {
            createHandledBackrelationsBeans(basePackage, registry);
        }
    }

    /**
     * Return the base packages from the {@code @EnableHandledBackrelations} value string array. If empty, it returns
     * the actual {@code @EnableHandledBackrelations} annotation location package.
     *
     * @param standardAnnotationMetadata the {@code @EnableHandledBackrelations} annotation metadata
     * @return the base packages where to apply backrelation beans
     */
    private String[] getBasePackages(StandardAnnotationMetadata standardAnnotationMetadata) {
        MultiValueMap<String, Object> allAnnotationAttributes =
                standardAnnotationMetadata.getAllAnnotationAttributes(EnableHandledBackrelations.class.getName());

        String[] basePackages = (String[]) Objects.requireNonNull(allAnnotationAttributes).getFirst("basePackages");
        String[] returnedBasePackages;
        assert basePackages != null;
        if (basePackages.length == 0) {
            returnedBasePackages = new String[1];
            returnedBasePackages[0] = standardAnnotationMetadata.getIntrospectedClass().getPackage().getName();
        } else returnedBasePackages = basePackages;

        return returnedBasePackages;
    }

    /**
     * Generate a {@link BackrelationsEventHandler} bean for each field annotated with {@link HandledBackrelation}
     * annotation.
     *
     * @param basePackage the analyzed base package
     * @param registry    the definition registry of the beans
     * @see BeanDefinitionRegistry
     */
    private void createHandledBackrelationsBeans(String basePackage, BeanDefinitionRegistry registry) {
        try {
            for (BeanDefinition beanDefinition : classpathScanner.findCandidateComponents(basePackage)) {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(HandledBackrelation.class)) {
                        HandledBackrelation handledBackrelation = field.getAnnotation(HandledBackrelation.class);
                        String beanName = Character.toLowerCase(handledBackrelation.value().getSimpleName().charAt(0))
                                + handledBackrelation.value().getSimpleName().substring(1);
                        BeanDefinition definition = BeanDefinitionBuilder
                                .genericBeanDefinition(BackrelationsEventHandler.class)
                                .addConstructorArgValue(beanName)
                                .addConstructorArgValue(clazz)
                                .addConstructorArgValue(field)
                                .addConstructorArgValue(handledBackrelation.value())
                                .setScope(BeanDefinition.SCOPE_SINGLETON)
                                .getBeanDefinition();

                        registry.registerBeanDefinition(beanName, definition);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}