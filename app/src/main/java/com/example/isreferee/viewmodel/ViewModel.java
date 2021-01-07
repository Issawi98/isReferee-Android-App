package com.example.isreferee.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.isreferee.model.Record;
import com.example.isreferee.model.RecordDao;
import com.example.isreferee.model.Repository;
import com.example.isreferee.view.MainActivity;
import com.example.isreferee.view.Register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewModel extends AndroidViewModel{

    private final Repository mRepository;
    public MutableLiveData<FirebaseUser> userMutableLiveData;


    private LiveData<List<Record>> AllRecords;

    public MutableLiveData<String> lane1Data;
    public MutableLiveData<String> lane2Data;
    public MutableLiveData<String> lane3Data;
    public MutableLiveData<String> lane4Data;

    public MutableLiveData<String> startedData;

    public ViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
        userMutableLiveData = mRepository.getUserMutableLiveData();

        lane1Data = mRepository.getLane1Changing();
        lane2Data = mRepository.getLane2Changing();
        lane3Data = mRepository.getLane3Changing();
        lane4Data = mRepository.getLane4Changing();
        startedData = mRepository.getStartedRef();

        AllRecords = mRepository.getAllRecords();
    }

    public void Register(EditText email, EditText password, EditText phone, EditText fullName, boolean isAdmin){
        Log.d("TAG", "TEST2 " + email.getText().toString() + " " + password.getText().toString());
        mRepository.Register(email,password,phone,fullName,isAdmin);

    }

    public void Login(EditText email, EditText password){
        Log.d("TAG", "TEST2 " + email.getText().toString() + " " + password.getText().toString());
        mRepository.Login(email,password);

    }

    public DocumentReference CheckUser(){
        DocumentReference df = mRepository.CheckUser();
        return df;
    }

    public void TriggerStart(){
        mRepository.TriggerStart();
    }

    public void TriggerStop(){
        mRepository.TriggerStop();
    }

    public void ChangeDistance(String Distance){
        mRepository.ChangeDistance(Distance);
    }

//    public String getDistance(String raceDistance){
//        return mRepository.getDistance(raceDistance);
//    }

//    public void setLanes(){
//        mRepository.setLanes();
//    }

    public void setLane1() {
        mRepository.setLane1();
    }

    public void setLane2() {
        mRepository.setLane2();
    }

    public void setLane3() {
        mRepository.setLane3();
    }

    public void setLane4() {
        mRepository.setLane4();
    }

//    public String getDistance(String raceDistance){
//        return mRepository.getDistance(raceDistance);
//    }

//    public void setLane1(String value){
//        Instance.setLane1(value);
//    }

    public MutableLiveData<String> getLane1Changing() {
        return lane1Data;
    }

    public MutableLiveData<String> getLane2Changing() {
        return lane2Data;
    }

    public MutableLiveData<String> getLane3Changing() {
        return lane3Data;
    }

    public MutableLiveData<String> getLane4Changing() {
        return lane4Data;
    }

    public MutableLiveData<String> getStartedRef() {
        return startedData;
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void SignOut(){
        mRepository.SignOut();
    }

    public LiveData<List<Record>> getAllRecords() { return AllRecords; }

    public void insert(Record record) { mRepository.insert(record); }

    public void delete(Record record) { mRepository.insert(record); }

    public String getRecord(int heat, int lane) {
      return mRepository.getRecord(heat, lane);

    }
}
