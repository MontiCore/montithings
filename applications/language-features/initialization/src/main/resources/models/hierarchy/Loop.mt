// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Loop {
  port in int input;
  port out int output;

  init {
    output = 0;
  }

  init input {
    log("First Input: " + input);
    after 1s {
      output = input + 1;
    }
  }

  behavior input {
    log("Input: " + input);
    after 1s {
      output = input + 1;
    }
  }
}
