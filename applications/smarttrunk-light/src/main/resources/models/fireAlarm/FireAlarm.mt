package fireAlarm;

import fireAlarm.sensors.*;
import fireAlarm.actuator.*;
import fireAlarm.contr.FireExtinguisherContr;

/* Test component FireAlarm */
<<deploy>> component FireAlarm {

  /* Subcomponents */
  
  component Alarm alarm;
  //component Alarm alarm2;
  component Sprinkler sprinkler;
  component FireExtinguisherContr fireExt;
  component SmokeSensor smokeSensor;
  component TemperatureSensor tempSensor;
  

  /* Connections */
  
  connect fireExt.alarmOn -> alarm.onn;
  connect fireExt.sprinklerOn -> sprinkler.onn;
  connect smokeSensor.value -> fireExt.smoke;
  connect tempSensor.value -> fireExt.temp;
  
}
