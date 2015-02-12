package com.app.quizak;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sa'eed Abdullah on 030, 30, 1, 2015.
 */
public class Parser {
    private static final String LOG_TAG = Parser.class.getSimpleName();

    public Question getQuestion(JSONObject questionJson) throws JSONException {
        final String QUIZAK_QUESTOION_STATEMENT = "questionStatement";
        final String QUIZAK_ANSWERS = "answers";

        String questionStatement;
        String[] answers = new String[3];

        questionStatement = questionJson.getString(QUIZAK_QUESTOION_STATEMENT);
        JSONArray answersJson = questionJson.getJSONArray(QUIZAK_ANSWERS);

        for(int i = 0; i<answersJson.length(); i++){
            answers[i] = answersJson.getString(i);
        }

        return new Question(questionStatement, answers);
    }

    public Quiz[] getQuiz(String quizzesJsonStr) throws JSONException {
        final String QUIZAK_QUIZ_ID = "mQuizID";
        final String QUIZAK_COURSE_ID = "mCourseID";
        final String QUIZAK_QUESTION_ARRAY = "mQuestions";
        final String QUIZAK_QUIZ_ARRAY = "mQuizes";
        final String QUIZAK_COURSE_NAME = "mCourseName";
        final String QUIZAK_QUIZ_ISTAKEN = "mIsTaken";
        final String QUIZAK_QUIZ_DEGREE = "mDegree";

        int quizId;
        int courseId;
        String courseName;
        boolean isTaken;
        int degree = -1;

        JSONObject quizManagerObject = new JSONObject(quizzesJsonStr);
        JSONArray quizesArrayJson = quizManagerObject.getJSONArray(QUIZAK_QUIZ_ARRAY);

        Quiz[] quizArray = new Quiz[quizesArrayJson.length()];

        for(int i=0; i<quizesArrayJson.length(); i++){
            JSONObject quizObject = quizesArrayJson.getJSONObject(i);
            quizId = quizObject.getInt(QUIZAK_QUIZ_ID);
            courseId = quizObject.getInt(QUIZAK_COURSE_ID);
            courseName = quizObject.getString(QUIZAK_COURSE_NAME);
            isTaken = quizObject.getBoolean(QUIZAK_QUIZ_ISTAKEN);
            degree = quizObject.getInt(QUIZAK_QUIZ_DEGREE);
            JSONArray questionsArrayJson = quizObject.getJSONArray(QUIZAK_QUESTION_ARRAY);

            quizArray[i] = new Quiz(courseId,quizId,courseName,isTaken);
            quizArray[i].setDegree(degree);

            for(int j=0; j<questionsArrayJson.length(); j++){
                Question question = getQuestion(questionsArrayJson.getJSONObject(j));
                quizArray[i].addQuestion(question);
            }
        }
        return quizArray;
    }

    public Question[] getQuestionsFromQuiz(String quizJsonStr) throws JSONException {
        final String QUIZAK_QUESTION_ARRAY = "mQuestions";

        JSONObject quizObject = new JSONObject(quizJsonStr);
        JSONArray questionsArrayJson = quizObject.getJSONArray(QUIZAK_QUESTION_ARRAY);

        Question[] questions = new Question[questionsArrayJson.length()];

        for(int i=0; i<questionsArrayJson.length(); i++){
            questions[i] =getQuestion(questionsArrayJson.getJSONObject(i));
        }
        return questions;
    }
}
