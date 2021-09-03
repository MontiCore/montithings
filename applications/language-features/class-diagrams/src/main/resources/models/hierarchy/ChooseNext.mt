// (c) https://github.com/MontiCore/monticore
package hierarchy;

import Colors.*;

component ChooseNext {
  port in Color input;
  port out Color output;

  behavior input {
    switch (input) {
      case RED:    output = GREEN;  break;
      case GREEN:  output = BLUE;   break;
      case BLUE:   output = YELLOW; break;
      case YELLOW: output = RED;    break;
    }
  }
}