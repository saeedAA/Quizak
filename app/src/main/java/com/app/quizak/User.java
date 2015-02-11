package com.app.quizak;

/**
 * Created by Sa'eed Abdullah on 010, 10, 2, 2015.
 */
public class User {
    public static String USER_ID_KEY = "user_id";
    public static String USER_TOKEN_KEY = "user_token";
    public static String USER_MAIL_KEY = "user_mail";
    private int mId;
    private int mEnrolledCourseId;
    private String mName;
    private String mMail;
    private String mToken;

    public User(int id, String name, int enrolled, String mail){
        this.mId = id;
        this.mEnrolledCourseId = enrolled;
        this.mName = name;
        this.mMail = mail;
        this.mToken = id + "~" +  name + "~" + enrolled;
    }

    private String[] decryptToken(String token){
        String[] credentials = token.split("~",3);
        return credentials;
    }

    public User(String token){
        this.mToken = token;
        this.mId = Integer.parseInt(decryptToken(token)[0]);
        this.mEnrolledCourseId = Integer.parseInt(decryptToken(token)[2]);
        this.mName = decryptToken(token)[1];
    }

    public String getName(){
        return mName;
    }

    public String getEncryptedToken(){
        return mToken;
    }

    public void setMail(String mail){
        this.mMail = mail;
    }

    public int getId(){
        return mId;
    }
}
