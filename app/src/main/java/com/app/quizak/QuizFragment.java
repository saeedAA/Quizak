package com.app.quizak;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.Arrays;

/**
 * Created by Sa'eed Abdullah on 010, 10, 2, 2015.
 */
public class QuizFragment extends Fragment {

    private User mUser;
    private QuizAdapter mQuizAdapter;
    private String mToken;

    public QuizFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mToken = savedInstanceState.getString(User.USER_TOKEN_KEY);
            Log.v(User.USER_TOKEN_KEY + " restoring", mToken);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz, container, false);
        String email;
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(User.USER_MAIL_KEY)
                && intent.hasExtra(User.USER_TOKEN_KEY)) {
            mToken = intent.getExtras().getString(User.USER_TOKEN_KEY);
            email = intent.getExtras().getString(User.USER_MAIL_KEY);
            mUser = new User(mToken);
            mUser.setMail(email);

            getActivity().setTitle(mUser.getName());

            ListView listView = (ListView) rootView.findViewById(R.id.quiz_listView);
            mQuizAdapter = new QuizAdapter(getActivity(),
                    R.layout.list_item_quiz,
                    new ArrayList<Quiz>());
            listView.setAdapter(mQuizAdapter);

            GetQuizes getQuizes = new GetQuizes();
            getQuizes.execute();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Quiz quiz = mQuizAdapter.getItem(position);
                    int quizId = quiz.getQuizId();

                    if(!quiz.isTaken()){
                        Intent intent = new Intent(getActivity(), QuestionActivity.class)
                                .putExtra(Quiz.QUIZ_ID_KEY, quizId)
                                .putExtra(User.USER_TOKEN_KEY,mToken);
                        startActivity(intent);
                    }
                }
            });

        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(User.USER_TOKEN_KEY, mToken);
        Log.v(User.USER_TOKEN_KEY + " saving", mToken);
    }

    private class GetQuizes extends AsyncTask<Void, Void, Quiz[]> {
        private final String LOG_TAG = GetQuizes.class.getSimpleName();

        @Override
        protected Quiz[] doInBackground(Void... params) {
            HttpURLConnection urlConnection;
            BufferedReader reader = null;
            String quizJsonStr = null;
            boolean isTrue = false;

            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http").authority("10.0.2.2").appendPath("quizak")
                        .appendPath("quizes.php")
                        .appendQueryParameter(User.USER_ID_KEY,Integer.toString(mUser.getId()));


                URL url = new URL(builder.toString());
                Log.v(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                Log.v(LOG_TAG, "id sent..");

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
                Quiz[] quiz = parser.getQuiz(quizJsonStr);

                return quiz;

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
        protected void onPostExecute(Quiz[] quizzes) {
            //ArrayList<Quiz> quizArrayList = new ArrayList<Quiz>(Arrays.asList(quizzes));
            for(int i=0; i<quizzes.length; i++){
                mQuizAdapter.add(quizzes[i]);
            }
            super.onPostExecute(quizzes);
        }
    }
}