// (c) https://github.com/MontiCore/monticore
package cocoTest.portsInBehaviorAreUsedCorrectly.invalid;

component NotAllIncomingPortsInBehaviors {
  port in int in1, in2;
  port out int result;
  behavior in1 {
    log "$in1";
  }
}