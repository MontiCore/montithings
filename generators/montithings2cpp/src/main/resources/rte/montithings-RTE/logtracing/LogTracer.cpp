/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "LogTracer.h"

namespace montithings {
    LogTracer::LogTracer(std::string instanceName, LogTracerInterface &interface)
            : instanceName(std::move(instanceName)), interface(&interface) {
        currInputId = uuid();
        currOutputId = uuid();
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
        sole::uuid id = uuid();
        LogEntry logEntry(logEntryIndex,
                          time(nullptr),
                          content,
                          currInputId,
                          currOutputId);
        logEntries.insert({id, logEntry});
        currInputLogs.push_back(id);
        logEntryIndex++;
    }


    sole::uuid
    LogTracer::getCurrUuidAndMarkOutput() {
        atLeastOneNewOutput = true;
        return currOutputId;
    }

    sole::uuid
    LogTracer::getCurrOutputUuid() {
        return currOutputId;
    }


    void LogTracer::handleOutput() {
        allOutputLogs[currOutputId] = currOutputLogs;
        currOutputId = uuid();
        atLeastOneNewOutput = false;
    }

    void
    LogTracer::resetCurrentOutput() {
        currOutputLogs.clear();
    }

    void
    LogTracer::resetCurrentInput() {
        currOutputLogs.clear();
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
                    snapshot[varSnap.first] =varSnap.second.begin()->second;
                    continue;
                }
            }

            //otherwise find most closely recorded key
            auto itlow = varSnap.second.lower_bound(time);

            if (itlow == varSnap.second.end() || itlow->first != time) {
                if (itlow != varSnap.second.begin()){
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

    std::multimap<std::string, std::string>
    LogTracer::getTraceUuids(sole::uuid inputUuid) {
        std::multimap<std::string, std::string> res;

        typedef std::multimap<sole::uuid, sole::uuid>::iterator OIRefsIterator;
        typedef std::multimap<sole::uuid, std::string>::iterator TracePortIterator;

        std::pair<OIRefsIterator, OIRefsIterator> traces = outputInputRefs.equal_range(inputUuid);
        std::vector<sole::uuid> seenIds;

        for (OIRefsIterator it = traces.first; it != traces.second; it++) {
            // get trace uuid associated with the input
            sole::uuid id = it->second;

            // avoid duplicates
            if(std::find(seenIds.begin(), seenIds.end(), id) != seenIds.end()) {
                continue;
            }
            seenIds.push_back(id);

            // find out which port names are associated with the trace
            // not that it can be multiple ports if they are getting messages from the same source port
            std::pair<TracePortIterator, TracePortIterator> portTraces = inputTracePortNameRefs.equal_range(id);
            for (TracePortIterator it2 = portTraces.first; it2 != portTraces.second; it2++) {
                // add corresponding port name
                res.insert(std::make_pair(id.str(), it2->second));
            }
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

        std::cout << "got something!" << std::endl;
    }

    void LogTracer::sendLogEntries(sole::uuid reqUuid, long fromTimestamp) {
        std::string payload;

        if(logEntries.empty()) {
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
        } else {
            payload = dataToJson(logEntries);
        }
        interface->response(reqUuid, payload);
    }

    void
    LogTracer::sendInternalData(sole::uuid reqUuid, sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid) {
        LogEntry *logEntry = &logEntries.at(logUuid);

        std::string input = "";
        if (serializedInputs.find(inputUuid) != serializedInputs.end()) {
            input = serializedInputs.at(inputUuid);
        }

        InternalDataResponse res(
                sourcesOfPortsMap,
                getVariableSnapshot(logEntry->getTime()),
                input,
                getTraceUuids(inputUuid)
        );
        interface->response(reqUuid, dataToJson(res));
    }

    void
    LogTracer::sendTraceData(sole::uuid reqUuid, sole::uuid traceUuid) {
        sole::uuid lastUuid;
        for (auto it = outputInputRefs.end(); it != outputInputRefs.begin(); --it){
            if (it->second == traceUuid) {
                lastUuid = it->first;
            }
        }

        std::map<std::string, std::string> emptyVarSnapshot;
        std::multimap<std::string, std::string> emptyTraceUuids;
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