package com.example.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by ouyangshen on 2017/9/24.
 */
@SuppressLint("DefaultLocale")
public class LoginForgetActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_password_first;
    private EditText et_password_second;
    private EditText et_verifycode;
    private String mVerifyCode;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_forget);
        et_password_first = findViewById(R.id.et_password_first);
        et_password_second = findViewById(R.id.et_password_second);
        et_verifycode = findViewById(R.id.et_verifycode);
        findViewById(R.id.btn_verifycode).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mPhone = getIntent().getStringExtra("phone");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_verifycode) {
            if (mPhone == null || mPhone.length() < 11) {
                Toast.makeText(this, "Please enter the correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            mVerifyCode = String.format("%06d", (int) (Math.random() * 1000000 % 1000000));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please remember the verify code");
            builder.setMessage("phone number" + mPhone + "，verifycode" + mVerifyCode + "，enter the verifycode");
            builder.setPositiveButton("OK", null);
            AlertDialog alert = builder.create();
            alert.show();
        } else if (v.getId() == R.id.btn_confirm) {
            String password_first = et_password_first.getText().toString();
            String password_second = et_password_second.getText().toString();
            if (password_first.length() < 6 || password_second.length() < 6) {
                Toast.makeText(this, "Please enter the correct new password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password_first.equals(password_second)) {
                Toast.makeText(this, "Two password are different", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!et_verifycode.getText().toString().equals(mVerifyCode)) {
                Toast.makeText(this, "Please enter the correct virifycode", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "modify the password successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("new_password", password_first);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }
}
