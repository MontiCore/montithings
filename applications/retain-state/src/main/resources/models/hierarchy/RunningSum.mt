// (c) https://github.com/MontiCore/monticore
package hierarchy;

component RunningSum {
  port in int input;
  port out int value;

  int sum = 0;

  behavior {
  	sum = sum + input;
    value = sum;
  }

  retain state;
}
