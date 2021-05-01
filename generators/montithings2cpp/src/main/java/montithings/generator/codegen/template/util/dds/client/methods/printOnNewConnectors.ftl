<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

CLOG(DEBUG, "DDS") << "DDSClient | Received connection configuration: " << payload;
std::string connection = payload;

// check if this message informs us about new component instances
<#list comp.getPorts() as p>
    <#if p.isIncoming()>

        CLOG(DEBUG, "DDS") << "Searching for own port: " << instanceName + ".${p.getName()}/in";

        if (connection.find(instanceName + ".${p.getName()}/in") != std::string::npos) {
            std::string topic = connection.substr(0, connection.find("->"));
            CLOG(DEBUG, "DDS") << "New connection! Creating ingoing port which listens on " << topic;
            comp->getInterface()->addInPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, INCOMING, topic, "${p.getName()}", true, false));

            <#if !comp.isAtomic()>
                // additional outgoing port for port incoming port ${p.getName()}
                // to forward data to subcomponents
                topic = instanceName + ".${p.getName()}/out";
                comp->getInterface()->addOutPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, OUTGOING, topic, "${p.getName()}", true, false));
            </#if>
        }
    </#if>
</#list>