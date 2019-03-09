import random
import matplotlib.pyplot as plt
import collections

size_list = [18] * 60
size_list_hubs = [50] * 60
min_distance = 110
connection_distance = min_distance + 80


class Hub:
    def __init__(self, name, x, y):
        self.name = name
        self.x = x
        self.y = y

    def distance_from_me(self, new_x, new_y):
        # manhattan distance from me to a new point
        return int(abs(self.x - new_x) + abs(self.y - new_y))

    def validate_new_point(self, new_x, new_y):
        manhattan_dist = self.distance_from_me(new_x, new_y)
        return manhattan_dist >= min_distance


class Station:
    def __init__(self, name, x, y):
        self.name = name
        self.x = x
        self.y = y
        self.size = 1

    def distance_from_me(self, new_x, new_y):
        # manhattan distance from me to a new point
        return int(abs(self.x - new_x) + abs(self.y - new_y))

    def validate_new_point(self, new_x, new_y):
        manhattan_dist = self.distance_from_me(new_x, new_y)
        return manhattan_dist >= min_distance

    def __str__(self):
        return "Name: {} Coordinate: ({:4}, {:4}) \t size {}".format(str(self.name), str(self.x), str(self.y),
                                                                     self.size)


class Points:
    def __init__(self, num_stations, num_hubs):
        self.stations = []
        self.hubs = []
        self.num_stations = num_stations
        self.num_hubs = num_hubs
        self.connections = collections.OrderedDict()
        # {My name1 : {connected's name1: distance1, connected's name2: distance2 ...}, My name2 : {...}, ...}

        self.x_values = []
        self.y_values = []
        self.hub_x_values = []
        self.hub_y_values = []

        self.num_freight = 15
        self.num_passenger = 5
        self.freight_schedule = collections.OrderedDict()
        # Stores {Name: [Home Hub, destination and start time]}
        self.passenger_schedule = collections.OrderedDict()
        # stores {Name: [Home Hub, destination and departure times]} Home Hub and destinations
        self.fail_max = 1000000

    def validate_point(self, new_x, new_y):
        if not self.stations:
            return True
        for station in self.stations:
            if not station.validate_new_point(new_x, new_y):
                return False

        for hub in self.hubs:
            if not hub.validate_new_point(new_x, new_y):
                return False
        return True

    def generate_points(self):
        self.hubs = []
        self.stations = []
        fails = 0
        name_stations = 1
        name_hubs = 1  # Iterates and increases by 1

        n = 0  # Counter for how many successful point placements. Stop When num stations is met
        while n < self.num_stations:
            x = random.randint(1, 1000)
            y = random.randint(1, 1000)
            if self.validate_point(x, y):
                self.stations.append(Station(name_stations, x, y))
                n += 1
                name_stations += 1

            elif fails > self.fail_max:
                print("Failed placing points more than {} times. Consider lower min dist".format(self.fail_max))
                return True, fails
            else:
                fails += 1

        n = 0
        while n < self.num_hubs:
            x = random.randint(1, 1000)
            y = random.randint(1, 1000)
            if self.validate_point(x, y):
                self.hubs.append(Hub("H" + str(name_hubs), x, y))
                n += 1
                name_hubs += 1

            elif fails > self.fail_max:
                print("Failed placing points more than {} times. Consider lower min dist".format(self.fail_max))
                return True, fails

            else:
                fails += 1

        return False, fails

    def connect_stations(self):
        for start_station in self.stations:
            connection_dict = {}
            smallest = None
            min_man_dist = 999999999
            for station in self.stations:
                manhattan_dist = abs(start_station.x - station.x) + abs(start_station.y - station.y)
                if manhattan_dist != 0 and manhattan_dist < connection_distance:
                    connection_dict[station] = manhattan_dist
                if manhattan_dist != 0 and manhattan_dist < min_man_dist:
                    min_man_dist = manhattan_dist
                    smallest = station
            if not connection_dict:
                connection_dict[smallest] = min_man_dist
            self.connections[start_station] = connection_dict

    def connect_hubs(self):
        for hub in self.hubs:
            connection_dict = {}
            stations_distances = []
            smallest = None
            min_man_dist = 999999999

            for station in self.stations:
                manhattan_dist = abs(hub.x - station.x) + abs(hub.y - station.y)
                stations_distances.append(manhattan_dist)
                # Find smallest manhattan distance station
                if smallest is None or manhattan_dist < min_man_dist:
                    smallest = station
                    min_man_dist = manhattan_dist
            connection_dict[smallest] = min_man_dist
            self.connections[smallest][hub] = min_man_dist
            self.connections[hub] = connection_dict

    def connect_points(self):
        for point, connections in self.connections.items():
            x_1 = point.x
            y_1 = point.y

            for external, distance in connections.items():
                x_2 = external.x
                y_2 = external.y
                # distance placed at the midpoint of line

                plt.plot([x_1, x_2], [y_1, y_2], c="00")

    def print_station_stats(self, fails):
        print("Statistics\n\nCoordinates")
        coordinates_stations = list(zip(self.x_values, self.y_values))
        coordinates_hubs = list(zip(self.hub_x_values, self.hub_y_values))
        for i, k in enumerate(coordinates_stations):
            print(i + 1, k)
        for i, k in enumerate(coordinates_hubs):
            print("H" + str(i + 1), k)

        print("#" * 60, "\n\t Below are connections to other stations")
        statement = ""
        for point, connections in self.connections.items():
            statement += "{:2} connects to: ".format(point.name)
            for external, distance in connections.items():
                statement += "({}, {}) ".format(external.name, distance)
            statement += "\n"
        print(statement)
        print("Num Fails: {}".format(fails))
        print("Fails Refers to the number of points that were attempted, but too close to another point")

    def create_trains_schedules(self):
        for freight in range(self.num_freight):
            home_hub = random.randint(1, 5)
            destination = random.randint(1, 60)
            start_time = None
            if random.randint(1, 2) == 1:
                start_time = random.randint(1, 1440/2)
                # NOTE: Freight trains must start before half day is up. 1440 minutes...

            self.freight_schedule[freight + 1] = [home_hub, destination, start_time]

        print(self.freight_schedule)

        for passenger in range(self.num_passenger):
            home_hub = random.randint(1, 5)
            num_destinations = random.randint(5, 10)
            destinations = []
            departure_times = []
            cur_time = 100

            while num_destinations != 0:
                destinations.append(random.randint(1, 60))
                departure_times.append(random.randint(cur_time, cur_time+100))
                cur_time += 100
                num_destinations -= 1

            self.passenger_schedule[passenger + 1] = [home_hub, destinations, departure_times]

        print(self.passenger_schedule)

    def set_coordinates(self):
        for station in self.stations:
            self.x_values.append(station.x)
            self.y_values.append(station.y)
            # print(station)

        for hub in self.hubs:
            self.hub_x_values.append(hub.x)
            self.hub_y_values.append(hub.y)

    def make_map(self):

        fig, ax = plt.subplots()
        ax.scatter(self.x_values, self.y_values, size_list, c="00")
        ax.scatter(self.hub_x_values, self.hub_y_values, size_list_hubs, c="C3")

        names = range(60)
        names_hubs = ["H1", "H2", "H3", "H4", "H5"]
        for i, txt in enumerate(names):
            ax.annotate(str(txt + 1), (self.x_values[int(i)] - 15, self.y_values[int(i)] + 20))

        for i, txt in enumerate(names_hubs):
            ax.annotate(str(txt), (self.hub_x_values[int(i)] - 15, self.hub_y_values[int(i)] + 20))

        self.connect_points()

        plt.show()

    def create_input_files(self):
        f = open("Input1.txt", "w+")
        for station in self.stations:
            f.write("{}: ({}, {})\n".format(station.name, station.x, station.y))
        f.close()

        g = open("Input2.txt", "w+")
        # g.write("format: station ## connects to: (station ##:distance)")
        statement = ""
        for point, connections in self.connections.items():
            statement += "{:2} connects to: ".format(point.name)
            for external, distance in connections.items():
                statement += "({}, {}) ".format(external.name, distance)
            statement += "\n"

        g.write(str(statement))
        g.close()

        self.create_trains_schedules()

        h = open("Input3.txt", "w+")
        statement = ""
        for name, details in self.freight_schedule.items():
            if details[2] is not None:
                statement += "Freight {:1}, Home_Hub: {:1}," \
                    " Destination: {:2}, Start_Time: {:4}\n".format(name, details[0], details[1], details[2])
            else:
                statement += "Freight {}, Home_Hub: {:1}," \
                    " Destination: {:2}, Start_Time: \n".format(name, details[0], details[1])
        h.write(statement)

        statement = ""
        for name, details in self.passenger_schedule.items():
            dest_string = ""
            dep_string = ""
            for destination in details[1]:
                dest_string += "{:2} ".format(str(destination))
            for departure_time in details[2]:
                dep_string += "{:4} ".format(str(departure_time))

            statement += "Passenger No. {}, Home_Hub: {}," \
                         " Destinations: {}, Departure_Times: {}\n".format(name, details[0], dest_string, dep_string)
        h.write(statement)

        h.close()

    def run(self):
        failed, fails = self.generate_points()

        if not failed:
            self.set_coordinates()
            self.connect_stations()
            self.connect_hubs()
            self.print_station_stats(fails)
            self.create_input_files()
            self.make_map()


if __name__ == "__main__":
    S = Points(60, 5)
    S.run()
