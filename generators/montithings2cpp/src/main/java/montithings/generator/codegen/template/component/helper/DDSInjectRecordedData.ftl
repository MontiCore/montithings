<#-- (c) https://github.com/MontiCore/monticore -->

<#-- This is part of the initialization of the component if replaying is activated.
     Hereby, the vector which holds the recorded system calls is filled up.
     As system calls are replayed on a lower level we have to do this in the
     generator instead of in transformations. -->

${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if replayEnabled && !ComponentHelper.isFlaggedAsGenerated(comp)>
{
    // Fill system call replayer with recorded data
    std::ifstream ifstreamRecordings("recordings.json");

    if ( ifstreamRecordings ) {
        json recordings;
        ifstreamRecordings >> recordings;

        // the instance name differs from the original model, as it is wrapped by a new component
        // thus, remove last qualifying name
        std::string oldInstanceName = getInstanceName().substr(0, getInstanceName().find_last_of("."));

        if (recordings["calls"].contains(oldInstanceName.c_str())) {
            for (auto &call : recordings["calls"][oldInstanceName.c_str()].items()) {
                montithings::library::hwcinterceptor::addRecordedCall(std::stoi(call.key()), call.value());
            }
        }
    }
    else {
        std::cerr << "Could not read recordings.json" << std::endl;
    }
}
</#if>
