package com.mobile.game;

public class Question {
    private int number1;
    private int number2;
    private String fuhao;//( "+", "-", "*", "/")
    private int awaser;// answer of the question

    public int getAwaser() {
        return awaser;
    }

    public void setAwaser(int awaser) {
        this.awaser = awaser;
    }

    public int getNumber1() {
        return number1;// Return the value of the second number field
    }

    public void setNumber1(int number1) {

        this.number1 = number1;// Set the value of the first number field
    }

    public int getNumber2() {

        return number2;// same
    }

    public void setNumber2(int number2) {
        this.number2 = number2;// same
    }

    public String getFuhao() {
        return fuhao;
    }//same

    public void setFuhao(String fuhao) {
        this.fuhao = fuhao;
    }//same
}
