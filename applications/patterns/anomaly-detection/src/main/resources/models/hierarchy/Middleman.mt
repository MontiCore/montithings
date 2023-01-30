// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Middleman {
  port in int input1;
  port in int input2;
  port out int output;

  int runningIndex = 0;

  behavior {
    runningIndex++;

    log("Middleman Input 1: " + input1);
    log("Middleman Input 2: " + input2);
    
    if (runningIndex == 3) {
      // Manually produce anomaly
      output = 10000;
    } else {
      output = input1 + input2;
    }
  }
}