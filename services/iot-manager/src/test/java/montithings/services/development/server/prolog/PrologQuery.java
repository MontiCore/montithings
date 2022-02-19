// (c) https://github.com/MontiCore/monticore
package montithings.services.development.server.prolog;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class PrologQuery implements AutoCloseable, Iterable<Map<String,String>> {
  
  private final Prolog prolog;
  private final String term;
  private QueryIterator iterator = null;
  
  private boolean started = false;
  
  public PrologQuery(Prolog prolog, String term) {
    this.prolog = prolog;
    this.term = term;
  }

  @Override
  public void close() throws Exception {
    if(this.iterator != null) {
      if(this.iterator.mayFetchAgain) {
        this.iterator.mayFetchAgain = false;
        this.iterator.hasNext = false;
        this.prolog.sendStop();
      }
    }
  }
  
  /**may only be called once*/
  @Override
  public Iterator<Map<String, String>> iterator() {
    if(started) {
      throw new RuntimeException("Cannot call iterator() twice on PrologQuery!");
    }
    this.iterator = new QueryIterator();
    return this.iterator;
  }
  
  private class QueryIterator implements Iterator<Map<String,String>> {
    
    private boolean mayFetchAgain = true;
    private boolean hasNext = true;
    private Map<String, String> next = null;
    
    public QueryIterator() {
      started = true;
      try {
        prolog.startQuery(term);
        this.prefetch();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    private void prefetch() {
      try {
        ImmutablePair<Map<String,String>, Boolean> res = prolog.readAssignment();
        this.hasNext = res.left != null;
        this.next = res.left;
        this.mayFetchAgain = res.right;
      } catch(IOException e) {
        e.printStackTrace();
        this.hasNext = false;
      }
    }

    @Override
    public boolean hasNext() {
      return this.hasNext;
    }

    @Override
    public Map<String, String> next() throws NoSuchElementException {
      if(!hasNext) throw new NoSuchElementException();
      Map<String, String> prevNext = next;
      if(mayFetchAgain) {
        try {
          prolog.sendOr();
          prefetch();
        }
        catch (IOException e) {
          this.hasNext = false;
          this.mayFetchAgain = false;
        }
      }
      return prevNext;
    }
    
  }
  
}
