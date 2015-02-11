package com.app.quizak;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Sa'eed Abdullah on 010, 10, 2, 2015.
 */
public class QuizFragment extends Fragment {

    private User mUser;

    public QuizFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz, container, false);
        String email;
        String token;
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(User.USER_MAIL_KEY)
                && intent.hasExtra(User.USER_TOKEN_KEY)) {
            token = intent.getExtras().getString(User.USER_TOKEN_KEY);
            email = intent.getExtras().getString(User.USER_MAIL_KEY);
            mUser = new User(token);
            mUser.setMail(email);

            Log.v("QuizFragment",Integer.toString(mUser.getId()));
        }
        return rootView;
    }

    private class SendAnswer extends AsyncTask<Void, Void, Boolean> {
        private final String LOG_TAG = SendAnswer.class.getSimpleName();

        @Override
        protected Boolean doInBackground(Void... params) {
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