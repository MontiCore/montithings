<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}

while (!this->tryInitializeDDS (argc, argv)) {
<#if config.getSplittingMode().toString() == "LOCAL">
    CLOG (DEBUG, "DDS") << "Creating dds instances failed. Is multicast enabled/allowed?";
<#else>
    CLOG (DEBUG, "DDS") << "Creating dds instances failed. Is the DCPSInfoRepo service running?";
</#if>
CLOG (DEBUG, "DDS") << "Trying again...";
std::this_thread::sleep_for(std::chrono::seconds(1));
}

<#if comp.getParameters()?size = 0>
    parameterConfig = json::object();
    receivedParameterConfig = true;
    initializeConnectorConfigPorts();
</#if>
