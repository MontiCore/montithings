// (c) https://github.com/MontiCore/monticore
package network.cocos;

import network._ast.ASTNet;
import network._cocos.NetworkCoCoChecker;

/**
 * Wrapper for test input data
 *
 * @author (last commit) kirchhof
 * @version 1.0, 01.03.2019
 * @since 1.0
 */
public class CocoTestInput {

  /** The AST to execute the test on */
  private ASTNet ast;

  /** The checker that executes the CoCo (i.e. the system under test) */
  private NetworkCoCoChecker checker;

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public CocoTestInput(ASTNet ast, NetworkCoCoChecker checker) {
    this.ast = ast;
    this.checker = checker;
  }

  public ASTNet getAst() {
    return ast;
  }

  public NetworkCoCoChecker getChecker() {
    return checker;
  }
}
