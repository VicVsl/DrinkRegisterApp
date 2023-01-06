package com.example.DrinkRegisterApp;

import androidx.annotation.NonNull;

import java.util.Objects;

public class User {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String group;
    private final String rank;
    private int balance;
    private final int pinCode;

    public User(String firstName, String lastName, String group, int pinCode) {
        this.id = 0;
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.rank = "regular";
        this.balance = 0;
        this.pinCode = pinCode;
    }

    public User(int id, String firstName, String lastName, String group, String rank, int balance, int pinCode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.rank = rank;
        this.balance = balance;
        this.pinCode = pinCode;
    }

    public void updateBalance(int change) {
        this.balance += change;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGroup() {
        return group;
    }

    public String getRank() {
        return rank;
    }

    public int getBalance() {
        return balance;
    }

    public int getPinCode() {
        return pinCode;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return firstName.equals(user.firstName) && lastName.equals(user.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, group, balance, pinCode);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", group='" + group + '\'' +
                ", rank='" + rank + '\'' +
                ", balance=" + balance +
                ", pinCode=" + pinCode +
                '}';
    }

    public String createShortName() {
        return firstName + " " + lastName.charAt(0) + ".";
    }
}
