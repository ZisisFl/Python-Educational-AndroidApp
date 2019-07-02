package logismikou.texnologia.tamethepython;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CommunityFragment extends Fragment {


    public CommunityFragment() {
        // Required empty public constructor
    }


    Button create_thread_btn;
    ImageView info3;

    FirebaseAuth firebaseAuth;

    RecyclerView thread_list;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<Post_thread> options;
    FirebaseRecyclerAdapter<Post_thread, ThreadViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Community");

        create_thread_btn = v.findViewById(R.id.create_thread_btn);
        thread_list = v.findViewById(R.id.thread_list);

        info3 = v.findViewById(R.id.info3);


        create_thread_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new CreateThreadFragment()).commit();
            }
        });

        init_recycle_view();

        info3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Community");
                alertDialog.setMessage("We believe that achieving knowledge is easier and " +
                        "more fun when is shared with others. We created this online thread posting " +
                        "where you can discuss over python topics that concern you with people in the " +
                        "same situation as you. You can find answers you seek from others threads, " +
                        "you can post a comment to ask something or solve someones problem.\n\n" +
                        "Click CREATE A THREAD fragment fill the form with the required fields and post" +
                        " your questions.\n\n" +
                        "Click an item from the list to read someone's thread.");
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

    public void init_recycle_view(){
        options = new FirebaseRecyclerOptions.Builder<Post_thread>()
                .setQuery(databaseReference, Post_thread.class).build();

        adapter = new FirebaseRecyclerAdapter<Post_thread, ThreadViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ThreadViewHolder holder, int position, @NonNull Post_thread model) {

                holder.title_view.setText(model.getThread_title());
                holder.author_view.setText("by " + model.getUser_id());
                holder.keywords_view.setText(model.getThread_keywords());
                // add click listener for the items

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // open product with the specific reference
                        String thread_reference = getRef(position).getKey();

                        ThreadHolderFragment fragment = new ThreadHolderFragment();
                        //create an bundle to send reference string of the specific product
                        Bundle arguments = new Bundle();
                        arguments.putString("1234", String.valueOf(thread_reference));
                        fragment.setArguments(arguments);
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.commit();
                    }
                });
            }

            @NonNull
            @Override
            public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate
                        (R.layout.card_view, parent, false);

                return new ThreadViewHolder(view);
            }
        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        thread_list.setLayoutManager(mLayoutManager);
        adapter.startListening();
        thread_list.setAdapter(adapter);
    }

    public class ThreadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title_view, author_view, keywords_view;

        private ItemClickListener itemClickListener;

        public ThreadViewHolder(View itemView)
        {
            super(itemView);

            title_view = itemView.findViewById(R.id.title_view);
            author_view = itemView.findViewById(R.id.author_view);
            keywords_view = itemView.findViewById(R.id.keywords_view);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }
    }

}
