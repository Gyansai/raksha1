package com.example.pradeep.raksha1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class falldetected extends AppCompatActivity implements View.OnClickListener {
   Button b1;
   ImageView imv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falldetected);
        b1=(Button)findViewById(R.id.button5);
        b1.setOnClickListener(this);





    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(falldetected.this, MainActivity.class);
        intent.putExtra("EXIT", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
