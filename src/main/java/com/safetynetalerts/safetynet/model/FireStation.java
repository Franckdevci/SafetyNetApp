package com.safetynetalerts.safetynet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FireStation Model
 *
 * @author Franck Armel
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireStation {

    private String address;
    private String station;

}
