#ifndef MT_LINEAR_REGRESSION_HEADER
#define MT_LINEAR_REGRESSION_HEADER

#include <vector>
#include <numeric>
#include <stdexcept>
#include <cmath>

class MTLinearRegression
{
protected:
  float mean(std::vector<float> values);
  float std(std::vector<float> values, float mean);
  float correlation(std::vector<float> x_values, std::vector<float> y_values, float mean_x, float mean_y, float std_x, float std_y);
  float slope(float correlation, float std_x, float std_y);
  float y_intercept(float slope, float mean_x, float mean_y);
  std::vector<float> x_values(std::vector<float> values);

public:
  float predict_value(float input, std::vector<float> values);
};

#endif