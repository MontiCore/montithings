/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "LogTracer.h"

namespace montithings {
    LogTracer::LogTracer(std::string instanceName, LogTracerInterface &interface)
            : instanceName(std::move(instanceName)), interface(&interface) {
        logEntryIndex = 0;
        montithings::subscribeLogTracer(this);

        interface.addOnRequestCallback(std::bind(&LogTracer::onRequest, this, std::placeholders::_1,
                                                 std::placeholders::_2,
                                                 std::placeholders::_3,
                                                 std::placeholders::_4,
                                                 std::placeholders::_5,
                                                 std::placeholders::_6));
    }

    sole::uuid
    LogTracer::uuid() {
        return sole::uuid4();
    }


    void
    LogTracer::handleLogEntry(const std::string &content) {
        LogEntry logEntry(logEntryIndex,
                          time(nullptr),
                          content,
                          currTraceInput.getUuid(),
                          currTraceOutput.getUuid());

        currInputLogs.push_back(logEntry);
        logEntryIndex++;
    }


    sole::uuid
    LogTracer::getCurrUuidAndMarkOutput() {
        atLeastOneNewOutput = true;
        return getCurrOutputUuid();
    }

    sole::uuid
    LogTracer::getCurrOutputUuid() {
        return currTraceOutput.getUuid();
    }


    void LogTracer::handleOutput() {
        currTraceOutput.setInputs(currInputGroup);
        currInputGroup.clear();

        traceOutputs.push_back(currTraceOutput);
        atLeastOneNewOutput = false;

        currTraceOutput = TraceOutput();
    }

    void
    LogTracer::resetCurrentOutput() {
        currInputGroup.clear();
    }

    void
    LogTracer::resetCurrentInput() {
        currInputGroup.clear();
    }

    LogTracerInterface *LogTracer::getInterface() {
        return interface;
    }

    std::map<std::string, std::string>
    LogTracer::getVariableSnapshot(time_t time) {
        std::map<std::string, std::string> snapshot;

        for (const auto &varSnap : variableSnapshots) {
            // varSnap.first -> variable name
            // varSnap.second -> map of assignments with time as key

            // retrieve the most closely recorded key which is equal or less than the given time
            // if only one entry is recorded, check if its suited or not
            if (varSnap.second.size() == 1) {
                if (varSnap.second.begin()->first <= time) {
                    snapshot[varSnap.first] = varSnap.second.begin()->second;
                    continue;
                }
            }

            //otherwise find most closely recorded key
            auto itlow = varSnap.second.lower_bound(time);

            if (itlow == varSnap.second.end() || itlow->first != time) {
                if (itlow != varSnap.second.begin()) {
                    // no key found
                    continue;
                } else {
                    --itlow; /* found prev */
                }
            }

            snapshot[varSnap.first] = itlow->second;
        }

        return snapshot;
    }

    std::multimap<sole::uuid, std::string>
    LogTracer::getTraceUuids(sole::uuid inputUuid) {
        tl::optional<TraceInput> traceInput = getInputByUuid(inputUuid);
        if (!traceInput.has_value()) {
            std::multimap<sole::uuid, std::string> res;
            return res;
        }

        return traceInput.value().getTraceIdsWithPortNames();
    }

    void
    LogTracer::onRequest(sole::uuid reqUuid, sole::uuid logUuid, sole::uuid inputUuid,
                         sole::uuid outputUuid, LogTracerInterface::Request reqType,
                         long fromTimestamp) {
        if (reqType == LogTracerInterface::INTERNAL_DATA) {
            sendInternalData(reqUuid, logUuid, inputUuid, outputUuid);
        } else if (reqType == LogTracerInterface::LOG_ENTRIES) {
            sendLogEntries(reqUuid, fromTimestamp);
        } else if (reqType == LogTracerInterface::TRACE_DATA) {
            // logUuid == traceUuid in this case
            sendTraceData(reqUuid, logUuid);
        }
    }

    std::vector<LogEntry> LogTracer::getAllLogEntries() {
        std::vector<LogEntry> res;

        for (auto &traceOutput : traceOutputs) {
            for (TraceInput traceInput : traceOutput.getInputs()) {
                if (traceInput.getLogEntries().empty()) {
                    // if input does not contain any log entry, create one.
                    /*
                    LogEntry virtualLogEntry(logEntryIndex, time_t
                    time, std::string
                    content)
                    res.push_back(LogEntry())
                    */
                    logEntryIndex++;
                }
                res.insert(
                        std::end(res),
                        std::begin(traceInput.getLogEntries()),
                        std::end(traceInput.getLogEntries()));
            }
        }

        // dont forget inputs which do not belong to an output, yet
        for (auto &traceInput : currInputGroup) {
            res.insert(
                    std::end(res),
                    std::begin(traceInput.getLogEntries()),
                    std::end(traceInput.getLogEntries()));
        }

        // and the current input
        res.insert(std::end(res),
                   std::begin(currInputLogs),
                   std::end(currInputLogs));


        return res;
    }


