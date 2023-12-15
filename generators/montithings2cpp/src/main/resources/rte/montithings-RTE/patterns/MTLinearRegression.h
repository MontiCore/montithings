// (c) https://github.com/MontiCore/monticore
#ifndef MT_LINEAR_REGRESSION_HEADER
#define MT_LINEAR_REGRESSION_HEADER

#include <cmath>
#include <iostream>
#include <numeric>
#include <stdexcept>
#include <vector>

class MTLinearRegression
{
protected:
  float mean(std::vector<float> values);
  float std(std::vector<float> values, float mean);
  float correlation(float covariance, float std_x, float std_y);
  float covariance(std::vector<float> x_values, std::vector<float> y_values, float mean_x,
                   float mean_y);
  float slope(float correlation, float std_x, float std_y);
  float y_intercept(float slope, float mean_x, float mean_y);
  std::vector<float> x_values(std::vector<float> values);

public:
  float predict_value(float input, std::vector<float> values);
};

#endif