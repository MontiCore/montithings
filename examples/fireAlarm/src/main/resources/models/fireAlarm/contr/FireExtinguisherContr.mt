// (c) https://github.com/MontiCore/monticore
package fireAlarm.contr;

component FireExtinguisherContr {

  port
  	in  boolean smoke,
  	in  int temp,
    out boolean alarmOn,
    out boolean sprinklerOn;
    
}
