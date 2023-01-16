#include "LinearRegression.h"

float LinearRegression::predict_value(float input, std::vector<float> values)
{
  if (values.empty())
  {
    return 0;
  }

  std::vector<float> x_values = this->x_values(values);

  float mean_x = this->mean(x_values);
  float mean_y = this->mean(values);

  float std_x = this->std(x_values, mean_x);
  float std_y = this->std(values, mean_y);

  float correlation = this->correlation(x_values, values, mean_x, mean_y, std_x, std_y);

  float slope = this->slope(correlation, std_x, std_y);

  float y_intercept = this->y_intercept(slope, mean_x, mean_y);

  return slope * input - y_intercept;
}

float LinearRegression::mean(std::vector<float> values)
{
  auto const count = static_cast<float>(values.size());

  return std::reduce(values.begin(), values.end()) / count;
}

float LinearRegression::std(std::vector<float> values, float mean)
{
  float subtotal = 0;
  for (int i = 0; i < values.size(); i++)
  {
    subtotal += (values[i] - mean) * (values[i] - mean);
  }

  return sqrt(1 / values.size() * subtotal);
}

float LinearRegression::correlation(std::vector<float> x_values, std::vector<float> y_values, float mean_x, float mean_y, float std_x, float std_y)
{
  if (x_values.size() != y_values.size())
  {
    throw std::invalid_argument("x values and y values don't have equal length.");
  }

  float subtotal = 0;

  for (int i = 0; i < x_values.size(); i++)
  {
    subtotal += (x_values[i] - mean_x) * (y_values[i] - mean_y);
  }

  return subtotal / std_x * std_y;
}

float LinearRegression::slope(float correlation, float std_x, float std_y)
{
  return correlation * (std_y / std_x);
}

float LinearRegression::y_intercept(float slope, float mean_x, float mean_y)
{
  return mean_y - slope * mean_x;
}

std::vector<float> LinearRegression::x_values(std::vector<float> values)
{
  std::vector<float> x_values(values.size());

  for (int i = 0; i < values.size(); i++)
  {
    x_values.push_back(i);
  }

  return x_values;
}