<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

std::cout << "onNewConnectors: " << payload << std::endl;
// check if this message informs us about new component instances
<#list comp.getPorts() as p>
  <#if p.isIncoming()>
    // for port name = ${p.getName()}
    std::cout << "Searching for own port: " << comp->getInstanceName() + ".${p.getName()}/in" << std::endl;
    if (payload.find(comp->getInstanceName() + ".${p.getName()}/in") != std::string::npos)
    {
      std::string topic = payload.substr(0, payload.find("->"));
      std::cout << "New connection! Creating INCOMING PORT: " << topic << std::endl;
      comp->addInPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, INCOMING, topic));

      <#if !comp.isAtomic()>
      // additional outgoing port for port incoming port ${p.getName()}
      // to forward data to subcomponents
      topic = topic.substr(0, payload.find("/") + "/out"; 
      comp->addOutPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, OUTGOING, topic));
      </#if>
    }
  </#if>
</#list>