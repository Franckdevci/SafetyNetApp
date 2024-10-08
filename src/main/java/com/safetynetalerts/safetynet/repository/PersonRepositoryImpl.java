package com.safetynetalerts.safetynet.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynetalerts.safetynet.model.Person;

import java.io.File;

/**
 * Implementation of the PersonRepository interface
 * This class manages the persistence of person data using a JSON file
 *
 * It provides methods to load, retrieve and save person data from/to JSON file
 * The data is initially loaded from the file specified by DATA_FILEPATH
 *
 * This class is in charge of reading and writing JSON objects from a file using Jackson for
 * serialization and deserialization
 * Convert object to json
 * Convert json to object
 */

@Repository
public class PersonRepositoryImpl implements PersonRepository {

    private static final Logger logger= LogManager.getLogger(PersonRepositoryImpl.class);

    // Path for JSON file
    private static final String DATA_FILEPATH = "src/main/resources/data.json";
    private List<Person> persons = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Constructor
     */
    public PersonRepositoryImpl() {
        loadData();
    }


    private void loadData() {
        try {
            JsonNode rootNode = objectMapper.readTree(new File(DATA_FILEPATH));
            JsonNode personsNode = rootNode.path("persons");
            persons = objectMapper.readValue(personsNode.toString(), new TypeReference<List<Person>>() {});
        } catch (IOException e) {
            logger.error("Failed to load person data from json file: {}", DATA_FILEPATH, e);
        }
    }

    @Override
    public List<Person> getAllPersons() {
        return persons;
    }

    @Override
    public void saveAllPersons(List<Person> persons) {
        this.persons = persons;
        try {
            //Read json existing json file
            JsonNode rootNode = objectMapper.readTree(new File(DATA_FILEPATH));

            //Update the section "persons"
            ((ObjectNode) rootNode).putPOJO("persons", persons);

            //Write all sections
            objectMapper.writeValue(new File(DATA_FILEPATH), rootNode);

        } catch (IOException e) {
            logger.error("Failed to save person data to json file: {}", DATA_FILEPATH, e);
        }
    }
}