package dk.cosby.loancalculator.server;

import java.io.Serializable;

public class BmiCalc implements Serializable {

    private double bmi;
    private String status = "normal";

    /*
        BMI under 18,5 = undervægtig
        BMI mellem 18,5-24,9 = normalvægt
        BMI mellem 25-29,9 = overvægtig
        BMI mellem 30-39,9 = fedme
        BMI over 40 = svær fedme
     */

    public BmiCalc(double height, double weight) {

        bmi = weight/Math.pow(height/100, 2);

        if(bmi < 18.5){
            status = "undervægtig";
        } else if(bmi < 24.9){
            status = "normalvægtig";
        } else if(bmi < 29.9){
            status = "overvægtig";
        } else if(bmi < 39.9){
            status = "fed";
        } else status = "svært fed";

    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
