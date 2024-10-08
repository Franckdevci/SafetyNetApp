package com.safetynetalerts.safetynet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynetalerts.safetynet.model.FireStation;
import com.safetynetalerts.safetynet.model.Person;
import com.safetynetalerts.safetynet.repository.FireStationRepository;
import com.safetynetalerts.safetynet.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PhoneAlertServiceTest {
	
	@Mock
	private FireStationRepository fireStationRepository;
	
	@Mock
	private PersonRepository personRepository;
	
	@InjectMocks
	private PhoneAlertServiceImpl phoneAlertService;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testGetPhoneNumbersByFireStation() {
		//ARRANGE
		int stationNumber = 3;
		
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("1509 Culver St","3"));
		
		List<Person> persons = new ArrayList<>();
		persons.add(new Person ("John","Boyd","1509 Culver St","Culver","97451","841-874-6512","jaboyd@email.com"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		when(personRepository.getAllPersons()).thenReturn(persons);
		
		//ACT
		List<String> phoneNumbers = phoneAlertService.getPhoneNumbersByFireStation(stationNumber);
		
		//ASSERT
		assertEquals(1, phoneNumbers.size());
		assertEquals("841-874-6512", phoneNumbers.get(0));
		
	}
}
