// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Three {
  port in int input;
  port out int output;

  statechart {
    initial state NotDividable ;
    state Dividable ;

    Dividable -> NotDividable [input % 3 != 0] ;
    NotDividable -> Dividable [input % 3 == 0] / { output = input; };
  }
}