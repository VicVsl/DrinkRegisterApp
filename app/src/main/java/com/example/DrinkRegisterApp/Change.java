package com.example.DrinkRegisterApp;

import java.util.Objects;

public class Change {

    private final String firstName;
    private final String lastName;
    private int amount;

    public Change(String firstName, String lastName, int amount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.amount = amount;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Change change = (Change) o;
        return Objects.equals(firstName, change.firstName) && Objects.equals(lastName, change.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    @Override
    public String toString() {
        return '\n' + firstName + ' ' + lastName.charAt(0) + ". : " + amount;
    }
}
