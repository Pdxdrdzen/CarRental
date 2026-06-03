package com.carrental.model.user;

public class Client {
    private Long id;
    private String firstName, lastName, email, password, phoneNumber, driverLicenseNumber;

    public Client(Long id, String firstName, String lastName, String email,
                  String password, String phoneNumber, String driverLicenseNumber) {
        this.id = id; this.firstName = firstName; this.lastName = lastName;
        this.email = email; this.password = password;
        this.phoneNumber = phoneNumber; this.driverLicenseNumber = driverLicenseNumber;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public String getFullName() { return firstName + " " + lastName; }
}