package com.safetynetalerts.safetynet.service;

import java.util.List;

import com.safetynetalerts.safetynet.dto.FireStationCoverageDTO;
import com.safetynetalerts.safetynet.dto.FireStationDTO;

public interface FireStationService {

    List<FireStationDTO> getAllFireStations();
    void addFireStation (FireStationDTO firestationDTO);
    void updateFireStation(FireStationDTO firestationDTO);
    void deleteFireStation(String address);
    FireStationCoverageDTO getCoverageByStationNumber(int stationNumber);

}

