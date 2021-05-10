// (c) https://github.com/MontiCore/monticore
package cocoTest.portsInBehaviorAreUsedCorrectly.invalid;

component OutgoingPortInBehaviorSpecification {
  port in int in1, in2;
  port out int result;

  behavior in1, in2, result {
    result = in1 + in2;
  }
}
