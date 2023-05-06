package view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.application.devicesecuritysolution.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import algorithm.DES;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsername, txtPassword;
    private CheckBox CbxRemember;
    private Button btnLogin, btnShowHidePw;
    private DES des;

    private final static String TAG = "LoginActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        CbxRemember = findViewById(R.id.cbxRemember);
        btnShowHidePw = findViewById(R.id.btnShowHidePw);
        btnLogin = findViewById(R.id.btnLogin);

        des = new DES();

        setActionButton();
    }

    private void setActionButton() {
        //Nhap vao pw
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pw = txtPassword.getText().toString();
                if(pw.length() > 0)
                    EnableShowHideButton();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                EnableShowHideButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pw = txtPassword.getText().toString();
                if(pw.length() > 0)
                    EnableShowHideButton();
                else
                    DisableShowHideButton();
            }
        });

        //Click an/hien pw
        btnShowHidePw.setOnClickListener(new View.OnClickListener() {
            boolean ShowPw = false;
            @Override
            public void onClick(View view) {
                if(ShowPw) {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Drawable iconHide = getResources().getDrawable(R.drawable.show, getTheme());
                    btnShowHidePw.setBackground(iconHide);
                    ShowPw = false;
                }
                else {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Drawable iconHide = getResources().getDrawable(R.drawable.hide, getTheme());
                    btnShowHidePw.setBackground(iconHide);
                    ShowPw = true;
                }
            }
        });

        //Click Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickLoginButton();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        boolean remember = sharedPreferences.getBoolean("remember_me", false);
        CbxRemember.setChecked(remember);

        if(remember) {
            String username_pref = sharedPreferences.getString("username", null);
            String password_pref = sharedPreferences.getString("password", null);

            txtUsername.setText(username_pref);
            txtPassword.setText(password_pref);
        }

        //Click Remember me
        CbxRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("remember_me", isChecked);
                editor.apply();
                CbxRemember.setChecked(isChecked);
            }
        });

    }

    private void ClickLoginButton() {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        //Kiem tra tinh hop le cua thong tin dang nhap
        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean isChecked = sharedPreferences.getBoolean("remember_me", false);
            if(isChecked) {
                editor.putString("username", txtUsername.getText().toString());
                editor.putString("password", txtPassword.getText().toString());
            }
            editor.apply();

            checkAccount(username, password);
        }

        else Toast.makeText(this, "Please fill all the information.", Toast.LENGTH_SHORT).show();
    }

    private void checkAccount(String username, String password) {
        boolean networkConnected = isNetworkConnected();
        if(networkConnected){
            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", des.Encrypt(password))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if(!task.getResult().isEmpty()) {
                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.apply();

                                        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(homeIntent);
                                        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                    Toast.makeText(LoginActivity.this, "Can not login, incorrect Username or Password", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(LoginActivity.this, "Can not get information from server", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
            Toast.makeText(LoginActivity.this, "No internet access", Toast.LENGTH_SHORT).show();

    }

    private void EnableShowHideButton() {
        btnShowHidePw.setEnabled(true);
        btnShowHidePw.setVisibility(View.VISIBLE);
    }

    private void DisableShowHideButton() {
        btnShowHidePw.setEnabled(false);
        btnShowHidePw.setVisibility(View.INVISIBLE);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetwork() != null; // return true =(connected),false=(not connected)
    }
}