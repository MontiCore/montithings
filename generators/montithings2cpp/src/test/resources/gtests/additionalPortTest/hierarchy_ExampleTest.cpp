// (c) https://github.com/MontiCore/monticore
#include "Example.h"
#include "Sink.h"
#include "Source.h"
#include "gtest/gtest.h"
#include <chrono>
#include <thread>

struct ExampleTest : testing::Test
{
  montithings::hierarchy::Example *cmp;
  montithings::hierarchy::Source *source;
  montithings::hierarchy::SourceImpl *sourceImpl;
  montithings::hierarchy::SinkImpl *sink;

  ExampleTest ()
  {
    cmp = new montithings::hierarchy::Example ("example");
    source = new montithings::hierarchy::Source ("example.sink");
    sourceImpl = new montithings::hierarchy::SourceImpl ();
    sink = new montithings::hierarchy::SinkImpl ();
  }
  ~ExampleTest ()
  {
    delete cmp;
    delete source;
    delete sourceImpl;
    delete sink;
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
  montithings::hierarchy::SourceResult result;
  source->setUp (EVENTBASED);
  for (int i = 1; i < 33; i++)
    {
      montithings::hierarchy::SourceInput input (source->getPortSensor ()->getCurrentValue (source->getUuid()));
      result = sourceImpl->compute (input);
      ASSERT_TRUE (result.getValue ().has_value ());
      EXPECT_EQ (result.getValue ().value (), i * 2);
    }
}