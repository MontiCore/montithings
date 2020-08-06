// (c) https://github.com/MontiCore/monticore
package phyprops._cocos;

/**
 * Context Conditions for Phyprops language
 *
 * @author (last commit) Julian Krebber
 */
public class PhypropsCoCos {
  public static PhypropsCoCoChecker createChecker() {
    final PhypropsCoCoChecker checker = new PhypropsCoCoChecker()
        .addCoCo(new RequirementNameExists());
    return checker;
  }
}
