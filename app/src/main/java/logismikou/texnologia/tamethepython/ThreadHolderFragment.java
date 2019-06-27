package logismikou.texnologia.tamethepython;


import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.constraint.Constraints.TAG;


public class ThreadHolderFragment extends Fragment {

    public ThreadHolderFragment() {
        // Required empty public constructor
    }

    ImageView close9;
    TextView thread_title, author_name, keywords, description, date;
    EditText comment_text;
    Button comment_btn;

    LinearLayout linear_layout_comments;

    FirebaseAuth firebaseAuth;
    DatabaseReference thread, comment_ref, user_data;

    boolean comments_loaded = false;

    long community_score;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_thread_holder, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user_data = FirebaseDatabase.getInstance().getReference();

        close9 = v.findViewById(R.id.close9);

        thread_title = v.findViewById(R.id.thread_title);
        author_name = v.findViewById(R.id.author_name);
        keywords = v.findViewById(R.id.keywords);
        description = v.findViewById(R.id.description);
        date = v.findViewById(R.id.date);

        linear_layout_comments = v.findViewById(R.id.linear_layout_comments);

        comment_btn = v.findViewById(R.id.comment_btn);
        comment_text = v.findViewById(R.id.comment_text);

        close9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new CommunityFragment()).commit();
            }
        });

        Bundle arguments = getArguments();
        final String thread_reference = arguments.getString("1234");

        open_thread(thread_reference);
        load_messages(thread_reference);

        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_comment(thread_reference);
            }
        });

        load_points();

        return v;
    }

    private void open_thread(String thread_reference){

        thread = FirebaseDatabase.getInstance().getReference().child("Community").child(thread_reference);

        ValueEventListener get_itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get PopularProducts object and use the values to update the UI
                Post_thread postThread = dataSnapshot.getValue(Post_thread.class);

                // load thread information
                thread_title.setText(postThread.thread_title);
                author_name.setText("by " + postThread.user_id);
                keywords.setText("keywords: " + postThread.thread_keywords);
                description.setText(postThread.thread_description);
                date.setText(postThread.date);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        thread.addValueEventListener(get_itemListener);
    }

    private void load_messages(String thread_reference){
        comment_ref = FirebaseDatabase.getInstance().getReference().child("Community")
                .child(thread_reference).child("Comments");

        comment_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get old messages under branch messages if isn't empty
                if(dataSnapshot.getChildrenCount() > 0 && !comments_loaded){
                    comments_loaded = true;  // change value to not happen again
                    //loop through messages in database and create textviews with create_message()
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        String comment = dataSnapshot1.getValue().toString();
                        create_message(comment);
                    }
                }
            // get every new message, loop through all messages and get the last one
                else if(dataSnapshot.getChildrenCount() > 0 && comments_loaded){
                String comment = null;
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    comment = dataSnapshot1.getValue().toString();
                }
                create_message(comment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void post_comment(String thread_reference){
        comment_ref = FirebaseDatabase.getInstance().getReference().child("Community")
                .child(thread_reference).child("Comments");

        String comment = comment_text.getText().toString();
        String poster = firebaseAuth.getCurrentUser().getDisplayName();

        String id = comment_ref.push().getKey();

        if (!comment.equals("")){
            comment_ref.child(id).setValue(poster+": "+comment);
            comment_text.setText("");

            long extra_points = 5;
            long new_practice_score = community_score + extra_points;
            user_data.child("Community score").setValue(new_practice_score);
        }
        else
            Toast.makeText(getActivity(),"Empty comment field",Toast.LENGTH_SHORT).show();

        //create_message(comment, poster);
    }

    private void create_message(String comment){
        TextView show_message = new TextView(getContext());
        show_message.setTextSize(16);
        //show_message.setPadding(30,0,30,0);
        show_message.setText(comment);
        show_message.setTextColor(Color.BLACK);
        linear_layout_comments.addView(show_message);
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