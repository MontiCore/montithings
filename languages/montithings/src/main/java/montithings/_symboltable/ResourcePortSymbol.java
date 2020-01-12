// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.SymbolPrinter;
import montithings._ast.ASTResourceParameter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO
 *
 * @author (last commit) Joshua Fürste
 */

public class ResourcePortSymbol extends ResourcePortSymbolTOP {

  private final Map<String, Optional<String>> stereotype = new HashMap<>();

  public ResourcePortSymbol(String name) {
    super(name);
  }

  /**
   * Maps direction incoming to true.
   */
  public static final boolean INCOMING = true;

  private String uri;

  /**
   * Store information about the used protocol
   */
  private boolean isIpc = false;
  private boolean isWS = false;
  private boolean isTcp = false;
  private boolean isFileSystem = false;
  private List<ASTResourceParameter> resourceParameters = new java.util.ArrayList<>();

  public boolean isIpc() {
    return isIpc;
  }

  public boolean isWebSocket() {
    return isWS;
  }

  public boolean isTcp() {
    return isTcp;
  }

  public boolean isFileSystem() {
    return isFileSystem;
  }

  public void setUri(String uri){
    this.uri = uri;
  }

  public String getUri(){
    return uri;
  }

  public void setResourceParameters(List<ASTResourceParameter> resourceParameters){
    this.resourceParameters = resourceParameters;
  }
  
  public List<ASTResourceParameter> getResourceParameters(){
    return resourceParameters;
  }


  public void setProtocol(String protocol) {
    switch (protocol) {
      case "tcp":
        isTcp = true;
        break;
      case "ipc":
        isIpc = true;
        break;
      case "ws":
        isWS = true;
        break;
      default:
        isFileSystem = true;
        break;
    }
  }

  /**
   * Indicates whether the port is incoming.
   */
  private boolean incoming;

  private JTypeReference<? extends JTypeSymbol> typeReference;

  /**
   * Setter for the direction of the port.
   *
   * @param isIncoming The direction of the port. If true, the port is incoming,
   *                   otherwise, it is outgoing.
   */
  public void setDirection(boolean isIncoming) {
    incoming = isIncoming;
  }

  /**
   * Indicates whether the port is incoming.
   *
   * @return true, if this is an incoming port, else false.
   */
  public boolean isIncoming() {
    return incoming;
  }

  /**
   * Indicates whether the port is outgoing.
   *
   * @return true, if this is an outgoing port, else false.
   */
  public boolean isOutgoing() {
    return !isIncoming();
  }

  /**
   * Getter for the type reference.
   *
   * @return The typeReference reference to the type from this port
   */
  private JTypeReference<? extends JTypeSymbol> getTypeReference() {
    return this.typeReference;
  }

  /**
   * Setter for the type reference.
   *
   * @param typeReference The reference to the type from this port
   */
  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }

  /**
   * returns the component which defines the connector this is independent from the component to
   * which the source and target ports belong to
   *
   * @return is optional, b/c a connector can belong to a component symbol or to an expanded
   * component instance symbol
   */
  public Optional<ComponentSymbol> getComponent() {
    if (!this.getEnclosingScope().getSpanningSymbol().isPresent()) {
      return Optional.empty();
    }
    if (!(this.getEnclosingScope().getSpanningSymbol().get() instanceof ComponentSymbol)) {
      return Optional.empty();
    }
    return Optional.of((ComponentSymbol) this.getEnclosingScope().getSpanningSymbol().get());
  }

  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key      the stereotype's key
   * @param optional the stereotype's value
   */
  public void addStereotype(String key, Optional<String> optional) {
    stereotype.put(key, optional);
  }

  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key   the stereotype's key
   * @param value the stereotype's value
   */
  public void addStereotype(String key, @Nullable String value) {
    if (value != null && value.isEmpty()) {
      value = null;
    }
    stereotype.put(key, Optional.ofNullable(value));
  }

  /**
   * @return map representing the stereotype of this component
   */
  public Map<String, Optional<String>> getStereotype() {
    return stereotype;
  }

  @Override
  public String toString() {
    IndentPrinter ip = new IndentPrinter();
    if (this.isIncoming()) {
      ip.print("in ");
    } else {
      ip.print("out ");
    }
    ip.print(this.getTypeReference().getName());
    ip.print(SymbolPrinter.printTypeArguments(this.getTypeReference().getActualTypeArguments()));
    ip.print(" ");
    ip.print(this.getName());
    return ip.getContent();
  }


}
