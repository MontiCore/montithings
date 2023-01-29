#ifndef AUTOREGRESSIVE_ANOMALY_DETECTION_HEADER
#define AUTOREGRESSIVE_ANOMALY_DETECTION_HEADER

#include <vector>
#include <algorithm>
#include "MTLinearRegression.h"

class AutoregressiveAnomalyDetection
{
private:
  MTLinearRegression *lr;
  int window_size;
  float tolerance;
  std::vector<float> get_regression_values(std::vector<float> past_values);

public:
  AutoregressiveAnomalyDetection(int window_size, float tolerance)
  {
    this->window_size = window_size;
    this->tolerance = tolerance;
    this->lr = new MTLinearRegression();
  }
  bool is_anomaly(float input, std::vector<float> past_values);
};

#endif