// (c) https://github.com/MontiCore/monticore
#include "AutoregressiveAnomalyDetection.h"

bool AutoregressiveAnomalyDetection::is_anomaly(float input, std::vector<float> past_values)
{
  std::vector<float> regression_values = this->get_regression_values(past_values);

  float predicted_value = this->lr->predict_value(input, regression_values);

  float err = std::abs(predicted_value - input);

  return err > this->tolerance;
}

std::vector<float> AutoregressiveAnomalyDetection::get_regression_values(std::vector<float> past_values)
{
  int values_len = std::min({this->window_size, static_cast<int>(past_values.size())});

  std::vector<float> values;

  int i = past_values.size();
  int count = 0;

  while (count < values_len)
  {
    count++;
    i--;
    values.push_back(past_values[i]);
  }

  return values;
}