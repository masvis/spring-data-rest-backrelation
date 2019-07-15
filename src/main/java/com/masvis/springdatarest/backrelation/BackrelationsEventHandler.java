package com.masvis.springdatarest.backrelation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

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
public class BackrelationsEventHandler<T> {

    /**
     * The name of bean
     */
    private final String beanName;
    /**
     * The target entity type
     */
    private final Class<T> clazz;
    /**
     * The target entity back-relation field
     */
    private final Field field;
    /**
     * The class type of the back-relation handler bean
     *
     * @see BackrelationHandler
     */
    private final Class<BackrelationHandler<T>> backrelationHandlerClass;

    /**
     * The application context, used to get the instanced back-relation handler bean
     *
     * @see ApplicationContext
     * @see Autowired
     */
    @Autowired
    public ApplicationContext applicationContext;

    /**
     * The event handler constructor.
     *
     * @param beanName                 {@link BackrelationsEventHandler#beanName}
     * @param clazz                    {@link BackrelationsEventHandler#clazz}
     * @param field                    {@link BackrelationsEventHandler#field}
     * @param backrelationHandlerClass {@link BackrelationsEventHandler#backrelationHandlerClass}
     */
    public BackrelationsEventHandler(String beanName, Class<T> clazz, Field field, Class<BackrelationHandler<T>> backrelationHandlerClass) {
        this.beanName = beanName;
        this.clazz = clazz;
        this.field = field;
        this.backrelationHandlerClass = backrelationHandlerClass;
    }

    /**
     * Launches the update of the front relation entity before saving the operation.
     *
     * @param backrelationObj the updated entity
     * @param ignore          the updated field. It cannot be used to check the value of the field before the update, so
     *                        it is ignored
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unused")
    @HandleBeforeLinkSave
    @HandleBeforeLinkDelete
    public void manageBackrelation(Object backrelationObj, Object ignore)
            throws IllegalAccessException {
        Map<String, BackrelationHandler<T>> beansOfType = applicationContext.getBeansOfType(this.backrelationHandlerClass);
        BackrelationHandler<T> backrelationHandler = beansOfType.get(beanName);

        if (backrelationHandler == null || !clazz.isAssignableFrom(backrelationObj.getClass()))
            return;
        T obj = clazz.cast(backrelationObj);
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Collection<? extends Serializable> finals = (Collection<? extends Serializable>) field.get(obj);
        Collection<? extends Serializable> deletables = backrelationHandler.findDeletablesByEntity(obj, finals);

        finals.forEach(f -> backrelationHandler.getFrontRelation(f).add(obj));
        deletables.forEach(d -> backrelationHandler.getFrontRelation(d).remove(obj));
    }
}
