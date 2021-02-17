<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

CLOG (DEBUG, "DDS") << "onNewConnectors: " << payload;
// check if this message informs us about new component instances
<#list comp.getPorts() as p>
  <#if p.isIncoming()>
    // for port name = ${p.getName()}
    CLOG (DEBUG, "DDS") << "Searching for own port: " << comp->getInstanceName() + ".${p.getName()}/in";
    if (payload.find(comp->getInstanceName() + ".${p.getName()}/in") != std::string::npos)
    {
      std::string topic = payload.substr(0, payload.find("->"));
      CLOG (DEBUG, "DDS") << "New connection! Creating INCOMING PORT: " << topic;
      comp->addInPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, INCOMING, topic));

      <#if !comp.isAtomic()>
      // additional outgoing port for port incoming port ${p.getName()}
      // to forward data to subcomponents
      topic = topic.substr(0, payload.find("/")) + "/out"; 
      comp->addOutPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, OUTGOING, topic));
      </#if>
    }
  </#if>
</#list>