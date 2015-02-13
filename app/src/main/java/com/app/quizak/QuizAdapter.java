package com.app.quizak;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sa'eed Abdullah on 011, 11, 2, 2015.
 */
public class QuizAdapter extends ArrayAdapter<Quiz> {
    final static String LOG_TAG = QuizAdapter.class.getSimpleName();
    public QuizAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    public QuizAdapter(Context context, int resource, List<Quiz> quizList){
        super(context, resource, quizList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_quiz, parent, false);
        }

        Quiz quiz = getItem(position);
        ViewHolder viewHolder = new ViewHolder(view);

        String courseName = quiz.getCourseName();
        viewHolder.courseNameView.setText(courseName);

        String questionCountStr = Integer.toString(quiz.getQuestionCount());
        viewHolder.questionCountView.setText(questionCountStr + " Questions");

        if(quiz.isTaken()){
            viewHolder.isTakenLayout.setVisibility(view.VISIBLE);
            viewHolder.isTakenView.setText("Taken");
            Log.v(LOG_TAG,Integer.toString(quiz.getDegree()));
            viewHolder.degreeView.setText(
                    Integer.toString(quiz.getDegree()) + "/" +
                            Integer.toString(quiz.getQuestionCount()));
        }

        return view;
    }

    private static class ViewHolder{

        public final TextView courseNameView;
        public final TextView questionCountView;
        public final TextView isTakenView;
        public final TextView degreeView;
        public final LinearLayout isTakenLayout;

        public ViewHolder(View view){
            courseNameView = (TextView) view.findViewById(R.id.list_item_course_name_view);
            questionCountView = (TextView) view.findViewById(R.id.list_item_question_count_view);
            isTakenView = (TextView) view.findViewById(R.id.list_item_isTaken_view);
            degreeView = (TextView) view.findViewById(R.id.list_item_degree_view);
            isTakenLayout = (LinearLayout) view.findViewById(R.id.list_item_isTaken_layout);
        }

    }
}
