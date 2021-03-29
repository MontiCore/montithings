<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getReplayMode().toString() == "ON">
    // Fill system call replayer with recorded data
    std::ifstream ifstreamRecordings("recordings.json");

    if ( ifstreamRecordings ) {
        json recordings;
        ifstreamRecordings >> recordings;

        // the instance name differs from the original model, as it is wrapped by a new component
        // thus, remove last qualifying name
        std::string oldInstanceName = instanceNameArg.getValue().substr(0, instanceNameArg.getValue().find_last_of("."));

        if (recordings["calls"].contains(oldInstanceName.c_str())) {
            for (auto &call : recordings["calls"][oldInstanceName.c_str()].items()) {
                montithings::library::hwcinterceptor::addRecordedCall(std::stoi(call.key()), call.value());
            }
        }
    }
    else {
        std::cerr << "Could not read recordings.json" << std::endl;
    }
</#if>
