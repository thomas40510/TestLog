package com.example.administrateur.testlog;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    private String nextScreen;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Bundle extras = getIntent().getExtras();
            nextScreen = extras.getString("nextScreen");
        } catch (Exception e){
            e.printStackTrace();
            nextScreen = "Main";
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        //del this line to remember user
        //auth.signOut();

        SharedPreferences preferences = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);
        String lastMail = preferences.getString("lastMail", null);

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainMenu.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        inputEmail.setText(lastMail);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        /*
        Signup option - to implement if needed

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
        */

        inputPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    btnLogin.callOnClick();
                    return true;
                }
                return false;
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Merci d'entrer une adresse mail", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Merci de rentrer le mot de passe", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    SharedPreferences.Editor editor  = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE).edit();
                                    editor.putString("lastMail", inputEmail.getText().toString());
                                    editor.commit();

                                    Intent intent;

                                    switch (nextScreen){
                                        case "cavaliers":
                                            intent = new Intent(LoginActivity.this, MainActivity.class);
                                            break;
                                        case "cavalerie":
                                            intent = new Intent(LoginActivity.this, cMainActivity.class);
                                            break;
                                        default:
                                            intent = new Intent(LoginActivity.this, MainMenu.class);
                                    }
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }


}
