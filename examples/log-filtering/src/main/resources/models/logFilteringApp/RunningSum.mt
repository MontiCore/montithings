// (c) https://github.com/MontiCore/monticore
package logFilteringApp;

component RunningSum {
  port in int input;
  port out int value;

  int sum = 0;

  behavior {
    log("Going to add the new input to the sum...");
  	sum = sum + input;
    log("Computation completed, passing result to outgoing port.");
    value = sum;
  }
}
