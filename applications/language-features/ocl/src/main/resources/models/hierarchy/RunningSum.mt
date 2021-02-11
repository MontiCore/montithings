// (c) https://github.com/MontiCore/monticore
package hierarchy;

component RunningSum {
  port in int in;
  port out int result;

  int cumulativeSum = 0;

  behavior {
    cumulativeSum += in;
    result = cumulativeSum;
  }

  post cumulativeSum == cumulativeSum@pre + in;
}