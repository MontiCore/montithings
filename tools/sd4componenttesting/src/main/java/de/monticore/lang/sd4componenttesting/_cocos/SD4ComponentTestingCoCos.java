// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

public class SD4ComponentTestingCoCos {
  public static SD4ComponentTestingCoCoChecker createChecker() {
    final SD4ComponentTestingCoCoChecker checker = new SD4ComponentTestingCoCoChecker();
    checker.addCoCo(new MainComponentExists());
    checker.addCoCo(new PortAccessValid());
    checker.addCoCo(new SD4CConnectionValid());
    checker.addCoCo(new SD4CConnectionMainOutputValid());
    checker.addCoCo(new SD4CConnectionMainInputValid());
    checker.addCoCo(new SD4CConnectionConnectorValid());
    return checker;
  }
}
