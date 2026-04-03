package com.project.patient_service.constants;

public class Endpoints {
    public static final String PATIENT_CONTROLLER_REQUEST = "/patients";
    public static final String PATIENT_CONTROLLER_UPDATE_PATIENT = "/{id}";
    public static final String PATIENT_CONTROLLER_DELETE_PATIENT = "/{id}";
    public static final String PATIENT_CONTROLLER_FIND_PATIENT_BY_ID = "/find/{id}";
    public static final String PATIENT_CONTROLLER_FIND_PATIENT_BY_EMAIL = "/find/email/{email}";


    public static final String MEDICAL_RECORD_CONTROLLER_REQUEST = "/patients/records";
    public static final String MEDICAL_RECORD_CONTROLLER_GET_BY_PATIENT = "/patient/{patientId}";
    public static final String PATIENT_LAB_RESULTS = "/{patientId}/lab-results";
    public static final String PATIENT_LAB_RESULTS_BY_ORDER = "/{patientId}/lab-results/{labOrderId}";

}
