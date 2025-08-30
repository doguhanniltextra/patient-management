package com.project.billing_service.constants;

public class LogMessages {
    public static final String LISTENER_RECEIVED_MESSAGE = "APPOINTMENT: KAFKA LISTENER - RECEIVED MESSAGE -> {}";
    public static final String INVOICE_GENERATED = "APPOINTMENT: KAFKA LISTENER - INVOICE GENERATED -> {}";
    public static final String FAILED_TO_PARSE_OR_GENERATE_INVOICE = "APPOINTMENT: KAFKA LISTENER - FAILED TO PARSE MESSAGE OR GENERATE INVOICE";
    public static final String FAILED_TO_SAVE_INVOICE_PDF = "Failed to save invoice PDF";
    public static final String FAILED_TO_CREATE_INVOICE_DIR = "Failed to save invoice PDF";
    public static final String FAILED_TO_CREATE_INVOICE_GENERATION = "Invoice generation failed";

}
