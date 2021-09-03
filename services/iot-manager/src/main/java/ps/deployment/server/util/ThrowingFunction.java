package ps.deployment.server.util;

public interface ThrowingFunction<P,R,T extends Throwable> {
  public R apply(P value) throws T;
}
