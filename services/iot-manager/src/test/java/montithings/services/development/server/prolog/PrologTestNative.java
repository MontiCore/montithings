// (c) https://github.com/MontiCore/monticore
package montithings.services.development.server.prolog;

import org.jpl7.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

public class PrologTestNative {
  public static void main(String[] args) {
    JPL.setDefaultInitArgs(new String[] {"--quiet"});
    System.out.println(JPL.init());
    
    System.out.println(Arrays.toString(JPL.getActualInitArgs()));
    
    // new Query(new Compound("consult", wrap(new Atom("helpers.pl")))).oneSolution();
    new Query(new Compound("consult", wrap(new Atom("facts.pl")))).oneSolution();
    new Query(new Compound("consult", wrap(new Atom("query.pl")))).oneSolution();
    
    System.out.println(new Query(new Compound("working_directory", wrap(new Variable(), new Atom(new File("./src").getAbsolutePath())))).oneSolution());;
    
    new Query(new Compound("assert", wrap(new Atom("test")))).allSolutions();
    new Query(new Compound("retractall", wrap(new Atom("test")))).allSolutions();
    
    Query query = new Query(new Compound("distribution", wrap(new Variable("X"), new Variable("Y"))));
    for(Map<String,Term> sol : query) {
      System.out.println("NEXT SOLUTION:");
      for(Entry<String, Term> e : sol.entrySet()) {
        System.out.print(e.getKey()+": ");
        Term term = e.getValue();
        if(Util.isList(term)) {
          String[] components = Util.atomListToStringArray(term);
          System.out.println(Arrays.toString(components));
        } else {
          System.err.println("Unexpected response from Prolog.");
        }
      }
    }
  }
  
  private static Term[] wrap(Term... terms) {
    return terms;
  }
}
