package com.safetynetalerts.safetynet.dto;

import java.util.List;

import lombok.Data;

/**
 * Provide the results of URL "/childAlert"
 *
 * @author Franck Armel
 *
 */

@Data
public class ChildAlertDTO {

    private String firstName;
    private String lastName;
    private int age;
    private List<HouseHoldMemberDTO> householdMembers;
}