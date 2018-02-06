package com.masvis.springinternalutils.backrelations;

import org.springframework.data.rest.core.annotation.HandleBeforeLinkDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;

@RepositoryEventHandler
public class BackrelationsEventHandler<T> {
    private final Class<T> clazz;
    private final ArrayList<Field> fields;

    public BackrelationsEventHandler(Class<T> clazz, ArrayList<Field> fields) {
        this.clazz = clazz;
        this.fields = fields;
    }

    @HandleBeforeLinkSave
    @HandleBeforeLinkDelete
    public void managerUserAddBackRelation(T backrelationObject, Object ignore) {
    }
}
