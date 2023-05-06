package view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.application.devicesecuritysolution.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.DeviceAdapter;
import model.DeviceInformation;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity1";
    private List<DeviceInformation> list;
    private RecyclerView rcvInfoDevice;
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.e(TAG, "OnCreate");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView()
    {
        TextView txtView = findViewById(R.id.txtTest);
        txtView.setText("Device List");
        rcvInfoDevice = findViewById(R.id.rcv_InfoDevice);
        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvInfoDevice.setLayoutManager(linearLayoutManager);
        Log.e(TAG, "init view");
        setListInfoDevice();
    }

    private void setListInfoDevice()
    {
        Log.e(TAG, "setLstInfoDevice: "+" runOn" );
        FirebaseFirestore.getInstance().collection("informations")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Log.e(TAG, "setLstInfoDevice: "+"success" );
                            DeviceInformation itemInfo = null;

                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                itemInfo = new DeviceInformation(doc.getString("policy")
                                        , doc.getString("serialNo")
                                        , doc.getString("token")
                                        , doc.getString("Model Name")
                                        , doc.getLong("Android Version Code"),
                                        doc.getString("Android Version Name"));
                                list.add(itemInfo);
                            }
                            Log.i(TAG, "onComplete: "+ list.size());

                            deviceAdapter = new DeviceAdapter(list , HomeActivity.this);
                            rcvInfoDevice.setAdapter(deviceAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: "+ "didn't work" );
                    }
                });
    }
}
