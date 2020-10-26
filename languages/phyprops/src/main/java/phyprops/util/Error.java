// (c) https://github.com/MontiCore/monticore
package phyprops.util;

import java.util.regex.Pattern;

/**
 * This is the mixin interface for all phyprops errors. This interface serves as an extension
 * point to add new error codes in submodules of the MontiArc framework.
 */
public interface Error {

  static Pattern ERROR_CODE_PATTERN = Pattern.compile("0xPHY(\\d{4})(\\d{4})*");

  /**
   * @return The unique error code of this error.
   */
  String getErrorCode();

  /**
   *
   * @return The error message of this error.
   */
  String printErrorMessage();
}