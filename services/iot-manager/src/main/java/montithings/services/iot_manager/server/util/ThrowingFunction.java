// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.util;

public interface ThrowingFunction<P,R,T extends Throwable> {
  public R apply(P value) throws T;
}
