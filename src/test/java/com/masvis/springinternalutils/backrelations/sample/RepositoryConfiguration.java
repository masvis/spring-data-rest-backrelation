package com.masvis.springinternalutils.backrelations.sample;

import com.masvis.springinternalutils.backrelations.sample.domain.City;
import com.masvis.springinternalutils.backrelations.sample.domain.Company;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class RepositoryConfiguration extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(City.class);
        config.exposeIdsFor(Company.class);

        config.setReturnBodyOnCreate(true);
        config.setReturnBodyOnUpdate(true);
    }

    @Override
    public void configureConversionService(ConfigurableConversionService service) {
        super.configureConversionService(service);
        service.addConverter(stringURIToLongConverter());
    }

    @Bean
    public Converter<String, Long> stringURIToLongConverter() {
        return new StringToLongConverter();
    }
}