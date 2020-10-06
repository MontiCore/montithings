${tc.signature("comp","compname")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign preconditions = ComponentHelper.getPreconditions(comp)>
<#list preconditions as statement>
    if (
    <#list ComponentHelper.getPortsInGuardExpression(statement.guard) as port>
        <#if !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.getName())>
            input.get${port.getName()?cap_first}()
        <#else>
            true // presence of value on port ${port.getName()} not checked as it is compared to NoData
        </#if>
        <#sep>&&</#sep>
    </#list>
    <#if ComponentHelper.getPortsInGuardExpression(statement.guard)?size == 0>
        true // presence of value on ports not checked as they are not used in precondition
    </#if>
    &&
    !(
    ${Utils.printExpression(statement.guard)}
    )) {
    <#if ComponentHelper.getCatch(comp, statement)??>
        ${ComponentHelper.printJavaBlock(ComponentHelper.getCatch(comp, statement).get().handler)}
    <#else>
        std::stringstream error;
        error << "Violated precondition ${Utils.printExpression(statement.guard, false)} on component ${comp.packageName}.${compname}" << std::endl;
        error << "Input port values: " << std::endl;
        <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort>
            if (input.get${inPort.getName()?cap_first} ().has_value()) {
            error << "Port \"${inPort.getName()}\": " << input.get${inPort.getName()?cap_first} ().value() << std::endl;
            } else {
            error << "Port \"${inPort.getName()}\": No data." << std::endl;
            }
        </#list>
        <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
            if (input.get${inPort.getName()?cap_first} ().has_value()) {
            error << "Port \"${inPort.getName()}\": " << input.get${inPort.getName()?cap_first} () << std::endl;
            } else {
            error << "Port \"${inPort.getName()}\": No data." << std::endl;
            }
        </#list>
        throw std::runtime_error(error.str ());
    </#if>
    }
</#list>