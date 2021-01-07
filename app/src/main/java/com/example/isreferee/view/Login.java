package com.example.isreferee.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.isreferee.R;
import com.example.isreferee.viewmodel.ViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText email,password;
    Button loginBtn,gotoRegister;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ViewModel loginViewModel ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        gotoRegister = findViewById(R.id.gotoRegister);

        loginViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        loginViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser ) {
                if(firebaseUser !=null){
                    DocumentReference df = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("TAG","onSuccess" + documentSnapshot.getData());
                            if( documentSnapshot.getString("isAdmin") != null){
                                // admin
                                email.setText(documentSnapshot.getString("UserEmail"));
                                password.setText(documentSnapshot.getString("PhoneNumber"));
                                startActivity( new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                            else if( documentSnapshot.getString("isUser") != null){
                                //user is not admin
                                email.setText(documentSnapshot.getString("UserEmail"));
                                password.setText(documentSnapshot.getString("PhoneNumber"));
                                startActivity( new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }
                    });
                }
            }
        } );

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(email);
                checkField(password);

                if (valid){
                    loginViewModel.Login(email, password);
                }
//                if (valid){
//                    fAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                        @Override
//                        public void onSuccess(AuthResult authResult) {
//                            //activity admin w user
//                            checkUserAccessLevel(authResult.getUser().getUid());
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getApplicationContext(), "Failed to LOGIN.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
            }
        });

    }



    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        DocumentReference df = loginViewModel.CheckUser();
//        if(df != null){
//            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    if( documentSnapshot.getString("isAdmin") != null){
//                        //user is an admin
//                        startActivity( new Intent(getApplicationContext(), MainActivity.class));
//                        finish();
//                    }
//                    else if( documentSnapshot.getString("isUser") != null){
//                        //user not an admin
//                        startActivity( new Intent(getApplicationContext(), MainActivity.class));
//                        finish();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    FirebaseAuth.getInstance().signOut();
//                    startActivity( new Intent(getApplicationContext(),Login.class));
//                    finish();
//                }
//            });
//        }
//
//
//    }
}