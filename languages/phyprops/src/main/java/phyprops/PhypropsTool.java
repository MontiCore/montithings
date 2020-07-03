// (c) https://github.com/MontiCore/monticore
package phyprops;

import phyprops._symboltable.PhypropsLanguage;

/**
 * Provides useful methods for handling the Phyprops language.
 *
 */
public class PhypropsTool {

  protected PhypropsLanguage language;

  public PhypropsTool() {
    this(new PhypropsLanguage());
  }

  public PhypropsTool(PhypropsLanguage language) {
    this.language = language;
  }
}
