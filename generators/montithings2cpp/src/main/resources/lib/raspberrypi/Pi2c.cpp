/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */
#include "Pi2c.h"

void
Pi2c::init (std::string devPath, int devAddr)
{
  if ((fd = open (devPath.c_str (), O_RDWR)) < 0)
    {
      CLOG (ERROR, "Pi2c") << "Failed to open the i2c bus";
    }
  if (ioctl (fd, I2C_SLAVE, devAddr) < 0)
    {
      CLOG (ERROR, "Pi2c") << "Failed to acquire bus access and/or talk to slave";
    }
}

void
Pi2c::send (uint8_t cmd, uint8_t *data, uint8_t len)
{
  std::lock_guard<std::mutex> guard (channelMutex);

  size_t bufferSize = len + 1;
  uint8_t *buf = (uint8_t *)malloc (bufferSize);
  buf[0] = cmd;
  memcpy (buf + 1, data, len);
  ssize_t writtenSize = write (fd, buf, bufferSize);
  free (buf);

  if (writtenSize != bufferSize)
    {
      CLOG (ERROR, "Pi2c") << "Failed to send to the i2c bus.";
    }
}

void
Pi2c::send (uint8_t reg)
{
  std::lock_guard<std::mutex> guard (channelMutex);

  ssize_t writtenSize = write (fd, &reg, 1);

  if (writtenSize != 1)
    {
      CLOG (ERROR, "Pi2c") << "Failed to send to the i2c bus.";
    }
}

void
Pi2c::receive (uint8_t cmd, uint8_t *data, uint8_t len)
{
  std::lock_guard<std::mutex> guard (channelMutex);

  if (write (fd, &cmd, 1) != 1)
    {
      CLOG (ERROR, "Pi2c") << "Failed to write to the i2c bus";
    }

  if (read (fd, data, len) != len)
    {
      CLOG (ERROR, "Pi2c") << "Failed to read from the i2c bus";
    }
}

uint8_t
Pi2c::receiveByte (uint8_t cmd)
{
  uint8_t result;
  receive (cmd, &result, 1);
  return result;
}
