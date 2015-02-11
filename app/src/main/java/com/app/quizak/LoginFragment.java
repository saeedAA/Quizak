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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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


public class LoginFragment extends Fragment {
    EditText mUserName;
    EditText mPassword;


    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        Button login = (Button) rootView.findViewById(R.id.btn_login);
        mUserName = (EditText) rootView.findViewById(R.id.login_username_textfield);
        mPassword = (EditText) rootView.findViewById(R.id.login_password_textfield);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameStr = mUserName.getText().toString();
                String passwordStr = mPassword.getText().toString();
                Log.v("Login Credentials", userNameStr + " " + passwordStr);
                Login login = new Login();
                login.execute();
            }
        });
        return rootView;
    }

    private class Login extends AsyncTask<Void, Void, String> {
        private final String LOG_TAG = Login.class.getSimpleName();
        private final String MAIL_HEADER = "mail";
        private final String PASSWORD_HEADER = "password";
        private final String LOGIN_SET = "login";

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection;
            BufferedReader reader = null;
            String encryptedToken;

            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http").authority("10.0.2.2").appendPath("quizak")
                        .appendPath("login.php");

                URL url = new URL(builder.toString());
                Log.v(LOG_TAG, url.toString());

                HttpPost httpPost = new HttpPost(url.toString());
                HttpClient client = new DefaultHttpClient();

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(
                        new BasicNameValuePair(MAIL_HEADER, mUserName.getText().toString()));
                nameValuePairs.add(
                        new BasicNameValuePair(PASSWORD_HEADER, mPassword.getText().toString()));
                nameValuePairs.add(
                        new BasicNameValuePair(LOGIN_SET,"1"));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(httpPost);

                Log.v(LOG_TAG, "answer sent..");

                //Receive into input stream
                InputStream inputStream = response.getEntity().getContent();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                encryptedToken = reader.readLine();

                Log.v(LOG_TAG, encryptedToken);



                return encryptedToken;

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
        protected void onPostExecute(String token) {
            if (token.contains("Invalid")) {
                Toast toast = Toast.makeText(
                        getActivity(), "Invalid mail/password", Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (token.contains("empty")){
                Toast toast = Toast.makeText(
                        getActivity(), "Login data cannot be empty!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                //User user = new User(token);
                //Log.v(LOG_TAG,user.getName());
                Intent loginToQuiz = new Intent(getActivity(), QuizActivity.class);
                loginToQuiz.putExtra(User.USER_TOKEN_KEY, token)
                        .putExtra(User.USER_MAIL_KEY, mUserName.getText().toString());
                startActivity(loginToQuiz);
            }
            super.onPostExecute(token);
        }
    }
}
