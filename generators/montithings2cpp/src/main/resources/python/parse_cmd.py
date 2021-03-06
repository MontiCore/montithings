
def parse_cmd_args():
    import argparse
    parser = argparse.ArgumentParser(description='Coordinate sensor / actuator access')
    parser.add_argument('--brokerPort', nargs='?', default=1883, type=int,
                        help='Network port of the MQTT broker')
    parser.add_argument('--brokerHostname', nargs='?', default='localhost', type=str,
                        help='Hostname of the MQTT broker')
    args_temp, unknown = parser.parse_known_args()
    args = vars(args_temp)
    return args['brokerHostname'], args['brokerPort']