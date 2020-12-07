// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

/**
 * Context Conditions for MTConfig language
 *
 * @author (last commit) Julian Krebber
 */
public class MTConfigCoCos {
  public static MTConfigCoCoChecker createChecker() {
    final MTConfigCoCoChecker checker = new MTConfigCoCoChecker()
        .addCoCo(new RequirementNameExists())
        .addCoCo(new CompConfigExists())
        .addCoCo(new PortTemplateTagExists());
    return checker;
  }
}
