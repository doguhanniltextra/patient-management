package com.project.constants;

public class Endpoints {
    public static final String DOCTOR_CONTROLLER_REQUEST = "/doctors";
    public static final String DOCTOR_CONTROLLER_UPDATE_DOCTOR = "/{id}";
    public static final String DOCTOR_CONTROLLER_DELETE_DOCTOR = "/{id}";
    public static final String DOCTOR_CONTROLLER_FIND_DOCTOR_BY_ID = "/find/{id}";
    public static final String DOCTOR_CONTROLLER_INCREASE_PATIENT = "/{id}/increase-patient";

    public static final String DOCTOR_CONTROLLER_SHIFTS = "/{doctorId}/shifts";
    public static final String DOCTOR_CONTROLLER_SHIFT_BY_ID = "/{doctorId}/shifts/{shiftId}";

    public static final String DOCTOR_CONTROLLER_LEAVES = "/{doctorId}/leaves";
    public static final String DOCTOR_CONTROLLER_LEAVE_APPROVE = "/{doctorId}/leaves/{leaveId}/approve";
    public static final String DOCTOR_CONTROLLER_LEAVE_BY_ID = "/{doctorId}/leaves/{leaveId}";

    public static final String DOCTOR_CONTROLLER_AVAILABILITY_BY_DOCTOR = "/{doctorId}/availability";
    public static final String DOCTOR_CONTROLLER_AVAILABILITY = "/availability";
    public static final String DOCTOR_CONTROLLER_LAB_ORDERS = "/{doctorId}/lab-orders";
}
