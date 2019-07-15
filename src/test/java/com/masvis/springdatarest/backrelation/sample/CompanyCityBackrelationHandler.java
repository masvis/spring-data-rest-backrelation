package com.masvis.springdatarest.backrelation.sample;

import com.masvis.springdatarest.backrelation.BackrelationHandler;
import com.masvis.springdatarest.backrelation.sample.domain.City;
import com.masvis.springdatarest.backrelation.sample.domain.Company;
import com.masvis.springdatarest.backrelation.sample.service.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class CompanyCityBackrelationHandler implements BackrelationHandler<Company, City> {
    @Autowired
    private final CityRepository cityRepository;

    public CompanyCityBackrelationHandler(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public Class<? extends City> supports() {
        return City.class;
    }

    @Override
    public Collection<? extends City> findDeletablesByEntity(Company updatedEntity, Collection<? extends City> finals) {
        Collection<City> olds = cityRepository.findByCompaniesContaining(updatedEntity);
        return olds.stream()
                .filter(ors -> finals.stream()
                        .map(f -> f.getId())
                        .noneMatch(frsid -> ors.getId().equals(frsid)))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Company> getFrontRelation(City entity) {
        return entity.getCompanies();
    }
}
