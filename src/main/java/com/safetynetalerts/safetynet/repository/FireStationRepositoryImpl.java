package com.safetynetalerts.safetynet.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynetalerts.safetynet.model.FireStation;

import java.io.File;

/**
 * Implementation of the FirestationRepository interface
 * This class manages the persistence of Firestation data using a JSON file
 *
 * It provides methods to load, retrieve and save firestation data from/to JSON file
 * The data is initially loaded from the file specified by DATA_FILEPATH
 *
 */
@Repository
public class FireStationRepositoryImpl implements FireStationRepository {

    private static final Logger logger= LogManager.getLogger(FireStationRepositoryImpl.class);

    // Path for JSON file
    private static final String DATA_FILEPATH = "src/main/resources/data.json";
    private List<FireStation> firestations = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    //Constructor
    public FireStationRepositoryImpl() {
        loadData();
    }

    private void loadData() {
        try {
            JsonNode rootNode = objectMapper.readTree(new File (DATA_FILEPATH));
            JsonNode firestationsNode = rootNode.path("firestations");
            firestations = objectMapper.readValue(firestationsNode.toString(),
                    new TypeReference<List<FireStation>>() {});
        } catch (IOException e) {
            logger.error("Failed to load fireStations from json file: {}", DATA_FILEPATH, e);
        }

    }

    @Override
    public List<FireStation> getAllFireStations() {
        return firestations;
    }

    @Override
    public void saveAllFireStations(List<FireStation> firestations) {
        this.firestations = firestations;
        try {

            //Read existing json file
            JsonNode rootNode = objectMapper.readTree(new File(DATA_FILEPATH));

            //Update the section fireStations
            ((ObjectNode) rootNode).putPOJO("firestations", firestations);

            //Write all sections in json file
            objectMapper.writeValue(new File(DATA_FILEPATH), rootNode);

        } catch (IOException e) {
            logger.error("Failed to save fireStations data to json file: {}", DATA_FILEPATH, e);
        }
    }
}