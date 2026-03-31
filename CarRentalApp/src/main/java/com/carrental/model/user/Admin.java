package com.carrental.model.user;

public class Admin extends User{

        public Admin(Long id, String firstName, String lastName,
                        String email, String password, String phoneNumber, UserRole role){
            super(id, firstName, lastName, email, password, phoneNumber, UserRole.ADMIN);
        }

}
