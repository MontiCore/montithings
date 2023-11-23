// (c) https://github.com/MontiCore/monticore
#include "MTLinearRegression.h"

float MTLinearRegression::predict_value(float input, std::vector<float> values)
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

  float covariance = this->covariance(x_values, values, mean_x, mean_y);

  float correlation = this->correlation(covariance, std_x, std_y);

  float slope = this->slope(correlation, std_x, std_y);

  float y_intercept = this->y_intercept(slope, mean_x, mean_y);

  return slope * input + y_intercept;
}

float MTLinearRegression::mean(std::vector<float> values)
{
  auto const count = static_cast<float>(values.size());

  float subtotal = 0;

  for (int i = 0; i < values.size(); i++)
  {
    subtotal += values[i];
  }

  return subtotal / count;
}

float MTLinearRegression::std(std::vector<float> values, float mean)
{
  float subtotal = 0;
  for (int i = 0; i < values.size(); i++)
  {
    subtotal += (values[i] - mean) * (values[i] - mean);
  }

  return sqrt(subtotal / values.size());
}

float MTLinearRegression::correlation(float covariance, float std_x, float std_y)
{
  return covariance / (std_x * std_y);
}

float MTLinearRegression::covariance(std::vector<float> x_values, std::vector<float> y_values,
                                     float mean_x, float mean_y)
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

  return subtotal / x_values.size();
}

float MTLinearRegression::slope(float correlation, float std_x, float std_y)
{
  return correlation * (std_y / std_x);
}

float MTLinearRegression::y_intercept(float slope, float mean_x, float mean_y)
{
  return mean_y - slope * mean_x;
}

std::vector<float>
MTLinearRegression::x_values(std::vector<float> values)
{
  std::vector<float> x_values;

  for (int i = 0; i < values.size(); i++)
  {
    x_values.push_back(i);
  }

  return x_values;
}