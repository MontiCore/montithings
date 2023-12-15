// (c) https://github.com/MontiCore/monticore
#include "MultivariateAutoregressiveAnomalyDetection.h"

std::vector<bool>
MultivariateAutoregressiveAnomalyDetection::is_anomaly(std::vector<float> inputs,
                                                       std::vector<std::vector<float>> past_values)
{
  std::vector<bool> res(inputs.size());

  std::vector<float> regression_values = this->get_regression_values(past_values);

  for (int i = 0; i < inputs.size(); i++)
  {
    float predicted_value = this->lr->predict_value(inputs[i], regression_values);

    float err = std::abs(predicted_value - inputs[i]);

    res[i] = err > this->tolerance;
  }

  return res;
}

std::vector<float>
MultivariateAutoregressiveAnomalyDetection::get_regression_values(
    std::vector<std::vector<float>> past_values)
{
  int values_len = std::min({this->window_size, static_cast<int>(past_values.size())});

  std::vector<float> values;

  int i = past_values.size();
  int count = 0;

  while (count < values_len)
  {
    count++;
    i--;

    for (int j = 0; j < past_values[i].size(); j++)
    {
      values.push_back(past_values[i][j]);
    }
  }

  return values;
}