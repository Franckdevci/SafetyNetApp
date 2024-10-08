package com.safetynetalerts.safetynet.repository;

import com.safetynetalerts.safetynet.model.FireStation;

import java.util.List;

public interface FireStationRepository {

    List<FireStation> getAllFireStations();
    void saveAllFireStations(List<FireStation> fireStations);

}
