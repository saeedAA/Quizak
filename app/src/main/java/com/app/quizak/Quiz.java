package com.app.quizak;

import java.util.ArrayList;

/**
 * Created by Sa'eed Abdullah on 010, 10, 2, 2015.
 */
public class Quiz {
    private int mCourseId;
    private int mQuizId;
    private ArrayList<Question> mQuestions;

    public Quiz(int courseId, int quizId){
        this.mCourseId = courseId;
        this.mQuizId = quizId;
        mQuestions = new ArrayList<Question>();
    }

    public boolean addQuestion(Question question){
        return mQuestions.add(question);
    }
}
