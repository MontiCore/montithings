/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "RecordProcessor.h"

long long
RecordProcessor::getFirstTimestamp(std::vector<DDSRecorderMessage::Message> messageStorage,
                                   json recordMessageDelays) {
    LOG_SCOPE_F (1, "Searching for the earliest timestamp...");
    long long timestamp_start = messageStorage.front().timestamp;
    for (auto &messageDelaySet : recordMessageDelays.items()) {
        for (const auto &record : messageStorage) {
            long recordTsAdjusted = record.timestamp;
            std::string recordId = std::to_string(record.id);
            if (!recordMessageDelays[messageDelaySet.key()][recordId]["recorder"].empty()) {
                recordTsAdjusted -= recordMessageDelays[messageDelaySet.key()][recordId]["recorder"].get<long>();
            } else {
                LOG_F (1, "No delay information found for record id %d ", record.id);
            }

            if (recordTsAdjusted < timestamp_start) {
                timestamp_start = recordTsAdjusted;
                LOG_F (2, "Current earliest timestamp = %ld", recordTsAdjusted);
            }
        }
    }

    return timestamp_start;
}

json
RecordProcessor::collectMessageDelays(const std::vector<DDSRecorderMessage::Message> &messageStorage,
                                      const std::string &identifier) {
    LOG_SCOPE_F (INFO, "Collecting Message Delays (%s)...", identifier.c_str());

    json allMessageDelays;

    for (const auto &record : messageStorage) {
        json messageDelays = json::parse(record.message_delays.in());

        for (auto &delay : messageDelays[identifier]) {
            // format: {"messages":[[<message id>,[<received_instance>,<actual delay>]], [...
            long id = delay[0];
            std::string receivingInstance = delay[1][0];
            long value = delay[1][1];
            allMessageDelays[record.topic.in()][std::to_string(id)][receivingInstance] = value;
        }
    }

    for (auto &delays : allMessageDelays.items()) {
        LOG_F (INFO, "Found %ld %s delays for %s.", delays.value().size(), identifier.c_str(), delays.key().c_str());
    }

    return allMessageDelays;
}

json
RecordProcessor::process(const std::vector<DDSRecorderMessage::Message> &messageStorage) {
    LOG_SCOPE_F (INFO, "Processing records...");
    LOG_F (INFO, "Calculating transport delays...");

    json messageDelays = collectMessageDelays(messageStorage, "messages");

    json recordMessageDelays = collectMessageDelays(messageStorage, "record_messages");

    LOG_F (INFO, "Adjusting timestamps and adding delays...");
    long long timestamp_start = getFirstTimestamp(messageStorage, recordMessageDelays);

    json records;

    for (const auto &record : messageStorage) {
        std::string id = std::to_string(record.id);
        std::string msgId = std::to_string(record.msg_id);

        json jRecord = json::object();

        jRecord["_recorder_id"] = record.id;
        jRecord["msg_id"] = record.msg_id;

        jRecord["msg_content"] = record.msg_content.in();


        // clock skew can lead to negative values
        long long ts_adjusted =
                record.timestamp
                - timestamp_start;


        if (recordMessageDelays[record.topic.in()][id]["recorder"].size()) {
            // .get<long>() does not work for some reason, workaround= dump & convert
            ts_adjusted -= std::stol(recordMessageDelays[record.topic.in()][id]["recorder"].dump());
        } else {
            LOG_F (1, "No recordMessageDelay for %s (id %s)", record.topic.in(), id.c_str());
        }

        if (ts_adjusted < 0) {
            jRecord["timestamp"] = 0;
        } else {
            jRecord["timestamp"] = ts_adjusted;
        }

        for (auto &item : messageDelays[record.topic.in()][msgId].items()) {
            jRecord["delay"][ item.key()] =  item.value();
        }

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
        LOG_F (INFO, "Sorting %ld records for %s", records[instance.key()].size(), instance.key().c_str());
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