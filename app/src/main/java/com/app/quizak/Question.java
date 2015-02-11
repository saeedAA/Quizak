package com.app.quizak;

/**
 * Created by Sa'eed Abdullah on 030, 30, 1, 2015.
 */
public class Question {
    private static final String LOG_TAG = Question.class.getSimpleName();
    private String questionStatement;
    //int answersNumber;
    private String[] answers = new String[3];

    public Question(String qs, String[] a){
        this.questionStatement = qs;
        //this.answersNumber = noAnswers;
        for(int i=0; i< a.length; i++){
            this.answers[i] = a[i];
        }
    }

    public String getQuestionStatement(){
        return this.questionStatement;
    }

    public String[] getAnswers(){
        return this.answers;
    }
}
