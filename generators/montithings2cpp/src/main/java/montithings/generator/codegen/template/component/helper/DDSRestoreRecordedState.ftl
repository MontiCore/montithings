<#-- (c) https://github.com/MontiCore/monticore -->

<#-- This is part of the initialization of the component if replaying is activated.
     Hereby, the variable state is restored based on the recordings. -->

${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if replayEnabled && !ComponentHelper.isFlaggedAsGenerated(comp)>
{
    // Restore internal state
    std::ifstream ifstreamRecordings("recordings.json");

    if ( ifstreamRecordings ) {
        json recordings;
        ifstreamRecordings >> recordings;

        // the instance name differs from the original model, as it is wrapped by a new component
        // thus, remove last qualifying name
        std::string oldInstanceName = getInstanceName().substr(0, getInstanceName().find_last_of("."));

        // restore state
        if (recordings["states"].contains(oldInstanceName.c_str())
            && !recordings["states"][oldInstanceName.c_str()].is_null()) {
            std::string recordedState = recordings["states"][oldInstanceName.c_str()].dump();
            getState()->restoreState(recordedState);
        }
    }
    else {
        std::cerr << "Could not read recordings.json" << std::endl;
    }
}
</#if>
