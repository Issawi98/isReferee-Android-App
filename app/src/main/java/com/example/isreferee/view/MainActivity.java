package com.example.isreferee.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.isreferee.R;
import com.example.isreferee.model.Record;
import com.example.isreferee.model.Repository;
import com.example.isreferee.viewmodel.ViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    TextToSpeech tts;
    String mostRecentUtteranceID;
    String TTS = "";
    static String raceDistance = "50";

    ViewModel MainViewModel ;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    DrawerLayout dl;

    TextView userName, userMail;
    TextView timer, heatNumber;
    TextView distanceDisplay;

    public static MainActivity Instance;
    public static int count =1;
    ImageButton startBtn, stopBtn, resetBtn;

    private int milliseconds,seconds, minutes;
    long startTime, tMilliSecond, tBuff, tUpdate = 0l;

    private boolean running = false;
    private boolean wasRunning = false;
    private boolean likeNew = true;
    private boolean isStarted = false;

    public Handler handler;

    public RadioButton d50;
    public RadioButton d100;
    public RadioButton d200;

    String lane1;
    String lane2;
    String lane3;
    String lane4;


    int notification_id = 1;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;


    Runnable runnable= new Runnable(){
        @Override
        public void run() {
            tMilliSecond = SystemClock.uptimeMillis() - startTime;
            tUpdate = tBuff + tMilliSecond;
            seconds = (int)(tUpdate/1000);
            minutes = seconds/60;
            seconds%=60;
            milliseconds = (int)(tUpdate%100);
            timer.setText(String.format("%02d",minutes)+":"+ String.format("%02d",seconds)+":" +String.format("%02d",milliseconds));
            handler.postDelayed(this, 60);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        createNotificationChannel();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        handler= new Handler();

        dl = findViewById(R.id.drawer_layout);
        userMail = findViewById(R.id.userMailNav);
        userName = findViewById(R.id.userNameNav);
        startBtn = findViewById(R.id.StartBtn);
        stopBtn = findViewById(R.id.StopBtn);
        resetBtn = findViewById(R.id.ResetBtn);
        timer = findViewById(R.id.timeTxt);
        heatNumber =findViewById(R.id.heatNumer);
        distanceDisplay =  findViewById(R.id.distanceDisplay);

         RadioButton d50 =  findViewById(R.id.meter_50);
         RadioButton d100 =  findViewById(R.id.meter_100);
         RadioButton d200 =  findViewById(R.id.meter_200);



        //MainViewModel.setLanes();
        MainViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        MainViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    DocumentReference df = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                Log.d("TAG","onSuccess" + documentSnapshot.getData());
//                                if( documentSnapshot.getString("isAdmin") != null){
//                                    welcomeTxt.setText("Hello " + documentSnapshot.getString("FullName") + "\nAdmin");
//                                }
//                                else if( documentSnapshot.getString("isUser") != null){
//                                    welcomeTxt.setText("Hello " + documentSnapshot.getString("FullName") + "\nUser");
//                                }
                            userName.setText(documentSnapshot.getString("FullName"));
                            userMail.setText(documentSnapshot.getString("UserEmail"));
                        }
                    });
                }
            }
        });

        MainViewModel.getStartedRef().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String started) {
                if(started.equals("1"))
                    isStarted = true;
                else
                    isStarted = false;
            }
        });

        MainViewModel.getLane1Changing().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String laneChange1) {

                if(laneChange1.equals("1") && isStarted)
                {
                    lane1 = timer.getText().toString();
                    Log.d("LANE1","------  " + lane1);
                    int heatNumber = count;
                    String laneNumber = "1";
                    String time = lane1;
                    Record record = new Record(heatNumber, laneNumber, time);
                    MainViewModel.insert(record);
                    MainViewModel.setLane1();
                }
                else if(laneChange1.equals("1") && !isStarted)
                {
                    MainViewModel.setLane1();
                }

            }
        });

        MainViewModel.getLane2Changing().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String laneChange2) {
                if(laneChange2.equals("1") && isStarted)
                {
                    lane2 = timer.getText().toString();
                    Log.d("LANE2","------  " + lane2);
                    int heatNumber = count;
                    String laneNumber = "2";
                    String time = lane2;
                    Record record = new Record(heatNumber, laneNumber, time);
                    MainViewModel.insert(record);
                    MainViewModel.setLane2();
                }else if(laneChange2.equals("1") && !isStarted)
                {
                    MainViewModel.setLane2(); /////// GHAYARHAAAAAAAAA %%%%%55555555
                }

            }
        });
        
        MainViewModel.getLane3Changing().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String laneChange3) {
                if(laneChange3.equals("1") && isStarted)
                {
                    lane3 = timer.getText().toString();
                    Log.d("LANE3","------  " + lane3);
                    int heatNumber = count;
                    String laneNumber = "3";
                    String time = lane3;
                    Record record = new Record(heatNumber, laneNumber, time);
                    MainViewModel.insert(record);
                    MainViewModel.setLane3();
                }else if(laneChange3.equals("1") && !isStarted)
                {
                    MainViewModel.setLane3(); /////// GHAYARHAAAAAAAAA %%%%%55555555
                }

            }
        });

        MainViewModel.getLane4Changing().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String laneChange4) {
                if(laneChange4.equals("1") && isStarted)
                {
                    lane4 = timer.getText().toString();
                    Log.d("LANE4","------  " + lane4);
                    int heatNumber = count;
                    String laneNumber = "4";
                    String time = lane4;
                    Record record = new Record(heatNumber, laneNumber, time);
                    MainViewModel.insert(record);
                    MainViewModel.setLane4();
                }else if(laneChange4.equals("1") && !isStarted)
                {
                    MainViewModel.setLane4(); /////// GHAYARHAAAAAAAAA %%%%%55555555
                }

            }
        });

        //get race distance from fire base and set it here ..
        //raceDistance = MainViewModel.getDistance(raceDistance);
        String temp = distanceDisplay.getText().toString();
        distanceDisplay.setText(temp.split(" ")[0] + " " + temp.split(" ")[1] + " "+ raceDistance);
        MainViewModel.ChangeDistance(raceDistance);

        if (raceDistance.equals("50")){
            d50.setChecked(true);

        } else if (raceDistance.equals("100")){
            d100.setChecked(true);

        } else if (raceDistance.equals("200")){
            d200.setChecked(true);

        }



        //radio buttons
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.distanceCheck);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String temp;
                switch (checkedId){
                    case R.id.meter_50:
                        raceDistance = "50";
                        temp = distanceDisplay.getText().toString();
                        distanceDisplay.setText(temp.split(" ")[0] + " " + temp.split(" ")[1] + " "+ raceDistance);
                        MainViewModel.ChangeDistance(raceDistance);
                        return;
                    case R.id.meter_100:
                        raceDistance = "100";
                        temp = distanceDisplay.getText().toString();
                        distanceDisplay.setText(temp.split(" ")[0] + " " + temp.split(" ")[1] + " "+ raceDistance);
                        MainViewModel.ChangeDistance(raceDistance);
                        return;
                    case R.id.meter_200:
                        raceDistance = "200";
                        temp = distanceDisplay.getText().toString();
                        distanceDisplay.setText(temp.split(" ")[0] + " " + temp.split(" ")[1] + " "+ raceDistance);
                        MainViewModel.ChangeDistance(raceDistance);
                        return;
                    default:
                        return;
                }
            }
        });



        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {
                    Log.d("speech", " ==== PASS");
                    tts.setLanguage(Locale.ENGLISH);
                    tts.setPitch((float) 0.7);
                    tts.setSpeechRate((float) 0.8);
                    // set unique utterance ID for each utterance
                    TTS = heatNumber.getText().toString()+ "  Take your Mark,    Start. " ;

                }

            }
        });

    }



    public void speaktext() {
        mostRecentUtteranceID = count + "ID"; // "" is String force
        tts.speak(TTS, TextToSpeech.QUEUE_ADD, null, mostRecentUtteranceID);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                if(!running){
                    running = true;
                    startTime = SystemClock.uptimeMillis();
                    MainViewModel.TriggerStart();
                    handler.postDelayed(runnable,0);
                }
            }

            @Override
            public void onError(String s) {

            }
        });
    }


    public void ClickMenu(View view) {
        if(!running) {
            OpenProfileDrawer(dl);
        } else {
            Toast.makeText(getApplicationContext(), "Stop the Heat First!", Toast.LENGTH_SHORT).show();
        }
    }

    private static void OpenProfileDrawer(DrawerLayout dl) {
        dl.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view) {
        CloseProfileDrawer(dl);
    }

    private static void CloseProfileDrawer(DrawerLayout dl) {
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
        }

    }

    public void ClickDistance(View view) {
        if(!running & likeNew) {
            OpenDistanceDrawer(dl);
        } else {
            Toast.makeText(getApplicationContext(), "Stop the Heat First!", Toast.LENGTH_SHORT).show();
        }

    }

    private static void OpenDistanceDrawer(DrawerLayout dl) {
        dl.openDrawer(GravityCompat.END);
    }


    private static void CloseDistanceDrawer(DrawerLayout dl) {
        if(dl.isDrawerOpen(GravityCompat.END)){
            dl.closeDrawer(GravityCompat.END);
        }

    }

    public void StopTime(View view) {
        if (running) {
            running = false;
            wasRunning = true;
            tBuff+=tMilliSecond;
            handler.removeCallbacks(runnable);
            likeNew = false;
            MainViewModel.TriggerStop();

            Log.d("TIME", "STOPPED AT "+ timer.getText());
        }
    }

    public void ResetTime(View view) {
       if(!running){
           startTime = 0l; tMilliSecond = 0l; tBuff= 0l ; tUpdate = 0l;
           timer.setText("00:00:00");
           running = false;
           wasRunning = false;
           likeNew = true;
           NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Notify")
                   .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.icon))
                   .setSmallIcon(R.drawable.iconwhite)
                   .setContentTitle("FINISHED")
                   .setContentText("Heat Number " + count + " is Finished")
                   .setStyle(new NotificationCompat.BigTextStyle().bigText("Heat Number " + count + " is Finished"))
                   .setPriority(NotificationCompat.PRIORITY_DEFAULT);

           final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
           notificationManager.notify(notification_id, builder.build());

       }
    }

    public void StartTime(View view) {
        if(!wasRunning){
            wasRunning = true;
            likeNew = false;
            speaktext();
        }
        else{
            if(!running){
                running = true;
                startTime = SystemClock.uptimeMillis();
                likeNew = false;
                MainViewModel.TriggerStart();
                handler.postDelayed(runnable,0);
            }
        }
    }

    public void IncreamentHeat(View view) {
        if(running){

        }
        else if(!running && likeNew){
            String temp = heatNumber.getText().toString();
            count ++;
            heatNumber.setText(temp.split(" ")[0]+" "+ temp.split(" ")[1] +" "+count);
            TTS = heatNumber.getText().toString()+ "  Take your Mark,    Start. " ;
        }

    }

    public void DecreamentHeat(View view) {
        if(running){

        }
        else if(!running && likeNew && count>1){
            String temp = heatNumber.getText().toString();
            count --;
            heatNumber.setText(temp.split(" ")[0]+" "+ temp.split(" ")[1] +" "+count);
            TTS = heatNumber.getText().toString()+ "  Take your Mark,    Start. " ;
        }

    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Stop the Heat First!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean shouldAllowBack() {
        if(!running){
            return true;
        }else
            return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainViewModel.TriggerStop();
        String temp = heatNumber.getText().toString();
        heatNumber.setText(temp.split(" ")[0]+" "+ temp.split(" ")[1] +" "+count);


    }

    // this or the drawer
    public void DistanceMenu(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.inflate(R.menu.distances);
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String temp;
                switch (menuItem.getItemId()){
                    case R.id.meter50:
                        //firebase set
                        raceDistance = "50";
                        temp = distanceDisplay.getText().toString();
                        distanceDisplay.setText(temp.split(" ")[0] + " " + temp.split(" ")[1] + " "+ raceDistance);
                        return true;

                    case R.id.meter100:
                        raceDistance = "100";
                        temp = distanceDisplay.getText().toString();
                        distanceDisplay.setText(temp.split(" ")[0] + " " + temp.split(" ")[1] + " "+ raceDistance);
                        return true;

                    case R.id.meter200:
                        raceDistance = "200";
                        temp = distanceDisplay.getText().toString();
                        distanceDisplay.setText(temp.split(" ")[0] + " " + temp.split(" ")[1] + " "+ raceDistance);
                        return true;

                    default:
                        raceDistance = "50";
                        temp = distanceDisplay.getText().toString();
                        distanceDisplay.setText(temp.split(" ")[0] + " " + temp.split(" ")[1] + " "+ raceDistance);
                        return true;
                }
            }
        });
    }

    public void LogOut(View view) {
        MainViewModel.SignOut();
        startActivity( new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void GoToRecords(View view) {
        startActivity( new Intent(getApplicationContext(), ContentRecords.class));
        //finish();
    }

    public void GoToGetSpecificRecord(View view) {
        Intent myIntent = new Intent(getApplicationContext(), GetRecord.class);
        myIntent.putExtra("HeatNumber", count);
        startActivity(myIntent);
        //finish();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        CharSequence name = getString(R.string.channelName);
        String description = getString(R.string.channelDesc);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("Notify", name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}