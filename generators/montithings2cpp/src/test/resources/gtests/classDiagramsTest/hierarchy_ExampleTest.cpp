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
  ASSERT_FALSE (result.getValue().has_value());

  for (int i = 2; i < 100; i++)
    {
      result = source->compute (montithings::hierarchy::SourceInput());
      ASSERT_TRUE (result.getValue ().has_value ());
      bool valueInRange = 0<=result.getValue ().value () && result.getValue ().value ()<4;
      EXPECT_EQ (valueInRange, 1);
    }
}

TEST_F (ExampleTest, AdapterTEST)
{
  montithings::hierarchy::ColorsAdapter adapter;
  uint8_t intValue;
  montithings::hierarchy::Colors::Color color;
  for (int i = 0; i < 4; i++)
    {
      intValue = i;
      color = adapter.convert (intValue);
      EXPECT_EQ (adapter.convert (color), intValue);
    }
  intValue = 5;
  color = adapter.convert (intValue);
  EXPECT_EQ (adapter.convert (color), 0);
  color = montithings::hierarchy::Colors::Color::RED;
  intValue = adapter.convert (color);
  EXPECT_EQ (adapter.convert (intValue), color);
  color = montithings::hierarchy::Colors::Color::GREEN;
  intValue = adapter.convert (color);
  EXPECT_EQ (adapter.convert (intValue), color);
  color = montithings::hierarchy::Colors::Color::BLUE;
  intValue = adapter.convert (color);
  EXPECT_EQ (adapter.convert (intValue), color);
  color = montithings::hierarchy::Colors::Color::YELLOW;
  intValue = adapter.convert (color);
  EXPECT_EQ (adapter.convert (intValue), color);
}