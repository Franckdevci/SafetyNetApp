package com.safetynetalerts.safetynet.service;

import java.util.List;
import java.util.stream.Collectors;

import com.safetynetalerts.safetynet.model.FireStation;
import com.safetynetalerts.safetynet.model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynetalerts.safetynet.repository.FireStationRepository;
import com.safetynetalerts.safetynet.repository.PersonRepository;

@Service
public class PhoneAlertServiceImpl implements PhoneAlertService {

    private static final Logger logger= LogManager.getLogger(PhoneAlertServiceImpl.class);

    @Autowired
    private FireStationRepository fireStationRepository;

    @Autowired
    private PersonRepository personRepository;

    /**
     *Retrieves a list of phone numbers for all persons covered by a specific fire station
     *
     *@param stationNumber (the number of the fireStation to search for covered persons)
     *@return a list of distinct phone numbers of persons covered by the specified fireStation
     *
     */
    @Override
    public List<String> getPhoneNumbersByFireStation(int stationNumber) {
        List<String> coveredAddresses = fireStationRepository.getAllFireStations().stream()
                .filter(fs -> fs.getStation().equals(String.valueOf(stationNumber)))
                .map(FireStation::getAddress).toList();

        List<String> phoneNumbers = personRepository.getAllPersons().stream()
                .filter(person -> coveredAddresses.contains(person.getAddress()))
                .map(Person::getPhone)
                .distinct()
                .collect(Collectors.toList());

        return phoneNumbers;
    }


}