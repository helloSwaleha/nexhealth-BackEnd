package com.clinic.management.controller;


import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinic.management.entity.Prescription;
import com.clinic.management.service.PrescriptionService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/patient")

public class PatientPrescriptionController {

    // 1. Declare the service variable (instance)
    private final PrescriptionService prescriptionService;

    // 2. Inject the service via Constructor
    public PatientPrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<?> getPatientPrescriptions(Principal principal) {
        try {
            String email = principal.getName();
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatientEmail(email);
            
            // ✅ Safety Check: If prescriptions is null, return an empty array response immediately
            if (prescriptions == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, Object>> response = prescriptions.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("doctorName", p.getDoctor() != null ? p.getDoctor().getName() : "Unknown");
                map.put("medicineName", p.getMedicineName());
                map.put("dosage", p.getDosage());
                map.put("instructions", p.getInstructions());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /* ======================================================
       ✅ ADD THIS METHOD: Fetch by Patient ID
       ====================================================== */
    @GetMapping("/prescriptions/{patientId}")
    public ResponseEntity<?> getPrescriptionsByPatientId(@PathVariable Long patientId) {
        try {
            // Use the service to fetch by the ID provided in the URL
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatientId(patientId);
            
            if (prescriptions == null || prescriptions.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, Object>> response = prescriptions.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                // Use null-safe checks for the Doctor object
                map.put("doctorName", p.getDoctor() != null ? p.getDoctor().getName() : "General Specialist");
                map.put("medicineName", p.getMedicineName());
                map.put("dosage", p.getDosage());
                map.put("instructions", p.getInstructions());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // This ensures we get a clear error message instead of a generic 500
            return ResponseEntity.status(500).body("Error fetching prescription for ID " + patientId + ": " + e.getMessage());
        }
    }
    
    @GetMapping("/prescriptions/{id}/download")
    public void downloadPrescription(@PathVariable Long id, HttpServletResponse response) throws IOException {
        // 1. Fetch the prescription
        Prescription prescription = prescriptionService.getPrescriptionById(id);

        // 2. Check if it actually exists
        if (prescription == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Prescription not found with ID: " + id);
            return;
        }

        // 3. Set up response headers
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=prescription_" + id + ".pdf";
        response.setHeader(headerKey, headerValue);

        // 4. Generate PDF
        prescriptionService.exportToPDF(prescription, response);
    }
}
