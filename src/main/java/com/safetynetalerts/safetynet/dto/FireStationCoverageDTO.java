package com.safetynetalerts.safetynet.dto;

import lombok.Data;

import java.util.List;

@Data
public class FireStationCoverageDTO {

    private List<FireStationPersonDTO> persons;
    private int numberOfAdults;
    private int numberOfChildren;

}
