/* 
 *  *************************************
 *  This program was made to command the 
 *  slave arduino which houses three 
 *  sensors(Am2315, MMA8451, MPL3115A2).
 *  
 *  Authors:
 *  -Erick Ramirez
 *    
 *    
 *  Notes(): 
 *  Error codes are as follows:
 *  
 *  -Flashing Light Errors-
 *    2 flashes = AM2315 wiring error
 *    4 flashes = MMA8451 wiring error
 *    6 flashes = MPL3115A2 wiring error
 *    
 *  -Solid Light Codes-
 *    Red = Flight Critical Error see flashing lights
 *    Green = System is operational
 *    Yellow = Sending/Writting data
 *    
 *   
 *  *************************************
 */

//Include the libraries required for our program to work.
#include <Wire.h> //i2c library to communicate with slave devices.
#include <Adafruit_AM2315.h> //The "Dongle" outside temp sensor.
#include <Adafruit_MMA8451.h> //Accelorometer
#include <Adafruit_Sensor.h> //Accerlorometer
#include <Adafruit_MPL3115A2.h> //Barometer
#include <LED.h> //Library for controlling leds
#include <I2C_Anything.h> //Library used to send sensor values to Slave





 
//Initialize the AM2315
Adafruit_AM2315 dongle;
//Initialize the MMA8451
Adafruit_MMA8451 accel = Adafruit_MMA8451();
//Initialize the MPL3115A2
Adafruit_MPL3115A2 baro;
//Initialize the LED'S
LED redLed = LED(4);
LED yellowLed = LED(3);
LED greenLed = LED(2);
//Declare the Slave Address
const byte SLAVE_ADDRESS = (8);


void setup() {
 //Join the i2c bus(Adress is optional for Master)
  Wire.begin(); 
  Serial.begin(9600);
  //Check sensor status
  if(!initSensors()){
    //Error team needs to inspect error codes
    redLed.on();
    //Keep displaying the error until team fixes it
    while(1); //If above is true it will keep looping
  }else{
    greenLed.on();
  }
}





void loop() {
  //Register the accel event 
  sensors_event_t event;
  accel.getEvent(&event);
  
  //Read sensor values no need for variables
  float sensorValues[] = {baro.getTemperature(), baro.getPressure(), baro.getAltitude(),
      event.acceleration.x, event.acceleration.y, event.acceleration.z};

  //Begin Master-Slave transmition
  
  for(int i = 0; i < 6; i++){
    Wire.beginTransmission(SLAVE_ADDRESS);
    I2C_writeAnything(sensorValues[i]);
    Wire.endTransmission();
    yellowLed.blink(500, 3);
    delay(500);
  }

  
  
  
   
}

//Function to check sensor status
boolean initSensors(){

  boolean stat = true;
  /*Check if the sensor is working
  if( !dongle.begin() ){
    //Turn on red light and blink for two seconds
    redLed.blink(1000, 2);
    stat = false;
  }*/
  if( !accel.begin() ){
    //Turn on red light and blink for four seconds
    redLed.blink(1000, 4);
    stat = false;
  }else{
    //If sensor was started then configure its range
    accel.setRange(MMA8451_RANGE_8_G);
  }
  if( !baro.begin() ){
    //Turn on red Light and blink for six seconds
    redLed.blink(1000, 6);
    stat = false;
  }
  return stat;
}


//write to slave




