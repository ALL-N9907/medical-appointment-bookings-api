package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.ReportDTOs.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    List<OfficeOccupancyResponse> getOfficeOccupancy(LocalDateTime from, LocalDateTime to);
    List<DoctorProductivityResponse> getDoctorProductivity();
    List<NoShowPatientResponse> getNoShowPatient(LocalDateTime from, LocalDateTime to);

}
