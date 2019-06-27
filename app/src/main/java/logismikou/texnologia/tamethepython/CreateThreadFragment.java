package logismikou.texnologia.tamethepython;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.constraint.Constraints.TAG;


public class CreateThreadFragment extends Fragment {


    public CreateThreadFragment() {
        // Required empty public constructor
    }

    ImageView close3;
    EditText title_text, description_text, keywords_text;
    Button post_btn;

    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase, user_data;

    long community_score;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_thread, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user_data = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        close3 = v.findViewById(R.id.close3);
        title_text = v.findViewById(R.id.title_text);
        description_text = v.findViewById(R.id.description_text);
        keywords_text = v.findViewById(R.id.keywords_text);
        post_btn = v.findViewById(R.id.post_btn);


        close3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new CommunityFragment()).commit();
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_thread();
            }
        });

        load_points();

        return v;
    }

    public void post_thread(){
        String thread_title = title_text.getText().toString();
        String thread_keywords = keywords_text.getText().toString();
        String thread_description = description_text.getText().toString();

        if (!thread_title.equals("") && !thread_keywords.equals("") && !thread_description.equals("")){
            //post question
            create_thread(thread_title, thread_keywords, thread_description);

            progressDialog.setMessage("Sending feedback...");
            progressDialog.show();
        }
        else{
            Toast.makeText(getActivity(),"Error: Blank field",Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void create_thread(String thread_title, String thread_keywords, String thread_description){
        // get current time
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String c_time = df.format(currentTime);
        //String c_time = currentTime.toString();
        // get key
        String id = mDatabase.push().getKey();

        if(firebaseAuth.getCurrentUser() != null){
            String userId = firebaseAuth.getCurrentUser().getDisplayName();

            Post_thread post_thread = new Post_thread(userId, thread_title, thread_keywords,
                    thread_description, c_time);

            mDatabase.child("Community").child(id).setValue(post_thread)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();

                            // return back
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    new CommunityFragment()).commit();

                            long extra_points = 10;
                            long new_practice_score = community_score + extra_points;
                            user_data.child("Community score").setValue(new_practice_score);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"An error occurred",Toast.LENGTH_SHORT).show();
                       }
                    });
        }
    }

    private void load_points() {
        if (firebaseAuth.getCurrentUser() != null) {
            String user_id = firebaseAuth.getUid();
            user_data = user_data.child("Users").child(user_id);

            ValueEventListener get_itemListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    community_score = Integer.parseInt(dataSnapshot.child("Community score").getValue().toString());
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
