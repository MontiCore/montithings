// (c) https://github.com/MontiCore/monticore
package cocoTest.portsInBehaviorAreUsedCorrectly.invalid;

component PreviousBehaviorPortsAreSubset {
  port in int in1, in2;
  port out int result;

  behavior in1 {
    log "$in1";
  }

  behavior in1, in2 {
    result = in1 + in2;
  }
}
