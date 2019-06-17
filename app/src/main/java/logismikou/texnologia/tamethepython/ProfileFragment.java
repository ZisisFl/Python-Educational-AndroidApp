package logismikou.texnologia.tamethepython;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

    Button goto_edit_acc;
    TextView display_email, display_name;

    FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        goto_edit_acc = v.findViewById(R.id.goto_edit_acc);

        display_email = v.findViewById(R.id.display_email);
        display_name = v.findViewById(R.id.display_name);


        goto_edit_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new EditProfileFragment()).commit();
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

            display_email.setText(email);
            display_name.setText(name);
        }
    }
}
