package com.masvis.springdatarest.backrelation.annotations;

import com.masvis.springdatarest.backrelation.utils.HandledBackrelationBeansRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import javax.persistence.Entity;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable the back relation in a Spring Data REST application, enabling a {@link HandledBackrelationBeansRegistrar} in
 * order to search {@link HandledBackrelation} annotated fields in {@link Entity} annotated objects. Usually you should
 * use {@code @EnableHandledBackrelations} annotation on a {@code @SpringBootApplication} or on a {@code @Configuration}
 * bean to specify the package where to find available entities.
 *
 * @author Ruggero Russo
 * @author Sante Stanisci
 * @see HandledBackrelationBeansRegistrar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        HandledBackrelationBeansRegistrar.class
})
public @interface EnableHandledBackrelations {
    /**
     * Base packages to scan for annotated components.
     *
     * @return base packages to scan
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * Base packages to scan for annotated components.
     *
     * @return base packages to scan
     */
    @AliasFor("value")
    String[] basePackages() default {};
}
