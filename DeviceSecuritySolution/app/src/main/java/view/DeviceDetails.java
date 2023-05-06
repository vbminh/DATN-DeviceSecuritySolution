package view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.application.devicesecuritysolution.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import ViewModel.RemoteDeviceControl;
import algorithm.DES;
import model.DeviceInformation;

public class DeviceDetails extends AppCompatActivity {

    private DeviceInformation deviceInformation;

    private String policy;

    private Button btnLock, btnUnlock;

    private static final String TAG = "DeviceDetails1";

    private DES des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        des = new DES();
        deviceInformation = (DeviceInformation) getIntent().getSerializableExtra("infoDevice");

        init();
    }

    private void init() {
        findViewById(R.id.btnInfo).setOnClickListener(v -> {
            Intent intent = new Intent(DeviceDetails.this, DeviceInfomationActivity.class);
            intent.putExtra("infoDevice", deviceInformation);
            startActivity(intent);
        });

        findViewById(R.id.btnPolicy).setOnClickListener(v -> {
            getDataFromServer();
            Intent intent = new Intent(DeviceDetails.this, PolicyChangeActivity.class);
            intent.putExtra("infoDevice", deviceInformation);
            startActivity(intent);
        });

        findViewById(R.id.btnTracking).setOnClickListener(v -> {
            boolean networkConnected = isNetworkConnected();
            if(networkConnected) {
                Intent intent = new Intent(DeviceDetails.this, TrackingDeviceActivity.class);
                intent.putExtra("infoDevice", deviceInformation);
                startActivity(intent);
            }
            else
                Toast.makeText(DeviceDetails.this, "No internet connection", Toast.LENGTH_SHORT).show();
        });

        btnLock = findViewById(R.id.btnLock);
        btnUnlock = findViewById(R.id.btnUnlock);

        btnLock.setOnClickListener(v -> { setLockButtonAction();
        });

        btnUnlock.setOnClickListener(v -> {setUnLockButtonAction();
        });
    }

    private void setLockButtonAction() {
        getDataFromServer();

        if(deviceInformation.getPolicy().charAt(3) == '0')
            Toast.makeText(this, "Remote device lock state not enabled", Toast.LENGTH_SHORT).show();
        else {
            boolean networkConnected = isNetworkConnected();
            if(networkConnected) {
                RemoteDeviceControl.postNotification(this, deviceInformation.getToken(), "lock", "lockDevice");
                btnUnlock.setEnabled(true);
                btnUnlock.setVisibility(View.VISIBLE);
                btnLock.setEnabled(false);
                btnLock.setVisibility(View.INVISIBLE);
            }
            else
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void setUnLockButtonAction() {
        boolean networkConnected = isNetworkConnected();
        if(networkConnected) {
            RemoteDeviceControl.postNotification(this, deviceInformation.getToken(), "unlock", "unLockDevice");
            btnUnlock.setEnabled(false);
            btnUnlock.setVisibility(View.INVISIBLE);
            btnLock.setEnabled(true);
            btnLock.setVisibility(View.VISIBLE);
        }
        else
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();

    }

    private void getDataFromServer()
    {
        boolean networkConnected = isNetworkConnected();
        if(networkConnected) {
            Log.e(TAG, "getDataFromServer: "+" runOn" );
            FirebaseFirestore.getInstance().collection("informations")
                    .whereEqualTo("serialNo", deviceInformation.getSerialNo())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot doc : task.getResult())
                                        policy = doc.getString("policy");

                                    deviceInformation.setPolicy(policy);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: "+ "didn't work" );
                        }
                    });
        }
        else
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetwork() != null; // return true =(connected),false=(not connected)
    }
}