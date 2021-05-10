// (c) https://github.com/MontiCore/monticore
package cocoTest.portsInBehaviorAreUsedCorrectly.invalid;

component InvalidIncomingPortInBehavior {
  port in int in1, in2;
  port out int result;

  behavior in1 {
    result = in1 + in2;
  }

  behavior in2 {
    result = in1 + in2;
  }
}
