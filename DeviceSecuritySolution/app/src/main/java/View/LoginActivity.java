package View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.application.devicesecurity.R;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsername, txtPassword;
    private CheckBox CbxRemember;
    private Button btnLogin, btnCancel;
    private Button btnShowHidePw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        CbxRemember = findViewById(R.id.cbxRemember);
        btnShowHidePw = findViewById(R.id.btnShowHidePw);

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
            @Override
            public void onClick(View view) {
                ClickShowHidePw();
            }
        });

        //Click Login
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Intent in = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(in);
            }
        });

        //Click Cancel
        findViewById(R.id.btnCancelLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ClickShowHidePw() {
        boolean ShowPw = false;
        if(ShowPw) {
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            Drawable iconHide = getResources().getDrawable(R.drawable.show, getTheme());
            btnShowHidePw.setBackground(iconHide);
            ShowPw = false;
        }
        else {
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            Drawable iconHide = getResources().getDrawable(R.drawable.hide
                    , getTheme());
            btnShowHidePw.setBackground(iconHide);
            ShowPw = true;
        }
    }

    private void EnableShowHideButton() {
        btnShowHidePw.setEnabled(true);
        btnShowHidePw.setVisibility(View.VISIBLE);
    }

    private void DisableShowHideButton() {
        btnShowHidePw.setEnabled(false);
        btnShowHidePw.setVisibility(View.INVISIBLE);
    }

}