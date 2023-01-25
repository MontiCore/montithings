#include <vector>
#include <algorithm>
#include "LinearRegression.h"

class MultivariateAutoregressivyAnomalyDetection
{
private:
  LinearRegression *lr;
  int window_size;
  float tolerance;
  std::vector<float> get_regression_values(std::vector<std::vector<float>> past_values);
  std::vector<float> get_regression_values(int idx, std::vector<std::vector<float>> past_values);

public:
  MultivariateAutoregressivyAnomalyDetection(int window_size, float tolerance)
  {
    this->window_size = window_size;
    this->tolerance = tolerance;
    this->lr = new LinearRegression();
  }
  std::vector<bool> is_anomaly(std::vector<float> inputs, std::vector<std::vector<float>> past_values);
};