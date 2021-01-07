#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

// Set these to run example.
#define WIFI_SSID "doda"
#define WIFI_PASSWORD "@170570r"
#define FIREBASE_HOST "isreferee-5a163-default-rtdb.firebaseio.com" 
#define FIREBASE_AUTH "NZwshtu141DeKMPzueCdDZu8ef5OPiJpraBXdSmi"

uint8_t GPIO_Pin1 = D1;
uint8_t GPIO_Pin2 = D2;
uint8_t GPIO_Pin3 = D3;
uint8_t GPIO_Pin4 = D4;

int switchOneState = 0;
int switchTwoState = 0;
int switchThreeState = 0;
int switchFourState = 0;

void ICACHE_RAM_ATTR IntCallback1 ();
void ICACHE_RAM_ATTR IntCallback2 ();
void ICACHE_RAM_ATTR IntCallback3 ();
void ICACHE_RAM_ATTR IntCallback4 ();


void setup() {
 Serial.begin(9600);
 pinMode(GPIO_Pin1, INPUT);
 pinMode(GPIO_Pin2, INPUT);
 pinMode(GPIO_Pin3, INPUT);
 pinMode(GPIO_Pin4, INPUT);
 
 attachInterrupt(digitalPinToInterrupt(GPIO_Pin1), IntCallback1, HIGH);
 attachInterrupt(digitalPinToInterrupt(GPIO_Pin2), IntCallback2, HIGH);
 attachInterrupt(digitalPinToInterrupt(GPIO_Pin3), IntCallback3, HIGH);
 attachInterrupt(digitalPinToInterrupt(GPIO_Pin4), IntCallback4, HIGH);

 // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println();
  Serial.print("connecting to WIFI");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
}

void loop() {
  if(switchOneState == 1) {
    switchOneState = 0;
    Serial.println("switch 1 pressed");
    if(WiFi.status() == WL_CONNECTED) {
      Firebase.setString("lane1", "1"); 
    }else{
      Serial.println("FAILED");
    }
  } 
  if(switchTwoState == 1) {
    switchTwoState = 0;
    Serial.println("switch 2 pressed");
    if(WiFi.status() == WL_CONNECTED){
      Firebase.setString("lane2", "1"); 
    }else{
      Serial.println("FAILED");
    }
  }
  if(switchThreeState == 1) {
    switchThreeState = 0;
    Serial.println("switch 3 pressed");
    if(WiFi.status() == WL_CONNECTED){
      Firebase.setString("lane3", "1"); 
    }else{
      Serial.println("FAILED");
    }
  }
  if(switchFourState == 1) {
    switchFourState = 0;
    Serial.println("switch 4 pressed");
    if(WiFi.status() == WL_CONNECTED){
      Firebase.setString("lane4", "1"); 
    }else{
      Serial.println("FAILED");
    }
  }
  delay(200);
  
}

void IntCallback1(){
 switchOneState = 1;
}

void IntCallback2(){
 switchTwoState = 1;
}

void IntCallback3(){
 switchThreeState = 1;
}

void IntCallback4(){
 switchFourState = 1;

}
