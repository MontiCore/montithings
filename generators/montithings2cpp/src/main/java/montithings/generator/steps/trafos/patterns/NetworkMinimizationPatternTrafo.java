package montithings.generator.steps.trafos.patterns;

import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montiarc._ast.ASTMACompilationUnit;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class NetworkMinimizationPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
  private static final String TOOL_NAME = "NetworkMinimizationPatternTrafo";

  @Override
  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                    Collection<ASTMACompilationUnit> addedModels,
                                                    ASTMACompilationUnit targetComp) throws Exception {
    Log.info("Apply transformation to: " + targetComp.getComponentType().getName(), TOOL_NAME);

    Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

    Log.info("Return " + additionalTrafoModels.size() + " additional trafo models", TOOL_NAME);
    return additionalTrafoModels;
  }
}
