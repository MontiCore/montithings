// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.distribution.suggestion.Suggestion;
import montithings.services.iot_manager.server.exception.DeploymentException;
import montithings.services.iot_manager.server.exception.DistributionException;
import montithings.services.iot_manager.server.util.InstanceNameResolver;
import org.jpl7.*;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class DefaultDistributionCalculator implements IDistributionCalculator {
  
  private static final String PROLOG_VAR_DROPPEDCONSTRAINTS = "DroppedConstraints";
  private static final String PROLOG_VAR_DEPENDENCIES = "Dependencies";
  
  private String plFacts, plQuery;
  private File fileFacts, fileQuery, workingDir;
  
  /**
   * @param plFacts facts as Prolog-source
   * @param plQuery query as Prolog-source
   * @param workingDir The (temporary) working directory.
   */
  public DefaultDistributionCalculator(String plFacts, String plQuery, File workingDir) {
    this.plFacts = plFacts;
    this.plQuery = plQuery;
    this.workingDir = workingDir.getAbsoluteFile();
    this.fileFacts = new File(this.workingDir, "facts.pl");
    this.fileQuery = new File(this.workingDir, "query.pl");
  }
  
  private Distribution computeDistributionSync(DistributionCalcRequest param) throws DistributionException {
    Collection<DeployClient> deployTargets = param.getDeployTargets();
    List<String> components = param.getComponents();
    try {
      if(deployTargets.size() == 0) {
        throw new DeploymentException("no clients for deployment available");
      }
      prepareWorkspace();
      
      // change working directory
      new Query(new Compound("working_directory", wrap(new Variable(), new Atom(workingDir.getAbsolutePath())))).oneSolution();
      // load Prolog files
      new Query(new Compound("consult", wrap(new Atom("query.pl")))).oneSolution();
      
      DistributionQueryType queryType = DistributionQueryType.DISTRIBUTION;
      if(param.getReferenceDistribution() != null) {
        // use the given distribution as reference
        queryType = DistributionQueryType.DISTRIBUTION_PERSIST;
      }
      
      // compute distribution solution
      Query query = new Query(this.constructQueryTerm(components, queryType));
      Map<String, Term> solution = query.oneSolution();
      Distribution distribution = null;
      
      // parse solution
      if (solution != null) {
        InstanceNameResolver nameResolver = new InstanceNameResolver(components);
        
        // parse dependencies
        List<HierarchyResolver.Dependency> dependencies = new LinkedList<>();
        for(Term depends : Util.listToTermArray(solution.get(PROLOG_VAR_DEPENDENCIES))) {
          // construct dependency from prolog term
          Term bind1 = depends.arg(1);
          Term bind2 = depends.arg(2);
          HierarchyResolver.Assignment assignment1 = new HierarchyResolver.Assignment(
              bind1.arg(1).name(), // clientID
              nameResolver.resolveFromPrologName(bind1.arg(2).name()) // instance name
          );
          HierarchyResolver.Assignment assignment2 = new HierarchyResolver.Assignment(
              bind2.arg(1).name(), // clientID
              nameResolver.resolveFromPrologName(bind2.arg(2).name()) // instance name
          );
          dependencies.add(new HierarchyResolver.Dependency(assignment1, assignment2));
        }
        solution.remove(PROLOG_VAR_DEPENDENCIES);
        
        // parse distribution
        Map<String, List<String>> dmap = new HashMap<>();
        
        // initialize empty list for every target 
        for(DeployClient client : deployTargets) {
          dmap.put(client.getClientID(), new ArrayList<>());
        }
        
        for (Entry<String, Term> e : solution.entrySet()) {
          String instanceName = e.getKey();
          String[] clients = Util.atomListToStringArray(e.getValue());
          
          for (String clientID : clients) {
            List<String> instances = dmap.get(clientID);
            instances.add(instanceName);
          }
        }
        
        distribution = Distribution.from(dmap);
        distribution = new HierarchyResolver(distribution, dependencies).resolve();
      }
      
      cleanup();
      return distribution;
    }
    catch (Exception e) {
      e.printStackTrace();
      // wrap in DistributionException for CompletableFuture-compatibility
      throw new DistributionException(e);
    }
  }
  
  private Map<Distribution, List<Suggestion>> computeDistributionSuggestionSync(DistributionSuggestionRequest request) throws DistributionException {
    Collection<DeployClient> deployTargets = request.getTargets();
    List<String> components = request.getComponents();
    try {
      prepareWorkspace();
      
      // change working directory
      new Query(new Compound("working_directory", wrap(new Variable(), new Atom(workingDir.getAbsolutePath())))).oneSolution();
      // load Prolog files
      new Query(new Compound("consult", wrap(new Atom("query.pl")))).oneSolution();
      
      // compute distribution solutions
      Query query = new Query(this.constructQueryTerm(components, DistributionQueryType.SUGGESTIONS));
      Map<Distribution, List<Suggestion>> results = new LinkedHashMap<>();
      
      int index = 0;
      int count = 0;
      
      List<Set<Suggestion>> lastSuggestions = new LinkedList<>();
      
      while(hasMoreSolutions(query) && count < request.getMaxCount()) {
        Map<String,Term> solution = query.nextSolution();
        
        index++;
        
        // parse solution
        if (solution != null) {
          // extract DroppedConstraints from solution
          String[] droppedConstraints = Util.atomListToStringArray(solution.get(PROLOG_VAR_DROPPEDCONSTRAINTS));
          List<Suggestion> suggestions = new ArrayList<Suggestion>(droppedConstraints.length);
          Lists.newArrayList(droppedConstraints).stream()
            .map((c)->Suggestion.parseProlog(c, request.getComponents()))
            .forEach(suggestions::add);
          
          // check, if there already is a super-set of suggestions
          // that have been made
          boolean foundSuperset = false;
          for(Set<Suggestion> prevSuggestion : lastSuggestions) {
            if(suggestions.containsAll(prevSuggestion)) {
              index--;
              foundSuperset = true;
              break;
            }
          }
          if(foundSuperset) {
            // if these suggestions are a super-set of previously made
            // suggestions, we do not want them.
            continue;
          }
          lastSuggestions.add(new HashSet<>(suggestions));
          
          if(index <= request.getOffset()) {
            // Ignore this suggestion as it should be skipped.
            continue;
          }
          
          solution.remove(PROLOG_VAR_DROPPEDCONSTRAINTS);
          solution.remove(PROLOG_VAR_DEPENDENCIES);
          
          Map<String, List<String>> dmap = new HashMap<>();
          
          // initialize empty list for every target
          for(DeployClient client : deployTargets) {
            dmap.put(client.getClientID(), new ArrayList<>());
          }
          
          for (Entry<String, Term> e : solution.entrySet()) {
            String instanceName = e.getKey();
            String[] clients = Util.atomListToStringArray(e.getValue());
            if(clients == null) clients = new String[0];
            
            for (String clientID : clients) {
              List<String> instances = dmap.get(clientID);
              if(instances == null) {
                // this may be the case for dummy clients (hardware suggestions)
                instances = new LinkedList<String>();
                dmap.put(clientID, instances);
              }
              instances.add(instanceName);
            }
          }
          
          Distribution distribution = Distribution.from(dmap);
          results.put(distribution, suggestions);
        }
        
        count++;
      }
      
      cleanup();
      return results;
    }
    catch (Exception e) {
      // wrap in DistributionException for CompletableFuture-compatibility
      throw new DistributionException(e);
    }
  }
  
  private Term constructQueryTerm(List<String> components, DistributionQueryType type) {
    return this.constructQueryTerm(components, type, true, null);
  }
  
  private Term constructQueryTerm(List<String> components, DistributionQueryType type, boolean distinct, @Nullable Distribution reference) {
    LinkedList<Term> terms = new LinkedList<>();
    components.stream()
      .map((str) -> new Variable(str))
      .forEach(terms::add);
    
    if(type == DistributionQueryType.DISTRIBUTION_PERSIST) {
      if(reference == null) {
        // If we do not have a reference, we still have to add the expected
        // terms to fit the query scheme.
        reference = new Distribution(new HashMap<>());
      }
      
      // try to persist reference distribution
      // reconstruct prolog lists from distribution
      Map<String, List<String>> cmap = new HashMap<String, List<String>>();
      for(String comp : components) {
        cmap.put(comp, new LinkedList<>());
      }
      for(Entry<String, String[]> e : reference.getDistributionMap().entrySet()) {
        for(String comp : e.getValue()) {
          List<String> clientList = cmap.get(comp);
          if(clientList != null) {
            clientList.add(e.getKey());
          }
        }
      }
      // add reference component list terms to query
      for(String comp : components) {
        List<String> clients = cmap.get(comp);
        terms.add(Util.stringArrayToList(clients.toArray(new String[clients.size()])));
      }
    }
    
    // add variable for constraint output
    Variable varDroppedConstraints = new Variable(PROLOG_VAR_DROPPEDCONSTRAINTS);
    if(type.withDroppedConstraints) {
      terms.add(varDroppedConstraints);
    }
    terms.add(new Variable(PROLOG_VAR_DEPENDENCIES));
    
    // select proper goal
    String goalName = type.prologName;
    Compound goal = new Compound(goalName, terms.toArray(new Term[terms.size()]));
    if(distinct) {
      if(type.withDroppedConstraints) {
        // find distinct solutions only regarding suggestions
        goal = new Compound("distinct", new Term[]{varDroppedConstraints, goal});        
      } else {
        goal = new Compound("distinct", new Term[]{goal});
      }
    }
    return goal;
  }
  
  private void prepareWorkspace() throws IOException {
    // Ensure the working directory exists.
    this.workingDir.mkdirs();
    
    Files.write(plFacts, fileFacts, StandardCharsets.UTF_8);
    Files.write(plQuery, fileQuery, StandardCharsets.UTF_8);
    
    // Write helpers.pl prolog file.
    File fileHelpers = new File(workingDir, "helpers.pl");
    try (InputStream inputHelpers = getClass().getResourceAsStream("/scripts/helpers.pl")) {
      try (OutputStream outputHelpers = new FileOutputStream(fileHelpers)) {
        ByteStreams.copy(inputHelpers, outputHelpers);
      }
    }
    
    // Retract facts from previous queries.
    new Query(new Compound("retract", wrap(new Atom("property")))).oneSolution();
    new Query(new Compound("retract", wrap(new Atom("distribution")))).oneSolution();
  }
  
  private void cleanup() throws IOException {
    if (this.fileFacts != null) {
      //this.fileFacts.delete();
    }
    if (this.fileQuery != null) {
      //this.fileQuery.delete();
    }
  }
  
  @Override
  public CompletableFuture<Distribution> computeDistribution(DistributionCalcRequest request) {
    return CompletableFuture.supplyAsync(() -> request).thenApply(this::computeDistributionSync);
  }
  
  @Override
  public CompletableFuture<Map<Distribution, List<Suggestion>>> computeDistributionSuggestion(DistributionSuggestionRequest request) {
    return CompletableFuture.supplyAsync(() -> request).thenApply(this::computeDistributionSuggestionSync);
  }
  
  private static Term[] wrap(Term... terms) {
    return terms;
  }
  
  /**
   * Calls {@code query.hasMoreSolutions()}. In case this call throws a PrologException, false is returned.
   * */
  private static boolean hasMoreSolutions(Query query) {
    try {
      return query.hasMoreSolutions();
    } catch(PrologException e) {
      return false;
    }
  }
  
  private static enum DistributionQueryType {
    DISTRIBUTION("distribution", false),
    DISTRIBUTION_PERSIST("distribution_persist", false),
    SUGGESTIONS("distribution_suggest", true);
    
    public final String prologName;
    public final boolean withDroppedConstraints;
    
    private DistributionQueryType(String prologName, boolean withDroppedConstraints) {
      this.prologName = prologName;
      this.withDroppedConstraints = withDroppedConstraints;
    }
  }
  
}
