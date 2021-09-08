// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.exception;

public class DistributionException extends RuntimeException {
  
  private static final long serialVersionUID = -1797437936053829983L;
  
  public DistributionException() {
    super();
  }
  
  public DistributionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
  
  public DistributionException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public DistributionException(String message) {
    super(message);
  }
  
  public DistributionException(Throwable cause) {
    super(cause);
  }
  
}
