package com.carrental.model.user;

public class Client extends User {

    private String driverLicenseNumber;

    public Client(Long id, String firstName, String lastName,
                  String email, String password, String phoneNumber,
                  String driverLicenseNumber) {
        super(id, firstName, lastName, email, password, phoneNumber, UserRole.CLIENT);
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }
}
