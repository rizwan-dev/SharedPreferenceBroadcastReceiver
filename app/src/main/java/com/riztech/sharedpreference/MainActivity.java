package com.riztech.sharedpreference;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnSave;
    EditText edtName, edtAddress, edtPhoneNumber;
    TextView txtMessage;
    SharedPreferences pref;
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PHONE_NUMBER = "phone_number";

    private static final int REQUEST_PERMISSION_SMS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        txtMessage = findViewById(R.id.txtMessage);
        btnSave = findViewById(R.id.btnSave);

        pref = getApplicationContext().getSharedPreferences("MyApp", MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                    REQUEST_PERMISSION_SMS);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String address = edtAddress.getText().toString().trim();
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(NAME, name);
                editor.putString(ADDRESS, address);
                editor.putString(PHONE_NUMBER, phoneNumber);
                editor.apply();

            }
        });
    }

    public void GetData(View view) {
        String name = pref.getString(NAME, "");
        String addrress = pref.getString(ADDRESS, "");
        String phoneNumber = pref.getString(PHONE_NUMBER, "");

        edtName.setText(name);
        edtAddress.setText(addrress);
        edtPhoneNumber.setText(phoneNumber);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "This permission is mandatory", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                        REQUEST_PERMISSION_SMS);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(smsReceiver, new IntentFilter("sms"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sms = intent.getStringExtra("sms");
            txtMessage.setText(sms);
        }
    };
}
