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

END $$;
