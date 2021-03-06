/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.masvis.springdatarest.backrelation.sample.domain;

import com.masvis.springdatarest.backrelation.annotations.HandledBackrelation;
import com.masvis.springdatarest.backrelation.sample.handler.CompanyCityBackrelationHandler;
import com.masvis.springdatarest.backrelation.sample.handler.CompanyEmployeeBackrelationHandler;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Entity
public class Company implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "companies")
    @HandledBackrelation(value = CompanyCityBackrelationHandler.class, backrelationClass = City.class)
    private List<City> cities = new ArrayList<>();

    @ManyToMany(mappedBy = "companies")
    @HandledBackrelation(value = CompanyEmployeeBackrelationHandler.class, backrelationClass = Employee.class)
    private List<Employee> employees = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
