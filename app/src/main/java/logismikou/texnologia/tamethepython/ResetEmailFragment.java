package logismikou.texnologia.tamethepython;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class ResetEmailFragment extends Fragment {


    Button reset_email_btn;
    EditText new_email_text, current_email_text, current_pass_text;
    ImageView close4;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reset_email, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());

        reset_email_btn = v.findViewById(R.id.reset_email_btn);

        new_email_text = v.findViewById(R.id.new_email_text);
        current_email_text = v.findViewById(R.id.current_email_text);
        current_pass_text = v.findViewById(R.id.current_pass_text);

        close4 = v.findViewById(R.id.close4);

        reset_email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_email();
            }
        });

        close4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new EditProfileFragment()).commit();
            }
        });

        return v;
    }

    public void reset_email(){
        String email = current_email_text.getText().toString();
        String password = current_pass_text.getText().toString();

        if(email.equals("")){
            Toast.makeText(getActivity(), "Black current email field", Toast.LENGTH_SHORT).show();
        }
        else if(password.equals("")){
            Toast.makeText(getActivity(), "Black current password field", Toast.LENGTH_SHORT).show();
        }
        else{
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, password);

            firebaseAuth.getCurrentUser().reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //Toast.makeText(getActivity(),"User reauthenticated",Toast.LENGTH_SHORT).show();
                                progressDialog.setMessage("Changing address...");
                                progressDialog.show();

                                String new_email = new_email_text.getText().toString();
                                if(!new_email.equals(""))
                                {
                                    firebaseAuth.getCurrentUser().updateEmail(new_email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(getActivity(),"Email address successfully changed",Toast.LENGTH_SHORT).show();
                                                        // return to account fragment
                                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                                                new EditProfileFragment()).commit();
                                                    }
                                                    else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                        Toast.makeText(getActivity(), "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                        Toast.makeText(getActivity(),"Error: Could change email address",Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                                else{
                                    Toast.makeText(getActivity(),"Provide a new email address",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                                Toast.makeText(getActivity(),"Wrong email or password",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
