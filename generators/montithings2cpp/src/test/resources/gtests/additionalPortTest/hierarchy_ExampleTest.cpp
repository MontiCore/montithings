// (c) https://github.com/MontiCore/monticore
#include "gtest/gtest.h"
#include "Source.h"
#include "Sink.h"
#include "Example.h"
#include <chrono>
#include <thread>

struct ExampleTest : testing::Test
{
  montithings::hierarchy::Example* cmp;
  montithings::hierarchy::SourceImpl* source;
  montithings::hierarchy::SinkImpl* sink;

  ExampleTest() {
    cmp = new montithings::hierarchy::Example ("example");
    source = new montithings::hierarchy::SourceImpl ();
    sink = new montithings::hierarchy::SinkImpl ();
  }
  ~ExampleTest () {
      delete cmp;
      delete source;
      delete sink;
  }
};


TEST_F(ExampleTest, MainTEST) {

    cmp->setUp (TIMESYNC);
    cmp->init ();

    for (int i = 0;i<2;i++)
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
  montithings::hierarchy::SourceResult result = source->getInitialValues();
  ASSERT_TRUE (result.getValue().has_value());
  EXPECT_EQ (result.getValue().value(), 1);

  for (int i = 2; i < 33; i++)
    {
      result = source->compute (montithings::hierarchy::SourceInput());
      ASSERT_TRUE (result.getValue ().has_value ());
      EXPECT_EQ (result.getValue ().value (), i);
    }
}