    void LogTracer::sendLogEntries(sole::uuid reqUuid, long fromTimestamp) {
        std::string payload;
        std::vector<LogEntry> logs = getAllLogEntries();
        if (logs.empty()) {
            /*
            // If no log entry is present create virtual ones based on input uuids
            long index = 0;
            std::map<sole::uuid, LogEntry> vLogEntries;

            // output uuids are tracked at log entry calls. This should be changed in future.
            // using a place holder for now
            sole::uuid placeholderOutputUuid = sole::uuid4();

            for(auto const& item: allInputLogs) {
                sole::uuid inputUuid = item.first;
                LogEntry entry(index,
                               inputTimeMap[inputUuid],
                               "Log entry for " + std::to_string(index + 1) + ". input.",
                               inputUuid,
                               placeholderOutputUuid);
                vLogEntries.insert({sole::uuid4(), entry});
                index++;
            }

            payload = dataToJson(vLogEntries);
             */
        } else {

            payload = dataToJson(logs);
        }
        interface->response(reqUuid, payload);
    }

    tl::optional<LogEntry> LogTracer::getLogEntryByUuid(sole::uuid uuid) {
        return getLogEntryByUuid(uuid, tl::nullopt);

    }

    tl::optional<LogEntry> LogTracer::getLogEntryByUuid(sole::uuid uuid, tl::optional<sole::uuid> outputUuid) {
        for (auto &traceOutput : traceOutputs) {
            // if an outputUuid is provided we can narrow down the relevant data
            if (outputUuid.has_value() && outputUuid != traceOutput.getUuid()) {
                continue;
            }
            for (TraceInput traceInput : traceOutput.getInputs()) {
                for (auto &logEntry : traceInput.getLogEntries()) {
                    if (logEntry.getUuid() == uuid) {
                        return tl::optional<LogEntry>(logEntry);
                    }
                }
            }
        }

        // dont forget current input
        for (auto &traceInput : currInputGroup) {
            for (auto &logEntry : traceInput.getLogEntries()) {
                if (logEntry.getUuid() == uuid) {
                    return tl::optional<LogEntry>(logEntry);
                }
            }
        }

        return tl::nullopt;
    }

    tl::optional<TraceInput> LogTracer::getInputByUuid(sole::uuid uuid) {
        return getInputByUuid(uuid, tl::nullopt);
    }

    tl::optional<TraceInput> LogTracer::getInputByUuid(sole::uuid uuid, tl::optional<sole::uuid> outputUuid) {
        for (auto &traceOutput : traceOutputs) {
            // if an outputUuid is provided we can narrow down the relevant data
            if (outputUuid.has_value() && outputUuid != traceOutput.getUuid()) {
                continue;
            }
            for (auto &traceInput : traceOutput.getInputs()) {
                if (traceInput.getUuid() == uuid) {
                    return tl::optional<TraceInput>(traceInput);
                }
            }
        }

        // dont forget current input
        for (auto &traceInput : currInputGroup) {
            if (traceInput.getUuid() == uuid) {
                return tl::optional<TraceInput>(traceInput);
            }
        }

        return tl::nullopt;
    }

    tl::optional<TraceOutput> LogTracer::getOutputByUuid(sole::uuid uuid) {
        for (auto &traceOutput : traceOutputs) {
            if (traceOutput.getUuid() == uuid) {
                return tl::optional<TraceOutput>(traceOutput);
            }
        }

        return tl::nullopt;
    }

    tl::optional<TraceOutput> LogTracer::getOutputByInputUuid(sole::uuid uuid) {
        for (auto &traceOutput : traceOutputs) {
            for (auto &traceInput : traceOutput.getInputs()) {
                if (traceInput.getUuid() == uuid) {
                    return tl::optional<TraceOutput>(traceOutput);
                }
            }
        }

        return tl::nullopt;
    }

    void
    LogTracer::sendInternalData(sole::uuid reqUuid, sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid) {
        tl::optional<LogEntry> logEntry = getLogEntryByUuid(logUuid, outputUuid);

        if (!logEntry.has_value()) {
            return;
        }

        tl::optional<TraceInput> traceInput = getInputByUuid(inputUuid, outputUuid);

        std::string serializedInput;
        if (traceInput.has_value()) {
            serializedInput = traceInput.value().getSerializedInput();
        }

        InternalDataResponse res(
                sourcesOfPortsMap,
                getVariableSnapshot(logEntry.value().getTime()),
                serializedInput,
                getTraceUuids(inputUuid)
        );
        interface->response(reqUuid, dataToJson(res));
    }

    void
    LogTracer::sendTraceData(sole::uuid reqUuid, sole::uuid traceUuid) {
        // traceUuid is an output uuid
        /*
        tl::optional<TraceOutput> relevantTraceOutput = getOutputByUuid(traceUuid);
        if (!relevantTraceOutput.has_value()) {
            return;
        }*/

        std::map<std::string, std::string> emptyVarSnapshot;
        std::multimap<sole::uuid, std::string> emptyTraceUuids;
        InternalDataResponse res(
                sourcesOfPortsMap,
                emptyVarSnapshot,
                "",
                emptyTraceUuids
        );
        interface->response(reqUuid, dataToJson(res));
    }

    void
    LogTracer::mapPortToSourceInstance(std::string portName, std::string instanceName) {
        sourcesOfPortsMap[portName] = instanceName;
    }
}