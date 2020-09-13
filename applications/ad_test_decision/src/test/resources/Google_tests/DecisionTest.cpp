#include "gtest/gtest.h"
#include "MergeDecision1.h"
#include "MergeDecision2.h"
#include "MergeDecision3.h"

// test simple if guards
TEST(DecisionTestSuite, SimpleGuardTest) {

    montithings::ad_test_decision::MergeDecision1Impl impl;
    montithings::ad_test_decision::MergeDecision1Input input;

    input.addInportFor_source7_outportElement(tl::optional<int>(2));
    montithings::ad_test_decision::MergeDecision1Result result = impl.compute(input);

    EXPECT_FALSE(result.getOutportFor_sink5_inport().has_value());
    EXPECT_FALSE(result.getOutportFor_sink6_inport().has_value());

    input.addInportFor_source7_outportElement(tl::optional<int>(4));
    montithings::ad_test_decision::MergeDecision1Result result2 = impl.compute(input);

    int expected_output = 4;
    EXPECT_EQ(result2.getOutportFor_sink5_inport().value(), expected_output);
    EXPECT_FALSE(result2.getOutportFor_sink6_inport().has_value());


    input.addInportFor_source7_outportElement(tl::optional<int>(5));
    montithings::ad_test_decision::MergeDecision1Result result3 = impl.compute(input);

    expected_output = 5;
    EXPECT_EQ(result3.getOutportFor_sink5_inport().value(), expected_output);
    EXPECT_EQ(result3.getOutportFor_sink6_inport().value(), expected_output);
}


// test simple target followed by if guards
TEST(DecisionTestSuite, SimpleTargetGuardTest) {

    montithings::ad_test_decision::MergeDecision2Impl impl;
    montithings::ad_test_decision::MergeDecision2Input input;

    input.addInportFor_source9_outportElement(tl::optional<int>(6));
    montithings::ad_test_decision::MergeDecision2Result result = impl.compute(input);

    int expected_output = 6;
    EXPECT_EQ(result.getOutportFor_sink7_inport().value(), expected_output);
    EXPECT_EQ(result.getOutportFor_sink9_inport().value(), expected_output);
}

// test complex guards
TEST(DecisionTestSuite, ComplexGuardTest) {

    montithings::ad_test_decision::MergeDecision3Impl impl;
    montithings::ad_test_decision::MergeDecision3Input input;

    input.addInportFor_source11_outportElement(tl::optional<int>(11));
    montithings::ad_test_decision::MergeDecision3Result result = impl.compute(input);

    int expected_output = 11;
    EXPECT_EQ(result.getOutportFor_sink10_inport().value(), expected_output);
    EXPECT_FALSE(result.getOutportFor_sink11_inport().has_value());
    EXPECT_FALSE(result.getOutportFor_sink12_inport().has_value());
    EXPECT_FALSE(result.getOutportFor_sink13_inport().has_value());

    input.addInportFor_source11_outportElement(tl::optional<int>(9));
    result = impl.compute(input);

    expected_output = 9;
    EXPECT_FALSE(result.getOutportFor_sink10_inport().has_value());
    EXPECT_EQ(result.getOutportFor_sink11_inport().value(), expected_output);
    EXPECT_FALSE(result.getOutportFor_sink12_inport().has_value());
    EXPECT_FALSE(result.getOutportFor_sink13_inport().has_value());

    input.addInportFor_source11_outportElement(tl::optional<int>(6));
    result = impl.compute(input);

    expected_output = 6;
    EXPECT_FALSE(result.getOutportFor_sink10_inport().has_value());
    EXPECT_FALSE(result.getOutportFor_sink11_inport().has_value());
    EXPECT_EQ(result.getOutportFor_sink12_inport().value(), expected_output);
    EXPECT_FALSE(result.getOutportFor_sink13_inport().has_value());

    input.addInportFor_source11_outportElement(tl::optional<int>(1));
    result = impl.compute(input);

    expected_output = 1;
    EXPECT_FALSE(result.getOutportFor_sink10_inport().has_value());
    EXPECT_FALSE(result.getOutportFor_sink11_inport().has_value());
    EXPECT_FALSE(result.getOutportFor_sink12_inport().has_value());
    EXPECT_EQ(result.getOutportFor_sink13_inport().value(), expected_output);
}

