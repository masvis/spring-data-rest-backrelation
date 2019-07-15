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
package com.masvis.springdatarest.backrelation.sample;

import com.masvis.springdatarest.backrelation.sample.domain.City;
import com.masvis.springdatarest.backrelation.sample.domain.Company;
import com.masvis.springdatarest.backrelation.sample.service.CityRepository;
import com.masvis.springdatarest.backrelation.sample.service.CompanyRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.valueOf;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CompanyRepositoryIntegrationTests {
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test1FillRest() {
        HttpEntity<String> entity;
        ResponseEntity<String> body;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(valueOf("text/uri-list"));

        City bari = new City();
        bari.setName("Bari");
        City conversano = new City();
        conversano.setName("Conversano");
        City lecce = new City();
        lecce.setName("Lecce");
        City brindisi = new City();
        brindisi.setName("Brindisi");
        City rutigliano = new City();
        rutigliano.setName("Rutigliano");
        City cassano = new City();
        cassano.setName("Cassano delle Murge");
        City gioia = new City();
        gioia.setName("Gioia del Colle");

        Company politecnico = new Company();
        politecnico.setName("Politecnico");
        Company masvis = new Company();
        masvis.setName("Masvis");
        Company university = new Company();
        university.setName("Università degli studi di Bari");

        bari = this.restTemplate.postForObject("/cities", bari, City.class);
        conversano = this.restTemplate.postForObject("/cities", conversano, City.class);
        lecce = this.restTemplate.postForObject("/cities", lecce, City.class);
        brindisi = this.restTemplate.postForObject("/cities", brindisi, City.class);
        rutigliano = this.restTemplate.postForObject("/cities", rutigliano, City.class);
        cassano = this.restTemplate.postForObject("/cities", cassano, City.class);
        gioia = this.restTemplate.postForObject("/cities", gioia, City.class);

        politecnico = this.restTemplate.postForObject("/companies", politecnico, Company.class);
        masvis = this.restTemplate.postForObject("/companies", masvis, Company.class);
        university = this.restTemplate.postForObject("/companies", university, Company.class);

        entity = new HttpEntity<>("/cities/" + bari.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + politecnico.getId() + "/cities", HttpMethod.POST, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);

        entity = new HttpEntity<>("/cities/" + conversano.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + politecnico.getId() + "/cities", HttpMethod.POST, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);
        entity = new HttpEntity<>("/cities/" + conversano.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + masvis.getId() + "/cities", HttpMethod.POST, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);

        entity = new HttpEntity<>("/cities/" + brindisi.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + politecnico.getId() + "/cities", HttpMethod.POST, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);
        entity = new HttpEntity<>("/cities/" + lecce.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + politecnico.getId() + "/cities", HttpMethod.POST, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);
        entity = new HttpEntity<>("/cities/" + cassano.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + masvis.getId() + "/cities", HttpMethod.POST, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);
        entity = new HttpEntity<>("/cities/" + gioia.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + masvis.getId() + "/cities", HttpMethod.POST, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);

        entity = new HttpEntity<>("/cities/" + rutigliano.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + university.getId() + "/cities", HttpMethod.PUT, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);
        entity = new HttpEntity<>("/cities/" + brindisi.getId(), headers);
        body = this.restTemplate.exchange("/companies/" + university.getId() + "/cities", HttpMethod.PUT, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.CREATED);

        entity = new HttpEntity<>(null, headers);
        body = this.restTemplate.exchange("/companies/" + masvis.getId() + "/cities/" + gioia.getId(), HttpMethod.DELETE, entity, String.class);
        assertThat(body.getStatusCode() == HttpStatus.ACCEPTED);
    }

    @Test
    public void test2BariCompaniesTest() {
        List<City> bariArray = this.cityRepository.findByName("Bari");
        assertThat(bariArray).hasSize(1);

        City bari = bariArray.get(0);

        List<Company> companiesInBari = this.companyRepository.findByCitiesContaining(bari);
        assertThat(companiesInBari).hasSize(1);
    }

    @Test
    public void test3ConversanoCompaniesTest() {
        List<City> conversanoArray = this.cityRepository.findByName("Conversano");
        assertThat(conversanoArray).hasSize(1);

        City conversano = conversanoArray.get(0);

        List<Company> companiesInConversano = this.companyRepository.findByCitiesContaining(conversano);

        assertThat(companiesInConversano).hasSize(2);
    }

    @Test
    public void test4LecceCompaniesTest() {
        List<City> lecceArray = this.cityRepository.findByName("Lecce");
        assertThat(lecceArray).hasSize(1);

        City lecce = lecceArray.get(0);

        List<Company> companiesInLecce = this.companyRepository.findByCitiesContaining(lecce);

        assertThat(companiesInLecce).hasSize(1);
    }

    @Test
    public void test5BrindisiCompaniesTest() {
        List<City> brindisiArray = this.cityRepository.findByName("Brindisi");
        assertThat(brindisiArray).hasSize(1);

        City brindisi = brindisiArray.get(0);

        List<Company> companiesInBrindisi = this.companyRepository.findByCitiesContaining(brindisi);

        assertThat(companiesInBrindisi).hasSize(2);
    }

    @Test
    public void test6RutiglianoCompaniesTest() {
        List<City> rutiglianoArray = this.cityRepository.findByName("Rutigliano");
        assertThat(rutiglianoArray).hasSize(1);

        City rutigliano = rutiglianoArray.get(0);

        List<Company> companiesInRutigliano = this.companyRepository.findByCitiesContaining(rutigliano);
        assertThat(companiesInRutigliano).hasSize(0);
    }

    @Test
    public void test7CassanoCompaniesTest() {
        List<City> cassanoArray = this.cityRepository.findByName("Cassano delle Murge");
        assertThat(cassanoArray).hasSize(1);

        City cassano = cassanoArray.get(0);

        List<Company> companiesInCassano = this.companyRepository.findByCitiesContaining(cassano);

        assertThat(companiesInCassano).hasSize(1);
    }

    @Test
    public void test8GioiaCompaniesTest() {
        List<City> gioiaArray = this.cityRepository.findByName("Gioia del Colle");
        assertThat(gioiaArray).hasSize(1);

        City gioia = gioiaArray.get(0);

        List<Company> companiesInGioia = this.companyRepository.findByCitiesContaining(gioia);

        assertThat(companiesInGioia).hasSize(0);
    }
}
