// (c) https://github.com/MontiCore/monticore
package montithings.services.development.server.prolog;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Prolog {
  
  public static void main(String[] args) throws Exception {
    
    Prolog prolog = new Prolog();
    System.out.println(prolog.consult(new File("helpers.pl")));
    System.out.println(prolog.consult(new File("facts.pl")));
    System.out.println(prolog.consult(new File("query.pl")));
    
    PrologQuery query = prolog.query("distribution(X,Y).");
    int count = 0;
    for (Map<String, String> ass : query) {
      System.out.println(ass);
      if(count++ > 10) query.close();
    }
    
  }
  
  private static void transcribe(InputStream is) {
    new Thread(() -> {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }
  
  private final Process proc;
  private final BufferedReader reader;
  private final BufferedWriter writer;
  
  public Prolog() throws IOException {
    this.proc = new ProcessBuilder("swipl", "--quiet").start();
    this.reader = new BufferedReader(new InputStreamReader(this.proc.getInputStream()));
    this.writer = new BufferedWriter(new OutputStreamWriter(this.proc.getOutputStream()));
    
    // kill Prolog on exit
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      this.proc.destroyForcibly();
    }));
  }
  
  public boolean consult(File file) throws IOException {
    this.writer.write("consult(\""+file.getAbsolutePath()+"\").\r\n");
    this.writer.flush();
    return readBool();
  }
  
  public boolean assertz(String term) throws IOException {
    this.writer.write("assert(" + term + ").\r\n");
    this.writer.flush();
    return readBool();
  }
  
  private boolean readBool() throws IOException {
    String line;
    while ((line = this.reader.readLine()).isEmpty())
      ;
    return line.equals("true.");
  }
  
  protected void sendOr() throws IOException {
    writer.write(";\r\n");
    writer.flush();
  }
  
  protected void sendStop() throws IOException {
    writer.write(".\r\n");
    writer.flush();
  }
  
  public PrologQuery query(String term) throws IOException {
    return new PrologQuery(this, term);
  }
  
  protected void startQuery(String term) throws IOException {
    writer.write(term + "\r\n");
    writer.flush();
  }
  
  protected ImmutablePair<Map<String, String>, Boolean> readAssignment() throws IOException {
    HashMap<String, String> assignments = new HashMap<>();
    boolean moreVariables = true;
    boolean moreAssignments = true;
    while (moreVariables) {
      boolean nonAssignment = false;
      moreVariables = false;
      char read;
      StringBuilder var = new StringBuilder();
      while ((read = (char) reader.read()) > 0) {
        if (read == ' ') {
          break;
        }
        else if (read == '.') {
          moreVariables = false;
          moreAssignments = false;
        }
        else if (read == ',') {
          moreVariables = true;
          break;
        }
        else if (read == '\n') {
          continue;
        }
        else {
          var.append(read);
        }
      }
      
      if (reader.read() != '=') {
        // unexpected
        moreAssignments = false;
        break;
      }
      reader.skip(1); // skip whitespace after equals sign
      
      // read value of variable assignment
      StringBuilder value = new StringBuilder();
      while ((read = (char) reader.read()) > 0) {
        if (read == ' ') {
          // this is the last variable of this assignment
          moreAssignments = true;
          break;
        }
        else if (read == '.') {
          moreVariables = false;
        }
        else if (read == ',') {
          moreVariables = true;
        }
        else if (read == '\n') {
          break;
        }
        else {
          value.append(read);
        }
      }
      
      assignments.put(var.toString(), value.toString());
    }
    
    return new ImmutablePair<>(assignments, moreAssignments);
  }
  
}
