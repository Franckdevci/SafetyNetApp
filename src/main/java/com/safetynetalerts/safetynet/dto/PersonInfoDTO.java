package com.safetynetalerts.safetynet.dto;

import java.util.List;

import lombok.Data;

/**
 * Provide the results of URL "/personInfo"
 *
 * @author Franck Armel
 *
 */


@Data
public class PersonInfoDTO {

    private String firstName;
    private String lastName;
    private String address;
    private int age;
    private String email;
    private List<String> medications;
    private List<String> allergies;

}