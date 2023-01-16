#include "AutoregressiveAnomalyDetection.h"

bool AutoregressiveAnomalyDetection::is_anomaly(float input)
{
  std::vector<float> regression_values = this->get_regression_values();

  this->past_values.push_back(input);

  float predicted_value = this->lr->predict_value(input, regression_values);

  float err = std::abs(predicted_value - input);

  return err > this->tolerance;
}

std::vector<float> AutoregressiveAnomalyDetection::get_regression_values()
{
  int values_len = std::min({this->window_size, static_cast<int>(this->past_values.size())});

  std::vector<float> values(values_len);

  for (int i = 0; i < values_len; i++)
  {
    values.push_back(this->past_values[i]);
  }

  return values;
}