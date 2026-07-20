# 📅 Appointment API

A RESTful API built using Java for managing appointments.
This API allows users to create, view, update, and delete appointments.

---

## 🚀 Features

- Create new appointments
- View all appointments
- Get appointment by ID
- Update appointment details
- Delete appointments

---

## 🛠️ Tech Stack

- Java
- Spring Boot
- REST API
- Maven

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------------------|----------------------------|
| GET | /appointments | Get all appointments |
| GET | /appointments/{id} | Get appointment by ID |
| POST | /appointments | Create new appointment |
| PUT | /appointments/{id} | Update appointment |
| DELETE | /appointments/{id} | Delete appointment |

---

## 📥 Sample Request

### ➤ Create Appointment

POST `/appointments`

```json
{
"name": "John Doe",
"date": "2026-07-25",
"time": "10:30 AM",
"description": "Doctor visit"
}

