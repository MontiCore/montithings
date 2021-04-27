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
    source = new montithings::hierarchy::Source ("example.source");
    sink = new montithings::hierarchy::Sink ("example.sink");
    sourceImpl = new montithings::hierarchy::SourceImpl ("example.source", *source, sourceState,
                                                         *source->getInterface ());
    sinkImpl = new montithings::hierarchy::SinkImpl ("example.sink", *sink, sinkState,
                                                     *sink->getInterface ());
  }
  ~ExampleTest ()
  {
    delete cmp;
    delete source;
    delete sink;
    delete sourceImpl;
    delete sinkImpl;
  }
};

TEST_F (ExampleTest, MainTEST)
{
  cmp->setUp (TIMESYNC);
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

TEST_F (ExampleTest, SourceTEST)
{
  montithings::hierarchy::SourceResult result = sourceImpl->getInitialValues ();
  ASSERT_FALSE (result.getValue ().has_value ());

  for (int i = 2; i < 100; i++)
    {
      result = sourceImpl->compute (montithings::hierarchy::SourceInput ());
      ASSERT_TRUE (result.getValue ().has_value ());
      bool valueInRange
          = 0 <= result.getValueAdap ().value () && result.getValueAdap ().value () < 4;
      EXPECT_EQ (valueInRange, 1);
    }
}

TEST_F (ExampleTest, AdapterTEST)
{
  montithings::hierarchy::ColorsAdapter adapter;
  uint8_t intValue;
  montithings::Colors::Color color;
  for (int i = 0; i < 4; i++)
    {
      intValue = i;
      color = adapter.convertColor (intValue);
      EXPECT_EQ (adapter.convertColor (color), intValue);
    }
  intValue = 5;
  color = adapter.convertColor (intValue);
  EXPECT_EQ (adapter.convertColor (color), 0);
  color = montithings::Colors::Color::RED;
  intValue = adapter.convertColor (color);
  EXPECT_EQ (adapter.convertColor (intValue), color);
  color = montithings::Colors::Color::GREEN;
  intValue = adapter.convertColor (color);
  EXPECT_EQ (adapter.convertColor (intValue), color);
  color = montithings::Colors::Color::BLUE;
  intValue = adapter.convertColor (color);
  EXPECT_EQ (adapter.convertColor (intValue), color);
  color = montithings::Colors::Color::YELLOW;
  intValue = adapter.convertColor (color);
  EXPECT_EQ (adapter.convertColor (intValue), color);
}