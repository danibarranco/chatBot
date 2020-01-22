package com.example.chatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private Button btLogin;
    private EditText etEmail,etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        btLogin=findViewById(R.id.btLogin);
        etEmail=findViewById(R.id.etEmail);
        etPass=findViewById(R.id.etPass);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(etEmail.getText().toString().equalsIgnoreCase("")&&etEmail.getText().toString().equalsIgnoreCase(""))){
                    initRegister(etEmail.getText().toString(),etPass.getText().toString());
                }else {
                    Snackbar.make(view,"Debe rellenar los campos con datos validos",Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void initRegister(final String email, final String pass) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        go();
                    }else {
                        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    System.out.println(firebaseAuth.getCurrentUser().getEmail());
                                    go();
                                }else{

                                }
                            }
                        });
                    }
                }

                private void sayYes() {
                    System.out.println("SI");

                }
            });

    }

    private void go() {
        startActivity(new Intent(this,ChatWithBot.class));
    }


}
