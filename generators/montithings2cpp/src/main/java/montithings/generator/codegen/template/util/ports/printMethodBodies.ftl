<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ports","comp","compname","config", "className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#list ports as port>
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
    <#assign name = port.getName()>
    ${Utils.printTemplateArguments(comp)}
    InOutPort<${type}>* ${className}${Utils.printFormalTypeParameters(comp)}::getPort${name?cap_first}(){
    return ${name};
    }

    ${Utils.printTemplateArguments(comp)}
    void ${className}${Utils.printFormalTypeParameters(comp)}::addInPort${name?cap_first}(Port<${type}>* port){
    ${name}->getInport ()->addManagedPort (port);
    }

    ${Utils.printTemplateArguments(comp)}
    void ${className}${Utils.printFormalTypeParameters(comp)}::removeInPort${name?cap_first}(Port<${type}>* port){
    ${name}->getInport ()->removeManagedPort (port);
    }

    ${Utils.printTemplateArguments(comp)}
    void ${className}${Utils.printFormalTypeParameters(comp)}::addOutPort${name?cap_first}(Port<${type}>* port){
    ${name}->getOutport ()->addManagedPort (port);
    }

    ${Utils.printTemplateArguments(comp)}
    void ${className}${Utils.printFormalTypeParameters(comp)}::removeOutPort${name?cap_first}(Port<${type}>* port){
    ${name}->getOutport ()->removeManagedPort (port);
    }

    ${Utils.printTemplateArguments(comp)}
    void ${className}${Utils.printFormalTypeParameters(comp)}::setPort${name?cap_first}ConversionFactor(int factor){
    ${name}ConversionFactor = factor;
    }
</#list>