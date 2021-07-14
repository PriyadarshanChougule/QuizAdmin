package com.example.quizadmin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText email,pass;
    private Button login;
    private FirebaseAuth firebaseAuth;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.loginB);

        loadingDialog = new Dialog(MainActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                //email.toString();
                String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(email.getText().toString());


                if(email.getText().toString().isEmpty()){
                    email.setError("Enter Email ID");
                    return;
                }

                 else if (!matcher.matches()) {
                        Toast.makeText(MainActivity.this,"Invalid email",Toast.LENGTH_SHORT).show();
                        return;
                    }

                else {
                    email.setError(null);
                }




                if(pass.getText().toString().isEmpty()){
                    pass.setError("Enter password");
                    return;
                }
                else {
                    pass.setError(null);
                }





                if(email.getText().toString().equalsIgnoreCase("priyadarshanchougule@gmail.com") && pass.getText().toString().equals("Darshan@123"))
                {
                    firebaseLogin();

                }else {
                    Toast.makeText(MainActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void firebaseLogin(){

        loadingDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });

    }
}