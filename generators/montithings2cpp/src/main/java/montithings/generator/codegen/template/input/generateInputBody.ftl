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
            <#if ComponentHelper.hasAgoQualification(comp, port)>
               auto nowOf__${port.getName()?cap_first} = std::chrono::system_clock::now();
               dequeOf__${port.getName()?cap_first}.push_back(std::make_pair(nowOf__${port.getName()?cap_first}, value.value()));
               std::pair<std::chrono::time_point<std::chrono::system_clock>, ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> firstElementOf__${port.getName()?cap_first} = dequeOf__${port.getName()?cap_first}.front();
               while (firstElementOf__${port.getName()?cap_first}.first < nowOf__${port.getName()?cap_first} - highestAgoOf__${port.getName()?cap_first}){
               firstElementOf__${port.getName()?cap_first} = dequeOf__${port.getName()?cap_first}.front();
               dequeOf__${port.getName()?cap_first}.pop_front();
               }
               dequeOf__${port.getName()?cap_first}.push_front(firstElementOf__${port.getName()?cap_first});
            </#if>
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
        <#if ComponentHelper.hasAgoQualification(comp, port)>
            auto now = std::chrono::system_clock::now();
            dequeOf__${port.getName()?cap_first}.push_back(std::make_pair(now, element.value()));
            std::pair<std::chrono::time_point<std::chrono::system_clock>, ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> firstElement = dequeOf__${port.getName()?cap_first}.front();
            while (firstElement.first < now - highestAgoOf__${port.getName()?cap_first}){
            firstElement = dequeOf__${port.getName()?cap_first}.front();
            dequeOf__${port.getName()?cap_first}.pop_front();
            }
            dequeOf__${port.getName()?cap_first}.push_front(firstElement);
        </#if>
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
        <#if ComponentHelper.hasAgoQualification(comp, port)>
          std::deque<std::pair<std::chrono::time_point<std::chrono::system_clock>, ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>> ${className}${Utils.printFormalTypeParameters(comp, false)}::dequeOf__${port.getName()?cap_first};
          ${Utils.printTemplateArguments(comp)}
          tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${className}${Utils.printFormalTypeParameters(comp, false)}::agoGet${port.getName()?cap_first}(const std::chrono::nanoseconds ago_time)
          {
          if(dequeOf__${port.getName()?cap_first}.empty()){
          return tl::nullopt;
          }
          auto now = std::chrono::system_clock::now();
          int i = 1;
          while (i <= dequeOf__${port.getName()?cap_first}.size()){
          if(dequeOf__${port.getName()?cap_first}.at(dequeOf__${port.getName()?cap_first}.size() - i).first < now-ago_time){
          return tl::make_optional(dequeOf__${port.getName()?cap_first}.at(dequeOf__${port.getName()?cap_first}.size() - i).second);
          }
          i++;
          }
          return tl::make_optional(dequeOf__${port.getName()?cap_first}.front().second);
          }
        </#if>
    </#if>
</#list>