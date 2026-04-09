package com.clinic.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDoctorRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String specialization;
    private String qualification; // Warning disappears once used in Controller
    private Integer experience;
    private Double fee;
    private Long clinicId;

    // --- MANUALLY ADD THESE GETTERS ---
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getSpecialization() { return specialization; }
    public String getQualification() { return qualification; }
    public Integer getExperience() { return experience; }
    public double getFee() { return fee; }
    public Long getClinicId() { return clinicId; }
    public String getPhone() { return phone; }


    // (Setters are also needed for Jackson to fill the object from JSON)
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public void setExperience(Integer experience) { this.experience = experience; }
    public void setFee(double fee) { this.fee = fee; }
    public void setClinicId(Long clinicId) { this.clinicId = clinicId; }
    public void setPhone(String phone) { this.phone = phone; }
}
