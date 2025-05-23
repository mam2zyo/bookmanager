package model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class User {
    private int id;
    private String name;
    private String phoneNumber;
    @Setter
    private Loan loan;

    public User(String name, String number) {
        this.name = name;
        this.phoneNumber = number;
    }
}