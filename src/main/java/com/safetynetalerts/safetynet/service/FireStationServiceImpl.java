package com.safetynetalerts.safetynet.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynetalerts.safetynet.dto.FireStationPersonDTO;
import com.safetynetalerts.safetynet.dto.FireStationCoverageDTO;
import com.safetynetalerts.safetynet.dto.FireStationDTO;
import com.safetynetalerts.safetynet.model.FireStation;
import com.safetynetalerts.safetynet.model.MedicalRecord;
import com.safetynetalerts.safetynet.model.Person;
import com.safetynetalerts.safetynet.repository.FireStationRepository;
import com.safetynetalerts.safetynet.repository.MedicalRecordRepository;
import com.safetynetalerts.safetynet.repository.PersonRepository;

@Service
public class FireStationServiceImpl implements FireStationService {

    private static final Logger logger= LogManager.getLogger(FireStationServiceImpl.class);

    @Autowired
    private FireStationRepository fireStationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     *GET all fireStations
     */
    @Override
    public List<FireStationDTO> getAllFireStations() {
        logger.debug("Fetching all fireStations from repository");

        List<FireStation> fireStations = fireStationRepository.getAllFireStations();

        logger.debug("Converting fireStations to DTOs.");
        return fireStations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * ADD firestation
     */
    @Override
    public void addFireStation (FireStationDTO fireStationDTO) {

        if (fireStationDTO.getAddress() == null || fireStationDTO.getStation() == null) {
            throw new IllegalArgumentException("Address and station cannot be null");
        }

        FireStation fireStation = convertToEntity(fireStationDTO);
        logger.debug("Adding fireStation to repository. ", fireStation);

        List<FireStation> fireStations = fireStationRepository.getAllFireStations();
        fireStations.add(fireStation);
        fireStationRepository.saveAllFireStations(fireStations);

        logger.debug("FireStation added successfully: {} ", fireStation);
    }

    /**
     *UPDATED a firestation
     */
    @Override
    public void updateFireStation(FireStationDTO fireStationDTO) {

        if (fireStationDTO.getAddress() == null || fireStationDTO.getStation() == null) {
            throw new IllegalArgumentException("Address and station cannot be null");
        }

        List<FireStation> fireStations = fireStationRepository.getAllFireStations();
        logger.debug("Updating fireStation in repository: {}", fireStationDTO);

        boolean stationExists = fireStations.stream().anyMatch(f -> f.getAddress().equals(fireStationDTO.getAddress()));

        if(!stationExists) {
            throw new IllegalArgumentException("FireStation not found");
        }

        fireStations.removeIf(f -> f.getAddress().equals(fireStationDTO.getAddress()));

        logger.warn("FireStation not found with address: {}", fireStationDTO.getAddress());

        fireStations.add(convertToEntity(fireStationDTO));

        fireStationRepository.saveAllFireStations(fireStations);
        logger.debug("FireStation updated successfully: {}", fireStationDTO);


    }


    /**
     * DELETED a fireStation
     */
    @Override
    public void deleteFireStation(String address) {
        List<FireStation> fireStations = fireStationRepository.getAllFireStations();
        logger.debug("Deleting a fireStation with address: {}", address);

        boolean removed = fireStations.removeIf(fs -> fs.getAddress().equals(address));

        if (removed) {
            fireStationRepository.saveAllFireStations(fireStations);
            logger.debug("Deleted a fireStation with address: {}", address);
        } else {
            logger.warn("No fireStation found with address: {}", address);
        }
    }


    /**
     * Retrieves the coverage information for a given firestation number
     *
     * @param stationNumber the fire station number for which the coverage information is to be retrieved
     * @return a {@link FireStationCoverageDTO} object containing the coverage information
     *
     */
    @Override
    public FireStationCoverageDTO getCoverageByStationNumber(int stationNumber) {
        logger.debug("Getting the coverage with station number: {}", stationNumber);

        List<Person> listOfPersons = personRepository.getAllPersons();
        logger.debug("Retrieved {} persons from repository.", listOfPersons.size());

        List<FireStation> listOfFireStations = fireStationRepository.getAllFireStations();
        logger.debug("Retrieved {} fireStations from repository.", listOfFireStations.size());

        String stationNumberStr = String.valueOf(stationNumber);
        List <String> coveredAddresses = listOfFireStations.stream()
                .filter(fs -> fs.getStation().equals(stationNumberStr))
                .map(FireStation::getAddress).toList();
        logger.debug("Found {} adresses covered by the station number {}", coveredAddresses.size(), stationNumber);

        List<Person> coveredPersons = listOfPersons.stream()
                .filter(person -> coveredAddresses
                        .contains(person.getAddress()))
                .toList();
        logger.debug("Found {} persons covered by the station number {}", coveredPersons.size(), stationNumber);

        List<FireStationPersonDTO> personCoverageDTOs = coveredPersons.stream()
                .map(this::convertToFireStationPersonDTO)
                .collect(Collectors.toList());
        logger.debug("Converted covered persons to DTOs.");

        long childrenCount = coveredPersons.stream()
                .filter(person -> {
                    MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecord(person.getFirstName(), person.getLastName());
                    if(medicalRecord == null) {
                        logger.debug("No medical record found for person: {} {}", person.getFirstName(), person.getLastName());
                        return false;
                    }
                    int age = getAge(medicalRecord.getBirthdate());
                    logger.debug("Person: {} {} is {} years old", person.getFirstName(), person.getLastName(), age);

                    return age <= 18;
                })
                .count();
        logger.debug("Found {} children covered by station number {}", childrenCount, stationNumber);

        FireStationCoverageDTO coverageDTO = new FireStationCoverageDTO();
        coverageDTO.setPersons(personCoverageDTOs);
        coverageDTO.setNumberOfAdults(coveredPersons.size() - (int) childrenCount);
        coverageDTO.setNumberOfChildren((int)childrenCount);
        logger.debug("Returning coverageDTO: {}", coverageDTO);
        return coverageDTO;

    }

    private FireStationDTO convertToDTO(FireStation firestation) {

        FireStationDTO dto = new FireStationDTO();
        dto.setAddress(firestation.getAddress());
        dto.setStation(firestation.getStation());
        return dto;

    }

    private FireStation convertToEntity(FireStationDTO dto) {

        FireStation firestation = new FireStation();
        firestation.setAddress(dto.getAddress());
        firestation.setStation(dto.getStation());
        return firestation;

    }

    private FireStationPersonDTO convertToFireStationPersonDTO(Person person) {

        FireStationPersonDTO firestationPersonDTO = new FireStationPersonDTO();
        firestationPersonDTO.setFirstName(person.getFirstName());
        firestationPersonDTO.setLastName(person.getLastName());
        firestationPersonDTO.setAddress(person.getAddress());
        firestationPersonDTO.setPhone(person.getPhone());
        return firestationPersonDTO;

    }

    private int getAge(String birthdate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthDate = LocalDate.parse(birthdate, formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();

    }

}