/* 
 *  *************************************
 *  This program was made to receive sensor data
 *  collected from the Master Arduino. This Slave
 *  will record data to a microSD card and be responible
 *  for maintaining bluetooth communication.
 *  
 *  Authors:
 *   -Erick Ramirez
 *    
 *    
 *  Notes(): 
 *  Error codes are as follows:
 *  
 *  -Flashing Light Errors-
 *    2 flashes = MicroSD shield wiring error
 *    4 flashes = Bluefruit friend wiring error.
 *   
 *   -Solid Light Codes-
 *    Red = Flight Critical Error see flashing lights
 *    Green = Indicates Slave is operational
 *    Yellow = Sending/Writting data
 *   
 *  *************************************
 */


//Include the libraries 
#include <SPI.h> //Library used to communicate with our SPI modules
#include <SD.h> //Library used to store our data
#include <Wire.h> //Library used to communicate via i2c
#include <LED.h> //Library used to control the LEDs



#include <I2C_Anything.h> //Libray used to receive sensor values from Master






//Initialize the LED'S
LED redLed = LED(4);
LED yellowLed = LED(3);
LED greenLed = LED(2);

//Declare the chip select for the SD
const int chipSelect = 10;
//Declare the slave Address
const byte MY_ADDRESS = (8);
//The array to store the incoming sensor values
float sensorValues[6];
volatile float sensorValue;
//The decision to check if we have received data
volatile boolean dataArrived = false;
int i = 0;
String fileName = "flightData.txt";


void setup() {
  //Join i2c as with the slave address of 8 
  Wire.begin(MY_ADDRESS); 
  //Check module status
  if(!initSensors()){
    //Error there is a wiring error check the error codes
    redLed.on();
    while(1); //If there is a critical error impede system from proceeding
  }else{
    //Turn Green if everything is good to go
    greenLed.on();
    
    //Function to perform when receiving data from Master
    Wire.onReceive(receiveEvent);
  }
  
}

void loop() {
  //String to store the data
  String sensorData = "";
  //Check if the data has arrived
  if(dataArrived){
    sensorValues[i] = sensorValue;
    sensorData += sensorValues[i];
    if(sizeof sensorValues < 6){
      sensorData += ",";
    }
    else{
    sensorData += "~";
    }
    if( i >= 6){
      i = 0; 
    }else{
      i++;
    }
  }



}

boolean initSensors(){
  boolean stat = true;

  if(!SD.begin(chipSelect)){
    //Turn on red light and blink for 4 times
    redLed.blink(1000, 4);
    stat = false;
  }
  
}

void receiveEvent(int howMany){
  //Check how many bytes we got
    if(howMany >= sizeof sensorValue){
    I2C_readAnything(sensorValue);
    dataArrived = true;
    
  
  }
  
}




