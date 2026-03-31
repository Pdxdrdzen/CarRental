package com.carrental.model.user;

import java.time.LocalDate;

public class Employee extends User{

    private String Position;
    private LocalDate hireDate;

    public Employee(Long id, String firstName, String lastName,
                String email, String password, String phoneNumber, UserRole role){
        super(id, firstName, lastName, email, password, phoneNumber, UserRole.EMPLOYEE);
        this.Position=Position;
        this.hireDate=hireDate;
    }
    //Gettery
    public String getPosition(){
        return Position;
    }
    public String getHireDate(){
        return hireDate.toString();
    }
    //Settery
    public void setPosition(String Position){
        this.Position=Position;
    }
    public void setHireDate(LocalDate hireDate){
        this.hireDate=hireDate;
    }

}
