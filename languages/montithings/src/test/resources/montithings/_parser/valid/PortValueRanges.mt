package valid;

component PortValueRanges {

  // Check if a value is within a certain range.
  // Only allow values between 0 and 5,
  // default to 3 if actual value not between 0 and 5
  // Will return 1, 2, 3, 4, 5, 3, 3, 3, ...
  port out int ( 0  :  5 ) value = 3;

  // Check if a value is one of the given values.
  // Only allow values 0, 1, and 4,
  // default to 3 if actual value is not 0, 1, or 4
  // (note that the default value does not have to be an allowed value)
  // Will return 1, 3, 3, 4, 3, 3, 3, 3, ...
  // port out int ( 0, 1, 4 ) value = 3;

  // Combine multiple checks.
  // Allow 1, 15, and values between 3 and 11 with a stepsize of 4
  // All allowed values: 1, 3, 7, 11, 15
  // default to 15 if actual value does not match
  // Will return 1, 15, 3, 15, 15, 15, 7, 15, 15, 15, 11, 15, 15, 15, 15, 15, ...
  // port out int ( 1, 3:4:11, 15 ) value = 15;

  // Check if value matches a regular expression.
  // Allow all numbers starting with a 2, e.g. 20, 27, 24456, or 2
  // Hint: This is handy for checking if Strings conform to a certain format
  // Will return 0, 2, 0, 0, ..., 0, 20, 21, ..., 28, 29, 0, 0, ...
  // port out int (format: "2[0-9]*") value = 0;
}
