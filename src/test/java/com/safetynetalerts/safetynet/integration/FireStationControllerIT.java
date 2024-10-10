package com.safetynetalerts.safetynet.integration;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;


import com.safetynetalerts.safetynet.service.FireStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynetalerts.safetynet.dto.FireStationCoverageDTO;
import com.safetynetalerts.safetynet.dto.FireStationDTO;
import com.safetynetalerts.safetynet.dto.FireStationPersonDTO;


@SpringBootTest
@AutoConfigureMockMvc
public class FireStationControllerIT {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private FireStationService fireStationService;
	
	
	/**
	 * Tests the GET endpoint for retrieving all fireStations
	 * 
	 * This test verifies that the "/firestation" endpoint returns a list of fireStations
	 * with the correct status code, content type, and JSON structure
	 * 
	 * The test mock the response from the fireStation service to ensure the endpoint
	 * correctly handles the response
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAllFireStations() throws Exception {
		List<FireStationDTO> fireStations = new ArrayList<>();
		fireStations.add(new FireStationDTO("123 Street Ad", "1"));
		
		when(fireStationService.getAllFireStations()).thenReturn(fireStations);
		
		mockMvc.perform(get("/firestation"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$[0].address").value("123 Street Ad"));
	}
	
	@Test
	public void testAddFireStation() throws Exception {
		FireStationDTO firestationDTO = new FireStationDTO("123 Street Ad", "1");
		
		mockMvc.perform(post("/firestation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firestationDTO)))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testUpdateFireStation() throws Exception {
		FireStationDTO firestationDTO = new FireStationDTO("123 Street Ad", "1");
		
		mockMvc.perform(put("/firestation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firestationDTO)))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testDeleteFirestation() throws Exception {
		mockMvc.perform(delete("/firestation")
				.param("address", "123 Street Ad")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testGetCoverageByStationNumber() throws Exception {
		FireStationCoverageDTO coverageDTO = new FireStationCoverageDTO();
		coverageDTO.setNumberOfAdults(2);
		coverageDTO.setNumberOfChildren(1);
		
		List<FireStationPersonDTO> persons = new ArrayList<>();
		persons.add(new FireStationPersonDTO("John","Doe","1234 street Ad" ,"123-456-7890"));
		coverageDTO.setPersons(persons);
		
		when(fireStationService.getCoverageByStationNumber(1)).thenReturn(coverageDTO);
		
		mockMvc.perform(get("/firestation?stationNumber=1"))
		.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.numberOfAdults").value(2))
		.andExpect(jsonPath("$.numberOfChildren").value(1))
		.andExpect(jsonPath("$.persons[0].firstName").value("John"))
		.andExpect(jsonPath("$.persons[0].lastName").value("Doe"));
		
	}
	
	
	@Test
	public void testAddFireStationThrowsException() throws Exception {
		FireStationDTO firestationDTO = new FireStationDTO("123 Street Ad", "1");
		
		doThrow(new RuntimeException("Unexpected error")).when(fireStationService).addFireStation(firestationDTO);
		
		mockMvc.perform(post("/firestation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firestationDTO)))
		.andExpect(status().isInternalServerError());
	}
		
	@Test
	public void testDeleteFireStationThrowsException() throws Exception {
		String address = "123 Street Ad";
		
		doThrow(new RuntimeException("Unexpected error")).when(fireStationService).deleteFireStation(address);
		
		mockMvc.perform(delete("/firestation")
				.param("address", address)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void testAddFireStationWithMissingFields() throws Exception {
		FireStationDTO firestationDTO = new FireStationDTO();
		firestationDTO.setAddress("");
		firestationDTO.setStation("1");
		
		mockMvc.perform(post("/firestation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firestationDTO)))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testUpdateFireStationWithMissingFields() throws Exception {
		FireStationDTO firestationDTO = new FireStationDTO();
		firestationDTO.setAddress("");
		firestationDTO.setStation("1");
		
		mockMvc.perform(put("/firestation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firestationDTO)))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testDeleteFireStationWithMissingFields() throws Exception {
		mockMvc.perform(delete("/firestation")
				.param("address", "")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
	}
}
