// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint.processor;

import montithings.services.iot_manager.server.data.DeploymentConfiguration;

public class ConstraintPipeline implements ConstraintProcessor {
  
  public static final ConstraintPipeline DEFAULT_PIPELINE = new ConstraintPipeline(
      new BasicConstraintAnyWildcardProcessor(),
      new BasicConstraintEveryWildcardProcessor(),
      new BasicConstraintInstanceWildcardProcessor()
  );
  
  private final ConstraintProcessor[] processors;
  
  public ConstraintPipeline(ConstraintProcessor... processors) {
    this.processors = processors;
  }
  
  @Override
  public void apply(ConstraintContext context, DeploymentConfiguration target) {
    for (ConstraintProcessor processor : processors) {
      processor.apply(context, target);
    }
  }
  
  @Override
  public void clean(ConstraintContext context, DeploymentConfiguration target) {
    // iterate processors backwards to treat it like a stack (LIFO).
    for (int i = processors.length - 1; i >= 0; i--) {
      ConstraintProcessor processor = processors[i];
      processor.clean(context, target);
    }
  }
  
}
