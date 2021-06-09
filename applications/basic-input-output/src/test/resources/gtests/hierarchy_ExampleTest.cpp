// (c) https://github.com/MontiCore/monticore
#include "Example.h"
#include "Sink.h"
#include "Source.h"
#include "easyloggingpp/easylogging++.h"
#include "gtest/gtest.h"
#include <chrono>
#include <thread>

INITIALIZE_EASYLOGGINGPP

struct ExampleTest : testing::Test
{
  montithings::hierarchy::Example *cmp;
  montithings::hierarchy::Source *source;
  montithings::hierarchy::Sink *sink;
  montithings::hierarchy::SourceImpl *sourceImpl;
  montithings::hierarchy::SinkImpl *sinkImpl;
  montithings::hierarchy::SourceState sourceState;
  montithings::hierarchy::SinkState sinkState;

  ExampleTest ()
  {
    cmp = new montithings::hierarchy::Example ("example");
    source = cmp->getSubcomp__Source();
    sink = cmp->getSubcomp__Sink();
    sourceImpl = source->getImpl();
    sinkImpl = sink->getImpl();
  }
  ~ExampleTest ()
  {
    delete cmp;
  }
};



// Check that Example component does not crash
TEST_F (ExampleTest, MainTEST)
{
  cmp->setUp (EVENTBASED);
  cmp->init ();

  for (int i = 0; i < 2; i++)
    {
      auto end = std::chrono::high_resolution_clock::now () + std::chrono::milliseconds (50);
      cmp->compute ();
      do
        {
          std::this_thread::yield ();
          std::this_thread::sleep_for (std::chrono::milliseconds (1));
        }
      while (std::chrono::high_resolution_clock::now () < end);
    }
}

// Check that Source component produces correct values
TEST_F (ExampleTest, SourceTEST)
{
  montithings::hierarchy::SourceResult result = {};

  for (int i = 0; i < 33; i++)
    {
      result = sourceImpl->compute__Every1 (montithings::hierarchy::SourceInput ());
      ASSERT_TRUE (result.getValue ().has_value ());
      EXPECT_EQ (result.getValue ().value (), i);
    }
}

/**
 * This (abstract) class records all messages going through port.
 * The recorded messages can then be checked by the test case against the expected values.
 *
 * \tparam ComponentType class of the component this spy is attached to
 * \tparam PortType typename of the messages going through the port
 */
template <typename ComponentType, typename PortType> class PortSpy : public EventObserver
{
protected:
  ComponentType *component;
  std::vector<tl::optional<PortType>> recordedMessages;

public:
  explicit PortSpy (ComponentType *component) : component (component) {}

  const std::vector<tl::optional<PortType>> &
  getRecordedMessages () const
  {
    return recordedMessages;
  }
};

/**
 * This class records values of the "Source" component's "value" port
 */
class PortSpy_Source_Value : public PortSpy<montithings::hierarchy::Source, int>
{
public:
  using PortSpy::PortSpy;

  void
  onEvent () override
  {
    tl::optional<int> value
        = component->getInterface ()->getPortValue ()->getCurrentValue (this->getUuid ());
    recordedMessages.push_back (value);
  }
};

/**
 * This class records values of the "Sink" component's "value" port
 */
class PortSpy_Sink_Value : public PortSpy<montithings::hierarchy::Sink, int>
{
public:
  using PortSpy::PortSpy;

  void
  onEvent () override
  {
    tl::optional<int> value
        = component->getInterface ()->getPortValue ()->getCurrentValue (this->getUuid ());
    recordedMessages.push_back (value);
  }
};

// Check that Source produces correct values
TEST_F (ExampleTest, SourcePort)
{
  // Given
  PortSpy_Source_Value portSpyExampleSourceValue (source);
  source->getInterface ()->getPortValue ()->attach (&portSpyExampleSourceValue);

  // When
  for (int i = 0; i < 33; i++)
    {
      sourceImpl->compute__Every1 (montithings::hierarchy::SourceInput ());
    }

  // Then
  for (int i = 0; i < 33; i++)
    {
      ASSERT_TRUE (portSpyExampleSourceValue.getRecordedMessages ().at (i).has_value ());
      EXPECT_EQ (portSpyExampleSourceValue.getRecordedMessages ().at (i).value (), i);
    }
}


// Check that Source is correctly connected to Sink (i.e. Sink receives Source's messages)
TEST_F (ExampleTest, Wiring)
{
  // Given
  PortSpy_Source_Value portSpyExampleSourceValue (source);
  source->getInterface ()->getPortValue ()->attach (&portSpyExampleSourceValue);
  PortSpy_Sink_Value portSpySinkValue (sink);
  sink->getInterface ()->getPortValue ()->attach (&portSpySinkValue);

  // When
  cmp->setUp(EVENTBASED);
  cmp->init();
  for (int i = 0; i < 33; i++)
  {
    source->compute__Every1 ();
  }

  // Then
  for (int i = 0; i < 2; i++)
  {
    ASSERT_TRUE (portSpyExampleSourceValue.getRecordedMessages ().at (i).has_value ());
    EXPECT_EQ (portSpyExampleSourceValue.getRecordedMessages ().at (i).value (), i);
    ASSERT_TRUE (portSpySinkValue.getRecordedMessages ().at (i).has_value ());
    EXPECT_EQ (portSpySinkValue.getRecordedMessages ().at (i).value (), i);
  }
}