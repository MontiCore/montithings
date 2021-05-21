/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "LogTracer.h"

#include <utility>

namespace montithings {
    LogTracer::LogTracer(std::string instanceName, LogTracerInterface &interface)
            : instanceName(std::move(instanceName)), interface(&interface) {
        currInputId = uuid();
        currOutputId = uuid();
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
        LogEntry logEntry(time(nullptr),
                          content,
                          currInputId,
                          currOutputId);
        logEntries.insert({id, logEntry});
        currInputLogs.push_back(id);
    }

    sole::uuid
    LogTracer::newOutput() {
        allOutputLogs[currOutputId] = currOutputLogs;
        currOutputId = uuid();

        return currOutputId;
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
            snapshot[varSnap.first] = varSnap.second.lower_bound(time)->second;
        }

        return snapshot;
    }

    std::multimap<std::string, std::string>
    LogTracer::getTraceUuids(sole::uuid inputUuid) {
        std::multimap<std::string, std::string> res;

        typedef std::multimap<sole::uuid, sole::uuid>::iterator OIRefsIterator;
        typedef std::multimap<sole::uuid, std::string>::iterator TracePortIterator;

        std::pair<OIRefsIterator, OIRefsIterator> traces = outputInputRefs.equal_range(inputUuid);

        for (OIRefsIterator it = traces.first; it != traces.second; it++) {
            // get trace uuid associated with the input
            sole::uuid id = it->second;


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
        }

        std::cout << "got something!" << std::endl;
    }

    void LogTracer::sendLogEntries(sole::uuid reqUuid, long fromTimestamp) {
        std::string payload = dataToJson(logEntries);
        interface->response(reqUuid, payload);
    }

    void
    LogTracer::sendInternalData(sole::uuid reqUuid, sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid) {
        LogEntry *logEntry = &logEntries.at(logUuid);

        InternalDataResponse res(
                sourcesOfPortsMap,
                getVariableSnapshot(logEntry->getTime()),
                serializedInputs.at(inputUuid),
                getTraceUuids(inputUuid)
                );
        interface->response(reqUuid, dataToJson(res));
    }


    void
    LogTracer::mapPortToSourceInstance(std::string portName, std::string instanceName) {
        sourcesOfPortsMap[portName] = instanceName;
    }

}