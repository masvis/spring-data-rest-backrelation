package com.masvis.springdatarest.backrelation.annotations;

import com.masvis.springdatarest.backrelation.BackrelationsEventHandler;

import javax.persistence.ManyToMany;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When a {@link ManyToMany} field is annotated with {@code @HandledBackrelation} annotation, tracks his value update
 * during a REST update request (POST or PATCH to add relationships, PUT to override existing relationships).
 * <p>This annotation has to be used on a {@code @ManyToMany(mappedBy="field")} field.</p>
 * To handle the back relationship, the {@link #value} value has to be set with a {@link BackrelationsEventHandler}
 * bean.
 *
 * @author Ruggero Russo
 * @author Sante Stanisci
 * @see BackrelationsEventHandler
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandledBackrelation {
    /**
     * The helper of the backrelationship
     *
     * @return the backrelationship handler.
     */
    Class<?> value();

    Class<?> backrelationClass();
}
