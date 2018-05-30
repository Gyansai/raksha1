package com.example.pradeep.raksha1;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class falldetected extends AppCompatActivity  {
   ImageView imv;
   TextView mtextField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falldetected);
        mtextField=(TextView)findViewById(R.id.textView4);
        imv=(ImageView)findViewById(R.id.imageV);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendSMS("+918179535894", "I need help ");

            }
        }, 15000);

        imv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(falldetected.this, MainActivity.class);
                intent.putExtra("EXIT", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        CountDownTimer countDownTimer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                mtextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mtextField.setText("done!");
            }
        }.start();





    }



    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);

    }

}
