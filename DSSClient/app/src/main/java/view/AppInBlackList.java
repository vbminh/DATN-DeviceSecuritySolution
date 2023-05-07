package view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.application.dssclient.R;

import module.BlackListApp;

public class AppInBlackList extends AppCompatActivity {
    private TextView txtTitle, txtName, txtPname, txtCate, txtVersion, txtOs, txtOwner;
    private Button btnback;

    private BlackListApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_in_black_list);
        init();
    }

    private void init() {
        mApp = (BlackListApp) getIntent().getSerializableExtra("infoApp");
        txtTitle = findViewById(R.id.txtTitle);
        txtName = findViewById(R.id.txtAppName);
        txtPname = findViewById(R.id.txtPName);
        txtVersion = findViewById(R.id.txtVersion);
        txtOs = findViewById(R.id.txtOS);
        txtCate = findViewById(R.id.txtCategory);
        txtOwner = findViewById(R.id.txtOwner);
        btnback = findViewById(R.id.btnBackToParent);

        txtTitle.setText(mApp.getName());
        txtName.setText(mApp.getName());
        txtPname.setText(mApp.getId());
        txtVersion.setText(mApp.getVersion());
        txtOs.setText(mApp.getOs());
        txtCate.setText(mApp.getCategory());
        txtOwner.setText(mApp.getOwner());

        btnback.setOnClickListener(v -> {
            finish();
        });
    }
}