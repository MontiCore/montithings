package montithings.generator.cd2proto;

/**
 * Used to indicate an error while parsing a model.
 */
public class ParseException extends Exception {
  /**
   * @see Exception#Exception(String)
   */
  public ParseException(String message) {
    super(message);
  }
}
