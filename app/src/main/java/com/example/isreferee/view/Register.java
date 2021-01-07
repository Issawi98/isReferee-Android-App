package com.example.isreferee.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.isreferee.R;
import com.example.isreferee.viewmodel.ViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText fullName,email,password,phone;
    Button registerBtn,goToLogin;
    RadioButton isReferee, isManager;
    boolean valid = true;
    boolean isAdmin = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ViewModel RViewModel ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);

        isReferee = findViewById(R.id.isReferee);
        isManager = findViewById(R.id.isManager);

        RViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        RViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser ) {
                if(firebaseUser !=null){
                    startActivity( new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        } );


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(fullName);
                checkField(email);
                checkField(password);
                checkField(phone);

                if(!(isReferee.isChecked() || isManager.isChecked())){
                    Toast.makeText(Register.this, "Select Type", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(valid){
                    if(isReferee.isChecked()){
                        isAdmin = false;
                    }
                    if(isManager.isChecked()){
                        isAdmin = true;
                    }
                    Log.d("TAG", "TEST1 " + email.getText().toString() + " " + password.getText().toString());
                    RViewModel.Register(email, password, phone, fullName,isAdmin);
                }
//
//                if(valid){
//                    fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
//                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                                @Override
//                                public void onSuccess(AuthResult authResult) {
//                                    FirebaseUser user = fAuth.getCurrentUser();
//                                    Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
//                                    DocumentReference df = fStore.collection("Users").document(user.getUid());
//                                    Map<String, Object> userInfo = new HashMap<>();
//                                    userInfo.put("FullName",fullName.getText().toString());
//                                    userInfo.put("UserEmail",email.getText().toString());
//                                    userInfo.put("PhoneNumber",phone.getText().toString());
//
//                                    //Access Level
//                                    if(isReferee.isChecked()){
//                                        userInfo.put("isUser", "1");
//                                    }
//                                    if(isManager.isChecked()){
//                                        userInfo.put("isAdmin", "1");
//                                    }
//
//                                    df.set(userInfo);
//
//                                    // check before sending
//                                    startActivity( new Intent(getApplicationContext(), MainActivity.class));
//                                    finish();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(Register.this, "Failed to Create Account.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(getApplicationContext(),Login.class));
                finish();
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
}