package com.safetynetalerts.safetynet.dto;

import java.util.List;

import lombok.Data;

/**
 * Provide the results of URL "/flood/stations"
 *
 * @author Franck Armel
 *
 */

@Data
public class FloodDTO {

    private String address;
    private List<FireDTO> residents;

}