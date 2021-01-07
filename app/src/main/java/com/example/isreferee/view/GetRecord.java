package com.example.isreferee.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.isreferee.R;
import com.example.isreferee.viewmodel.ViewModel;

public class GetRecord extends AppCompatActivity {

    EditText heatNum;
    EditText laneNum;
    TextView recordGot;
    Button getRecord;
    ViewModel RecordsViewModel;
    int heatNumber;
    int laneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_record);
        getSupportActionBar().hide();

        heatNum = findViewById(R.id.HeatToGet);
        laneNum = findViewById(R.id.LaneToGet);
        recordGot = findViewById(R.id.recordGot);
        getRecord = findViewById(R.id.getSpecificRecord);


        RecordsViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        Intent mIntent = getIntent();
        int count = mIntent.getIntExtra("HeatNumber", 1);

        getRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(heatNum.getText().toString().isEmpty() || laneNum.getText().toString().isEmpty())){
                    heatNumber = Integer.parseInt(heatNum.getText().toString());
                    laneNumber = Integer.parseInt(laneNum.getText().toString());

                    Log.w("getSpec", "Heat Value "+ heatNumber);
                    Log.w("getSpec", "Lane Value "+ laneNumber);

                    if (heatNumber > count || heatNumber == 0) {
                        Toast.makeText(getApplicationContext(), "Heat Didn't come yet", Toast.LENGTH_SHORT).show();
                    } else if (laneNumber > 4 || laneNumber == 0) {
                        Toast.makeText(getApplicationContext(), "Lane Do Not Exist", Toast.LENGTH_SHORT).show();
                    } else {
                        String temp = RecordsViewModel.getRecord(heatNumber, laneNumber);
                        recordGot.setText(temp);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter Numbers!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void Refresh(View view) {
        startActivity(getIntent());
        finish();
    }
}