<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

CLOG(DEBUG, "DDS") << "DDSParticipant | Received connection configuration: " << payload;
json jPayload = json::parse(payload);

// check if this message informs us about new component instances
<#list comp.getPorts() as p>
    <#if p.isIncoming()>

        CLOG(DEBUG, "DDS") << "Searching for own port: " << instanceName + ".${p.getName()}/in";

        for (auto &jConnection : jPayload) {
            std::string connection = jConnection.get<std::string>();
            if (connection.find(instanceName + ".${p.getName()}/in") != std::string::npos) {
                std::string topic = connection.substr(0, connection.find("->"));
                CLOG(DEBUG, "DDS") << "New connection! Creating ingoing port which listens on " << topic;
                comp->addInPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, INCOMING, topic));

                <#if !comp.isAtomic()>
                    // additional outgoing port for port incoming port ${p.getName()}
                    // to forward data to subcomponents
                    topic = instanceName + ".${p.getName()}/out";
                    comp->addOutPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, OUTGOING, topic));
                </#if>
            }
        }
    </#if>
</#list>