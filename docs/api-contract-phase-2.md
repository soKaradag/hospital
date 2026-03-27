# Faz 2 API Contract

Bu dokuman, mevcut backend yapisini UI gelistirmeye uygun sekilde ozetler. Amac, frontend tarafinin ekran, form, liste ve detay akislarini backend kodunu tekrar okumadan planlayabilmesidir.

## 1. Genel Kurallar

Base path:

`/api`

Korumali endpoint'lerde header:

`Authorization: Bearer <accessToken>`

Tum basarili cevaplar:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "timestamp": "2026-03-27T12:00:00Z"
}
```

Tum hata cevaplari:

```json
{
  "success": false,
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "errors": [
    {
      "field": "username",
      "rejectedValue": "",
      "message": "username must not be blank"
    }
  ],
  "timestamp": "2026-03-27T12:00:00Z"
}
```

Ortak hata kodlari:
- `VALIDATION_ERROR`
- `RESOURCE_NOT_FOUND`
- `BUSINESS_RULE_VIOLATION`
- `DUPLICATE_RESOURCE`
- `UNAUTHORIZED`
- `FORBIDDEN`
- `INTERNAL_SERVER_ERROR`

Sayfali liste yapisi:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 120,
  "totalPages": 6,
  "first": true,
  "last": false
}
```

## 2. Veri Formatlari

- `UUID`: string
- `Instant`: UTC ISO-8601, ornek `2026-03-27T12:30:00Z`
- `LocalDate`: `YYYY-MM-DD`
- `LocalTime`: `HH:mm:ss`

Enum alanlari:
- `Role`: `ADMIN`, `DOCTOR`, `RECEPTIONIST`, `CASHIER`, `NURSE`
- `Gender`: `MALE`, `FEMALE`
- `AppointmentStatus`: `SCHEDULED`, `COMPLETED`, `CANCELLED`
- `Currency`: `TRY`, `USD`, `EUR`
- `PaymentMethod`: `CASH`, `CARD`, `TRANSFER`
- `PaymentStatus`: `PENDING`, `PAID`, `CANCELLED`

Ortak nested alanlar:

`contact`
```json
{
  "phone": {
    "countryCode": "90",
    "number": "5551234567"
  },
  "email": "user@example.com"
}
```

`address`
```json
{
  "country": {
    "code": "TR",
    "name": "Turkiye"
  },
  "city": {
    "code": "34",
    "name": "Istanbul"
  },
  "district": "Kadikoy",
  "postalCode": "34710",
  "addressLine": "Example street no 10"
}
```

## 3. Auth

### `POST /api/auth/login`
Public endpoint.

Request:
```json
{
  "username": "admin",
  "password": "secret"
}
```

Response data:
```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "refresh-token"
}
```

### `POST /api/auth/refresh`
Public endpoint.

Request:
```json
{
  "refreshToken": "refresh-token"
}
```

Response data:
```json
{
  "accessToken": "new-jwt-access-token",
  "refreshToken": "new-refresh-token"
}
```

### `POST /api/auth/logout`
Protected endpoint.

Request:
```json
{
  "refreshToken": "refresh-token"
}
```

Response data:
`null`

### `GET /api/auth/me`
Protected endpoint.

Response data:
```json
{
  "id": "uuid",
  "username": "admin",
  "role": "ADMIN",
  "firstName": "Serdar",
  "lastName": "Yilmaz",
  "email": "serdar@example.com"
}
```

## 4. Departments

### `POST /api/departments`
Request:
```json
{
  "name": "Cardiology",
  "description": "Heart related services"
}
```

### `PUT /api/departments/{id}`
Body, create ile ayni.

### `GET /api/departments/{id}`

### `GET /api/departments`
Query:
- `search`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "name": "Cardiology",
  "description": "Heart related services",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 5. Doctors

### `POST /api/doctors`
```json
{
  "firstName": "Ahmet",
  "lastName": "Yilmaz",
  "specialization": "Cardiology",
  "contact": {
    "phone": {
      "countryCode": "90",
      "number": "5551234567"
    },
    "email": "doctor@example.com"
  },
  "departmentId": "uuid"
}
```

### `PUT /api/doctors/{id}`
Body, create ile ayni.

### `GET /api/doctors/{id}`

### `GET /api/doctors`
Query:
- `search`
- `departmentId`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "firstName": "Ahmet",
  "lastName": "Yilmaz",
  "specialization": "Cardiology",
  "contact": {
    "phone": {
      "countryCode": "90",
      "number": "5551234567"
    },
    "email": "doctor@example.com"
  },
  "departmentId": "uuid",
  "departmentName": "Cardiology",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 6. Patients

