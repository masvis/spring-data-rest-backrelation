package com.masvis.springdatarest.backrelation;

import java.util.Collection;

/**
 * A relationship handler helper.
 *
 * @param <T> the handled entity
 * @author Ruggero Russo
 * @author Sante Stanisci
 */
public interface BackrelationHandler<T, V> {
    /**
     * Finds the subset of instances in a collection that are not available in the analyzed object.
     *
     * @param updatedEntity the analyzed entity
     * @param finals        the collection of instances used to match the analyzed entity field
     * @return a list of deletable relationships
     */
    Collection<? extends V> findDeletablesByEntity(T updatedEntity, Collection<? extends V> finals);

    /**
     * Get the instances available in a front relation field.
     *
     * @param entity the target entity
     * @return a collection of instances of the back-related entity
     */
    Collection<T> getFrontRelation(V entity);
}
