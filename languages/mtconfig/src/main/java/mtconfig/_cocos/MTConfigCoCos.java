// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

/**
 * Context Conditions for MTConfig language
 *
 * @author (last commit) Julian Krebber
 */
public class MTConfigCoCos {
  public static MTConfigCoCoChecker createChecker() {
    final MTConfigCoCoChecker mtConfigCoCoChecker = new MTConfigCoCoChecker()
      .addCoCo(new AllConfigsReferToTheSameComponent())
      .addCoCo(new HookpointExists())
      .addCoCo(new CompConfigExists())
      .addCoCo(new PortTemplateTagExists())
      .addCoCo(new MqttHasNoArguments());
    return mtConfigCoCoChecker;
  }
}
