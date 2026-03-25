import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// Base API URL
const BASE_URL = 'http://localhost:4004';

// Test setup runs ONCE at the beginning of the test
export function setup() {
  const username = `testuser_${randomString(8)}`;
  const email = `${username}@example.com`;
  const password = 'Password123!';

  // 1. Register a user
  const registerPayload = JSON.stringify({
    name: username,
    email: email,
    password: password,
    registerDate: new Date().toISOString().split('T')[0]
  });

  const headers = { 'Content-Type': 'application/json' };
  
  const registerRes = http.post(`${BASE_URL}/auth/register`, registerPayload, { headers });
  
  // If user already exists or similar issue, try to login anyway.
  
  // 2. Login
  const loginPayload = JSON.stringify({
    name: username,
    password: password
  });

  const loginRes = http.post(`${BASE_URL}/auth/login`, loginPayload, { headers });
  
  // Extract token
  let token = 'placeholder_token';
  try {
     const body = JSON.parse(loginRes.body);
     token = body.token;
  } catch (e) {
     console.error("Failed to parse token from login response", loginRes.body);
  }

  // Return token so VUs can use it
  return { token: token };
}

// User Journey
export default function (data) {
  const params = {
    headers: {
      'Authorization': `Bearer ${data.token}`,
      'Content-Type': 'application/json'
    }
  };

  // Group 1: View Doctors
  const doctorsRes = http.get(`${BASE_URL}/doctors?page=0&size=20`, params);
  check(doctorsRes, {
    'doctors returned 200': (r) => r.status === 200,
  });

  // Group 2: View Patients
  const patientsRes = http.get(`${BASE_URL}/patients?page=0&size=20`, params);
  check(patientsRes, {
    'patients returned 200': (r) => r.status === 200,
  });

  // Group 3: View Appointments
  const appointmentsRes = http.get(`${BASE_URL}/appointments?page=0&size=20`, params);
  check(appointmentsRes, {
    'appointments returned 200 or 204': (r) => r.status === 200 || r.status === 204,
  });

  // Small sleep to simulate realistic user action
  sleep(1);
}
