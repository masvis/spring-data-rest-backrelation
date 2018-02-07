package com.masvis.springinternalutils.backrelations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;

@RepositoryEventHandler
public class BackrelationsEventHandler<T> {
    private final Class<T> clazz;
    private final Field field;
    private final Class<BackrelationHandler<T>> backrelationHandlerClass;

    @Autowired
    public ApplicationContext applicationContext;

    public BackrelationsEventHandler(Class<T> clazz, Field field, Class<BackrelationHandler<T>> backrelationHandlerClass) throws IllegalAccessException, InstantiationException {
        this.clazz = clazz;
        this.field = field;
        this.backrelationHandlerClass = backrelationHandlerClass;
    }

    @HandleBeforeLinkSave
    @HandleBeforeLinkDelete
    public void managerUserAddBackRelation(Object backrelationObject, Object ignore) throws IllegalAccessException {
        BackrelationHandler<T> backrelationHandler = applicationContext.getBean(this.backrelationHandlerClass);
        if (!clazz.isAssignableFrom(backrelationObject.getClass()))
            return;
        T obj = clazz.cast(backrelationObject);
        field.setAccessible(true);
        Collection<? extends Serializable> finals = (Collection<? extends Serializable>) field.get(obj);
        Collection<? extends Serializable> deletables = backrelationHandler.findDeletablesByEntity(obj, finals);

        finals.forEach(f -> backrelationHandler.getFrontRelation(f).add(obj));
        deletables.forEach(d -> backrelationHandler.getFrontRelation(d).remove(obj));
    }
}
