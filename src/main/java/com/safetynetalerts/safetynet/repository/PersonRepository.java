package com.safetynetalerts.safetynet.repository;

import com.safetynetalerts.safetynet.model.Person;

import java.util.List;

public interface PersonRepository {

    List<Person> getAllPersons();
    void saveAllPersons(List<Person> persons);

}
