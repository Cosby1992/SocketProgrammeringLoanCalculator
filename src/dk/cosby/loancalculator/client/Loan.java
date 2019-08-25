package dk.cosby.loancalculator.client;

import java.io.Serializable;

public class Loan implements Serializable {

    private double amount = 0;
    private double interest = 0;
    private int duration = 0;

    public Loan() {
    }

    public Loan(double amount, double interest, int duration) {
        this.amount = amount;
        this.interest = interest;
        this.duration = duration;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
