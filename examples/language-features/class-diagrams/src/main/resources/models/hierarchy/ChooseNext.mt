// (c) https://github.com/MontiCore/monticore
package hierarchy;

import Colors.*;

component ChooseNext {
  port in Colors.Color input;
  port out Colors.Color output;

  behavior input {
    switch (input) {
      case Colors.Color.RED:    output = Colors.Color.GREEN;  break;
      case Colors.Color.GREEN:  output = Colors.Color.BLUE;   break;
      case Colors.Color.BLUE:   output = Colors.Color.YELLOW; break;
      case Colors.Color.YELLOW: output = Colors.Color.RED;    break;
    }
  }
}