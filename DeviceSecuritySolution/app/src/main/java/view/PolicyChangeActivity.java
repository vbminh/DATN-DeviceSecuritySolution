package view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;

import com.application.devicesecuritysolution.R;

import ViewModel.RemoteDeviceControl;
import model.DeviceInformation;

public class PolicyChangeActivity extends AppCompatActivity {

    private static final String TAG = "PolicyChangeActivity1";
    private DeviceInformation deviceInformation;
    private Switch swPopUp , swWatermark , swBlockNetwork , swLockDevice , swSendLocation;

    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_change);
        initView();
    }

    private  void initView()
    {
        deviceInformation = (DeviceInformation) getIntent().getSerializableExtra("infoDevice");

        swPopUp = findViewById(R.id.swPopup);
        swWatermark = findViewById(R.id.swWaterMark);
        swLockDevice = findViewById(R.id.swLockDevice);
        swBlockNetwork = findViewById(R.id.swBlockNetwork);
        swSendLocation = findViewById(R.id.swSendLocation);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancelPolicy);
        String policy = deviceInformation.getPolicy();
        initSwitch(policy);
    }
    private void initSwitch(String policy) {
        Log.e(TAG, "initSwitch: "+ policy );
        String[] words = policy.split("");
        swPopUp.setChecked(getStateofDevice(words[0] ));
        swWatermark.setChecked(getStateofDevice(words[1] ));
        swBlockNetwork.setChecked(getStateofDevice(words[2] ));
        swLockDevice.setChecked(getStateofDevice(words[3] ));
        swSendLocation.setChecked(getStateofDevice(words[4] ));

        btnSave.setOnClickListener(v ->  {
            postNotification();
        });
        btnCancel.setOnClickListener(v -> {;
            finish();
        });
    }

    private void postNotification() {
        String policy = "";
        if(swPopUp.isChecked())    policy +="1";
        else policy+="0";
        if(swWatermark.isChecked())    policy +="1";
        else policy+="0";
        if(swBlockNetwork.isChecked())    policy +="1";
        else policy+="0";
        if(swLockDevice.isChecked())    policy +="1";
        else policy+="0";
        if(swSendLocation.isChecked())    policy +="1";
        else policy+="0";
        Log.e(TAG, "token: "+ deviceInformation.getToken() );
        RemoteDeviceControl.postNotification(this , deviceInformation.getToken() , policy, "updatePolicy");
        finish();
    }

    private boolean getStateofDevice(String word) {
        if(word.equals("1"))     return true;
        return false;
    }
}