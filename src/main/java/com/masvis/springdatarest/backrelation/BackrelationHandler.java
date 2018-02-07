package com.masvis.springdatarest.backrelation;

import java.io.Serializable;
import java.util.Collection;

public interface BackrelationHandler<T> {
    Collection<? extends Serializable> findDeletablesByEntity(T updatedEntity, Collection<? extends Serializable> finalRailwayStations);

    Collection<T> getFrontRelation(Serializable entity);
}
