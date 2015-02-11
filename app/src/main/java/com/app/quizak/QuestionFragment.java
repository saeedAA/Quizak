package com.app.quizak;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
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

/**
 * Created by Sa'eed Abdullah on 030, 30, 1, 2015.
 */
public class QuestionFragment extends Fragment {

    private TextView tvQuestion;
    private RadioButton rbAnswer1;
    private RadioButton rbAnswer2;
    private RadioButton rbAnswer3;
    private Button btnSubmit;

    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.quiz_fragment, container, false);
        tvQuestion = (TextView)rootView.findViewById(R.id.tv_question);
        rbAnswer1 = (RadioButton)rootView.findViewById(R.id.answer1);
        rbAnswer2 = (RadioButton)rootView.findViewById(R.id.answer2);
        rbAnswer3 = (RadioButton)rootView.findViewById(R.id.answer3);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);

        new FetchQuestion().execute();

        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SendAnswer sendAnswer = new SendAnswer();
                sendAnswer.execute();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.quiz_fragment,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh) {
            FetchQuestion fetchQuestion = new FetchQuestion();
            fetchQuestion.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchQuestion extends AsyncTask<Void, Void, Question>{

        private final String LOG_TAG = FetchQuestion.class.getSimpleName();

        @Override
        protected Question doInBackground(Void... arg0) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String questionJsonStr = null;
            Question question = null;
            Parser parser = new Parser();

            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http").authority("10.0.2.2").appendPath("quizak");
                URL url = new URL(builder.toString());
                Log.v(LOG_TAG, url.toString());

                //Send request to back-end
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                Log.v(LOG_TAG, "connected..");

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
                questionJsonStr = buffer.toString();
                Log.v(LOG_TAG, questionJsonStr);

                question = parser.getQuestion(questionJsonStr);

                return question;
            }
            catch (IOException e){
                Log.e(LOG_TAG, e.toString());
                return null;
            } catch (JSONException e) {
                Log.v(LOG_TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Question aQuestion) {
            tvQuestion.setText(aQuestion.getQuestionStatement());
            rbAnswer1.setText(aQuestion.getAnswers()[0]);
            rbAnswer2.setText(aQuestion.getAnswers()[1]);
            rbAnswer3.setText(aQuestion.getAnswers()[2]);

            btnSubmit.setEnabled(true);

            super.onPostExecute(aQuestion);
        }
    }

    private class SendAnswer extends AsyncTask<Void, Void, Boolean>{
        private final String LOG_TAG = SendAnswer.class.getSimpleName();

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection urlConnection;
            BufferedReader reader = null;
            String correctAnswer = null;
            boolean isTrue = false;

            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http").authority("10.0.2.2").appendPath("quizak")
                        .appendPath("answer.php");
                if(rbAnswer1.isChecked())
                    builder.appendQueryParameter("index","0");
                else if(rbAnswer2.isChecked())
                    builder.appendQueryParameter("index","1");
                else if(rbAnswer3.isChecked())
                    builder.appendQueryParameter("index","2");
                else{
                    return null;
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

                correctAnswer = reader.readLine();

                Log.v(LOG_TAG, correctAnswer);

                if(correctAnswer.equals("true")){
                    isTrue = true;
                }
                else if(correctAnswer.equals("false")) {
                    isTrue = false;
                }

                return isTrue;

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
        protected void onPostExecute(Boolean aBoolean) {
            Toast toast = Toast.makeText(getActivity(),aBoolean.toString(),Toast.LENGTH_SHORT);
            toast.show();
            super.onPostExecute(aBoolean);
        }
    }
}