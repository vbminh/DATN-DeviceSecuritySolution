package view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.dssclient.R;

import service.LocationTrackingService;
import service.PrintWatermarkService;

public class HomeActivity extends AppCompatActivity {

    private Intent locationIntent;
    private Intent watermarkIntent;
    private TextView txtText, txtBlackList;
    private Button btnLogOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtText = findViewById(R.id.txtText);
        txtBlackList = findViewById(R.id.txtBlackList);
        btnLogOut = findViewById(R.id.btnLogOut);

        init(savedInstanceState);
        setContent();
    }

    private void init(@NonNull Bundle savedInstanceState) {
        locationIntent = new Intent(this,  LocationTrackingService.class);
        startService(locationIntent);

        watermarkIntent = new Intent(this, PrintWatermarkService.class);
        startService(watermarkIntent);

        createText(savedInstanceState);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private void createText(@NonNull Bundle savedInstanceState) {
        SpannableString spannableString = new SpannableString("Black list");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent in = new Intent(HomeActivity.this, BlackListActivity.class);
                startActivity(in);
            }
        };

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtBlackList.setText(spannableString);
        txtBlackList.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void logOut() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean networkConnected = isNetworkConnected();
        if(networkConnected) {
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            try {
                stopService(locationIntent);
            }
            catch (Exception e) {
                //Log.e(TAG, "Service is not running");
            }

            Intent in = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(in);
        }
        else
            Toast.makeText(HomeActivity.this, "No internet access", Toast.LENGTH_SHORT).show();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetwork() != null; // return true =(connected),false=(not connected)
    }

    private void setContent() {
        String str = "Welcome to try our latest device. To ensure that your experience is the best, you need to" +
                " adhere to the following rules:\n1. You must be logged in Device Security App. If you don't login, " +
                "Login Reminder message will appear every minute.\n" +
                "2. A text will be watermarked on screen.\n" +
                "3. You can't open blacklisted apps. You can see blacklisted apps below." +
                " \n5. We have access to device location when you logged in.\n" +
                "6. The device will be locked if unauthorized behavior is detected.";

        txtText.setText(str);
    }
}