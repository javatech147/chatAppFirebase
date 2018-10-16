package com.waytojava.ichatapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.waytojava.ichatapp.R;
import com.waytojava.ichatapp.utils.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvNotAMember;
    private EditText etEmail;
    private EditText etPassword;
    private CardView btnLogin;
    private Context context;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getId();
        context = this;
        mAuth = FirebaseAuth.getInstance();
    }

    private void getId() {
        tvNotAMember = findViewById(R.id.tv_not_member_signup);
        tvNotAMember.setText(Html.fromHtml(getString(R.string.txt_not_a_member_text)));
        tvNotAMember.setOnClickListener(this);

        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_email_password);
        btnLogin = findViewById(R.id.cv_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_not_member_signup:
                startActivity(new Intent(this, SignupActivity.class));
                finish();
                break;


            case R.id.cv_login:
                Utils.hideKeyboard(this);
                if (validateInputFields()) {
                    proceedLogin();
                }
                break;
        }
    }

    private void proceedLogin() {
        Utils.showProgressDialog(this);
        Task<AuthResult> task = mAuth.signInWithEmailAndPassword(etEmail.getText().toString().trim()
                , etPassword.getText().toString().trim());
        task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Utils.dismissProgressDialog();
                if (task.isSuccessful()) {
                    Utils.toast(context, getString(R.string.txt_success));
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                } else {
                    Utils.toast(context, getString(R.string.txt_invalid_credentials));
                }
            }
        });
    }

    private boolean validateInputFields() {
        if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            Utils.toast(this, getString(R.string.txt_pleas_enter_email));
            return false;
        } else if (!Utils.isValidEmail(etEmail.getText().toString().trim())) {
            Utils.toast(this, getString(R.string.txt_not_a_valid_email));
            return false;
        } else if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            Utils.toast(this, getString(R.string.txt_please_enter_password));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.super.onBackPressed();
                    }
                }).setNegativeButton("No", null)
                .show();
    }
}
