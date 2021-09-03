// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.exception;

public class DeploymentException extends Exception {
  
  private static final long serialVersionUID = -2311162396307346179L;
  
  public DeploymentException() {
    super();
  }
  
  public DeploymentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
  
  public DeploymentException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public DeploymentException(String message) {
    super(message);
  }
  
  public DeploymentException(Throwable cause) {
    super(cause);
  }
  
}
