package logismikou.texnologia.tamethepython;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpFragment extends Fragment {

    Button verify_sign_up;
    EditText email_sign_up, password_sign_up, repassword_sign_up,
            firstname_sign_up, surname_sign_up;
    TextView goto_sign_in;


    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());

        verify_sign_up = v.findViewById(R.id.verify_sign_up);

        email_sign_up = v.findViewById(R.id.email_sign_up);
        password_sign_up = v.findViewById(R.id.password_sign_up);
        repassword_sign_up = v.findViewById(R.id.repassword_sign_up);
        firstname_sign_up = v.findViewById(R.id.firstname_sign_up);
        surname_sign_up = v.findViewById(R.id.surname_sign_up);

        goto_sign_in = v.findViewById(R.id.goto_sign_in);

        verify_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register_user();
            }
        });

        goto_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new SignInFragment()).commit();
            }
        });

        return v;
    }

    private void register_user(){
        String email = email_sign_up.getText().toString();
        String password = password_sign_up.getText().toString();
        String password_ver = repassword_sign_up.getText().toString();
        String firstname = firstname_sign_up.getText().toString();
        String surname = surname_sign_up.getText().toString();

        if (email.equals(""))
            Toast.makeText(getActivity(),"Blank email field",Toast.LENGTH_SHORT).show();
        else if (password.equals(""))
            Toast.makeText(getActivity(),"Blank password field",Toast.LENGTH_SHORT).show();
        else if (password_ver.equals(""))
            Toast.makeText(getActivity(),"Blank retype password field",Toast.LENGTH_SHORT).show();
        else if (firstname.equals(""))
            Toast.makeText(getActivity(),"Blank firstname field",Toast.LENGTH_SHORT).show();
        else if (surname.equals(""))
            Toast.makeText(getActivity(),"Blank surname field",Toast.LENGTH_SHORT).show();
        else if (password.length() < 6)
            Toast.makeText(getActivity(),"Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
        else
        {
            String regx = "^[\\p{L} .'-]+$";
            // \\p{L} is a Unicode Character Property that matches any kind of letter from any language
            Pattern pattern = Pattern.compile(regx);

            CharSequence firstname_check = firstname_sign_up.getText().toString();
            Matcher matcher1 = pattern.matcher(firstname_check);

            CharSequence surname_check = surname_sign_up.getText().toString();
            Matcher matcher2 = pattern.matcher(surname_check);

            if(!matcher1.matches())
            {
                Toast.makeText(getActivity(),"Invalid first name format",Toast.LENGTH_SHORT).show();
            }
            else if(!matcher2.matches())
            {
                Toast.makeText(getActivity(),"Invalid surname format",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (password.equals(password_ver)){
                    progressDialog.setMessage("Signing up...");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        // user is successfully registered
                                        Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                                        // write users display name
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        String diplay_name = firstname_sign_up.getText().toString() +
                                                " " + surname_sign_up.getText().toString();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(diplay_name).build();

                                        user.updateProfile(profileUpdates);
                                        // intent to app's main body
                                        Intent intent = new Intent(getActivity(), MainApp.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        // check if there is already an account on this email
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(getActivity(), "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                            Toast.makeText(getActivity(), "Wrong email format.", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getActivity(),"Error: Could not register",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(getActivity(),"Password fields do not match",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
