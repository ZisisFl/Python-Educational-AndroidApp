package logismikou.texnologia.tamethepython;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.support.constraint.Constraints.TAG;


public class ProfileFragment extends Fragment {

    Button goto_edit_acc;
    TextView display_email, display_name, user_since, practice_score_text, community_score_text,
            total_score_text;

    ImageView community_badge, practice_badge, info2;

    FirebaseAuth firebaseAuth;
    DatabaseReference user_data;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user_data = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        goto_edit_acc = v.findViewById(R.id.goto_edit_acc);

        display_email = v.findViewById(R.id.display_email);
        display_name = v.findViewById(R.id.display_name);
        user_since = v.findViewById(R.id.user_since);
        practice_score_text = v.findViewById(R.id.practice_score_text);
        community_score_text = v.findViewById(R.id.community_score_text);
        total_score_text = v.findViewById(R.id.total_score_text);

        community_badge = v.findViewById(R.id.community_badge);
        practice_badge = v.findViewById(R.id.practice_badge);
        info2 = v.findViewById(R.id.info2);

        progressDialog.setMessage("Loading Info...");
        progressDialog.show();


        goto_edit_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new EditProfileFragment()).commit();
            }
        });

        info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Profile");
                alertDialog.setMessage("Here you can check your personal information and edit them." +
                        "Clicking on the cogwheel image on the top of the screen\n\n" +
                        "We have created a rewarding system in order to keep track of your progress. " +
                        "The overall score is split in two areas. You earn points either from getting " +
                        "right answers in our quizzes or from you interaction with our community " +
                        " like posting thread asking question or commenting on others threads.\n\nEach" +
                        " right quiz answer gives you 5 points in order to unlock the 'Python Expert' badge" +
                        ", posting a thread gives you 10 points and making a comment gives you 5 points " +
                        "in order to unlock the 'Community Contributor' badge.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        load_user_data();

        return v;
    }

    public void load_user_data(){
        //check if user is logged in
        if(firebaseAuth.getCurrentUser() != null){
            String email = firebaseAuth.getCurrentUser().getEmail();
            String name = firebaseAuth.getCurrentUser().getDisplayName();
            String user_id = firebaseAuth.getUid();

            display_email.setText(email);
            display_name.setText(name);

            user_data = user_data.child("Users").child(user_id);

            ValueEventListener get_itemListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String user_since_date = dataSnapshot.child("User since").getValue().toString();
                    user_since.setText(user_since_date);

                    int community_score = Integer.parseInt(dataSnapshot.child("Community score").getValue().toString());
                    int practice_score = Integer.parseInt(dataSnapshot.child("Practice score").getValue().toString());

                    if (community_score<150){
                        community_score_text.setText(community_score+"/150 to Unlock");
                    }
                    else {
                        community_score_text.setText("Community Constributor");
                        Picasso.get().load(R.drawable.shield).into(community_badge, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    if (practice_score<150){
                        practice_score_text.setText(practice_score+"/150 to Unlock");
                    }
                    else {
                        practice_score_text.setText("Python Expert");
                        Picasso.get().load(R.drawable.medal).into(practice_badge, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    int total_score = community_score + practice_score;
                    total_score_text.setText(""+total_score);
                    progressDialog.dismiss();
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
