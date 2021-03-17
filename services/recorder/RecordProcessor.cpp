/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "RecordProcessor.h"

long long
RecordProcessor::getFirstTimestamp(std::vector<DDSRecorderMessage::Message> debugStorage,
                                   std::unordered_map<long, long> recordMessageDelays) {
    LOG_SCOPE_F (1, "Searching for the earliest timestamp...");
    long long timestamp_start = debugStorage.front().timestamp;

    for (const auto &record : debugStorage) {
        long recordTsAdjusted = record.timestamp - recordMessageDelays[record.id];
        if (recordTsAdjusted < timestamp_start) {
            timestamp_start = recordTsAdjusted;
            LOG_SCOPE_F (2, "Current earliest timestamp = %ld", recordTsAdjusted);
        }
    }

    return timestamp_start;
}

std::unordered_map<long, long>
RecordProcessor::collectMessageDelays(const std::vector<DDSRecorderMessage::Message> &debugStorage,
                                      const std::string &identifier) {
    LOG_SCOPE_F (INFO, "Collecting Message Delays (%s)...", identifier.c_str());
    std::unordered_map<long, long> allMessageDelays;

    for (const auto &record : debugStorage) {
        json messageDelays = json::parse(record.message_delays.in());
        for (auto &delay : messageDelays[identifier]) {
            long id = delay[0];
            long value = delay[1];
            allMessageDelays[id] = value;
        }
    }

    return allMessageDelays;
}

json
RecordProcessor::process(const std::vector<DDSRecorderMessage::Message> &debugStorage) {
    LOG_SCOPE_F (INFO, "Processing records...");
    LOG_F (INFO, "Calculating transport delays...");

    std::unordered_map<long, long> messageDelays = collectMessageDelays(debugStorage, "messages");
    LOG_F (INFO, "Found %ld delays.", messageDelays.size());

    std::unordered_map<long, long> recordMessageDelays
            = collectMessageDelays(debugStorage, "record_messages");
    LOG_F (INFO, "Found %ld delays.", recordMessageDelays.size());

    LOG_F (INFO, "Adjusting timestamps and adding delays...");
    long long timestamp_start = getFirstTimestamp(debugStorage, recordMessageDelays);

    json records;

    for (const auto &record : debugStorage) {
        json jRecord = json::object();

        jRecord["msg_content"] = record.msg_content.in();

        // clock skew can lead to negative values
        long long ts_adjusted = record.timestamp - timestamp_start - recordMessageDelays[record.msg_id];
        if (ts_adjusted < 0) {
            jRecord["timestamp"] = 0;
        } else {
            jRecord["timestamp"] = ts_adjusted;
        }
        jRecord["delay"] = messageDelays[record.msg_id]; // what if no entry?
        jRecord["topic"] = record.topic.in();

        jRecord["_vclocksum"] = 0;
        auto vectorClock = jsonToData<vclock>(record.serialized_vector_clock.in());
        for (auto &clock : vectorClock) {
            jRecord["vclock"][clock.first] = clock.second;
            jRecord["_vclocksum"] = jRecord["_vclocksum"].get<int>() + clock.second;
        }

        records[record.instance_name.in()].push_back(jRecord);
    }

    LOG_F (INFO, "Sorting records (primary vector clock, secondary timestamp) ...");
    for (auto &instance : records.items()) {
        LOG_F (INFO, "Sorting %d records for %s", records[instance.key()].size(), instance.key().c_str());
        records[instance.key()] = sortRecords(records[instance.key()]);
    }

    return records;
}

json RecordProcessor::sortRecords(json records) {
    int minVClockSum = INT_MAX;
    int maxVClockSum = 0;

    // First find min and max clock values
    for (auto &record : records) {
        if (record["_vclocksum"].get<int>() > maxVClockSum) {
            maxVClockSum = record["_vclocksum"].get<int>();
        }

        if (record["_vclocksum"].get<int>() < minVClockSum) {
            minVClockSum = record["_vclocksum"].get<int>();
        }
    }

    json sortedRecords = json::array();
    int referenceClockSum = minVClockSum;

    while (referenceClockSum <= maxVClockSum) {
        // search for all record which have the value as the reference clock
        std::vector<json> sameClockRecords;

        for (auto &record : records) {
            int recordClockSum = record["_vclocksum"].get<int>();
            if (recordClockSum == referenceClockSum) {
                sameClockRecords.push_back(record);
            }
        }

        // sort these by the timestamp
        while (!sameClockRecords.empty()) {
            json earliestRecord = sameClockRecords[0];
            for (json &record : sameClockRecords) {
                if (record["timestamp"] < earliestRecord["timestamp"]) {
                    earliestRecord = record;
                }
            }
            sortedRecords.push_back(earliestRecord);
            sameClockRecords.erase(std::remove(sameClockRecords.begin(), sameClockRecords.end(), earliestRecord),
                                   sameClockRecords.end());
        }

        referenceClockSum++;
    }

    LOG_F (INFO, "Adjusting timestamps if necessary...");
    if (sortedRecords.size() > 0) {
        long long lastTimestamp = sortedRecords[0]["timestamp"].get<long long>();
        for (auto record : sortedRecords) {
            if (record["timestamp"] < lastTimestamp) {
                LOG_F (INFO, "Adjusted timestamp: %lld -> %lld", record["timestamp"].get<long long>(), lastTimestamp);
                record["timestamp"] = lastTimestamp;
            }
            lastTimestamp = record["timestamp"];
        }

    }
    return sortedRecords;
}