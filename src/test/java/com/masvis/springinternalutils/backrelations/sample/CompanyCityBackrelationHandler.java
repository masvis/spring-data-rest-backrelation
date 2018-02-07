package com.masvis.springinternalutils.backrelations.sample;

import com.masvis.springinternalutils.backrelations.BackrelationHandler;
import com.masvis.springinternalutils.backrelations.sample.domain.City;
import com.masvis.springinternalutils.backrelations.sample.domain.Company;
import com.masvis.springinternalutils.backrelations.sample.service.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class CompanyCityBackrelationHandler implements BackrelationHandler<Company> {
    @Autowired
    private final CityRepository cityRepository;

    public CompanyCityBackrelationHandler(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public Collection<? extends Serializable> findDeletablesByEntity(Company updatedEntity, Collection<? extends Serializable> finals) {
        Collection<City> olds = cityRepository.findByCompaniesContaining(updatedEntity);
        return olds.stream()
                .filter(ors -> finals.stream()
                        .map(f -> ((City) f).getId())
                        .noneMatch(frsid -> ors.getId().equals(frsid)))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Company> getFrontRelation(Serializable entity) {
        return ((City) entity).getCompanies();
    }
}
