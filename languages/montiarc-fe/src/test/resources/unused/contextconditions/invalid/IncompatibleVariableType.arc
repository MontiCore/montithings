package contextconditions.invalid;

import java.util.HashMap;

// TODO: Unused?
component IncompatibleVariableType {

  port in String s;

  HashMap<String, Integer> x;
  
  init {
    x = new HashMap<String, Integer>();
  }
  
}