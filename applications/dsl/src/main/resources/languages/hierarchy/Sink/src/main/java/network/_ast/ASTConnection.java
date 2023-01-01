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
public class ASTConnection extends ASTConnectionTOP {

  protected  ASTConnection () {
    // empty body
  }

  protected  ASTConnection (String knoten1, String knoten2) {
    setKnoten1(knoten1);
    setKnoten2(knoten2);
  }

  /**
   * Returns the name of the node.
   * @return name of node
   */
  public String getKnoten1Name() {
    return getKnoten1();
  }
  public String getKnoten2Name() {
    return getKnoten2();
  }
}
