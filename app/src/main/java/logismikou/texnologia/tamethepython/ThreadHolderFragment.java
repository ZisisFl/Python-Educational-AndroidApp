package logismikou.texnologia.tamethepython;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    DatabaseReference thread, comment_ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_thread_holder, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

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

        open_thread();

        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_comment();
            }
        });


        return v;
    }

    public void open_thread(){
        Bundle arguments = getArguments();
        String thread_reference = arguments.getString("1234");
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

    public void post_comment(){
        Bundle arguments = getArguments();
        String thread_reference = arguments.getString("1234");
        comment_ref = FirebaseDatabase.getInstance().getReference().child("Community")
                .child(thread_reference).child("Comments");

        String comment = comment_text.getText().toString();
        String poster = firebaseAuth.getCurrentUser().getDisplayName();

        comment_ref.child(poster).setValue(comment);
        comment_text.setText("");

        create_message(comment, poster);
    }

    public void create_message(String comment, String poster){
        TextView show_message = new TextView(getContext());
        show_message.setTextSize(16);
        //show_message.setPadding(30,0,30,0);
        show_message.setText(poster+": " + comment);
        show_message.setTextColor(Color.BLACK);
        linear_layout_comments.addView(show_message);
    }
}
