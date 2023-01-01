// (c) https://github.com/MontiCore/monticore
package network._ast;

import java.util.Optional;

/**
 * a single node of the network
 *
 * @author (welsing
 * @version 1.0
 * @since 1.0
 */
public class ASTKnoten extends ASTKnotenTOP {

  protected  ASTKnoten () {
    // empty body
  }

  protected  ASTKnoten (String knotenName) {
    setKnotenName(knotenName);
  }

  /**
   * Returns the name of the node.
   * @return name of node
   */
  public String getName() {
    return getKnotenName();
  }
}
