package com.clinic.management.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clinic.management.entity.Admin;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.AdminRepository;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.repository.PatientRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    
    @Autowired 
    private PatientRepository patientRepository;

    public CustomUserDetailsService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Attempting to load user for Security: " + username);

        /* =========================
           1. CHECK ADMIN
           ========================= */
        Optional<Admin> adminOpt = adminRepository.findByEmail(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPassword().toString())
                    .authorities("ROLE_ADMIN")
                    .disabled(false)
                    .build();
        }

        /* =========================
           2. CHECK DOCTOR
           ========================= */
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(username);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            return User.builder()
                    .username(doctor.getEmail())
                    .password(doctor.getPassword().toString())
                    .authorities("ROLE_DOCTOR")
                    .disabled(false)
                    .build();
        }

        /* =========================
           3. CHECK PATIENT
           ========================= */
        Optional<Patient> patientOpt = patientRepository.findByEmail(username);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            // ✅ FIX: Using builder ensures ROLE_PATIENT is explicitly assigned.
            // This prevents the 403 error caused by empty authorities in the Entity.
            return User.builder()
                    .username(patient.getEmail())
                    .password(patient.getPassword().toString())
                    .authorities("ROLE_PATIENT")
                    .disabled(false)
                    .build();
        }

        /* =========================
           USER NOT FOUND
           ========================= */
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}
