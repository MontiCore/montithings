# (c) https://github.com/MontiCore/monticore

def parse_cmd_args():
    import argparse
    parser = argparse.ArgumentParser(description='Coordinate sensor / actuator access')
    parser.add_argument('--brokerPort', nargs='?', default=1883, type=int,
                        help='Network port of the MQTT broker')
    parser.add_argument('--brokerHostname', nargs='?', default='localhost', type=str,
                        help='Hostname of the MQTT broker')
    parser.add_argument('--name', nargs='?', default=__name__, type=str,
                        help='Identifier for this python component (e.g. MQTT-Communication)')
    args_temp, unknown = parser.parse_known_args()
    args = vars(args_temp)
    return args['brokerHostname'], args['brokerPort']