### `POST /api/patients`
```json
{
  "firstName": "Ayse",
  "lastName": "Demir",
  "nationalId": "12345678901",
  "birthDate": "1990-06-10",
  "gender": "FEMALE",
  "contact": {
    "phone": {
      "countryCode": "90",
      "number": "5552223344"
    },
    "email": "ayse@example.com"
  },
  "address": {
    "country": {
      "code": "TR",
      "name": "Turkiye"
    },
    "city": {
      "code": "34",
      "name": "Istanbul"
    },
    "district": "Besiktas",
    "postalCode": "34353",
    "addressLine": "Example address"
  }
}
```

### `PUT /api/patients/{id}`
Body, create ile ayni.

### `GET /api/patients/{id}`

### `GET /api/patients`
Query:
- `search`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "firstName": "Ayse",
  "lastName": "Demir",
  "nationalId": "12345678901",
  "birthDate": "1990-06-10",
  "gender": "FEMALE",
  "contact": {
    "phone": {
      "countryCode": "90",
      "number": "5552223344"
    },
    "email": "ayse@example.com"
  },
  "address": {
    "country": {
      "code": "TR",
      "name": "Turkiye"
    },
    "city": {
      "code": "34",
      "name": "Istanbul"
    },
    "district": "Besiktas",
    "postalCode": "34353",
    "addressLine": "Example address"
  },
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 7. Appointments

### `POST /api/appointments`
```json
{
  "patientId": "uuid",
  "doctorId": "uuid",
  "appointmentDateTime": "2026-03-28T08:30:00Z",
  "status": "SCHEDULED",
  "notes": "First visit"
}
```

### `PUT /api/appointments/{id}`
Body, create ile ayni.

### `GET /api/appointments/{id}`

### `GET /api/appointments`
Query:
- `search`
- `patientId`
- `doctorId`
- `startDateTime`
- `endDateTime`
- `page`
- `size`
- `sort`

Not:
- `search` varsa diger filtrelere gore onceliklidir.

