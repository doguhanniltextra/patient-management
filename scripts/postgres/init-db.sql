-- Disable public schema access by default for safety
REVOKE ALL ON SCHEMA public FROM public;

-- Function to create user and schema with isolated permissions
DO $$
BEGIN
    -- Auth Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'auth_user') THEN
        CREATE USER auth_user WITH PASSWORD 'auth_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS auth_schema AUTHORIZATION auth_user;
    GRANT ALL ON SCHEMA auth_schema TO auth_user;
    ALTER USER auth_user SET search_path = auth_schema;

    -- Patient Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'patient_user') THEN
        CREATE USER patient_user WITH PASSWORD 'patient_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS patient_schema AUTHORIZATION patient_user;
    GRANT ALL ON SCHEMA patient_schema TO patient_user;
    ALTER USER patient_user SET search_path = patient_schema;

    -- Appointment Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'appointment_user') THEN
        CREATE USER appointment_user WITH PASSWORD 'appointment_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS appointment_schema AUTHORIZATION appointment_user;
    GRANT ALL ON SCHEMA appointment_schema TO appointment_user;
    ALTER USER appointment_user SET search_path = appointment_schema;

    -- Doctor Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'doctor_user') THEN
        CREATE USER doctor_user WITH PASSWORD 'doctor_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS doctor_schema AUTHORIZATION doctor_user;
    GRANT ALL ON SCHEMA doctor_schema TO doctor_user;
    ALTER USER doctor_user SET search_path = doctor_schema;

    -- Billing Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'billing_user') THEN
        CREATE USER billing_user WITH PASSWORD 'billing_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS billing_schema AUTHORIZATION billing_user;
    GRANT ALL ON SCHEMA billing_schema TO billing_user;
    ALTER USER billing_user SET search_path = billing_schema;

    -- Lab Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'lab_user') THEN
        CREATE USER lab_user WITH PASSWORD 'lab_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS lab_schema AUTHORIZATION lab_user;
    GRANT ALL ON SCHEMA lab_schema TO lab_user;
    ALTER USER lab_user SET search_path = lab_schema;

    -- Inventory Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'inventory_user') THEN
        CREATE USER inventory_user WITH PASSWORD 'inventory_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS inventory_schema AUTHORIZATION inventory_user;
    GRANT ALL ON SCHEMA inventory_schema TO inventory_user;
    ALTER USER inventory_user SET search_path = inventory_schema;

    -- Admission Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'admission_user') THEN
        CREATE USER admission_user WITH PASSWORD 'admission_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS admission_schema AUTHORIZATION admission_user;
    GRANT ALL ON SCHEMA admission_schema TO admission_user;
    ALTER USER admission_user SET search_path = admission_schema;

    -- Audit Service Pre-setup
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'audit_user') THEN
        CREATE USER audit_user WITH PASSWORD 'audit_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS audit_schema AUTHORIZATION audit_user;
    GRANT ALL ON SCHEMA audit_schema TO audit_user;
    ALTER USER audit_user SET search_path = audit_schema;

    -- Notification Service
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'notification_user') THEN
        CREATE USER notification_user WITH PASSWORD 'notification_pass_123';
    END IF;
    CREATE SCHEMA IF NOT EXISTS notification_schema AUTHORIZATION notification_user;
    GRANT ALL ON SCHEMA notification_schema TO notification_user;
    ALTER USER notification_user SET search_path = notification_schema;

END $$;

-- Explicitly create Notification Template table in case notification-service hasn't yet
CREATE TABLE IF NOT EXISTS notification_schema.notification_templates (
    id UUID PRIMARY KEY,
    template_code VARCHAR(255) NOT NULL UNIQUE,
    channel VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Ensure notification_user can access the table
GRANT ALL ON TABLE notification_schema.notification_templates TO notification_user;

-- Seed Templates
INSERT INTO notification_schema.notification_templates (id, template_code, channel, subject, body, created_at, updated_at)
VALUES 
(gen_random_uuid(), 'LAB_RESULT_READY', 'EMAIL', 'Your Lab Results are Ready', 'Dear Patient, your lab results for order [(${patientId})] are now available at: [(${reportUrl})]', now(), now()),
(gen_random_uuid(), 'APPOINTMENT_CONFIRMATION', 'EMAIL', 'Appointment Confirmation', 'Dear Patient, your appointment is confirmed for [(${appointmentDate})].', now(), now()),
(gen_random_uuid(), 'HOSPITAL_DISCHARGE', 'EMAIL', 'Discharge Summary', 'Dear Patient, you have been discharged. Admission ID: [(${admissionId})]. Get well soon!', now(), now())
ON CONFLICT (template_code) DO NOTHING;
