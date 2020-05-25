// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import montithings.generator.helper.ComponentHelper
import montithings._symboltable.ComponentSymbol
import de.se_rwth.commons.logging.Log
import montiarc._ast.ASTValuation
import montithings._ast.ASTPort
import montithings._ast.ASTMTValueList
import montithings._ast.ASTMTValueRange
import montithings._ast.ASTMTValueFormat
import montiarc._symboltable.PortSymbol

class ValueCheck {


	def static String printPortValuecheck(ComponentSymbol component, PortSymbol port) {
	    if (!port.getAstNode().isPresent()) {
	      Log.warn("0xMT011 Port " + port.getName() + " has no AST Node in symbol.")
	      return ""
	    }
	    if (!(port.getAstNode().get() instanceof ASTPort)) {
	      Log.info("0xMT012 Port " + port.getName() + " is no MT Port. No value checks generated.",
	          "printPortValuecheck")
	      return ""
	    }
	    var mtPort = port.getAstNode().get() as ASTPort
	    if (!mtPort.isPresentAllowedValues()) {
	      return ""
	    }
	    var valueContainer = ""
	    if (port.isIncoming) { valueContainer = "input" } 
	    else { valueContainer = "result" }
		
		return '''
		{
		«FOR decl : mtPort.getMTPortDeclarationList»
			«var portValue = valueContainer + ".get" + decl.name.toFirstUpper + " ().value ()"»
			«IF !component.isBatchPort(port)»
				«formatCheckPreparation(portValue)»
				if («valueContainer».get«decl.name.toFirstUpper» () && !(
			«ELSE»
				«var helper = new ComponentHelper(component)»
				std::vector<«helper.getRealPortCppTypeString(port)»> copy«decl.name.toFirstUpper» = input.get«decl.name.toFirstUpper» ();
				for_each(copy«decl.name.toFirstUpper».begin (), copy«decl.name.toFirstUpper».end (), [](«helper.getRealPortCppTypeString(port)»& v)
				{
					«formatCheckPreparation(portValue = 'v')»
					if  (!(
			«ENDIF»
			«IF mtPort.allowedValues instanceof ASTMTValueList»
				«var list = mtPort.allowedValues as ASTMTValueList»
				«valueListToString(portValue, list, valueContainer)»
			«ELSEIF mtPort.allowedValues instanceof ASTMTValueRange»
				«var range = mtPort.allowedValues as ASTMTValueRange»
				«valueRangeToString(portValue, range, valueContainer)»
			«ELSEIF mtPort.allowedValues instanceof ASTMTValueFormat»
				«var format = mtPort.allowedValues as ASTMTValueFormat»
				«IF component.isBatchPort(port)»
					«formatCheckToString("allowedValuesCheck.str ()", format)»
				«ELSE»
					«formatCheckToString(portValue, format)»
				«ENDIF»
			«ENDIF»
			)) { 
			«IF decl.isPresentDefault»
				«IF component.isBatchPort(port)»
					v = «valuationToString(decl.getDefault())»;
				«ELSEIF valueContainer.equals("result")»
					«valueContainer».set«decl.name.toFirstUpper»(«valuationToString(decl.getDefault())»);
				«ELSEIF valueContainer.equals("input")»
					«valueContainer».add«decl.name.toFirstUpper»Element(«valuationToString(decl.getDefault())»);
				«ENDIF»
			«ELSE»
				std::ostringstream error;
				error << "Violated allowed values for port \"«decl.name»\". Actual value: ";
				«IF component.isBatchPort(port)»
				error << v;
				«ELSE»
				error << «valueContainer».get«decl.name.toFirstUpper» ().value ();
				«ENDIF»
				throw std::runtime_error(error.str ());
			«ENDIF»
			}
			«IF component.isBatchPort(port)»
			});
			input.set«decl.name.toFirstUpper»(copy«decl.name.toFirstUpper»);
			«ENDIF»
		«ENDFOR»
		}
		'''
	}
	
	def private static String formatCheckPreparation(String port) {
    	return '''
    	std::ostringstream allowedValuesCheck; 
    	allowedValuesCheck << «port»;'''
  	}

	def private static String formatCheckToString(String port, ASTMTValueFormat format) {
    	return '''std::regex_match(allowedValuesCheck.str(), std::regex("«format.format»"))'''
	} 
	
	def private static String valueRangeToString(String port, ASTMTValueRange range, String valueContainer) {
	    var lowerBound = valuationToString(range.getLowerBound());
	    var upperBound = valuationToString(range.getUpperBound());
	
		return '''
		«port» >= «lowerBound» && 
		«IF range.isPresentStepsize»
		((«port» - «lowerBound») % «valuationToString(range.stepsize)» == 0) && 
		«ENDIF»
		«port» <= «upperBound»
		'''
  	}
  	
  	def private static String valueListToString(String port, ASTMTValueList list, String valueContainer) {
		var ranges = list.getMTValueRangeList();
		var valuations = list.getValuationList();
		return '''
		«FOR v : valuations SEPARATOR ' || '»
			«port» == «valuationToString(v)»
		«ENDFOR»
		«IF !valuations.isEmpty && !ranges.isEmpty» || «ENDIF»
		«FOR r : ranges SEPARATOR ' || '»
			(«valueRangeToString(port, r, valueContainer)»)
		«ENDFOR»
		'''
	}
	
	def private static String valuationToString(ASTValuation valuation) {
		return Utils.printExpression(valuation.expression);
	}
}
