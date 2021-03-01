/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "DDSRecorder.h"

#include <utility>

void
DDSRecorder::init ()
{
  ddsCommunicator.setVerbose (false);
  ddsCommunicator.setPortIdentifier (portIdentifier);
  ddsCommunicator.initConfig ();
  ddsCommunicator.initParticipant ();
  ddsCommunicator.initMessageTypes ();
  ddsCommunicator.initTopics ();
  ddsCommunicator.initSubscriber ();
  ddsCommunicator.initReaderCommandMessage ();
  if (isOutgoingPort ())
    {
      ddsCommunicator.initReaderAcknowledgement (true);
      ddsCommunicator.addOnAcknowledgementMessageCallback (
          std::bind (&DDSRecorder::onAcknowledgementMessage, this, std::placeholders::_1));
    }

  ddsCommunicator.addOnCommandMessageCallback (
      std::bind (&DDSRecorder::onCommandMessage, this, std::placeholders::_1));
}

void
DDSRecorder::setInstanceName (std::string name)
{
  instanceName = std::move(name);
}

void
DDSRecorder::setPortIdentifier (std::string name)
{
  portIdentifier = std::move(name);
  ddsCommunicator.setPortIdentifier (portIdentifier);
}

void
DDSRecorder::start ()
{
  std::cout << "DDSRecorder | starting recording... " << std::endl;
  HWCInterceptor::startNondeterministicRecording ();
  HWCInterceptor::storage["instance"] = instanceName;

  ddsCommunicator.initPublisher ();
  ddsCommunicator.initWriter ();
}

void
DDSRecorder::stop ()
{
  std::cout << "DDSRecorder | stopping recording... " << std::endl;
  HWCInterceptor::stopNondeterministicRecording ();
  ddsCommunicator.cleanupRecorderMessageWriter ();
}

void
DDSRecorder::sendNDCalls (int commandId)
{
  std::cout << "DDSRecorder | sendNDCalls" << std::endl;
  HWCInterceptor::storage["instance"] = instanceName;

  DDSRecorderMessage::CommandReply message;
  message.command_id = commandId;
  message.instance_name = instanceName.c_str ();
  message.content = HWCInterceptor::storage.dump ().c_str ();
  message.id = 0;

  std::cout << "DDSRecorder | sendNDCalls:" << message.content << std::endl;
  ddsCommunicator.send (message);

  // std::cout << "DDSRecorder | Waiting for ACKs " << std::endl;
  // ddsCommunicator.commandReplyWaitForAcks();

  std::cout << "DDSRecorder | Cleaning up commandReply writers" << std::endl;
  ddsCommunicator.cleanupCommandReplyMessageWriter ();
  std::cout << "DDSRecorder | Cleaning up commandReply writers done" << std::endl;
}

void
DDSRecorder::sendInternalRecords ()
{
  // std::cout << "DDSRecorder | sendInternalRecords" << std::endl;

  DDSRecorderMessage::Message recorderMessage;
  recorderMessage.id = messageId;
  recorderMessage.instance_name = instanceName.c_str ();
  recorderMessage.type = DDSRecorderMessage::INTERNAL_RECORDS;

  nlohmann::json content;
  content["calls"] = HWCInterceptor::storageCalls;
  content["calc_latency"] = HWCInterceptor::storageComputationLatency;
  recorderMessage.msg_content = content.dump ().c_str ();

  // This can be dangerous...
  HWCInterceptor::storageCalls.clear ();
  HWCInterceptor::storageComputationLatency.clear ();

  messageId++;

  // std::cout << "DDSRecorder | sendInternalRecords:" << recorderMessage.msg_content << std::endl;
  ddsCommunicator.send (recorderMessage);
}

std::string
DDSRecorder::getSendingInstanceNameFromTopic (const std::string &topicId)
{
  return topicId.substr (0, topicId.find_last_of ('.'));
}

bool
DDSRecorder::isOutgoingPort ()
{
  std::string sendingInstance = getSendingInstanceNameFromTopic (portIdentifier);
  return sendingInstance == instanceName;
}

