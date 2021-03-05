/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "RecordProcessor.h"

long long
RecordProcessor::getFirstTimestamp (std::vector<DDSRecorderMessage::Message> debugStorage,
                                    std::unordered_map<long, long> recordMessageDelays)
{
  LOG_SCOPE_F (1, "Searching for the earliest timestamp...");
  long long timestamp_start = debugStorage.front ().timestamp;

  for (const auto &record : debugStorage)
    {
      long recordTsAdjusted = record.timestamp - recordMessageDelays[record.id];
      if (recordTsAdjusted < timestamp_start)
        {
          timestamp_start = recordTsAdjusted;
          LOG_SCOPE_F (2, "Current earliest timestamp = %ld", recordTsAdjusted);
        }
    }

  return timestamp_start;
}

std::unordered_map<long, long>
RecordProcessor::collectMessageDelays (const std::vector<DDSRecorderMessage::Message> &debugStorage,
                                       const std::string &identifier)
{
  LOG_SCOPE_F (INFO, "Collecting Message Delays (%s)...", identifier.c_str ());
  std::unordered_map<long, long> allMessageDelays;

  for (const auto &record : debugStorage)
    {
      nlohmann::json messageDelays = nlohmann::json::parse (record.message_delays.in ());
      for (auto &delay : messageDelays[identifier])
        {
          long id = delay[0];
          long value = delay[1];
          allMessageDelays[id] = value;
        }
    }

  return allMessageDelays;
}

nlohmann::json
RecordProcessor::process (const std::vector<DDSRecorderMessage::Message> &debugStorage)
{
  LOG_SCOPE_F (INFO, "Processing records...");
  LOG_F (INFO, "Calculating transport delays...");

  std::unordered_map<long, long> messageDelays = collectMessageDelays (debugStorage, "messages");
  LOG_F (INFO, "Found %ld delays.", messageDelays.size ());

  std::unordered_map<long, long> recordMessageDelays
      = collectMessageDelays (debugStorage, "record_messages");
  LOG_F (INFO, "Found %ld delays.", recordMessageDelays.size ());

  LOG_F (INFO, "Adjusting timestamps and adding delays...");
  long long timestamp_start = getFirstTimestamp (debugStorage, recordMessageDelays);

  nlohmann::json records;
  for (const auto &record : debugStorage)
    {
      nlohmann::json jRecord = nlohmann::json::object ();
      jRecord["id"] = record.msg_id;
      jRecord["msg_content"] = record.msg_content.in ();
      jRecord["timestamp"]
          = record.timestamp - timestamp_start - recordMessageDelays[record.msg_id];
      jRecord["delay"] = messageDelays[record.msg_id]; // what if no entry?
      jRecord["topic"] = record.topic.in ();

      records[record.instance_name.in ()].push_back (jRecord);
    }
  return records;
}