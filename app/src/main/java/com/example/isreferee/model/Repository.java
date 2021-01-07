package com.example.isreferee.model;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Repository {

    private Application application;

    public MutableLiveData<FirebaseUser> userMutableLiveData;
    public MutableLiveData<String> lane1Data;
    public MutableLiveData<String> lane2Data;
    public MutableLiveData<String> lane3Data;
    public MutableLiveData<String> lane4Data;
    public MutableLiveData<String> startedData;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference isStartedRef = database.getReference("started");
    DatabaseReference distanceRef = database.getReference("distance");

    DatabaseReference lane1Ref = database.getReference("lane1");
    DatabaseReference lane2Ref = database.getReference("lane2");
    DatabaseReference lane3Ref = database.getReference("lane3");
    DatabaseReference lane4Ref = database.getReference("lane4");

    RecordDao RecordDao;
    LiveData<List<Record>> AllRecords;
    //LiveData<Record> Records;

    public Repository(Application application) {
        RecordsRoomDatabase db = RecordsRoomDatabase.getDatabase(application);
        RecordDao = db.RecordDao();
        AllRecords = RecordDao.getAllRecords();

        this.application= application;
        userMutableLiveData = new MutableLiveData<>();

        lane1Data = new MutableLiveData<>();
        lane2Data = new MutableLiveData<>();
        lane3Data = new MutableLiveData<>();
        lane4Data = new MutableLiveData<>();
        startedData = new MutableLiveData<>();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userMutableLiveData.postValue(fAuth.getCurrentUser());
        }

        int theatNumber = 0;
        String tlaneNumber = "0";
        String ttime = "00:00:00";

        Record trecord = new Record(theatNumber, tlaneNumber, ttime);
        insert(trecord);
        delete(trecord);


    }

    public void Register(EditText email, EditText password, EditText phone, EditText fullName, boolean isAdmin){
        Log.d("TAG", "TEST3 " + email.getText().toString() + " " + password.getText().toString());
        fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        userMutableLiveData.postValue(user);
                        Toast.makeText(application, "Account Created", Toast.LENGTH_SHORT).show();

                        DocumentReference df = fStore.collection("Users").document(user.getUid());
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("FullName",fullName.getText().toString());
                        userInfo.put("UserEmail",email.getText().toString());
                        userInfo.put("PhoneNumber",phone.getText().toString());

                        //Access Level
                        if(!isAdmin){
                            userInfo.put("isUser", "1");
                        }
                        if(isAdmin){
                            userInfo.put("isAdmin", "1");
                        }

                        df.set(userInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, "Failed to Create Account.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Login(EditText email, EditText password){
        Log.d("TAG", "TEST3 " + email.getText().toString() + " " + password.getText().toString());
        fAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //activity admin w user
                FirebaseUser user = fAuth.getCurrentUser();
                userMutableLiveData.postValue(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, "Failed to LOGIN.", Toast.LENGTH_SHORT).show();
            }
        });


//        fAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                //activity admin w user
//                checkUserAccessLevel(authResult.getUser().getUid());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Failed to LOGIN.", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public DocumentReference CheckUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DocumentReference df = fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            return df;
        }
        return null;
    }

    public void SignOut() {
        FirebaseAuth.getInstance().signOut();
    }


    public void TriggerStart(){
        isStartedRef.setValue("1");
    }

    public void TriggerStop(){
        isStartedRef.setValue("0");
        lane1Ref.setValue("0");
        lane2Ref.setValue("0");
        lane3Ref.setValue("0");
        lane4Ref.setValue("0");
    }

    public void ChangeDistance(String Distance){
        distanceRef.setValue(Distance);
    }

