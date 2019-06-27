package logismikou.texnologia.tamethepython;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class TheoryHolderFragment extends Fragment {


    public TheoryHolderFragment() {
        // Required empty public constructor
    }

    ListView list_of_subchapters;
    List<String> subchapter_name = new ArrayList<String>();
    List<String> list_of_pictures = new ArrayList<String>();

    ImageView image_theory;
    Button next_btn2;
    ConstraintLayout read_layout;
    int subchapter_image_number = 0;
    int subchapter_number;

    long startTime, difference;

    DatabaseReference theory, pictures, user_data;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_theory_holder, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user_data = FirebaseDatabase.getInstance().getReference();

        list_of_subchapters = v.findViewById(R.id.list_of_subchapters);
        image_theory = v.findViewById(R.id.image_theory);
        next_btn2 = v.findViewById(R.id.next_btn2);

        read_layout = v.findViewById(R.id.read_layout);

        // load list of subchapters
        Bundle arguments = getArguments();
        final String chapter_reference = arguments.getString("12");

        // load listview
        load_subchapters(chapter_reference);
        String user_id = firebaseAuth.getUid();
        user_data = user_data.child("Users").child(user_id);

        next_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move_next(chapter_reference);
            }
        });

        return v;
    }

    private void load_subchapters(final String chapter_reference){

        theory = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(chapter_reference).child("Courses");

        ValueEventListener get_itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    // get subchapters names in list
                    subchapter_name.add(dataSnapshot1.getKey());
                }
                //Toast.makeText(getActivity(),subchapter_name.toString(),Toast.LENGTH_SHORT).show();

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, subchapter_name);
                list_of_subchapters.setAdapter(adapter);

                list_of_subchapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String subchapter_reference = subchapter_name.get(position);
                        open_subchapter(chapter_reference, subchapter_reference);
                        subchapter_number = position;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        theory.addValueEventListener(get_itemListener);
    }

    private void open_subchapter(final String chapter_reference, String subchapter_reference){
        list_of_subchapters.setVisibility(View.GONE);
        read_layout.setVisibility(View.VISIBLE);


        pictures = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(chapter_reference).child("Courses").child(subchapter_reference);

        ValueEventListener get_itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    list_of_pictures.add(dataSnapshot1.getValue().toString());
                }
                move_next(chapter_reference);
                startTime = System.currentTimeMillis();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        pictures.addValueEventListener(get_itemListener);
    }

    private void move_next(final String chapter_reference){
        if (subchapter_image_number< list_of_pictures.size()) {
            Picasso.get().load(list_of_pictures.get(subchapter_image_number)).into(image_theory, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            subchapter_image_number++;
            //Toast.makeText(getActivity(),subchapter_image_number+"",Toast.LENGTH_SHORT).show();
        }
        else {
            //reset list of pictures and counter and save reading time
            list_of_pictures.clear();
            subchapter_image_number = 0;
            difference = System.currentTimeMillis() - startTime;
            user_data.child("Time spent reading "+subchapter_name.get(subchapter_number)).setValue(difference/1000+" sec");

            //go to next subchapter
            subchapter_number++;

            if (subchapter_number<subchapter_name.size()){
                open_subchapter(chapter_reference, subchapter_name.get(subchapter_number));
            }
            else
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new PracticeFragment()).commit();
        }
    }
}
