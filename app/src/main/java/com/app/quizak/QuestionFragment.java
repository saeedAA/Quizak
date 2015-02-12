package com.app.quizak;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sa'eed Abdullah on 030, 30, 1, 2015.
 */
public class QuestionFragment extends Fragment {

    private int mQuizId;
    private TextView questionNumberView;
    private TextView questionStatementView;
    private RadioButton answer1RadioButton;
    private RadioButton answer2RadioButton;
    private RadioButton answer3RadioButton;
    private RadioGroup answersGroup;
    private Button btnNxt;
    private Button btnPrev;
    private Button btnSubmit;
    private Question[] mQuestions;
    private int mQuestionNumber;
    private int[] answers;
    private String mToken;

    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);
        btnNxt = (Button) rootView.findViewById(R.id.btn_nxt);
        btnPrev = (Button) rootView.findViewById(R.id.btn_prev);
        questionNumberView = (TextView) rootView.findViewById(R.id.question_num_view);
        questionStatementView = (TextView) rootView.findViewById(R.id.questionStatement_view);
        answer1RadioButton = (RadioButton) rootView.findViewById(R.id.answer1_radio);
        answer2RadioButton = (RadioButton) rootView.findViewById(R.id.answer2_radio);
        answer3RadioButton = (RadioButton) rootView.findViewById(R.id.answer3_radio);
        answersGroup = (RadioGroup) rootView.findViewById(R.id.answers_group);
        mQuestionNumber = 0;


        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Quiz.QUIZ_ID_KEY)
                && intent.hasExtra(User.USER_TOKEN_KEY)){

            mQuizId = intent.getExtras().getInt(Quiz.QUIZ_ID_KEY);
            mToken = intent.getExtras().getString(User.USER_TOKEN_KEY);

            GetQuestions getQuestions = new GetQuestions();
            getQuestions.execute();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                recordAnswer();

                if(!isQuizFinished()){
                    Toast toast = Toast.makeText(getActivity(),
                            "Please, answer all " + mQuestions.length + " questions before submitting!"
                            ,Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    SendAnswer sendAnswer = new SendAnswer();
                    sendAnswer.execute();
                }
            }
        });

        btnNxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mQuestionNumber == mQuestions.length - 2) {
                    btnNxt.setEnabled(false);
                }

                recordAnswer();

                mQuestionNumber++;

                typeQuestion();

                btnPrev.setEnabled(true);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mQuestionNumber == 1) {
                    btnPrev.setEnabled(false);
                }

                recordAnswer();

                mQuestionNumber--;

                typeQuestion();

                btnNxt.setEnabled(true);
            }
        });

        return rootView;
    }

    void recordAnswer(){
        switch (answersGroup.getCheckedRadioButtonId()){
            case R.id.answer1_radio:
                answers[mQuestionNumber]=0;
                break;
            case R.id.answer2_radio:
                answers[mQuestionNumber]=1;
                break;
            case R.id.answer3_radio:
                answers[mQuestionNumber]=2;
                break;
        }
    }

    void typeQuestion(){
        questionStatementView.setText(mQuestions[mQuestionNumber].getQuestionStatement());
        answer1RadioButton.setText(mQuestions[mQuestionNumber].getAnswers()[0]);
        answer2RadioButton.setText(mQuestions[mQuestionNumber].getAnswers()[1]);
        answer3RadioButton.setText(mQuestions[mQuestionNumber].getAnswers()[2]);
        questionNumberView.setText((mQuestionNumber+1) + " of " + mQuestions.length);

        switch (answers[mQuestionNumber]){
            case 0:
                answersGroup.check(R.id.answer1_radio);
                break;
            case 1:
                answersGroup.check(R.id.answer2_radio);
                break;
            case 2:
                answersGroup.check(R.id.answer3_radio);
                break;
            default:
                answersGroup.clearCheck();
        }
    }

    boolean isQuizFinished(){
        boolean finished = true;
        for(int i = 0; i < answers.length; i++){
            if (answers[i] == -1)
                finished = false;
        }
        return finished;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.quiz_fragment,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh) {
            //FetchQuestion fetchQuestion = new FetchQuestion();
            //fetchQuestion.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetQuestions extends AsyncTask<Void, Void, Question[]> {
        private final String LOG_TAG = GetQuestions.class.getSimpleName();

        @Override
        protected Question[] doInBackground(Void... params) {
            HttpURLConnection urlConnection;
            BufferedReader reader = null;
            String quizJsonStr = null;
            boolean isTrue = false;

            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http").authority("10.0.2.2").appendPath("quizak")
                        .appendPath("questions.php")
                        .appendQueryParameter(Quiz.QUIZ_ID_KEY,Integer.toString(mQuizId));


                URL url = new URL(builder.toString());
                Log.v(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Receive into input stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                quizJsonStr = buffer.toString();
                Log.v(LOG_TAG,quizJsonStr);

                Parser parser = new Parser();
                Question[] questions = parser.getQuestionsFromQuiz(quizJsonStr);

                return questions;

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG,e.toString());
            } catch (ProtocolException e) {
                Log.e(LOG_TAG,e.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG,e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Question[] questions) {
            //ArrayList<Quiz> quizArrayList = new ArrayList<Quiz>(Arrays.asList(quizzes));
            mQuestions = questions;
            answers = new int[mQuestions.length];

            for (int i = 0; i < answers.length; i++) {
                answers[i] = -1;
            }

            mQuestionNumber = 0;
            questionStatementView.setText(mQuestions[0].getQuestionStatement());
            answer1RadioButton.setText(mQuestions[0].getAnswers()[0]);
            answer2RadioButton.setText(mQuestions[1].getAnswers()[1]);
            answer3RadioButton.setText(mQuestions[2].getAnswers()[2]);
            questionNumberView.setText((mQuestionNumber+1) + " of " + mQuestions.length);

            btnSubmit.setEnabled(true);
            btnNxt.setEnabled(true);

            super.onPostExecute(questions);
        }
    }


    private class SendAnswer extends AsyncTask<Void, Void, Integer>{
        private final String LOG_TAG = SendAnswer.class.getSimpleName();

        @Override
        protected Integer doInBackground(Void... params) {
            HttpURLConnection urlConnection;
            BufferedReader reader = null;
            String degree = null;
            User user = new User(mToken);


            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http").authority("10.0.2.2").appendPath("quizak")
                        .appendPath("answer.php")
                        .appendQueryParameter(Quiz.QUIZ_ID_KEY,Integer.toString(mQuizId))
                        .appendQueryParameter(User.USER_ID_KEY,Integer.toString(user.getId()));
                for(int i=0; i<mQuestions.length; i++){
                    builder.appendQueryParameter(Integer.toString(i),Integer.toString(answers[i]+1));
                }

                URL url = new URL(builder.toString());
                Log.v(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                Log.v(LOG_TAG, "answer sent..");

                //Receive into input stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                degree = reader.readLine();

                Log.v(LOG_TAG, degree);

                return Integer.parseInt(degree);

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG,e.toString());
            } catch (ProtocolException e) {
                Log.e(LOG_TAG,e.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG,e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer aInteger) {
            Toast toast = Toast.makeText(getActivity(),aInteger.toString(),Toast.LENGTH_SHORT);
            toast.show();
            super.onPostExecute(aInteger);
        }
    }
}