package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.ReportDTOs.*;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import edu.unimag.medical.domain.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public List<OfficeOccupancyResponse> getOfficeOcupancy(LocalDate from, LocalDate to) {
        return appointmentRepository.findOfficeOccupancyByDateRange(from, to).stream().map(
                row -> new OfficeOccupancyResponse(
                        (UUID) row[0],
                        (Integer) row[1],
                        (Long) row[2]
                ))
                .toList();
    }

    @Override
    @Transactional
    public List<DoctorProductivityResponse> getDoctorProductivity() {
        return appointmentRepository.findDoctorRankingByCompletedAppointments(AppointmentStatus.COMPLETED).stream().map(
                    row -> new DoctorProductivityResponse(
                            (UUID) row[0],
                            (String) row[1],
                            (Long) row[2]
                    ))
                    .toList();

    }

    @Override
    @Transactional
    public List<NoShowPatientResponse> getNoShowPatient(LocalDate from, LocalDate to) {
        return appointmentRepository.findNoShowCountByPatientAndDateRange(AppointmentStatus.NO_SHOW, from, to).stream().map(
                row -> new NoShowPatientResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (Long) row[2]
                ))
                .toList();
    }


}
