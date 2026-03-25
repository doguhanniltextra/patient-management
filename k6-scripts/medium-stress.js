import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: '15s', target: 50 }, // Ramp-up to 50 users
    { duration: '30s', target: 50 }, // Stay at 50 users
    { duration: '15s', target: 0 },  // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<800'],
    http_req_failed: ['rate<0.05'],    // Max 5% errors
  },
};

const BASE_URL = 'http://localhost:4004';

export function setup() {
  const username = `meduser_${randomString(8)}`;
  const password = 'Password123!';
  const headers = { 'Content-Type': 'application/json' };

  http.post(`${BASE_URL}/api/auth/register`, JSON.stringify({
    name: username, email: `${username}@example.com`, password: password, registerDate: new Date().toISOString().split('T')[0]
  }), { headers });

  const loginRes = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({ name: username, password: password }), { headers });
  
  let token = '';
  try { token = JSON.parse(loginRes.body).token; } catch (e) { }
  return { token };
}

export default function (data) {
  const params = { headers: { 'Authorization': `Bearer ${data.token}`, 'Content-Type': 'application/json' } };

  check(http.get(`${BASE_URL}/api/doctors?page=0&size=20`, params), { 'get doctors status 200': (r) => r.status === 200 });
  check(http.get(`${BASE_URL}/api/patients?page=0&size=20`, params), { 'get patients status 200': (r) => r.status === 200 });
  check(http.get(`${BASE_URL}/api/appointments/get?page=0&size=20`, params), { 'get appointments status 200/204': (r) => r.status === 200 || r.status === 204 });

  sleep(1);
}
