package com.example.authdemo.module.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class UpdateProfileRequest {

    @Size(max = 100, message = "Họ tên quá dài")
    private String fullName;

    @Pattern(regexp = "^\\d{10,15}$", message = "SĐT phải là số, từ 10-15 ký tự")
    private String phoneNumber;

    private String address;

    // Định dạng ngày-tháng-năm (Ví dụ: 30-01-2000)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    private String sex;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String fullName, String phoneNumber, String address, LocalDate dateOfBirth, String sex) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
    }

    // Getters & Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
}