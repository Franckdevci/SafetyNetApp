package com.safetynetalerts.safetynet.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynetalerts.safetynet.dto.FireStationCoverageDTO;
import com.safetynetalerts.safetynet.dto.FireStationDTO;
import com.safetynetalerts.safetynet.service.FireStationService;

import lombok.Data;

@Data
@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private static final Logger logger = LogManager.getLogger(FireStationController.class);

    @Autowired
    private FireStationService fireStationService;

    /**
     *
     * @return Status ok (200) if no exception (fetching all fireStations)
     */
    @GetMapping
    public ResponseEntity<List<FireStationDTO>> getAllFireStations() {
        logger.debug("Fetching all fireStations.");

        try {
            List<FireStationDTO> fireStations = fireStationService.getAllFireStations();
            logger.info("Fetched all fireStations successfully. Number of fireStations: {} ", fireStations.size());
            return ResponseEntity.ok(fireStations);
        } catch (Exception e) {
            logger.error("Error fetching all fireStations: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * @param stationNumber
     * @return a list of persons covered by the corresponding fireStation
     * if status ok
     */
    @GetMapping(params = "stationNumber")
    public ResponseEntity<FireStationCoverageDTO> getCoverageByStationNumber(@RequestParam("stationNumber") int stationNumber) {
        logger.debug("Getting a list of persons covered by the corresponding fireStation.");

        try {
            FireStationCoverageDTO coverage = fireStationService.getCoverageByStationNumber(stationNumber);
            logger.info("Getting a list of persons covered by the corresponding fireStation , successfully.");
            return ResponseEntity.ok(coverage);
        } catch (Exception e) {
            logger.error("Error fetching the list: ", e);
            return ResponseEntity.status(500).build();
        }
    }


    /**
     * @param fireStationDTO
     * @return if status ok, adding a fireStation
     */
    @PostMapping
    public ResponseEntity<Void> addFireStation(@RequestBody FireStationDTO fireStationDTO) {
        logger.debug("Adding a fireStation: {}", fireStationDTO);

        if(fireStationDTO.getAddress() == null || fireStationDTO.getAddress().isEmpty() ||
                fireStationDTO.getStation() == null || fireStationDTO.getStation().isEmpty()
        ) {
            logger.error("Address or station is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            fireStationService.addFireStation(fireStationDTO);
            logger.info("Added fireStation successfully: {}", fireStationDTO);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error adding a fireStation: ", fireStationDTO, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * @param fireStationDTO
     * @return if status ok, update a fireStation
     */
    @PutMapping
    public ResponseEntity<Void> updateFireStation (@RequestBody FireStationDTO fireStationDTO) {
        logger.debug("Updating a fireStation: {}", fireStationDTO);

        if(fireStationDTO.getAddress() == null || fireStationDTO.getAddress().isEmpty() ||
                fireStationDTO.getStation() == null || fireStationDTO.getStation().isEmpty()
        ) {
            logger.error("Address or station is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            fireStationService.updateFireStation(fireStationDTO);
            logger.info("Updated fireStation successfully: {}", fireStationDTO);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error updating fireStation: ", fireStationDTO ,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @param address
     * @return if status ok, deleted a fireStation successfully
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteFireStation(@RequestParam(required =false) String address) {
        logger.debug("Deleting a fireStation: {}", address);

        if(address == null || address.isEmpty()) {
            logger.error("Address is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            fireStationService.deleteFireStation(address);
            logger.info("Deleted fireStation: {}", address);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Deleted fireStation successfully with address: {}", address, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}