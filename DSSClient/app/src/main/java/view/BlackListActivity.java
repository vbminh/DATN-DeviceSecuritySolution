package view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.application.dssclient.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.BlacklistAppAdapter;
import module.BlackListApp;

public class BlackListActivity extends AppCompatActivity {

    List<BlackListApp> lstBlackApp;

    BlacklistAppAdapter blacklistAppAdapter ;

    RecyclerView rcvBlackApp;

    private static final String TAG = "BlackListActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        init();
    }

    private void init() {
        lstBlackApp = new ArrayList<>();
        rcvBlackApp = findViewById(R.id.rcvBlackApp);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvBlackApp.setLayoutManager(linearLayoutManager);
        setDatafromServer();
        findViewById(R.id.btnBackToParent).setOnClickListener(view -> finish());
    }

    private void setDatafromServer() {
        FirebaseFirestore.getInstance().collection("blackListApp")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            BlackListApp itemInfo = null;

                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                itemInfo = new BlackListApp(doc.getString("name")
                                        , doc.getString("id")
                                        , doc.getString("imageUrl")
                                        , doc.getString("category")
                                        , doc.getString("version")
                                        , doc.getString("os")
                                        , doc.getString("owner"));
                                lstBlackApp.add(itemInfo);
                            }


                            blacklistAppAdapter = new BlacklistAppAdapter(lstBlackApp , BlackListActivity.this);
                            rcvBlackApp.setAdapter(blacklistAppAdapter);
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