void
DDSRecorder::recordMessage (DDSMessage::Message message, char *topicName,
                            const std::unordered_map<std::string, long>& newVectorClock)
{
  // std::cout << "DDSRecorder | recordMessage | Size of nd storage: " << Recorder::storage.size ()
  // << std::endl;

  if (HWCInterceptor::isRecording)
    {
      long long timestamp = Util::Time::getCurrentTimestampNano ();

      // Only send ack if message was received, not sent
      if (!isOutgoingPort ())
        {
          std::string sendingInstance = getSendingInstanceNameFromTopic (topicName);
          updateVectorClock (newVectorClock, sendingInstance);
          ddsCommunicator.sendAck (message.id, portIdentifier, getSerializedVectorClock ());
        }
      else
        {
          // std::cout << "DDSRecorder | adding  unackedMessageTimestampMap " << message.id
          // << " ts: " << timestamp << std::endl;
          // message was sent and not received. Thus, add message to the map of unacked messages
          unackedMessageTimestampMap[message.id] = timestamp;
          unackedRecordedMessageTimestampMap[messageId] = timestamp;
        }

      // Remove new lines from content
      std::string content{ message.content.in () };
      content.erase (std::remove (content.begin (), content.end (), '\n'), content.end ());

      nlohmann::json jUnsentDelays;
      jUnsentDelays["messages"] = unsentMessageDelays;
      unsentMessageDelays.clear ();
      jUnsentDelays["record_messages"] = unsentRecordMessageDelays;
      unsentRecordMessageDelays.clear ();

      DDSRecorderMessage::Message recorderMessage;
      recorderMessage.id = messageId;
      recorderMessage.instance_name = instanceName.c_str ();
      recorderMessage.type = DDSRecorderMessage::MESSAGE_RECORD;
      recorderMessage.msg_id = message.id;
      recorderMessage.msg_content = content.c_str ();
      recorderMessage.timestamp = timestamp;
      recorderMessage.topic = topicName;
      recorderMessage.message_delays = jUnsentDelays.dump ().c_str ();

      // std::cout << "DDSRecorder | sending .. " << std::endl;
      ddsCommunicator.send (recorderMessage);

      ++messageId;

      sendInternalRecords ();
    }
}

void
DDSRecorder::onCommandMessage (const DDSRecorderMessage::Command &command)
{
  switch (command.cmd)
    {
    case DDSRecorderMessage::RECORDING_START:
      start ();
      break;
    case DDSRecorderMessage::RECORDING_STOP:
      stop ();
      break;
    case DDSRecorderMessage::SEND_INTERNAL_ND_CALLS:
      sendNDCalls (command.id);
      break;
    default:
      std::cerr << "DDSRecorder | onCommandMessage: unknown command" << std::endl;
    }
}

void
DDSRecorder::onAcknowledgementMessage (const DDSRecorderMessage::Acknowledgement& ack)
{
  std::cout << "onAcknowledgementMessage: " << ack.id << ", " << ack.instance.in () << ", "
            << ack.serialized_vector_clock.in () << ", " << ack.acked_id << std::endl;

  if (strcmp (ack.instance.in (), "recorder") == 0)
    {
      handleAck (unackedRecordedMessageTimestampMap, unsentRecordMessageDelays, ack.acked_id);
    }
  else if (isOutgoingPort ())
    {
      handleAck (unackedMessageTimestampMap, unsentMessageDelays, ack.acked_id);
    }
}

void
DDSRecorder::handleAck (std::unordered_map<long, long long> &unackedMap,
                        std::unordered_map<long, long long> &unsentDelayMap, long ackedId)
{
  long long timestamp_ack_received = Util::Time::getCurrentTimestampNano ();

  if (unackedMap.count (ackedId) == 0)
    {
      std::cerr << "no entry found for id " << ackedId << " in unackedMessageTimestampMap!"
                << std::endl;
      for (auto x : unackedMap)
        {
          std::cout << x.first << " " << x.second << std::endl;
        }
      return;
    }

  long long timestamp_sent = unackedMap[ackedId];
  // TODO: change by message size
  long delay = (timestamp_ack_received - timestamp_sent) / 2;

  unsentDelayMap[ackedId] = delay;

  // Delete entry
  unackedMap.erase (ackedId);
}