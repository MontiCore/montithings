// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Three {
  port in int input;

  statechart {
    initial state Dividable ;
    state NotDividable ;

    Dividable -> NotDividable [input % 3 != 0] ;
    NotDividable -> Dividable [input % 3 == 0] / { log("Three: " + input); };
  }
}