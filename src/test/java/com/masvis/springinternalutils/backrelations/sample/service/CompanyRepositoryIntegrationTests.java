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
package com.masvis.springinternalutils.backrelations.sample.service;

import com.masvis.springinternalutils.backrelations.sample.domain.City;
import com.masvis.springinternalutils.backrelations.sample.domain.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.valueOf;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompanyRepositoryIntegrationTests {
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void exampleTest() {
        City bari = new City();
        bari.setName("Bari");
        City conversano = new City();
        conversano.setName("Conversano");

        Company politecnico = new Company();
        politecnico.setName("Politecnico");
        Company masvis = new Company();
        masvis.setName("Masvis");

        bari = this.restTemplate.postForObject("/cities", bari, City.class);
        conversano = this.restTemplate.postForObject("/cities", conversano, City.class);

        politecnico = this.restTemplate.postForObject("/companies", politecnico, Company.class);
        masvis = this.restTemplate.postForObject("/companies", masvis, Company.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(valueOf("text/uri-list"));
        HttpEntity<String> tHttpEntity = new HttpEntity<>("/cities/"+bari.getId(), headers);
        ResponseEntity<String> body = this.restTemplate.exchange("/companies/"+politecnico.getId()+"/cities", HttpMethod.PUT, tHttpEntity, String.class);
    }

    @Test
    public void executesQueryMethodsCorrectly() {
        List<City> bariArray = this.cityRepository.findByName("Bari");
        List<City> conversanoArray = this.cityRepository.findByName("Conversano");
        assertThat(bariArray).hasSize(1);
        assertThat(conversanoArray).hasSize(1);

        City bari = bariArray.get(0);
        City conversano = conversanoArray.get(0);

        List<Company> companiesInBari = this.companyRepository.findByCitiesContaining(bari);
        List<Company> companiesInConversano = this.companyRepository.findByCitiesContaining(conversano);

        assertThat(companiesInBari).hasSize(1);
        assertThat(companiesInConversano).hasSize(2);
    }
}
