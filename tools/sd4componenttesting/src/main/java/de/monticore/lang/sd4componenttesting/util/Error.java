// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting.util;

import java.util.regex.Pattern;

/**
 * This is the mixin interface for all sd4componenttesting errors. This interface serves as an extension
 * point to add new error codes in submodules of the MontiArc framework.
 */
public interface Error {

  static Pattern ERROR_CODE_PATTERN = Pattern.compile("0xSD4CPT(\\d{4})(\\d{4})*");

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
