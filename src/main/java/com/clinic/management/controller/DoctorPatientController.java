package com.clinic.management.controller;

import com.clinic.management.entity.Patient;
import com.clinic.management.entity.Prescription;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor-my-data")
public class DoctorPatientController {

    private final PrescriptionService prescriptionService;
    private final PatientRepository patientRepository; // Use Repository directly

    public DoctorPatientController(PrescriptionService prescriptionService, PatientRepository patientRepository) {
        this.prescriptionService = prescriptionService;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/my-unique-patients")
    public ResponseEntity<List<Patient>> getMyUniquePatients(Principal principal) {
        String email = principal.getName();
        
        // 1. Get all prescriptions written by this doctor
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDoctorEmail(email);
        
        // 2. Extract unique Patient IDs, then fetch the actual Patient objects from DB
        List<Patient> uniquePatients = prescriptions.stream()
                .map(Prescription::getPatientId) // This is a Long
                .distinct()
                .map(id -> patientRepository.findById(id).orElse(null)) // Fetch from DB
                .filter(Objects::nonNull) // Remove any patients not found in DB
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(uniquePatients);
    }

    @GetMapping("/patient-history/{patientId}")
    public ResponseEntity<List<Prescription>> getPatientHistoryByMe(
            @PathVariable Long patientId, 
            Principal principal) {
        
        String doctorEmail = principal.getName();
        // This ensures the doctor only sees history they created for this patient
        List<Prescription> history = prescriptionService.getHistoryByDoctorAndPatient(doctorEmail, patientId);
        
        return ResponseEntity.ok(history);
    }
}