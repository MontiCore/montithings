// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

/**
 * Context Conditions for MTConfig language
 *
 * @author (last commit) Julian Krebber
 */
public class MTConfigCoCos {
  public static MTConfigCoCoChecker createChecker() {
    final MTConfigCoCoChecker checker = new MTConfigCoCoChecker();
    checker.addCoCo(new AllConfigsReferToTheSameComponent());
    checker.addCoCo(new HookpointExists());
    checker.addCoCo(new CompConfigExists());
    checker.addCoCo(new PortTemplateTagExists());
    checker.addCoCo(new OnlyOneEveryPerPort());
    checker.addCoCo(new MqttHasNoArguments());
    return checker;
  }
}
