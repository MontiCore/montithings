// (c) https://github.com/MontiCore/monticore
#include "gtest/gtest.h"
#include "Source.h"
#include "Sink.h"

TEST(ExampleTestSuite, SimpleTargetGuardTest) {

    montithings::hierarchy::SinkImpl impl;
    montithings::hierarchy::SinkInput input;

    /*input.addInportFor_source9_outportElement(tl::optional<int>(6));
    montithings::ad_test_decision::MergeDecision2Result result = impl.compute(input);

    int expected_output = 6;
    EXPECT_EQ(result.getOutportFor_sink7_inport().value(), expected_output);
    EXPECT_EQ(result.getOutportFor_sink9_inport().value(), expected_output);*/
}


