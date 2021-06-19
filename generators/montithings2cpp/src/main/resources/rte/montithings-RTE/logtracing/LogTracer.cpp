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
    LogTracer::getCurrOutputUuid() {
        return currTraceOutput.getUuid();
    }


    void LogTracer::handleOutput() {
        currTraceOutput.setInputs(currInputGroup);
        currInputGroup.clear();

        traceOutputs.push_back(currTraceOutput);

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
        std::multimap<sole::uuid, std::string> res;

        tl::optional<TraceInput> traceInput = getInputByUuid(inputUuid);

        if (traceInput.has_value()) {
            res.insert(traceInput.value().getTraceIdsWithPortNames().begin(),
                       traceInput.value().getTraceIdsWithPortNames().end());
        }

        return res;
    }

    std::multimap<sole::uuid, std::string>
    LogTracer::getTraceUuidsDecomposed(sole::uuid outputUuid) {
        std::multimap<sole::uuid, std::string> res;
        tl::optional<TraceOutput> traceOutput = getOutputByUuid(outputUuid);

        if (traceOutput.has_value()) {
            res.insert(traceOutput.value().getTraceIdsWithPortNames().begin(),
                       traceOutput.value().getTraceIdsWithPortNames().end());
        }

        return res;
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

        long lastIndex = 0;
        long secondaryIndex = 0;
        for (auto &traceOutput : traceOutputs) {
            for (TraceInput traceInput : traceOutput.getInputs()) {
                std::vector<LogEntry> entries = traceInput.getLogEntries();
                if (traceInput.getLogEntries().empty()) {
                    // if input does not contain any log entry, create one.

                    LogEntry virtualLogEntry(lastIndex,
                                             traceInput.getArrivalTime(), "(No log captured)",
                                             traceInput.getUuid(),
                                             traceOutput.getUuid());
                    virtualLogEntry.setIndexSecondary(++secondaryIndex);
                    res.push_back(virtualLogEntry);
                } else {
                    res.insert(
                            std::end(res),
                            std::begin(entries),
                            std::end(entries));
                    lastIndex = entries.back().getIndex();
                    secondaryIndex = 0;
                }
            }
        }

        // dont forget inputs which do not belong to an output, yet
        for (auto &traceInput : currInputGroup) {
            std::vector<LogEntry> entries = traceInput.getLogEntries();
            if (traceInput.getLogEntries().empty()) {
                // if input does not contain any log entry, create one.

                LogEntry virtualLogEntry(lastIndex,
                                         traceInput.getArrivalTime(), "(No log captured)",
                                         traceInput.getUuid(),
                                         currTraceOutput.getUuid());
                virtualLogEntry.setIndexSecondary(++secondaryIndex);
                res.push_back(virtualLogEntry);
            } else {
                res.insert(
                        std::end(res),
                        std::begin(entries),
                        std::end(entries));
                lastIndex = entries.back().getIndex();
                secondaryIndex = 0;
            }
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
        payload = dataToJson(logs);

        interface->response(reqUuid, payload);
    }

    tl::optional<LogEntry> LogTracer::getLogEntryByUuid(sole::uuid uuid) {
        for (auto &traceOutput : traceOutputs) {
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

        // and the current input
        for(auto &logEntry : currInputLogs) {
            if (logEntry.getUuid() == uuid) {
                return tl::optional<LogEntry>(logEntry);
            }
        }

        return tl::nullopt;
    }

    tl::optional<TraceInput> LogTracer::getInputByUuid(sole::uuid uuid) {
        for (auto &traceOutput : traceOutputs) {
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

            for (auto &traceUuid : traceOutput.getTraceIdsWithPortNames()) {
                if (traceUuid.first == uuid) {
                    return tl::optional<TraceOutput>(traceOutput);
                }
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
        tl::optional<LogEntry> logEntry = getLogEntryByUuid(logUuid);

        std::map<std::string, std::string> varSnapshot;

        if (logEntry.has_value()) {
            varSnapshot = getVariableSnapshot(logEntry.value().getTime());
        }

        tl::optional<TraceInput> traceInput = getInputByUuid(inputUuid);

        std::string serializedInput;
        if (traceInput.has_value()) {
            serializedInput = traceInput.value().getSerializedInput();
        }

        InternalDataResponse res(
                sourcesOfPortsMap,
                varSnapshot,
                serializedInput,
                externalPorts,
                getTraceUuids(inputUuid),
                getTraceUuidsDecomposed(outputUuid)
        );
        interface->response(reqUuid, dataToJson(res));
    }

    void
    LogTracer::sendTraceData(sole::uuid reqUuid, sole::uuid traceUuid) {
        std::map<std::string, std::string> varSnapshot;
        std::multimap<sole::uuid, std::string> traceUuids;
        std::multimap<sole::uuid, std::string> traceUuidsDecomposed;
        std::string serializedInputs;

        // traceUuid is an output uuid
        tl::optional<TraceOutput> relevantTraceOutput = getOutputByUuid(traceUuid);
        if (relevantTraceOutput.has_value()) {
            if (!relevantTraceOutput.value().getInputs().empty()) {
                TraceInput lastInput = relevantTraceOutput.value().getInputs().back();

                serializedInputs = lastInput.getSerializedInput();
                traceUuids = getTraceUuids(lastInput.getUuid());
                traceUuidsDecomposed = getTraceUuidsDecomposed(relevantTraceOutput->getUuid());
                varSnapshot = getVariableSnapshot(lastInput.getArrivalTime());
            }
        }

        InternalDataResponse res(
                sourcesOfPortsMap,
                varSnapshot,
                serializedInputs,
                externalPorts,
                traceUuids,
                traceUuidsDecomposed
        );
        interface->response(reqUuid, dataToJson(res));
    }

    void
    LogTracer::mapPortToSourceInstance(std::string portName, std::string instanceName) {
        sourcesOfPortsMap[portName] = instanceName;
    }

    void
    LogTracer::registerExternalPort(std::string portName){
        externalPorts.push_back(portName);
    }

    TraceInput &LogTracer::getCurrTraceInput() {
        return currTraceInput;
    }

    TraceOutput &LogTracer::getCurrTraceOutput() {
        return currTraceOutput;
    }

    std::vector<TraceInput> &LogTracer::getCurrInputGroup() {
        return currInputGroup;
    }
}