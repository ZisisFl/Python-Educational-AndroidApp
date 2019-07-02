package logismikou.texnologia.tamethepython;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class QuizHolderFragment extends Fragment {


    public QuizHolderFragment() {
        // Required empty public constructor
    }

    TextView question_text, answer1, answer2, answer3, answer4, question_index, code_text,
            score_text, finished_msg, review_msg;
    Button next_btn, goback_btn;
    ConstraintLayout finished_layout, test_layout;

    Question list_of_questions[] = new Question[10];
    String answers[] = new String[10];
    List<String> wrong_answers_cat = new ArrayList<String>();
    int question_number = 0;
    String answer_picked;

    long practice_score;


    DatabaseReference databaseReference, question_ref, user_data;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quiz_holder, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Community");
        user_data = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        finished_layout = v.findViewById(R.id.finished_layout);
        test_layout = v.findViewById(R.id.test_layout);

        question_text = v.findViewById(R.id.question_text);
        answer1 = v.findViewById(R.id.answer1);
        answer2 = v.findViewById(R.id.answer2);
        answer3 = v.findViewById(R.id.answer3);
        answer4 = v.findViewById(R.id.answer4);
        question_index = v.findViewById(R.id.question_index);
        code_text = v.findViewById(R.id.code_text);

        score_text = v.findViewById(R.id.score_text);
        finished_msg = v.findViewById(R.id.finished_msg);
        review_msg = v.findViewById(R.id.review_msg);

        next_btn = v.findViewById(R.id.next_btn);
        goback_btn = v.findViewById(R.id.goback_btn);

        // hide finished layout
        finished_layout.setVisibility(View.GONE);

        // load current points
        load_points();

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (question_number<10) {

                    String ra = list_of_questions[question_number].ra;
                    String category = list_of_questions[question_number].category;

                    if (!answer_picked.equals("")){
                        if (answer_picked.equals(ra)){
                            answers[question_number] = "right";
                        }
                        else{
                            answers[question_number] = "wrong";
                            if (!wrong_answers_cat.contains(category)){
                                wrong_answers_cat.add(category);
                            }
                        }

                        // reset choices for next question
                        answer_picked = "";
                        answer1.setBackgroundColor(Color.WHITE);
                        answer2.setBackgroundColor(Color.WHITE);
                        answer3.setBackgroundColor(Color.WHITE);
                        answer4.setBackgroundColor(Color.WHITE);

                        // load new question
                        question_number ++;
                        if (question_number<10)
                            open_question(question_number);
                        else{
                            int count = 0;
                            for (int i=0; i<10; i++){
                                if (answers[i].equals("right"))
                                    count ++;
                            }
                            score_text.setText("You got "+count+"/10");

                            //0-10 - 4/10
                            if (count < 5){
                                finished_msg.setText("You didn't pass the quiz. " +
                                        "Do not give up you were almost there keep " +
                                        "studying and come back to retry");
                                String joined = TextUtils.join(", ", wrong_answers_cat);
                                review_msg.setText("Based on your wrong answers we " +
                                        "recommend that you should review subchapters: "+ joined);
                            }
                            //5/10 - 9/10
                            else if (count < 10){
                                finished_msg.setText("You did great passing the quiz. " +
                                    "Keep in mind to come anytime back to review this chapter " +
                                    "and retake the quiz.");
                                String joined = TextUtils.join(", ", wrong_answers_cat);
                                review_msg.setText("Based on your wrong answers we " +
                                        "recommend that you should review subchapters: "+ joined);
                            }
                            //10/10
                            else{
                                finished_msg.setText("Awesome you got everything right. Come back " +
                                        "anytime you feel you have forgotten something.");
                                review_msg.setText("");
                            }
                            // change layout
                            finished_layout.setVisibility(View.VISIBLE);
                            test_layout.setVisibility(View.GONE);

                            long quiz_score = count*5;
                            long new_practice_score = practice_score + quiz_score;
                            user_data.child("Practice score").setValue(new_practice_score);

                            // write quiz score
                            Bundle arguments = getArguments();
                            final String quiz_reference = arguments.getString("123");

                            String joined = TextUtils.join(", ", wrong_answers_cat);
                            if(quiz_reference.equals("1 Python Introduction")){
                                user_data.child("Quiz 1 score:").setValue(count+"/10");
                                user_data.child("Quiz 1 mistake categories").setValue(joined);
                            }
                            else if(quiz_reference.equals("2 Python Decision Making and Loop")){
                                user_data.child("Quiz 2 score:").setValue(count+"/10");
                                user_data.child("Quiz 2 mistake categories").setValue(joined);
                            }
                            else if(quiz_reference.equals("3 Python Functions")){
                                user_data.child("Quiz 3 score:").setValue(count+"/10");
                                user_data.child("Quiz 3 mistake categories").setValue(joined);
                            }
                            else{
                                user_data.child("Quiz 4 score:").setValue(count+"/10");
                                user_data.child("Quiz 4 mistake categories").setValue(joined);
                            }
                        }

                    }
                    else
                        Toast.makeText(getActivity(),"Please pick an answer",Toast.LENGTH_LONG).show();
                }
            }
        });

        goback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new PracticeFragment()).commit();
            }
        });

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer_picked = "a1";
                answer1.setBackgroundColor(Color.parseColor("#00574B"));
                answer2.setBackgroundColor(Color.WHITE);
                answer3.setBackgroundColor(Color.WHITE);
                answer4.setBackgroundColor(Color.WHITE);
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer_picked = "a2";
                answer1.setBackgroundColor(Color.WHITE);
                answer2.setBackgroundColor(Color.parseColor("#00574B"));
                answer3.setBackgroundColor(Color.WHITE);
                answer4.setBackgroundColor(Color.WHITE);
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer_picked = "a3";
                answer1.setBackgroundColor(Color.WHITE);
                answer2.setBackgroundColor(Color.WHITE);
                answer3.setBackgroundColor(Color.parseColor("#00574B"));
                answer4.setBackgroundColor(Color.WHITE);
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer_picked = "a4";
                answer1.setBackgroundColor(Color.WHITE);
                answer2.setBackgroundColor(Color.WHITE);
                answer3.setBackgroundColor(Color.WHITE);
                answer4.setBackgroundColor(Color.parseColor("#00574B"));
            }
        });


        load_questions();
        return v;
    }

    public void open_question(int question_number){
        String q = list_of_questions[question_number].q;
        String a1 = list_of_questions[question_number].a1;
        String a2 = list_of_questions[question_number].a2;
        String a3 = list_of_questions[question_number].a3;
        String a4 = list_of_questions[question_number].a4;
        String ra = list_of_questions[question_number].ra;
        String category = list_of_questions[question_number].category;
        String code = list_of_questions[question_number].code;

        question_text.setText(q);
        answer1.setText(a1);
        answer2.setText(a2);
        answer3.setText(a3);
        answer4.setText(a4);
        code_text.setText(code);

        question_index.setText(question_number+1+"");
    }

    public void load_questions(){
        progressDialog.setMessage("Loading Questions...");
        progressDialog.show();
        Bundle arguments = getArguments();
        final String quiz_reference = arguments.getString("123");
        question_ref = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(quiz_reference).child("Quiz");
        question_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Question question = dataSnapshot1.getValue(Question.class);
                    list_of_questions[i] = question;
                    i++;
                }
                open_question(0);
                //init pick
                answer_picked = "";
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void load_points() {
        if (firebaseAuth.getCurrentUser() != null) {
            String user_id = firebaseAuth.getUid();
            user_data = user_data.child("Users").child(user_id);

            ValueEventListener get_itemListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    practice_score = Integer.parseInt(dataSnapshot.child("Practice score").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            user_data.addValueEventListener(get_itemListener);
        }
    }
}
