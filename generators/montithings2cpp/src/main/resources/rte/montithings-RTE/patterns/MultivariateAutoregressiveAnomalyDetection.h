#ifndef MULTIREGRESSIVE_ANOMALY_DETECTION_HEADER
#define MULTIREGRESSIVE_ANOMALY_DETECTION_HEADER

#include <vector>
#include <algorithm>
#include "MTLinearRegression.h"

class MultivariateAutoregressiveAnomalyDetection
{
private:
  MTLinearRegression *lr;
  int window_size;
  float tolerance;
  std::vector<float> get_regression_values(std::vector<std::vector<float>> past_values);
  std::vector<float> get_regression_values(int idx, std::vector<std::vector<float>> past_values);

public:
  MultivariateAutoregressiveAnomalyDetection(int window_size, float tolerance)
  {
    this->window_size = window_size;
    this->tolerance = tolerance;
    this->lr = new MTLinearRegression();
  }
  std::vector<bool> is_anomaly(std::vector<float> inputs, std::vector<std::vector<float>> past_values);
};

#endif