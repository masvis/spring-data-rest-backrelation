/*
 * Copyright 2012-2016 the original author or authors.
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

package com.masvis.springdatarest.backrelation.sample.service;

import com.masvis.springdatarest.backrelation.sample.domain.City;
import com.masvis.springdatarest.backrelation.sample.domain.Company;
import com.masvis.springdatarest.backrelation.sample.domain.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CompanyRepository extends PagingAndSortingRepository<Company, Long> {

    List<Company> findByCitiesContaining(City city);

    List<Company> findByEmployeesContaining(Employee employee);
}
