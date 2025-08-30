package com.project.constants;

public class Endpoints {

    public static final String APPOINTMENT_CONTROLLER_REQUEST = "/appointments";

    public static final String CREATE_APPOINTMENT = "/";
    public static final String UPDATE_APPOINTMENT = "/{id}";
    public static final String UPDATE_APPOINTMENT_STATUS = "/appointment-update/{id}/{status}";
    public static final String DELETE_APPOINTMENT = "/{id}";
    public static final String GET_ALL_APPOINTMENTS = "/get";
    public static final String VALIDATE_IDS = "/validate/{patientId}/{doctorId}";

}
