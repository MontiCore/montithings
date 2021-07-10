// (c) https://github.com/MontiCore/monticore
package logFilteringApp;

component Sum {
  port in int in1;
  port in int in2;
  sync in1, in2;

  port out int result;

  int res = 0;

  behavior {
    log("Going to compute the sum of both numbers...");
    res = in1 + in2;
    log("Computation completed, passing result to outgoing port.");
    result = res;
  }
}