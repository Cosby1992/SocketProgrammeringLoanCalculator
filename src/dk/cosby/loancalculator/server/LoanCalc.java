package dk.cosby.loancalculator.server;

import java.io.Serializable;

public class LoanCalc implements Serializable {

    private double monthlyPay;
    private double totalPay;

    public LoanCalc(double amount, double interest, int years) {

        totalPay = calculate(amount, interest, years);

        monthlyPay = totalPay / (years*12);

    }

    //private loan calculation method
    private double calculate(double amount, double interest, int years){

        //Kn = K0 * (1 + r)^n
        return amount * Math.pow(1 + (interest/100), years);

    }

    public double getMonthlyPay() {
        return monthlyPay;
    }

    public void setMonthlyPay(double monthlyPay) {
        this.monthlyPay = monthlyPay;
    }

    public double getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(double totalPay) {
        this.totalPay = totalPay;
    }
}
