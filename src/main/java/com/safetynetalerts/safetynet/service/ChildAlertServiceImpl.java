package com.safetynetalerts.safetynet.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynetalerts.safetynet.dto.ChildAlertDTO;
import com.safetynetalerts.safetynet.dto.HouseHoldMemberDTO;
import com.safetynetalerts.safetynet.model.Person;
import com.safetynetalerts.safetynet.model.MedicalRecord;
import com.safetynetalerts.safetynet.repository.MedicalRecordRepository;
import com.safetynetalerts.safetynet.repository.PersonRepository;

@Service
public class ChildAlertServiceImpl implements ChildAlertService {

    private static final Logger logger= LogManager.getLogger(ChildAlertServiceImpl.class);

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * Retrieves a list of children living at a specific address along with their household members
     * A child is defined as person who is 18 years old or younger.
     *
     *  @param address (the address to search for children)
     *  @return a list of ChildAlertDTO objects, each representing a child ad their household members
     *
     */
    @Override
    public List<ChildAlertDTO> getChildrenByAddress(String address) {

        List<Person> personsAtSameAddress = personRepository.getAllPersons().stream()
                .filter(person -> person.getAddress().equals(address))
                .toList();

        List<ChildAlertDTO> children = personsAtSameAddress.stream().map(person -> {
                    MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecord(person.getFirstName(), person.getLastName());

                    int age = getAge(medicalRecord.getBirthdate());
                    if (age <= 18) {
                        ChildAlertDTO childAlertDTO = new ChildAlertDTO();
                        childAlertDTO.setFirstName(person.getFirstName());
                        childAlertDTO.setLastName(person.getLastName());
                        childAlertDTO.setAge(age);

                        List<HouseHoldMemberDTO> householdMembers = personsAtSameAddress.stream()
                                .filter(member -> !member.equals(person))
                                .map(member -> {
                                    HouseHoldMemberDTO householdMemberDTO = new HouseHoldMemberDTO();
                                    householdMemberDTO.setFirstName(member.getFirstName());
                                    householdMemberDTO.setLastName(member.getLastName());
                                    return householdMemberDTO;
                                })
                                .collect(Collectors.toList());
                        childAlertDTO.setHouseholdMembers(householdMembers);
                        return childAlertDTO;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull).collect(Collectors.toList());

        return children;
    }

    private int getAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthDate = LocalDate.parse(birthdate, formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

}