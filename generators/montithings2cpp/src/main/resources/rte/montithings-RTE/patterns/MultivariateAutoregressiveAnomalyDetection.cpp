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

    std::cout << "[MAD] Input " << i << " is anomaly: " << res[i]
              << " (0==false, 1==true) because " << err << " > " << this->tolerance
              << " == " << res[i] << std::endl;
  }

  return res;
}

std::vector<float>
MultivariateAutoregressiveAnomalyDetection::get_regression_values(
    std::vector<std::vector<float>> past_values)
{
  int dimension = static_cast<int>(past_values.size());

  int past_values_len = 0;

  if (past_values.size() > 0)
  {
    past_values_len = static_cast<int>(past_values[0].size());
  }

  int values_len = std::min({this->window_size * dimension, past_values_len});

  std::vector<float> values(values_len);

  for (int i = 0; i < past_values.size(); i++)
  {
    std::vector<float> values_of_dimension = this->get_regression_values(i, past_values);

    for (int j = 0; j < values_of_dimension.size(); j++)
    {
      values[i] = values_of_dimension[j];
    }
  }

  return values;
}

std::vector<float>
MultivariateAutoregressiveAnomalyDetection::get_regression_values(
    int idx, std::vector<std::vector<float>> past_values)
{
  int values_len = std::min({this->window_size, static_cast<int>(past_values[idx].size())});

  std::vector<float> values(values_len);

  for (int i = 0; i < values_len; i++)
  {
    values[i] = past_values[idx][i];
  }

  return values;
}