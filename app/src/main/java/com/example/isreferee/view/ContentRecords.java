package com.example.isreferee.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.isreferee.model.Record;
import com.example.isreferee.viewmodel.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.example.isreferee.R;

import java.util.List;

public class ContentRecords extends AppCompatActivity {

    private ViewModel RecordsViewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_records);
        getSupportActionBar().hide();
        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.recyclerview1);
        final RecordListAdapter adapter1 = new RecordListAdapter(this);
        recyclerView1.setAdapter(adapter1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));


//        RecyclerView recyclerView2 = findViewById(R.id.recyclerview2);
//        final HeatsListAdapter adapter2 = new HeatsListAdapter(this);
//        recyclerView2.setAdapter(adapter2);
//        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        RecordsViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        RecordsViewModel.getAllRecords().observe(this, new Observer<List<Record>>() {
            @Override
            public void onChanged(@Nullable final List<Record> records) {
                // Update the cached copy of the words in the adapter.
                adapter1.setRecords(records);
//                adapter2.setRecords(records);
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
