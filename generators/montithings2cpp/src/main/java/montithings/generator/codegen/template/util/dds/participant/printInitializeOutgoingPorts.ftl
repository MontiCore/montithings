<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
std::string topic = "";

<#list comp.getPorts() as p>

    <#if p.isOutgoing()>
      // outgoing port ${p.getName()}
      comp->addOutPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, OUTGOING, comp->getInstanceName() + ".${p.getName()}/out", true, false));

        <#if !comp.isAtomic()>
            <#list comp.getAstNode().getConnectors() as connector>
                <#list connector.getTargetList() as target>
                    <#if target.getQName() == p.getName()>
                        // port "${p.getName()}" is target port of a subcomponent
                        topic =  comp->getInstanceName() + ".${connector.getSource().getQName()}" + "/out";
                        CLOG(DEBUG, "DDS") << "Creating additional port for incoming port ${p.getName()} to forward data to subcomponents " << topic;
                        comp->addInPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, INCOMING, topic, true, false));
                    </#if>
                </#list>
            </#list>
        </#if>
    </#if>
</#list>