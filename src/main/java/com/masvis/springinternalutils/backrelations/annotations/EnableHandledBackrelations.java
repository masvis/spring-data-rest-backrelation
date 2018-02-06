package com.masvis.springinternalutils.backrelations.annotations;

import com.masvis.springinternalutils.backrelations.HandledBackrelationBeansRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        HandledBackrelationBeansRegistrar.class
})
public @interface EnableHandledBackrelations {
    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};
}
