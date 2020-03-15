// (c) https://github.com/MontiCore/monticore
package fireAlarm.contr;

/* Component for alarm*/
component FireExtinguisherContr {

  port
  	in  Boolean smoke,
  	in  Integer temp,
    out Boolean alarmOn,
    out Boolean sprinklerOn;
    
}
