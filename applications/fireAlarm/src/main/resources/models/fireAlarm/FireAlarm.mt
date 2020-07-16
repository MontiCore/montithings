// (c) https://github.com/MontiCore/monticore
package fireAlarm;

import fireAlarm.sensors.*;
import fireAlarm.actuator.*;
import fireAlarm.contr.FireExtinguisherContr;

/* Test component FireAlarm */
application FireAlarm {

  /* Subcomponents */
  
  Alarm alarm;
  //Alarm alarm2;
  Sprinkler sprinkler;
  FireExtinguisherContr fireExt;
  SmokeSensor smokeSensor;
  TemperatureSensor tempSensor;
  

  /* Connections */
  
  fireExt.alarmOn -> alarm.onn;
  fireExt.sprinklerOn -> sprinkler.onn;
  smokeSensor.value -> fireExt.smoke;
  tempSensor.value -> fireExt.temp;
  
}
