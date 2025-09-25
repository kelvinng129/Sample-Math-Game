package com.mobile.game;

public class RecordBean {
    private String date;// date of the record
    private int correct;//number of correct answers
    private int time;//time taken for the record

    public String getDate() {

        return date;
    }// Return the value of the date field

    public void setDate(String date) {

        this.date = date;
    }// Set the value of the date field

    public int getCorrect() {

        return correct;
    }//same

    public void setCorrect(int correct) {
        this.correct = correct;
    }//same

    public int getTime() {
        return time;
    }//same

    public void setTime(int time) {
        this.time = time;
    }//same
}
