package com.masvis.springdatarest.backrelation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * The main back-relation event handler class. It catches the {@link HandleBeforeLinkSave} and the
 * {@link HandleBeforeLinkDelete} to update correctly the front relationship. It analyzes the actual modified
 * relationships of the target entity
 *
 * @param <T> the target entity type
 * @author Ruggero Russo
 * @author Sante Stanisci
 * @see RepositoryEventHandler
 * @see HandleBeforeLinkSave
 * @see HandleBeforeLinkDelete
 */
@RepositoryEventHandler
public class BackrelationsEventHandler<T, V> {
    /**
     * The target entity type
     */
    private final Class<T> targetClass;

    /**
     * The source entity type
     */
    private final Class<V> sourceClass;
    /**
     * The target entity back-relation field
     */
    private final Field field;
    /**
     * The class type of the back-relation handler bean
     *
     * @see BackrelationHandler
     */
    private final Class<BackrelationHandler<T, V>> backrelationHandlerClass;

    /**
     * The application context, used to get the instanced back-relation handler bean
     *
     * @see ApplicationContext
     * @see Autowired
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * The event handler constructor.
     *
     * @param targetClass              {@link BackrelationsEventHandler#targetClass}
     * @param sourceClass              {@link BackrelationsEventHandler#sourceClass}
     * @param field                    {@link BackrelationsEventHandler#field}
     * @param backrelationHandlerClass {@link BackrelationsEventHandler#backrelationHandlerClass}
     */
    public BackrelationsEventHandler(Class<T> targetClass, Class<V> sourceClass, Field field,
                                     Class<BackrelationHandler<T, V>> backrelationHandlerClass) {
        this.targetClass = targetClass;
        this.sourceClass = sourceClass;
        this.field = field;
        this.backrelationHandlerClass = backrelationHandlerClass;
    }

    /**
     * Launches the update of the front relation entity before saving the operation.
     *
     * @param backrelationObj the updated entity
     * @param persistentSet   the updated field. It cannot be used to check the value of the field before the update, so
     *                        it is ignored
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    @HandleBeforeLinkSave
    @HandleBeforeLinkDelete
    public void manageBackrelation(Object backrelationObj, Collection persistentSet)
            throws IllegalAccessException {
        BackrelationHandler<T, V> backrelationHandler = applicationContext.getBean(this.backrelationHandlerClass);
        if ((!persistentSet.isEmpty() && !backrelationHandler.supports().isAssignableFrom(getCollectionClass(persistentSet))) ||
                !targetClass.isAssignableFrom(backrelationObj.getClass()))
            return;
        T obj = targetClass.cast(backrelationObj);
        field.setAccessible(true);

        Collection<? extends V> finals = (Collection<? extends V>) field.get(obj);
        Collection<? extends V> deletables = backrelationHandler.findDeletablesByEntity(obj, finals);

        finals.forEach(f -> backrelationHandler.getFrontRelation(f).add(obj));
        deletables.forEach(d -> backrelationHandler.getFrontRelation(d).remove(obj));
    }

    private Class<?> getCollectionClass(Collection collection) {
        return collection.stream().findAny().get().getClass();
    }
}
