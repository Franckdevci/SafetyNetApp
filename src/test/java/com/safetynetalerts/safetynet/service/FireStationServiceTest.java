package com.safetynetalerts.safetynet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynetalerts.safetynet.dto.FireStationCoverageDTO;
import com.safetynetalerts.safetynet.dto.FireStationDTO;
import com.safetynetalerts.safetynet.model.FireStation;
import com.safetynetalerts.safetynet.model.MedicalRecord;
import com.safetynetalerts.safetynet.model.Person;
import com.safetynetalerts.safetynet.repository.FireStationRepository;
import com.safetynetalerts.safetynet.repository.MedicalRecordRepository;
import com.safetynetalerts.safetynet.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {

	@Mock
	private FireStationRepository fireStationRepository;
	
	@Mock
	private PersonRepository personRepository;
	
	@Mock
	private MedicalRecordRepository medicalRecordRepository;
	
	@InjectMocks
	private FireStationServiceImpl fireStationService;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testGetAllFireStations() {
		//ARRANGE
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("834 Binoc Ave","4"));
		fireStations.add(new FireStation("947 E. Rose Dr", "3"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		List<FireStationDTO> fireStationDTO = fireStationService.getAllFireStations();
		
		//ASSERT
		assertEquals(2, fireStationDTO.size());
		assertEquals("834 Binoc Ave", fireStationDTO.get(0).getAddress());
		assertEquals("947 E. Rose Dr", fireStationDTO.get(1).getAddress());
	}
	
	@Test
	public void testAddFireStation() {
		//ARRANGE
		FireStationDTO firestationDTO = new FireStationDTO();
		firestationDTO.setAddress("1509 Culver St");
		firestationDTO.setStation("4");
		
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("29 15th St", "1"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		fireStationService.addFireStation(firestationDTO);
		
		//ASSERT
		verify(fireStationRepository, times(1)).saveAllFireStations(anyList());
	}
	
	@Test
	public void testUpdateFireStation() {
		//ARRANGE
		FireStationDTO firestationDTO = new FireStationDTO();
		firestationDTO.setAddress("29 15th St");
		firestationDTO.setStation("1");
		
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("29 15th St", "4"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		fireStationService.updateFireStation(firestationDTO);
		
		//ASSERT
		ArgumentCaptor<List<FireStation>> captor = ArgumentCaptor.forClass(List.class);
		verify(fireStationRepository, times(1)).saveAllFireStations(captor.capture());
		
		List<FireStation> updatedFireStations = captor.getValue();
		assertEquals(1, updatedFireStations.size());
		assertEquals("1", updatedFireStations.get(0).getStation());
	}
	
	
	@Test
	public void testUpdateNonExistingFireStation() {
		//ARRANGE
		FireStationDTO firestationDTO = new FireStationDTO();
		firestationDTO.setAddress("1509 NonExistingAddress St");
		firestationDTO.setStation("1");
		
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("29 15th St", "4"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT & ASSERT
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			fireStationService.updateFireStation(firestationDTO);
		});
		
		//ASSERT
		assertEquals("FireStation not found", exception.getMessage());
		
		//VERIFY NO SAVE OPERATION
		verify(fireStationRepository, never()).saveAllFireStations(anyList());
	}
	
	
	@Test
	public void testDeleteFireStation() {
		//ARRANGE
		String address = "748 Townings Dr";
		
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("748 Townings Dr","3"));
		fireStations.add(new FireStation("947 E. Rose Dr", "1"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		fireStationService.deleteFireStation(address);
		
		//ASSERT
		ArgumentCaptor<List<FireStation>> captor = ArgumentCaptor.forClass(List.class);
		verify(fireStationRepository, times(1)).saveAllFireStations(captor.capture());
		
		List<FireStation> updatedFireStations = captor.getValue();
		assertEquals(1, updatedFireStations.size());
		assertEquals("947 E. Rose Dr", updatedFireStations.get(0).getAddress());
	}
	
	
	@Test
	public void testDeleteNonExistingFireStation() {
		//ARRANGE
		String address = "1234 NonExisting Ad";
		
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("748 Townings Dr", "3"));
		fireStations.add(new FireStation("947 E. Rose Dr", "1"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		fireStationService.deleteFireStation(address);
		
		//ASSERT
		verify(fireStationRepository, never()).saveAllFireStations(anyList());
	}
	
	
	/**
	 * Tests the getCoverageByStationNumber method of the FireStationService
	 * 
	 * This test verifies that the method correctly retrieves and processes the coverage information
	 * for a given fireStation number, including the number of adults and children
	 * 
	 */
	@Test
	public void testGetCoverageByStationNumber() {
		//ARRANGE
		int stationNumber = 3;
		
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("John","Boyd","1509 Culver St","Culver","97451","841-874-6512","jaboyd@email.com"));
		persons.add(new Person("Jacob","Boyd","1509 Culver St","Culver","97451","841-874-6513","drk@email.com"));
		persons.add(new Person("Tenley","Boyd","1509 Culver St","Culver","97451","841-874-6512","tenz@email.com"));
		
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("1509 Culver St", "3"));
		fireStations.add(new FireStation("29 15th St", "2"));
		
		List<MedicalRecord> medicalRecords = new ArrayList<>();
		medicalRecords.add(new MedicalRecord("John","Boyd","03/06/1984", new ArrayList<>(), new ArrayList<>()));
		medicalRecords.add(new MedicalRecord("Jacob","Boyd","03/06/1989",new ArrayList<>(), new ArrayList<>()));
		medicalRecords.add(new MedicalRecord("Tenley","Boyd", "02/18/2012",new ArrayList<>(), new ArrayList<>()));
		
		when(personRepository.getAllPersons()).thenReturn(persons);
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		when(medicalRecordRepository.getMedicalRecord("John","Boyd")).thenReturn(medicalRecords.get(0));
		when(medicalRecordRepository.getMedicalRecord("Jacob","Boyd")).thenReturn(medicalRecords.get(1));
		when(medicalRecordRepository.getMedicalRecord("Tenley","Boyd")).thenReturn(medicalRecords.get(2));
		
		//ACT
		FireStationCoverageDTO coverage = fireStationService.getCoverageByStationNumber(stationNumber);
		
		//ASSERT
		assertEquals(3, coverage.getPersons().size()); //3 address covered by station number 3
		assertEquals(2, coverage.getNumberOfAdults()); //2 adults and 1 child (Tenley Boyd)
		assertEquals(1, coverage.getNumberOfChildren()); //1 Child: Tenley Boyd 02/18/20212
	}
	
}
