package logismikou.texnologia.tamethepython;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class PracticeFragment extends Fragment {

    Button quiz1, quiz2, quiz3, quiz4, theory1, theory2, theory3, theory4;
    ImageView info1;

    DatabaseReference user_data;
    FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_practice, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user_data = FirebaseDatabase.getInstance().getReference();

        quiz1 = v.findViewById(R.id.quiz1);
        quiz2 = v.findViewById(R.id.quiz2);
        quiz3 = v.findViewById(R.id.quiz3);
        quiz4 = v.findViewById(R.id.quiz4);

        theory1 = v.findViewById(R.id.theory1);
        theory2 = v.findViewById(R.id.theory2);
        theory3 = v.findViewById(R.id.theory3);
        theory4 = v.findViewById(R.id.theory4);

        info1 = v.findViewById(R.id.info1);

        quiz1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reference = "1 Python Introduction";
                open_quiz(reference);
            }
        });

        quiz2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reference = "2 Python Decision Making and Loop";
                open_quiz(reference);
            }
        });

        quiz3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reference = "3 Python Functions";
                open_quiz(reference);
            }
        });

        quiz4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reference = "4 Python Data types";
                open_quiz(reference);
            }
        });

        theory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reference = "1 Python Introduction";
                open_theory(reference);
            }
        });

        theory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reference = "2 Python Decision Making and Loop";
                open_theory(reference);
            }
        });

        theory3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reference = "3 Python Functions";
                open_theory(reference);
            }
        });

        theory4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reference = "4 Python Data types";
                open_theory(reference);
            }
        });

        info1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Practice");
                alertDialog.setMessage("We have split python theory in 4 main different chapters " +
                        "which contain some sub chapters in order to make studying easier for you." +
                        "\n\nBecause this app is intended for people with different programming " +
                        "backgrounds you can choose any chapter you like to read or quiz to exercise " +
                        "without limitations. \n\nBased on your answers in our quizzes we will provide" +
                        "information to you in order to overcome difficulties you may face.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        return v;
    }

    private void open_quiz(String reference){
        QuizHolderFragment fragment = new QuizHolderFragment();
        //create an bundle to send reference string of the specific product
        Bundle arguments = new Bundle();
        arguments.putString("123", reference);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void open_theory(String reference){
        TheoryHolderFragment fragment = new TheoryHolderFragment();
        //create an bundle to send reference string of the specific product
        Bundle arguments = new Bundle();
        arguments.putString("12", reference);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }
}
