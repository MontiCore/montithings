#include <vector>
#include <algorithm>
#include "LinearRegression.h"

class AutoregressiveAnomalyDetection
{
private:
  LinearRegression *lr;
  int window_size;
  float tolerance;
  std::vector<float> get_regression_values(std::vector<float> past_values);

public:
  AutoregressiveAnomalyDetection(int window_size, float tolerance)
  {
    this->window_size = window_size;
    this->tolerance = tolerance;
    this->lr = new LinearRegression();
  }
  bool is_anomaly(float input, std::vector<float> past_values);
};