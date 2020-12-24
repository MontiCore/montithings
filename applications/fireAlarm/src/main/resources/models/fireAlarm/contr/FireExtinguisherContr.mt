// (c) https://github.com/MontiCore/monticore
package fireAlarm.contr;

component FireExtinguisherContr {

  port
  	in  Boolean smoke,
  	in  int temp,
    out Boolean alarmOn,
    out Boolean sprinklerOn;
    
}
