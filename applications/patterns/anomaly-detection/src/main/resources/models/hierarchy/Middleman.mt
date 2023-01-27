// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Middleman {
  port in int input1;
  port in int input2;
  port out int output;

  int index = 0;

  behavior {
    index++;

    log("Middleman Input 1: " + input1);
    log("Middleman Input 2: " + input2);
    
    if (index == 3) {
      // Manually produce anomaly
      output = 10000;
    } else {
      output = input1 + input2;
    }
  }
}