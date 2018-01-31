${tc.params(
	"de.montiarcautomaton.generator.helper.ComponentHelper helper", 
	"String _package", 
	"java.util.Collection<de.monticore.symboltable.ImportStatement> imports",
	"String name", 
	"String resultName", 
	"String inputName", 
	"String implName", 
	"java.util.Collection<montiarc._symboltable.VariableSymbol> variables", 
	"java.util.Collection<montiarc._symboltable.PortSymbol> portsIn", 
	"java.util.Collection<montiarc._symboltable.PortSymbol> portsOut",
	"java.util.Collection<de.monticore.symboltable.types.JFieldSymbol> configParams")}
package ${_package};

import ${_package}.${inputName};
import ${_package}.${resultName};
<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;
import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import de.montiarcautomaton.runtimes.Log;

public class ${name}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> implements IComponent {
  
  //component variables
  <#list variables as var>
    private ${helper.getVariableTypeName(var)} ${var.getName()};
  </#list>
  
  // config parameters
  <#list configParams as param>
  private final ${helper.getParamTypeName(param)} ${param.getName()};
  </#list>
  
  // port fields
  <#list portsIn as port>
  private Port<${helper.getPortTypeName(port)}> ${port.getName()};
  </#list>
  
  <#list portsOut as port>
  private Port<${helper.getPortTypeName(port)}> ${port.getName()};
  </#list>
  
  // port setter
  <#list portsIn as port>
  public void setPort${port.getName()?cap_first}(Port<${helper.getPortTypeName(port)}> port) {
  	this.${port.getName()} = port;
  }
  
  </#list>
  // port getter
  <#list portsOut as port>
  public Port<${helper.getPortTypeName(port)}> getPort${port.getName()?cap_first}() {
  	return this.${port.getName()};
  }
  
  </#list>
  
  // the components behavior implementation
  private final IComputable<${inputName}, ${resultName}> behaviorImpl;
  
  public ${name}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
    behaviorImpl = new ${implName}(<#list configParams as param>${param.getName()}<#sep>, </#list>);
  }
  
  @Override
  public void setUp() {
    // set up output ports
    <#list portsOut as port>
    this.${port.getName()} = new Port<${helper.getPortTypeName(port)}>();
    </#list>
    
    this.initialize();
  }

  @Override
  public void init() {  
  	// set up unused input ports
  	<#list portsIn as port>
  	if (this.${port.getName()} == null) {this.${port.getName()} = Port.EMPTY;}
  	</#list>
  	
  

  }
  
  private void setResult(${resultName}<#if helper.isGeneric()> < <#list helper.getGenericParameters() as param>${param}<#sep>,</#list> > </#if> result) {
  	<#list portsOut as port>
  	this.${port.getName()}.setNextValue(result.get${port.getName()?cap_first}());
  	</#list>
  }

  <#-- <#if behaviorEmbedding.isPresent()> -->
  @Override
  public void compute() {
    // collect current input port values
    final ${inputName} input = new ${inputName}(<#list portsIn as port>this.${port.getName()}.getCurrentValue()<#sep>, </#list>);
    //Logger.log("${name}", "compute(" + input.toString() + ")");
    
    try {
      // perform calculations
      final ${resultName} result = behaviorImpl.compute(input);
      
      // set results to ports
      setResult(result);
    } catch (Exception e) {
      Log.error("${name}", e);
    }
  }
  <#-- </#if> -->

  @Override
  public void update() {
    // update computed value for next computation cycle in all outgoing ports
  	<#list portsOut as port>
  	this.${port.getName()}.update();
  	</#list>
  }
  
  private void initialize() {
     // get initial values from behavior implementation
    final ${resultName} result = behaviorImpl.getInitialValues();
    
    // set results to ports
    setResult(result);
  }
  
}
