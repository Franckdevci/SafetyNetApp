package com.safetynetalerts.safetynet.dto;

import lombok.Data;

import java.util.List;

/**
 * Provide the results of URL "/firestationCoverage"
 *
 * @author Franck Armel
 *
 */

@Data
public class FireStationCoverageDTO {

    private List<FireStationPersonDTO> persons;
    private int numberOfAdults;
    private int numberOfChildren;

}
