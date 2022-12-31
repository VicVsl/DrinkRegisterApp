package com.example.DrinkRegisterApp;

import androidx.annotation.NonNull;

import java.util.Objects;

public class User {

    private final int id;
    private final String firstName;
    private final String lastName;
    private String group;
    private String rank;
    private double balance;
    private int pinCode;

    public User(int id, String firstName, String lastName, String group, String rank, int pinCode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.rank = rank;
        this.balance = 0;
        this.pinCode = pinCode;
    }

    public void addBalance(double addition) {
        this.balance += addition;
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

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public double getBalance() {
        return Math.round(balance * 100.0) / 100.0;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return pinCode == user.pinCode && firstName.equals(user.firstName) && lastName.equals(user.lastName) && group.equals(user.group) && rank.equals(user.rank);
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
                ", balance=" + Math.round(balance * 100.0) / 100.0 +
                ", pinCode=" + pinCode +
                '}';
    }
}
