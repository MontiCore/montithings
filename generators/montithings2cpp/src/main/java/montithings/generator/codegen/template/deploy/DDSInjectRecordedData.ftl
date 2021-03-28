<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getReplayMode().toString() == "ON">

    // Fill system call replayer with recorded data
    std::ifstream ifstreamRecordings("recordings.json");
    json recordings;
    ifstreamRecordings >> recordings;

    if (recordings["calls"].contains(instanceNameArg.getValue().c_str())) {
        for (auto &call : recordings["calls"][instanceNameArg.getValue().c_str()].items()) {
            montithings::library::hwcinterceptor::addRecordedCall(std::stoi(call.key()), call.value());
        }
    }
</#if>
