package view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.application.devicesecuritysolution.R;

import model.DeviceInformation;

public class DeviceInfomationActivity extends AppCompatActivity {
    private TextView txtTitle, txtModel, txtSerial, txtVersion, txtSDK, txtToken;
    private Button btnback;

    private DeviceInformation deviceInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_infomation);
        init();
    }

    private void init() {
        deviceInformation = (DeviceInformation) getIntent().getSerializableExtra("infoDevice");
        txtTitle = findViewById(R.id.txtTitle);
        txtModel = findViewById(R.id.txtModel);
        txtSerial = findViewById(R.id.txtSerial);
        txtVersion = findViewById(R.id.txtVersion);
        txtSDK = findViewById(R.id.txtSDK);
        txtToken = findViewById(R.id.txtToken);
        btnback = findViewById(R.id.btnBackToParent);

        txtTitle.setText(deviceInformation.getModelName());
        txtModel.setText(deviceInformation.getModelName());
        txtSerial.setText(deviceInformation.getSerialNo());
        txtVersion.setText(deviceInformation.getAndroidVersion());
        txtSDK.setText(deviceInformation.getSdk()+ "");
        txtToken.setText(deviceInformation.getToken());

        btnback.setOnClickListener(v -> {
            finish();
        });
    }
}