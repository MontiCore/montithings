#include "MultivariateAutoregressivyAnomalyDetection.h"

std::vector<bool> MultivariateAutoregressivyAnomalyDetection::is_anomaly(std::vector<float> inputs)
{
  std::vector<bool> res(inputs.size());

  std::vector<float> regression_values = this->get_regression_values();

  this->past_values.push_back(inputs);

  for (int i = 0; i < inputs.size(); i++)
  {
    float predicted_value = this->lr->predict_value(inputs[i], regression_values);

    float err = std::abs(predicted_value - inputs[i]);

    res.push_back(err > this->tolerance);
  }
}

std::vector<float> MultivariateAutoregressivyAnomalyDetection::get_regression_values()
{
  int dimension = static_cast<int>(this->past_values.size());

  int past_values_len = 0;

  if (this->past_values.size() > 0)
  {
    past_values_len = static_cast<int>(this->past_values[0].size());
  }

  int values_len = std::min({this->window_size * dimension, past_values_len});

  std::vector<float> values(values_len);

  for (int i = 0; i < this->past_values.size(); i++)
  {
    std::vector<float> values_of_dimension = this->get_regression_values(i);

    for (int j = 0; j < values_of_dimension.size(); j++)
    {
      values.push_back(values_of_dimension[j]);
    }
  }

  return values;
}

std::vector<float> MultivariateAutoregressivyAnomalyDetection::get_regression_values(int idx)
{
  int values_len = std::min({this->window_size, static_cast<int>(this->past_values[idx].size())});

  std::vector<float> values(values_len);

  for (int i = 0; i < values_len; i++)
  {
    values.push_back(this->past_values[idx][i]);
  }

  return values;
}