Response item:
```json
{
  "id": "uuid",
  "patientId": "uuid",
  "patientFullName": "Ayse Demir",
  "doctorId": "uuid",
  "doctorFullName": "Ahmet Yilmaz",
  "appointmentDateTime": "2026-03-28T08:30:00Z",
  "status": "SCHEDULED",
  "notes": "First visit",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 8. Encounters

### `POST /api/encounters`
```json
{
  "appointmentId": "uuid",
  "patientId": "uuid",
  "doctorId": "uuid",
  "complaint": "Chest pain",
  "diagnosisNote": "Initial diagnosis note",
  "treatmentNote": "Treatment note",
  "encounterDateTime": "2026-03-28T09:00:00Z"
}
```

### `PUT /api/encounters/{id}`
Body, create ile ayni.

### `GET /api/encounters/{id}`

### `GET /api/encounters`
Query:
- `search`
- `patientId`
- `doctorId`
- `startDateTime`
- `endDateTime`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "appointmentId": "uuid",
  "patientId": "uuid",
  "patientFullName": "Ayse Demir",
  "doctorId": "uuid",
  "doctorFullName": "Ahmet Yilmaz",
  "complaint": "Chest pain",
  "diagnosisNote": "Initial diagnosis note",
  "treatmentNote": "Treatment note",
  "encounterDateTime": "2026-03-28T09:00:00Z",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 9. Payments

### `POST /api/payments`
```json
{
  "patientId": "uuid",
  "encounterId": "uuid",
  "amount": 750.00,
  "currency": "TRY",
  "paymentMethod": "CARD",
  "paymentStatus": "PAID",
  "paidAt": "2026-03-28T09:30:00Z"
}
```

### `PUT /api/payments/{id}`
Body, create ile ayni.

### `GET /api/payments/{id}`

### `GET /api/payments`
Query:
- `patientId`
- `encounterId`
- `startPaidAt`
- `endPaidAt`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "patientId": "uuid",
  "patientFullName": "Ayse Demir",
  "encounterId": "uuid",
  "amount": 750.00,
  "currency": "TRY",
  "paymentMethod": "CARD",
  "paymentStatus": "PAID",
  "paidAt": "2026-03-28T09:30:00Z",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 10. Doctor Schedules

Role notu:
- Write: `ADMIN`, `RECEPTIONIST`
- Read: `ADMIN`, `RECEPTIONIST`, `DOCTOR`, `NURSE`

### `POST /api/doctor-schedules`
```json
{
  "doctorId": "uuid",
  "dayOfWeek": "MONDAY",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "active": true
}
```

### `PUT /api/doctor-schedules/{id}`
Body, create ile ayni.

### `GET /api/doctor-schedules/{id}`

### `GET /api/doctor-schedules`
Query:
- `doctorId`
- `dayOfWeek`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "doctorId": "uuid",
  "doctorFullName": "Ahmet Yilmaz",
  "dayOfWeek": "MONDAY",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "active": true,
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 11. Diseases

Role notu:
- Write: `ADMIN`, `DOCTOR`
- Read: `ADMIN`, `DOCTOR`, `NURSE`, `RECEPTIONIST`

### `POST /api/diseases`
```json
{
  "code": "I10",
  "name": "Hypertension",
  "description": "Primary hypertension"
}
```

### `PUT /api/diseases/{id}`
Body, create ile ayni.

### `GET /api/diseases/{id}`

### `GET /api/diseases`
Query:
- `search`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "code": "I10",
  "name": "Hypertension",
  "description": "Primary hypertension",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 12. Patient Diseases

Role notu:
- Write: `ADMIN`, `DOCTOR`, `NURSE`
- Read: `ADMIN`, `DOCTOR`, `NURSE`, `RECEPTIONIST`

### `POST /api/patient-diseases`
```json
{
  "patientId": "uuid",
  "diseaseId": "uuid",
  "diagnosedAt": "2026-03-28T10:00:00Z",
  "notes": "Known chronic condition"
}
```

### `PUT /api/patient-diseases/{id}`
Body, create ile ayni.

### `GET /api/patient-diseases/{id}`

### `GET /api/patient-diseases`
Query:
- `patientId`
- `diseaseId`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "patientId": "uuid",
  "patientFullName": "Ayse Demir",
  "diseaseId": "uuid",
  "diseaseCode": "I10",
  "diseaseName": "Hypertension",
  "diagnosedAt": "2026-03-28T10:00:00Z",
  "notes": "Known chronic condition",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 13. Encounter Diagnoses

Role notu:
- Write: `ADMIN`, `DOCTOR`, `NURSE`
- Read: `ADMIN`, `DOCTOR`, `NURSE`, `RECEPTIONIST`

### `POST /api/encounter-diagnoses`
```json
{
  "encounterId": "uuid",
  "diseaseId": "uuid",
  "notes": "Confirmed during encounter"
}
```

### `PUT /api/encounter-diagnoses/{id}`
Body, create ile ayni.

### `GET /api/encounter-diagnoses/{id}`

### `GET /api/encounter-diagnoses`
Query:
- `encounterId`
- `diseaseId`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "encounterId": "uuid",
  "diseaseId": "uuid",
  "diseaseCode": "I10",
  "diseaseName": "Hypertension",
  "notes": "Confirmed during encounter",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 14. Prescriptions

Role notu:
- Write: `ADMIN`, `DOCTOR`
- Read: `ADMIN`, `DOCTOR`, `NURSE`, `RECEPTIONIST`

### `POST /api/prescriptions`
```json
{
  "encounterId": "uuid",
  "patientId": "uuid",
  "doctorId": "uuid",
  "prescriptionDate": "2026-03-28",
  "notes": "Take twice daily"
}
```

### `PUT /api/prescriptions/{id}`
Body, create ile ayni.

### `GET /api/prescriptions/{id}`

### `GET /api/prescriptions`
Query:
- `encounterId`
- `patientId`
- `doctorId`
- `startDate`
- `endDate`
- `page`
- `size`
- `sort`

Response item:
```json
{
  "id": "uuid",
  "encounterId": "uuid",
  "patientId": "uuid",
  "patientFullName": "Ayse Demir",
  "doctorId": "uuid",
  "doctorFullName": "Ahmet Yilmaz",
  "prescriptionDate": "2026-03-28",
  "notes": "Take twice daily",
  "createdAt": "2026-03-27T12:00:00Z",
  "updatedAt": "2026-03-27T12:00:00Z"
}
```

## 15. UI Icin Pratik Notlar

- Login sonrasinda UI hem `accessToken` hem `refreshToken` saklamalidir.
- Korumali ekranlarda 401 alindiginda once `refresh` denenmeli, refresh de basarisizsa login ekranina donulmelidir.
- Liste ekranlari `page`, `size`, `sort` parametrelerini ortak kullanabilir.
- Form ekranlari `VALIDATION_ERROR` icindeki `errors` listesini field bazli gosterebilir.
- Klinik ekranlarda genel hasta gecmisi icin `patient-diseases`, encounter anina ait teshis icin `encounter-diagnoses` kullanilmalidir.
- UI ekran tasarimi icin Faz 3 beklenmeden bu contract baz alinabilir; yeni alanlar gerektikce contract incremental buyutulmelidir.
