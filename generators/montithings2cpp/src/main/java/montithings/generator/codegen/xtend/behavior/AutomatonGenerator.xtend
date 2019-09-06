package montithings.generator.codegen.xtend.behavior

import montiarc._symboltable.ComponentSymbol
import montiarc._ast.ASTAutomaton
import montiarc._ast.ASTElement
import montiarc._ast.ASTComponent
import montiarc._ast.ASTAutomatonBehavior
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Identifier
import de.monticore.prettyprint.IndentPrinter
import de.monticore.mcexpressions._ast.ASTExpression
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import montithings.generator.visitor.CDAttributeGetterTransformationVisitor
import montiarc._ast.ASTIOAssignment
import montiarc._symboltable.VariableSymbol
import java.util.Optional
import java.util.Collection
import montiarc._symboltable.StateSymbol
import montiarc._symboltable.TransitionSymbol
import java.util.ArrayList
import montiarc._ast.ASTValueList
import montithings.generator.helper.ComponentHelper
import java.util.stream.Collectors

class AutomatonGenerator{
	
  var Collection<StateSymbol> states

  var Collection<VariableSymbol> variables

  var Collection<TransitionSymbol> transitions

  var ComponentSymbol comp;

  new(ComponentSymbol component) {
    this.comp = comp
    this.states = new ArrayList;
    this.transitions = new ArrayList;
    this.variables = new ArrayList;

    // store all states of automaton
    component.getSpannedScope().getSubScopes().stream().forEach(
      scope |
        scope.<StateSymbol>resolveLocally(StateSymbol.KIND).forEach(state|this.states.add(state))
    );
    
    // store all transitions of automaton
    component.getSpannedScope().getSubScopes().stream().forEach(
      scope |
        scope.<TransitionSymbol>resolveLocally(TransitionSymbol.KIND).forEach(transition|
          this.transitions.add(transition)
        )
    );
    
    // variables can only be defined in the component's body unlike in JavaP
    component.getSpannedScope().<VariableSymbol>resolveLocally(VariableSymbol.KIND).forEach(
      variable |
        this.variables.add(variable)
    );
  }
	
	
/**
   * Prints the compute implementation of automaton behavior.
   */
  def String printCompute(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if (element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return '''
			«resultName»«Utils.printFormalTypeParameters(comp)»
			  «comp.name»Impl::compute(«comp.name»Input«Utils.printFormalTypeParameters(comp)» «Identifier.inputName») {
			    
«««			  Lists all ingoing ports and stores the values of the passed parameter input.
			    // inputs
			    «FOR inPort : comp.allIncomingPorts»
			    	«ComponentHelper.getRealPortCppTypeString(comp, inPort)» «inPort.name» = «Identifier.inputName».get«inPort.name.toFirstUpper»();
			    «ENDFOR»
			    
«««			  Initialize result
			    «resultName»«Utils.printFormalTypeParameters(comp)» «Identifier.resultName»;
			    
«««			  Generate implementation of automaton:
«««			  switch-case statement for every state name 
			    switch («Identifier.currentStateName») {
			    «FOR state : automaton.stateDeclarationList.get(0).stateList»
			    	case «state.name»:
			    	  «FOR transition : transitions.stream.filter(s | s.source.name == state.name).collect(Collectors.toList)»
			    	  	// transition: «transition.toString»
«««			    	  if statement for each guard of a transition from this state	
			    	  	if («IF transition.guardAST.isPresent»«printExpression(transition.guardAST.get.guardExpression.expression)»«ELSE» true «ENDIF») {
			    	  	  //reaction
«««			    	  	if true execute reaction of transition  
			    	  	  «IF transition.reactionAST.present»
			    	  	  	«FOR assignment : transition.reactionAST.get.getIOAssignmentList»
			    	  	  		«IF assignment.isAssignment»
			    	  	  			«IF isVariable(assignment.name, assignment)»
			    	  	  				«assignment.name» = «printRightHandSide(assignment)»;
			    	  	  			«ELSE»
			    	  	  				«Identifier.resultName».set«assignment.name.toFirstUpper»(«printRightHandSide(assignment)»);
			    	  	  			«ENDIF»
			    	  	  		«ELSE»
			    	  	  			«printRightHandSide(assignment)»;  
			    	  	  		«ENDIF»
			    	  	  	«ENDFOR»
			    	  	  «ENDIF»
			    	  	  
«««			    	  	and change state to target state of transition
			    	  	  «Identifier.currentStateName» = «comp.name»State.«transition.target.name»;
			    	  	  break;
			    	  	}
			    	  	
			    	  	
			    	  «ENDFOR»
			    «ENDFOR»
			    }
			    return result;
			  }
		'''
  }
def String printGetInitialValues(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if (element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return '''

			 «resultName»«Utils.printFormalTypeParameters(comp)»
			«comp.name»Impl::getInitialValues() {
«««			initialize initial result
			   «resultName»«Utils.printFormalTypeParameters(comp)» «Identifier.resultName»;
			  
			  // initial reaction
			  «var StateSymbol initialState = states.stream.filter(state | state.isInitial).findFirst.get»
«««			if an initial reaction is present
			  «IF initialState.initialReactionAST.isPresent»
			  	«FOR assignment : initialState.initialReactionAST.get.getIOAssignmentList»
«««			  	set initial result			  	
			  		«IF assignment.isAssignment»
			  			«IF comp.getPort(assignment.name).isPresent»
			  				«Identifier.resultName».set«assignment.name.toFirstUpper»(«printRightHandSide(assignment)»);
			  			«ELSE»
			  				«assignment.name» = «printRightHandSide(assignment)»;
			  			«ENDIF»
			  		«ELSE»
			  			«printRightHandSide(assignment)»;  
			  		«ENDIF»
			  	«ENDFOR»
			  «ENDIF»
			  
			  «Identifier.currentStateName» = «comp.name»State.«automaton.initialStateDeclarationList.get(0).name»;
			  return «Identifier.resultName»;
			}
		'''
  }

	

  /**
   * Adds a enum for alls states of the automtaton and the attribute currentState for storing 
   * the current state of the automaton.
   */
  def String hook(ComponentSymbol comp) {
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if (element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return '''
			«Utils.printMember(comp.name + "State", Identifier.currentStateName, "private")»
			
			«printStateEnum(automaton, comp)»
		'''
  }
	
	 /**
   * Prints a enum with all states of the automaton.
   */
  def private String printStateEnum(ASTAutomaton automaton, ComponentSymbol comp) {
    return '''
			enum «comp.name»State {
			«FOR state : automaton.getStateDeclaration(0).stateList SEPARATOR ','»
				«state.name»
			«ENDFOR»;
			}
		'''
  }
  
   /**
   * Returns <tt>true</tt> if the given name is a variable name.
   * 
   * @param name
   * @return
   */
  def private boolean isVariable(String name, ASTIOAssignment assignment) {
    var Optional<VariableSymbol> symbol = assignment.getEnclosingScopeOpt().get().<VariableSymbol>resolve(name,
      VariableSymbol.KIND);
    if (symbol.isPresent()) {
      return true;
    }
    return false;
  }

  /**
   * Returns the right side of an assignment/comparison. ValueLists &
   * Alternatives are not supported.
   * 
   * @return
   */
  def private String printRightHandSide(ASTIOAssignment assignment) {
    if (assignment.isPresentAlternative()) {
      throw new RuntimeException("Alternatives not supported.");
    } else {
      var ASTValueList vl = assignment.getValueList();
      if (vl.isPresentValuation()) {
        return printExpression(vl.getValuation().getExpression(), assignment.isAssignment);
      } else {
        throw new RuntimeException("ValueLists not supported.");
      }
    }
  }

  /**
   * Prints the java expression of the given AST expression node.
   * 
   * @param expr
   * @return
   */
  def private String printExpression(ASTExpression expr, boolean isAssignment) {
    var IndentPrinter printer = new IndentPrinter();
    var JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(printer);
    if (isAssignment) {
      prettyPrinter = new CDAttributeGetterTransformationVisitor(printer);
    }
    expr.accept(prettyPrinter);
    return printer.getContent();
  }

  def private String printExpression(ASTExpression expr) {
    return printExpression(expr, true);
  }
	
	  def String generateHeader(ComponentSymbol comp) {
  	var String generics = Utils.printFormalTypeParameters(comp)
    return '''
    #pragma once
    #include "«comp.name»Input.h"
    #include "«comp.name»Result.h"
    #include "IComputable.h"
    #include <stdexcept>
    «Utils.printCPPImports(comp)»
		
    class «comp.name»«generics»Impl : IComputable<«comp.name»Input«generics»,«comp.name»Result«generics»>{ {
	private:  
        «Utils.printVariables(comp)»
        «Utils.printConfigParameters(comp)»
		
        
    public:
      	«hook(comp)»
		«printConstructor(comp)»
		virtual «comp.name»Result getInitialValues() override;
		virtual «comp.name»Result compute(«comp.name»Input input) override;

    }
    '''
  }
  
  def String generateBody(ComponentSymbol comp){
  	return'''
  	#include "«comp.name»Impl.h"

    «printGetInitialValues(comp)»
    
    «printCompute(comp)»
  	'''
  }

  def String printConstructor(ComponentSymbol comp) {
    return '''
       «comp.name»Impl(«Utils.printConfiurationParametersAsList(comp)») {
        «FOR param : comp.configParameters»
          this.«param.name» = «param.name»; 
        «ENDFOR»
      }
    '''

  }

  
	
}