//    public void setLanes() {
//        lane1Ref.setValue("0");
//        lane2Ref.setValue("0");
//        lane3Ref.setValue("0");
//        lane4Ref.setValue("0");
//    }

    public void setLane1() {
        lane1Ref.setValue("0");
    }

    public void setLane2() {
        lane2Ref.setValue("0");
    }

    public void setLane3() {
        lane3Ref.setValue("0");
    }

    public void setLane4() {
        lane4Ref.setValue("0");
    }


    public MutableLiveData<FirebaseUser> getUserMutableLiveData () {
        return userMutableLiveData;
    }


    public MutableLiveData<String> getStartedRef() {
        listenStarted();
        return startedData;
    }

    public MutableLiveData<String> getLane1Changing() {
        lane1Change();
        return lane1Data;
    }

    public MutableLiveData<String> getLane2Changing() {
        lane2Change();
        return lane2Data;
    }

    public MutableLiveData<String> getLane3Changing() {
        lane3Change();
        return lane3Data;
    }

    public MutableLiveData<String> getLane4Changing() {
        lane4Change();
        return lane4Data;
    }

    public void listenStarted(){
        // Read from the database
        isStartedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                startedData.postValue(value+"");
                Log.d("Started", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                String value = "failed";
                Log.w("Started", "Failed to Start.", error.toException());
            }
        });
    }

    public void lane1Change(){
        // Read from the database
        lane1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value1 = dataSnapshot.getValue(String.class);
                lane1Data.postValue(value1+"");
                Log.d("lane1 at repo", "Value is: " + value1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                String value = "failed";
                Log.w("lane 1 at repo", "Failed to read value.", error.toException());
            }
        });
    }

    public void lane2Change(){
        // Read from the database
        lane2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value2 = dataSnapshot.getValue(String.class);
                lane2Data.postValue(value2+"");
                Log.d("lane 2 at repo", "Value is: " + value2);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                String value = "failed";
                Log.w("lane 2 at repo", "Failed to read value.", error.toException());
            }
        });
    }

    public void lane3Change(){
        // Read from the database
        lane3Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value3 = dataSnapshot.getValue(String.class);
                lane3Data.postValue(value3+"");
                Log.d("lane 3 at repo", "Value is: " + value3);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                String value = "failed";
                Log.w("lane 3 at repo", "Failed to read value.", error.toException());
            }
        });
    }

    public void lane4Change(){
        // Read from the database
        lane4Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value4 = dataSnapshot.getValue(String.class);
                lane4Data.postValue(value4+"");
                Log.d("lane 4 at repo", "Value is: " + value4);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                String value = "failed";
                Log.w("lane 4 at repo", "Failed to read value.", error.toException());
            }
        });
    }


    public LiveData<List<Record>> getAllRecords() {
        return AllRecords;
    }

    public void insert (Record record) {
        new insertAsyncTask(RecordDao).execute(record);
    }

    public void delete (Record record) {
        new deleteAsyncTask(RecordDao).execute(record);
    }

    private static class insertAsyncTask extends AsyncTask<Record, Void, Void> {

        private RecordDao AsyncTaskDao;

        insertAsyncTask(RecordDao dao) {
            AsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Record... params) {
            AsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Record, Void, Void> {

        private RecordDao AsyncTaskDao;

        deleteAsyncTask(RecordDao dao) {
            AsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Record... params) {
            AsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    public String getRecord(int heat, int lane) {
        Integer[] myTaskParams = { heat, lane };
        String time = "Didn't Arrive Yet";
        try {
            time = new getAsyncTask(RecordDao).execute(myTaskParams).get();
        } catch (ExecutionException e) {
            return time;
        } catch (InterruptedException e) {
            return time;
        }
        return time;
    }

    private static class getAsyncTask extends AsyncTask<Integer, Void, String> {

        private RecordDao AsyncTaskDao;

        getAsyncTask(RecordDao dao) {
            AsyncTaskDao = dao;
        }

        @Override
        protected String doInBackground(final Integer... params) {
            String time = AsyncTaskDao.getRecord(params[0],params[1]);
            return time;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
