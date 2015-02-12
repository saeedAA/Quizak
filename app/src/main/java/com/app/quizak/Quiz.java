package com.app.quizak;

import java.util.ArrayList;

/**
 * Created by Sa'eed Abdullah on 010, 10, 2, 2015.
 */
public class Quiz {
    public static final String QUIZ_ID_KEY = "quiz_id";

    private int mCourseId;
    private int mQuizId;
    private String mCourseName;
    private ArrayList<Question> mQuestions;
    private boolean mIsTaken;
    private int mDegree;

    public Quiz(int courseId, int quizId, String courseName, boolean isTaken){
        this.mCourseId = courseId;
        this.mQuizId = quizId;
        this.mCourseName = courseName;
        this.mIsTaken = isTaken;
        mQuestions = new ArrayList<Question>();
        this.mDegree = -1;
    }

    public String getCourseName(){
        return mCourseName;
    }

    public Question getQuestionAt(int index){
        return mQuestions.get(index);
    }

    public int getQuestionCount(){
        return mQuestions.size();
    }

    public boolean addQuestion(Question question){
        return mQuestions.add(question);
    }

    public int getQuizId(){
        return mQuizId;
    }

    public void setDegree(int degree){
        mDegree = degree;
    }

    public int getDegree(){
        return mDegree;
    }

    public boolean isTaken(){
        return mIsTaken;
    }
}
