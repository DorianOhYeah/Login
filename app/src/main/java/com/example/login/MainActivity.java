package com.example.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private RadioGroup rg_login;
    private RadioButton rb_password;
    private RadioButton rb_verifycode;
    private EditText et_phone;
    private TextView tv_password;
    private EditText et_password;
    private Button btn_forget; //
    private CheckBox ck_remember;

    private int mRequestCode = 0; // the request code for transferring the pages
    private int mType = 0; // user style
    private boolean bRemember = false; // if remember the password
    private String mPassword = "111111"; // default password
    private String mVerifyCode; // verifycode
    private UserDBHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_login = findViewById(R.id.rg_login);
        rb_password = findViewById(R.id.rb_password);
        rb_verifycode = findViewById(R.id.rb_verifycode);
        et_phone = findViewById(R.id.et_phone);
        tv_password = findViewById(R.id.tv_password);
        et_password = findViewById(R.id.et_password);
        btn_forget = findViewById(R.id.btn_forget);
        ck_remember = findViewById(R.id.ck_remember);
        // set the listener for login radio group
        rg_login.setOnCheckedChangeListener(new RadioListener());
        // set the listener for the remember checkbox
        ck_remember.setOnCheckedChangeListener(new CheckListener());
        // add text changeed listener for et_phone
        et_phone.addTextChangedListener(new HideTextWatcher(et_phone));
        // add et_password for TextChanged listener
        et_password.addTextChangedListener(new HideTextWatcher(et_password));
        btn_forget.setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        initTypeSpinner();
        // set a focus change listener for the edit password box
        et_password.setOnFocusChangeListener(this);
    }

    // inital the spinner for the users
    private void initTypeSpinner() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, typeArray);
        // set the view for the adapter
        typeAdapter.setDropDownViewResource(R.layout.item_dropdown);
        // get the spinner by id
        Spinner sp_type = findViewById(R.id.sp_type);
        // set the prompt of the spinner
        sp_type.setPrompt("Please choose the user type");
        // set the adapter in the spinner
        sp_type.setAdapter(typeAdapter);
        // set the default items to show
        sp_type.setSelection(mType);
        sp_type.setOnItemSelectedListener(new TypeSelectedListener());
    }

    private String[] typeArray = {"personal user", "business user"};
    // define the item selected listener for choosing the user type
    class TypeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mType = arg2;
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // radio listener for single selection
    private class RadioListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_password) { // 选择了密码登录
                tv_password.setText("Password：");
                et_password.setHint("Please enter the password");
                btn_forget.setText("forget the password");
                ck_remember.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_verifycode) { // 选择了验证码登录
                tv_password.setText("　Verifycode：");
                et_password.setHint("Please enter the verifycode");
                btn_forget.setText("Get verifycode");
                ck_remember.setVisibility(View.INVISIBLE);
            }
        }
    }
    // define if remembering the password
    private class CheckListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.ck_remember) {
                bRemember = isChecked;
            }
        }
    }

    // define the text listener
    private class HideTextWatcher implements TextWatcher {
        private EditText mView;
        private int mMaxLength;
        private CharSequence mStr;

        HideTextWatcher(EditText v) {
            super();
            mView = v;
            mMaxLength = ViewUtil.getMaxLength(v);
        }


        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mStr = s;
        }

        public void afterTextChanged(Editable s) {
            if (mStr == null || mStr.length() == 0)
                return;

            if ((mStr.length() == 11 && mMaxLength == 11) ||
                    (mStr.length() == 6 && mMaxLength == 6)) {
                ViewUtil.hideOneInputMethod(MainActivity.this, mView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        String phone = et_phone.getText().toString();
        if (v.getId() == R.id.btn_forget) { // press the forget button
            if (phone.length() < 11) { // length not enough
                Toast.makeText(this, "Please enter the correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rb_password.isChecked()) { // choose the check method and turn to password page选择了密码方式校验，此时要跳到找回密码页面
                Intent intent = new Intent(this, LoginForgetActivity.class);
                // take the password to the main page
                intent.putExtra("phone", phone);
                startActivityForResult(intent, mRequestCode);
            } else if (rb_verifycode.isChecked()) { // random verify code
                // make the verify code
                mVerifyCode = String.format("%06d", (int) (Math.random() * 1000000 % 1000000));
                // pop up the alertdialog inorder to remind the verify code
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Remember code");
                builder.setMessage("Phone number" + phone + "，Verifycode" + mVerifyCode + "，please enter the verify code");
                builder.setPositiveButton("OK", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        } else if (v.getId() == R.id.btn_login) { // click the login button
            if (phone.length() < 11) { // the length of phone number is less than 11
                Toast.makeText(this, "Please enter the correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rb_password.isChecked()) { // check the password
                if (!et_password.getText().toString().equals(mPassword)) {
                    Toast.makeText(this, "Please enter the correct password", Toast.LENGTH_SHORT).show();
                } else { // verify the password
                    loginSuccess(); // login successfully
                }
            } else if (rb_verifycode.isChecked()) { // verifycode
                if (!et_password.getText().toString().equals(mVerifyCode)) {
                    Toast.makeText(this, "Please enter the correct verifycode", Toast.LENGTH_SHORT).show();
                } else { // verifycode passed
                    loginSuccess();
                }
            }
        }
    }

        // take the parameters and go back from the last page
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == mRequestCode && data != null) {
                // user password changes to new password
                mPassword = data.getStringExtra("new_password");
            }
        }

        // trun to login page and clean the password
        @Override
        protected void onRestart() {
            et_password.setText("");
            super.onRestart();
        }

    @Override
    protected void onResume() {
        super.onResume();
        // 获得用户数据库帮助器的一个实例
        mHelper = UserDBHelper.getInstance(this, 2);
        // 恢复页面，则打开数据库连接
        mHelper.openWriteLink();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停页面，则关闭数据库连接
        mHelper.closeLink();
    }

        // pass and login successfully
        private void loginSuccess() {
            String desc = String.format("您的手机号码是%s，类型是%s。恭喜你通过登录验证，点击“确定”按钮返回上个页面",
                    et_phone.getText().toString(), typeArray[mType]);
            // 弹出提醒对话框，提示用户登录成功
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("登录成功");
            builder.setMessage(desc);
            builder.setPositiveButton("确定返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("wait a second", null);
            AlertDialog alert = builder.create();
            alert.show();
            // 如果勾选了“记住密码”，则把手机号码和密码保存为数据库的用户表记录
            if (bRemember) {
                // 创建一个用户信息实体类
                UserInfo info = new UserInfo();
                info.phone = et_phone.getText().toString();
                info.password = et_password.getText().toString();
                info.update_time = DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss");
                // 往用户数据库添加登录成功的用户信息（包含手机号码、密码、登录时间）
                mHelper.insert(info);
            }
        }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String phone = et_phone.getText().toString();
        // 判断是否是密码编辑框发生焦点变化
        if (v.getId() == R.id.et_password) {
            // 用户已输入手机号码，且密码框获得焦点
            if (phone.length() > 0 && hasFocus) {
                // 根据手机号码到数据库中查询用户记录
                UserInfo info = mHelper.queryByPhone(phone);
                if (info != null) {
                    // 找到用户记录，则自动在密码框中填写该用户的密码
                    et_password.setText(info.password);
                }
            }
        }
    }

}
