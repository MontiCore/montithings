<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/ConfigPreamble.ftl">

while (!this->tryInitializeDDS (argc, argv)) {
<#if splittingModeIsLocal>
    CLOG (DEBUG, "DDS") << "Creating dds instances failed. Is multicast enabled/allowed?";
<#else>
    CLOG (DEBUG, "DDS") << "Creating dds instances failed. Is the DCPSInfoRepo service running?";
</#if>
CLOG (DEBUG, "DDS") << "Trying again...";
std::this_thread::sleep_for(std::chrono::seconds(1));
}
