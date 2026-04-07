package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.DoctorScheduleDTOs;
import edu.unimag.medical.api.dto.DoctorScheduleDTOs.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(UUID doctorId, DoctorScheduleDTOs.CreateDoctorScheduleRequest req);
    DoctorScheduleResponse findByDoctorId_AndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
    List<DoctorScheduleResponse> findAll(UUID doctorId);

}
