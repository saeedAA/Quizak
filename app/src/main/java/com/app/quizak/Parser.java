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
        Log.v(LOG_TAG, questionStatement);
        JSONArray answersJson = questionJson.getJSONArray(QUIZAK_ANSWERS);

        for(int i = 0; i<answersJson.length(); i++){
            answers[i] = answersJson.getString(i);
        }

        return new Question(questionStatement, answers);
    }

    public Quiz[] getQuiz(String quizJsonStr) throws JSONException {
        final String QUIZAK_QUIZ_ID = "mQuizID";
        final String QUIZAK_COURSE_ID = "mCourseID";
        final String QUIZAK_QUESTION_ARRAY = "mQuestions";
        final String QUIZAK_QUIZ_ARRAY = "mQuizes";

        int quizId;
        int courseId;

        JSONObject quizManagerObject = new JSONObject(quizJsonStr);
        JSONArray quizesArrayJson = quizManagerObject.getJSONArray(QUIZAK_QUIZ_ARRAY);

        Quiz[] quizArray = new Quiz[quizesArrayJson.length()];

        for(int i=0; i<quizesArrayJson.length(); i++){
            JSONObject quizObject = quizesArrayJson.getJSONObject(i);
            quizId = quizObject.getInt(QUIZAK_QUIZ_ID);
            courseId = quizObject.getInt(QUIZAK_COURSE_ID);
            quizArray[i] = new Quiz(courseId,quizId);
            JSONArray questionsArrayJson = quizObject.getJSONArray(QUIZAK_QUESTION_ARRAY);
            for(int j=0; j<questionsArrayJson.length(); j++){
                Question question = getQuestion(questionsArrayJson.getJSONObject(j));
                quizArray[i].addQuestion(question);
            }
        }
        return quizArray;
    }
}
