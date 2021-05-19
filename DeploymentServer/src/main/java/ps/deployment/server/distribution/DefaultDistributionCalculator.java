package ps.deployment.server.distribution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;
import org.jpl7.Util;
import org.jpl7.Variable;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.exception.DistributionException;

public class DefaultDistributionCalculator implements IDistributionCalculator {
  
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
    this.fileFacts = new File(workingDir, "facts.pl");
    this.fileQuery = new File(workingDir, "query.pl");
    this.workingDir = workingDir;
  }
  
  private Distribution computeDistributionSync(List<String> components) throws DistributionException {
    try {
      prepareWorkspace();
      
      // change working directory
      new Query(new Compound("working_directory", wrap(new Variable(), new Atom(workingDir.getAbsolutePath())))).oneSolution();
      // load Prolog files
      new Query(new Compound("consult", wrap(new Atom("query.pl")))).oneSolution();
      
      // compute distribution solution
      Query query = new Query(this.constructQueryTerm(components));
      Map<String, Term> solution = query.oneSolution();
      Distribution distribution = null;
      
      // parse solution
      if (solution != null) {
        Map<String, List<String>> dmap = new HashMap<>();
        
        for (Entry<String, Term> e : solution.entrySet()) {
          String instanceName = e.getKey();
          String[] clients = Util.atomListToStringArray(e.getValue());
          
          for (String clientID : clients) {
            List<String> instances = dmap.get(clientID);
            if (instances == null) {
              instances = new ArrayList<>();
              dmap.put(clientID, instances);
            }
            instances.add(instanceName);
          }
        }
        
        distribution = Distribution.from(dmap);
      }
      
      cleanup();
      return distribution;
    }
    catch (Exception e) {
      // wrap in DistributionException for CompletableFuture-compatibility
      throw new DistributionException(e);
    }
  }
  
  private Term constructQueryTerm(List<String> components) {
    Variable[] vars = components.stream().map((str) -> new Variable(str)).toArray((l) -> new Variable[l]);
    return new Compound("distribution", vars);
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
  }
  
  private void cleanup() throws IOException {
    if (this.fileFacts != null) {
      this.fileFacts.delete();
    }
    if (this.fileQuery != null) {
      this.fileQuery.delete();
    }
  }
  
  @Override
  public CompletableFuture<Distribution> computeDistribution(Collection<DeployClient> targets, List<String> components) {
    return CompletableFuture.supplyAsync(() -> components).thenApply(this::computeDistributionSync);
  }
  
  private static Term[] wrap(Term... terms) {
    return terms;
  }
  
}
