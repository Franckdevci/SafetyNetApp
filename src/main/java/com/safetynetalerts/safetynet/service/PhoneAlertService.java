package com.safetynetalerts.safetynet.service;

import java.util.List;

public interface PhoneAlertService {

    List<String> getPhoneNumbersByFireStation(int stationNumber);

}