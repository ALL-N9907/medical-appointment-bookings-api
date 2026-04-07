## Medical-Appointment-Bookings-Api

## *Description*

This project involves the development of a REST API for managing appointments at university medical clinics, with the goal of optimizing the administration of medical appointments within an educational institution.

The platform was created as a solution to the problems associated with manual or disorganized systems, such as duplicate appointments, scheduling conflicts, inefficient use of clinic rooms, and a lack of traceability in patient care.

The system enables centralized management of entities like:

Patients
Doctors and specialties
Clinics
Appointment types
Hours of operation
Medical appointments

Additionally, it ensures compliance with key business rules such as availability validation, prevention of schedule overlaps, and tracking of appointment statuses (scheduled, confirmed, canceled, completed, or no-show).

## ER Diagram

## Business rules
- The patient must exist and have an `ACTIVE` status.
- The doctor must exist and be active.
- The practice must exist and have an `AVAILABLE` status.
- The appointment cannot be created for a past date and time.
- The appointment must fall within the doctor's working hours.
- `endAt` is calculated by the system using the duration of the appointment type; the client must not send it.
- There cannot be any schedule overlap for the doctor, office, or patient.
- The initial status of every appointment is `SCHEDULED`.
### Confirmation
- Only a `SCHEDULED` appointment can be confirmed.

### Cancellation
- Only appointments in `SCHEDULED` or `CONFIRMED` status can be cancelled.
- A cancellation reason is required.
- Cancellation must immediately release that time slot from both the doctor's and the office's schedule.

### Completion
- Only a `CONFIRMED` appointment can be completed.
- An appointment cannot be completed before its scheduled start time.
- Administrative observations can be recorded upon completion.

### No-show
- Only a `CONFIRMED` appointment can be marked as `NO_SHOW`.
- An appointment cannot be marked as `NO_SHOW` before its scheduled start time.

### Availability and reports
- Availability depends on the doctor's working hours, existing appointments, and the appointment type duration.
- The system must be able to determine available slots, office occupancy, doctor productivity, and patients with the highest number of no-shows within a given date range.

## Design decisions

The project follows a **layered architecture** combined with the **Repository pattern**. The domain layer isolates entities, enums, and repositories from business logic, while `OfficeRepository` mixes Spring Data derived methods with custom `@Query` JPQL, keeping all data access concerns in one place.

The service layer applies **Interface Segregation** — `AvailabilityServiceImpl` implements `AvailabilityService` — and uses constructor injection via `@RequiredArgsConstructor`, enabling clean Mockito-based testing. Each service owns a bounded responsibility, delegating schedule queries to `DoctorScheduleService`.

The `Appointment` entity uses `@PrePersist` and `@PreUpdate` hooks to auto-manage timestamps and default status, while `@ManyToOne(fetch = LAZY)` and `@Builder` reflect a consistent **Builder pattern** and optimized persistence strategy across the domain model.

## Testing
Unit tests were implemented to validate:
- Appointment creation success flow
- Availability and reports
- Date and time validation for past appointments
- Doctor and office schedule overlap
- `endAt` calculation
- State transitions
- Working hours validation


## Project structure
```bash
src/main/java/edu/unimag/medical/
├── api/dto/
	├── request/        
    └── response/    
├── domain/
│   ├── entity/         
│    └── enums/      
│     └── repository/            
├── services/          
│   └── mapper/                        
└── test/               
           
```

## *Technology stack*

| Tecnología      | Versión |
|----------------|--------|
| Java           | 21     |
| JUnit 5        | 6.x    |
| Lombok         | 1.18.x |
| MapStruct      | 1.5.5  |
| Mockito        | 5.x    |
| PostgreSQL     | 16     |
| Spring Boot    | 4.x    |
| Testcontainers | 2.x    |

***Prerequisites***

 - Java 21 
 - Docker Desktop (required by Testcontainers for testing) 
 - Maven 3.9+

## steps for execution



***1. clone repository :***

```bash
https://github.com/ALL-N9907/medical-appointment-bookings-api.git

cd [medical-appointment-bookings-api
```
***2.Execute application***

```bash
mvn spring-boot:run
```
***3. Execute tests (requires Docker)***
```bash
mvn test
```








