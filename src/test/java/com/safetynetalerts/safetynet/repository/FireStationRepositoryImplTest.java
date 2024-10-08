package com.safetynetalerts.safetynet.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.safetynetalerts.safetynet.dto.FireStationDTO;
import com.safetynetalerts.safetynet.model.FireStation;
import com.safetynetalerts.safetynet.service.FireStationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)
public class FireStationRepositoryImplTest {

	@Mock
	private FireStationRepository fireStationRepository;

	@InjectMocks
	private FireStationServiceImpl fireStationService;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testSaveAllFireStationsDoesNotAffectOtherSections() throws IOException {
		//ARRANGE
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("1509 Culver St", "3"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		fireStationService.addFireStation(new FireStationDTO("1509 Culver St", "3"));
		
		//ASSERT
		verify(fireStationRepository, times(1)).saveAllFireStations(anyList());
		
		//Check if any other section has not be change by the action
		JsonNode rootNode = objectMapper.readTree(new File("src/main/resources/data.json"));
		JsonNode personsNode = rootNode.path("persons");
		JsonNode medicalRecordsNode = rootNode.path("medicalrecords");
		
		assertNotNull(personsNode, "Persons section should not be null");
		assertNotNull(medicalRecordsNode, "Medical records section should not be null");
	}
	
	@Test
	public void testGetAllFireStations() {
		//ARRANGE
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("1509 Culver St", "3"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		List<FireStationDTO> result = fireStationService.getAllFireStations();
		
		//ASSERT
		assertEquals(1, result.size());
		assertEquals("1509 Culver St", result.get(0).getAddress());
		assertEquals("3", result.get(0).getStation());
	}
	
	@Test
	public void testAddFireStation() {
		//ARRANGE
		FireStationDTO firestationDTO = new FireStationDTO();
		firestationDTO.setAddress("1234 Newaddress Nw");
		firestationDTO.setStation("5");
		
		List<FireStation> fireStations = new ArrayList<>();
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		fireStationService.addFireStation(firestationDTO);
		
		//ASSERT
		verify(fireStationRepository, times(1)).saveAllFireStations(anyList());
	}
	
	@Test
	public void testUpdateFireStation() {
		//ARRANGE
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("1509 Culver St", "3"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		FireStationDTO updatedFirestationDTO = new FireStationDTO("1509 Culver St", "4");
		
		//ACT
		fireStationService.updateFireStation(updatedFirestationDTO);
		
		//ASSERT
		verify(fireStationRepository, times(1)).saveAllFireStations(anyList());
		assertEquals("4", fireStations.get(0).getStation());
	}
	
	@Test
	public void testDeleteFireStation() {
		//ARRANGE
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("1509 Culver St", "3"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		fireStationService.deleteFireStation("1509 Culver St");
		
		//ASSERT
		verify(fireStationRepository, times(1)).saveAllFireStations(anyList());
		assertTrue(fireStations.isEmpty());
	}
	
	@Test
	public void testGetAllFireStationsEmpty() {
		//ARRANGE
		when(fireStationRepository.getAllFireStations()).thenReturn(new ArrayList<>());
		
		//ACT
		List<FireStationDTO> result = fireStationService.getAllFireStations();
		
		//ASSERT
		assertEquals(0, result.size());
	}
	
	@Test
	public void testUpdateNonExistingFireStation() {
		//ARRANGE
		List<FireStation> fireStations = new ArrayList<>();
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		FireStationDTO updatedFirestationDTO = new FireStationDTO("NonExisting address", "5");
		
		//ACT & ASSERT
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {	
			fireStationService.updateFireStation(updatedFirestationDTO);
		});
		
		//ADDITIONNAL ASSERT
		assertEquals("FireStation not found", exception.getMessage());
		
		verify(fireStationRepository, times(1)).getAllFireStations();
		verify(fireStationRepository, times(0)).saveAllFireStations(anyList());
	}
	
	@Test
	public void testDeleteNonExistingFireStation() {
		//ARRANGE
		List<FireStation> fireStations = new ArrayList<>();
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		fireStationService.deleteFireStation("NonExisting address");
		
		//ASSERT
		verify(fireStationRepository, times(1)).getAllFireStations();
		verify(fireStationRepository, times(0)).saveAllFireStations(anyList());
	}
	
	@Test
	public void testAddFireStationWithNullValuesThrowsException() {
		//ARRANGE
		FireStationDTO firestationDTO = new FireStationDTO(null, null);
		
		//ACT & ASSERT
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			fireStationService.addFireStation(firestationDTO);
		});
		
		assertEquals("Address and station cannot be null", exception.getMessage());
	}
	
	@Test
	public void testUpdateFireStationWithNullValuesThrowsException() {
		//ARRANGE	
		FireStationDTO updatedFirestationDTO = new FireStationDTO(null, null);
		
		//ACT & ASSERT
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			fireStationService.updateFireStation(updatedFirestationDTO);
		});
		
		assertEquals("Address and station cannot be null", exception.getMessage());
	}
	
	@Test
	public void testGetAllFireStationsWithMultipleEntries() {
		//ARRANGE
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("1509 Culver St", "3"));
		fireStations.add(new FireStation("29 15th St", "4"));
		
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);
		
		//ACT
		List<FireStationDTO> result = fireStationService.getAllFireStations();
		
		//ASSERT
		assertEquals(2, result.size());
		assertEquals("1509 Culver St", result.get(0).getAddress());
		assertEquals("29 15th St", result.get(1).getAddress());
	}
}
