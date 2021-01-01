<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>

<#if !isBatch>
    <#if !(comp.getAllIncomingPorts()?size == 0)>
        ${Utils.printTemplateArguments(comp)}
        ${className}${Utils.printFormalTypeParameters(comp, false)}::${className}(
        <#list comp.getAllIncomingPorts() as port>
            tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()}
            <#sep>,</#sep>
        </#list>){
        <#if comp.isPresentParentComponent()>
            super(
            <#list comp.parent().loadedSymbol.allIncomingPorts as port >
                port.getName()
            </#list>);
        </#if>
        <#list comp.getIncomingPorts() as port >
            this->${port.getName()} = std::move(${port.getName()});
        </#list>
        }
    </#if>
</#if>
<#list ComponentHelper.getPortsInBatchStatement(comp) as port>
    <#if port.isIncoming()>
        ${Utils.printTemplateArguments(comp)}
        std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>
        ${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
        {
        return ${port.getName()};
        }

        ${Utils.printTemplateArguments(comp)}
        void
        ${className}${Utils.printFormalTypeParameters(comp, false)}::add${port.getName()?cap_first}Element(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> element)
        {
        if (element)
        {
        ${port.getName()}.push_back(element.value());
        }
        }

        ${Utils.printTemplateArguments(comp)}
        void
        ${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> vector)
        {
        this->${port.getName()} = std::move(vector);
        }
    </#if>
</#list>

<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port>
    <#if port.isIncoming()>
        ${Utils.printTemplateArguments(comp)}
        tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>
        ${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
        {
        return ${port.getName()};
        }

        ${Utils.printTemplateArguments(comp)}
        void
        ${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> element)
        {
        this->${port.getName()} = std::move(element);
        }

        <#if ComponentHelper.portUsesCdType(port)>
            <#assign cdeImportStatementOpt = ComponentHelper.getCDEReplacement(port, config)>
            <#if cdeImportStatementOpt.isPresent()>
                <#assign fullImportStatemantName = cdeImportStatementOpt.get().getSymbol().getFullName()?split(".")>
                <#assign adapterName = fullImportStatemantName[0]+"Adapter">

                tl::optional<${cdeImportStatementOpt.get().getImportClass().toString()}>
                ${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}Adap() const
                {
                if (!get${port.getName()?cap_first}().has_value()) {
                return {};
                }

                ${adapterName?cap_first} ${adapterName?uncap_first};
                return ${adapterName?uncap_first}.convert(*get${port.getName()?cap_first}());
                }

                void
                ${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(${cdeImportStatementOpt.get().getImportClass().toString()} element)
                {
                ${adapterName?cap_first} ${adapterName?uncap_first};
                this->${port.getName()} = ${adapterName?uncap_first}.convert(element);
                }

            </#if>
        </#if>
    </#if>
</#list>