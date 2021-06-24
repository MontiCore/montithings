// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

public class SD4ComponentTestingCoCos {
  public static SD4ComponentTestingCoCoChecker createChecker() {
    final SD4ComponentTestingCoCoChecker checker = new SD4ComponentTestingCoCoChecker();
        // Coco 1: Assert that mainComponent exists
    //checker.addCoCo(new MainComponentExists());
    checker
        // Coco 1: Assert that all Component Instances exists
        .addCoCo(new ComponentInstanceExists());

    //TODO warum wird der checker nicht in addCoCo returned
    return checker;
  }
}
