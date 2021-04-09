<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign className = comp.getName() + "DDSParticipant">


#pragma once
#include ${"<ace/Log_Msg.h>"}
#include ${"<iostream>"}
#include ${"<string>"}
#include ${"<vector>"}
#include ${"<chrono>"}
#include ${"<thread>"}
#include "easyloggingpp/easylogging++.h"

#include ${"<dds/DdsDcpsInfrastructureC.h>"}
#include ${"<dds/DdsDcpsPublicationC.h>"}
#include ${"<dds/DCPS/Marked_Default_Qos.h>"}
#include ${"<dds/DCPS/Service_Participant.h>"}
#include ${"<dds/DCPS/StaticIncludes.h>"}

<#-- Load libraries for rtps_udp transport communication -->
<#if config.getSplittingMode().toString() != "DISTRIBUTED">
#include ${"<dds/DCPS/RTPS/RtpsDiscovery.h>"}
#include ${"<dds/DCPS/transport/rtps_udp/RtpsUdp.h>"}
</#if>

#include "DDSPort.h"
#include "DDSParticipant.h"
#include "${comp.getName()}.h"

${Utils.printNamespaceStart(comp)}

class ${className} : public DDSParticipant
{
    private:
        ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()}* comp;

        // ports handling connection configurations
        std::unique_ptr${"<"}DDSPort ${"<"} std::string${">>"} connectorPortOut;
        std::unique_ptr${"<"}DDSPort ${"<"} std::string${">>"} connectorPortIn;

        // ports handling parameter configurations
        std::unique_ptr${"<"}DDSPort ${"<"} std::string${">>"} configPortOut;
        std::unique_ptr${"<"}DDSPort ${"<"} std::string${">>"} configPortIn;

        // stores instance name of the component
        std::string instanceName;

        // is set true after parameterConfig is set.
        bool receivedParameterConfig = false;
        json parameterConfig;

        json getSerializedState() {
            return comp->getState()->serializeState();
        }

        bool tryInitializeDDS(int argc, char *argv[]) {
            DDS::DomainParticipantFactory_var dpf = TheParticipantFactoryWithArgs(argc, argv);

            // We do not make use of multiple DDS domains yet, arbitrary but fixed id will do it
            int domainId = 42;
            if (!participant) {
                participant = dpf->create_participant(domainId, PARTICIPANT_QOS_DEFAULT,
                // no listener required
                0,
                // default status mask ensures that
                // all relevant communication status
                // changes are communicated to the
                // application
                OpenDDS::DCPS::DEFAULT_STATUS_MASK);
            }

            if (!participant) {
                std::cerr << "DDS creation of the participant instance failed." << std::endl;
                return false;
            }

            DDSMessage::MessageTypeSupport_var ts = new DDSMessage::MessageTypeSupportImpl;
            type_name = ts->get_type_name();

            if (ts->register_type(participant, "") != DDS::RETCODE_OK) {
                std::cerr << "DDS creation of the message type support failed." << std::endl;
                return false;
            }

            if (!publisher) {
                publisher = participant->create_publisher(PUBLISHER_QOS_DEFAULT, 0,
                OpenDDS::DCPS::DEFAULT_STATUS_MASK);
            }
            if (!publisher) {
                std::cerr << "DDS creation of the publisher instance failed." << std::endl;
                return false;
            }
            if (!subscriber) {
                subscriber = participant->create_subscriber(SUBSCRIBER_QOS_DEFAULT, 0,
                OpenDDS::DCPS::DEFAULT_STATUS_MASK);
            }
            if (!subscriber) {
                std::cerr << "DDS creation of the subscriber instance failed." << std::endl;
                return false;
            }

            return true;
        }

        void onNewConfig(std::string payload) {
            CLOG(DEBUG, "DDS") << "DDSParticipant | Received parameter configuration: " << payload;
            json jPayload = json::parse(payload);
            if (jPayload.contains(instanceName)) {
                parameterConfig = jPayload;
                receivedParameterConfig = true;
            }
            CLOG(DEBUG, "DDS") << "onNewConfig: " << payload;
        }

    public:
        ${className} (std::string instanceName, int argc, char *argv[]);
        ~${className}() = default;

        void setComp(${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()}* cmp) {
            comp = cmp;
        }

        void initializeOutgoingPorts();

        void publishConnectorConfig();

        void publishParameterConfig();

        void onNewConnectors(std::string payload);

        bool isReceivedParameterConfig() {
            return receivedParameterConfig;
        }

        json getParameterConfig() {
            return parameterConfig;
        }

        std::string getInstanceName() {
            return instanceName;
        }

        void initializeParameterConfigPortPub() {
            std::string topicConfig = "parameterConfig";
            configPortOut = std::unique_ptr${"<"}DDSPort ${"<"} std::string${">>"}(
                new DDSPort${"<"}std::string>(*this, OUTGOING, topicConfig, "_parametersOut", false, true));
        }

        void initializeParameterConfigPortSub() {
            std::function${"<"}void (std::string)${">"} onDataAvailableCallback = std::bind(&${className}::onNewConfig, this, std::placeholders::_1);

            std::string topicConfig = "parameterConfig";
            configPortIn = std::unique_ptr${"<"}DDSPort ${"<"} std::string${">>"}(
                new DDSPort${"<"}std::string${">"}(*this, INCOMING, topicConfig, "_parametersIn", false, true, onDataAvailableCallback));
        }

        void setReceivedParameterConfigTrue(){
            parameterConfig = json::object();
            receivedParameterConfig = true;
        }

        void initializeConnectorConfigPortPub() {
            std::string topicConnections = "connectorConfig";
            connectorPortOut = std::unique_ptr${"<"}DDSPort ${"<"} std::string${">>"}(
                new DDSPort${"<"}std::string${">"}(*this, OUTGOING, topicConnections, "_connectionsOut", false, true));
        }

        void initializeConnectorConfigPortSub() {
            std::function${"<"}void (std::string)${">"} onDataAvailableCallback = std::bind(&${className}::onNewConnectors, this, std::placeholders::_1);
            std::string topicConnections = "connectorConfig";
            connectorPortIn = std::unique_ptr${"<"}DDSPort ${"<"} std::string${">>"}(
            new DDSPort${"<"}std::string${">"}(*this, INCOMING, topicConnections, "_connectionsIn", false, true, onDataAvailableCallback));
        }
    };

${Utils.printNamespaceEnd(comp)}