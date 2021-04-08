<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#if !(comp.getAllOutgoingPorts()?size == 0)>
    ${Utils.printTemplateArguments(comp)}
    ${className}${Utils.printFormalTypeParameters(comp, false)}::${className}(<#list comp.getAllOutgoingPorts() as port> ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}
    ${port.getName()} <#sep>,</#sep>
</#list>){
    <#if comp.isPresentParentComponent()>
        super(<#list comp.parent().loadedSymbol.getAllOutgoingPorts() as port >
        port.getName()
    </#list>);
    </#if>
    <#list comp.getOutgoingPorts() as port >
        this->${port.getName()} = ${port.getName()};
        <#if ComponentHelper.hasAgoQualification(comp, port)>
          auto nowOf__${port.getName()?cap_first} = std::chrono::system_clock::now();
          dequeOf__${port.getName()?cap_first}.push_back(std::make_pair(nowOf__${port.getName()?cap_first}, ${port.getName()}));
          cleanDequeOf${port.getName()?cap_first}(nowOf__${port.getName()?cap_first});
        </#if>
    </#list>
    }
</#if>

<#list comp.getOutgoingPorts() as port>
    ${Utils.printTemplateArguments(comp)}
    tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>
    ${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
    {
    return ${port.getName()};
    }

    ${Utils.printTemplateArguments(comp)}
    void
    ${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()})
    {
    this->${port.getName()} = ${port.getName()};
    <#if ComponentHelper.hasAgoQualification(comp, port)>
      auto now = std::chrono::system_clock::now();
      dequeOf__${port.getName()?cap_first}.push_back(std::make_pair(now, ${port.getName()}.value()));
      cleanDequeOf${port.getName()?cap_first}(now);
    </#if>
    }
    <#if ComponentHelper.portUsesCdType(port)>
        <#assign cdeImportStatementOpt = ComponentHelper.getCDEReplacement(port, config)>
        <#if cdeImportStatementOpt.isPresent()>
            <#assign fullImportStatemantName = cdeImportStatementOpt.get().getSymbol().getFullName()?split(".")>
            <#assign adapterName = fullImportStatemantName[0]+"Adapter">
            <#assign cdSimpleName = cdeImportStatementOpt.get().getSymbol().getFullName()?keep_after_last(".")>

            tl::optional<${cdeImportStatementOpt.get().getImportClass().toString()}>
            ${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}Adap() const
            {
            if (!get${port.getName()?cap_first}().has_value()) {
            return {};
            }

            ${adapterName?cap_first} ${adapterName?uncap_first};
            return ${adapterName?uncap_first}.convert${cdSimpleName}(*get${port.getName()?cap_first}());
            }

            void
            ${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(${cdeImportStatementOpt.get().getImportClass().toString()} element)
            {
            ${adapterName?cap_first} ${adapterName?uncap_first};
            this->${port.getName()} = ${adapterName?uncap_first}.convert${cdSimpleName}(element);
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
      for (auto it = dequeOf__${port.getName()?cap_first}.crbegin(); it != dequeOf__${port.getName()?cap_first}.crend(); ++it)
      {
      if (it->first < now - ago_time)
      {
      return tl::make_optional(it->second);
      }
      }
      return tl::make_optional(dequeOf__${port.getName()?cap_first}.front().second);
      }

      ${Utils.printTemplateArguments(comp)}
      void ${className}${Utils.printFormalTypeParameters(comp, false)}::cleanDequeOf${port.getName()?cap_first}(std::chrono::time_point<std::chrono::system_clock> now)
      {
      while (dequeOf__${port.getName()?cap_first}.size() > 1 && dequeOf__${port.getName()?cap_first}.at (1).first < now - highestAgoOf__${port.getName()?cap_first})
      {
      dequeOf__${port.getName()?cap_first}.pop_front ();
      }
      }
    </#if>
</#list>