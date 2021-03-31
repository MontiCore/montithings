<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getReplayMode().toString() == "ON" && !ComponentHelper.isFlaggedAsGenerated(comp)>
{
    // Restore internal state
    std::ifstream ifstreamRecordings("recordings.json");

    if ( ifstreamRecordings ) {
        json recordings;
        ifstreamRecordings >> recordings;

        // the instance name differs from the original model, as it is wrapped by a new component
        // thus, remove last qualifying name
        std::string oldInstanceName = instanceNameArg.getValue().substr(0, instanceNameArg.getValue().find_last_of("."));

        // restore state
        if (recordings["states"].contains(oldInstanceName.c_str())
            && !recordings["states"][oldInstanceName.c_str()].is_null()) {
            std::string recordedState = recordings["states"][oldInstanceName.c_str()].dump();
            cmp.getState()->restoreState(recordedState);
        }
    }
    else {
        std::cerr << "Could not read recordings.json" << std::endl;
    }
}
</#if>
