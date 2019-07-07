package trainsimulator.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InputReader {

	private File fileToRead;
	private static ArrayList<String> hubs = new ArrayList<>();
	private static ArrayList<String> stations = new ArrayList<>();
	private static ArrayList<String> edges = new ArrayList<>();
	private static ArrayList<String> loco = new ArrayList<>();
	private static HashMap<Integer, String> errors = new HashMap<>();
	private static HashMap<Integer, ArrayList<String>> passengerRoutes = new HashMap<>();
	private static ArrayList<String> freightRoutes = new ArrayList<>();
	private static ArrayList<String> queries = new ArrayList<>();
	private static int numberOfControls = 0;
	private static int numberOfNonControls = 0;
	private static int numberOfLines = 0;
	private static int freightRouteID = 1;
	private static int passengerRouteID = 1;
	private static int maintenanceID = 1;

	private static Connection connection;

	private int numOfHubs = 0;
	private int numOfStations = 0;
	private int numOfEdges = 0;
	private int numOfLocomotives = 0;
	private int numOfRoutes = 0;
	private int sumOfFreightRoutes = 0;
	private int numOfStopsInRoute = 0;
	private int numOfControls = 0;
	private int numOfNonControls = 0;

	public static void main(String[] args) {

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cis_375_schema", "root", "");
			// ResultSet set = connection.prepareCall("SELECT MAX(`RouteID`)
			// FROM `freight_route`").executeQuery();
			/*
			 * while (set.next()) { freightRouteID =
			 * set.getInt("MAX(`RouteID`)") + 1; } set = connection.
			 * prepareCall("SELECT MAX(`RouteID`) FROM `passenger_route`").
			 * executeQuery(); while (set.next()) { passengerRouteID =
			 * set.getInt("MAX(`RouteID`)") + 1; }
			 * 
			 * set = connection.
			 * prepareCall("SELECT MAX(`MaintenanceID`) FROM `maintenance`").
			 * executeQuery(); while (set.next()) { maintenanceID =
			 * set.getInt("MAX(`MaintenanceID`)") + 1; }
			 */

		} catch (

		SQLException e) {
			e.printStackTrace();
		}

		long start = System.currentTimeMillis();
		resetDatabase();
		/* File path to read */
		/*
		 * new InputReader(new File("C:/Users/Cameron/Desktop/Structure"));
		 * numberOfControls = 0; numberOfNonControls = 0; numberOfLines = 0; new
		 * InputReader(new File("C:/Users/Cameron/Desktop/Repeatable Routes"));
		 */
		new InputReader(new File("C:/Users/Cameron/Desktop/Structure"));
		// new InputReader(new File("C:/Users/Cameron/Desktop/Maitenence"));
		numberOfControls = 0;
		numberOfNonControls = 0;
		numberOfLines = 0;
		// new InputReader(new File("C:/Users/Cameron/Desktop/Daily Routes"));
		System.out.println();
		System.out.println(System.currentTimeMillis() - start + "\n");
	}

	public InputReader(File file) {
		fileToRead = file;
		boolean success = parseFile();
		System.out.println("Success? " + success);
		if (success) {
			try {
				sendQueries(queries);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (true) {
			for (int key : errors.keySet()) {
				System.out.println("Error on line " + key + ": " + errors.get(key));
			}
		}
		// printSummary();

	}

	/**
	 * Detects the type of file and then returns the method to parse it
	 * 
	 * @return a boolean if it worked correctly or not
	 */
	public boolean parseFile() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(fileToRead));
			int lineCount = 0;
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.startsWith("H")) {

				} else {
					if (line.startsWith("C")) {
						System.out.println(line);
						switch (line.split(" ")[1]) {
						case "FREIGHT":
							return processRepeatableRoutes();
						case "HUB":
							return handleStructureFile();
						case "DAY":
							String nextLine = reader.readLine();
							if (nextLine.startsWith("C")) {
								switch (nextLine.split(" ")[1]) {
								case "EDGE":
									return processMaintenanceFile();
								case "FREIGHT":
									return processDailyRoutes();
								}
							}
						}
					} else if (line.startsWith("LOCOMOTIVE"))
						return processConfigurationFile();
					else {
						System.out.println("File structure not found");
						return false;
					}
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleStructureFile() {
		try {
			if (parseStructureFile()) {
				if ((hubs.size() == numOfHubs) && (stations.size() == numOfStations) && (edges.size() == numOfEdges)
						&& (loco.size() == numOfLocomotives))
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * Parses a structure file
	 * 
	 * @return a boolean if it worked correctly or not DONE?
	 */

	private boolean parseStructureFile() throws IOException {
		System.out.println("Structure File Detected!");
		BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
		while (reader.ready()) {
			String line = reader.readLine();
			numberOfLines++;
			System.out.println(line);
			if (numberOfLines == 1) {
				if (line.startsWith("H")) {
					if (line.split("H")[1].split(" ")[0].length() != 4) {
						errors.put(numberOfLines, "Header value needs to be formatted correctly!");
						return false;
					} else if (!isDigit(line.split("H")[1].split(" ")[0])) {
						errors.put(numberOfLines, "The file number needs to be a number");
						return false;
					}
					String fileNumberString = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumberString + " " + date);
					int fileError = checkFileNumber(Integer.parseInt(fileNumberString));
					int fileNumber = Integer.parseInt(fileNumberString);
					System.out.println(fileError);
					System.out.println(fileNumber);
					if (fileError == -1) {
						errors.put(numberOfLines, "The file number is not correct");
						return false;
					} else if ((fileError + 1) == fileNumber) {
						submitQuery("DELETE FROM `file_number` WHERE 1");
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					} else if (fileError == -555) {
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					}
				} else
					return false;
			} else {
				if (line.startsWith("C")) {
					try {
						sendQueries(queries);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					numberOfControls++;
					numberOfLines++;
					if (line.split(" ").length != 3 || line.split(" ")[0].length() != 1) {
						errors.put(numberOfLines, "Control line structure not correct");
						return false;
					} else if (line.split(" ")[2].length() != 4) {
						errors.put(numberOfLines, "Number of entities is wrong");
						return false;
					} else {
						if (!isDigit(line.split(" ")[2])) {
							errors.put(numberOfLines, "Number of entities needs to be a number");
							return false;
						}
						switch (line.split(" ")[1]) {
						case "HUB":
							numOfHubs = Integer.parseInt(line.split(" ")[2]);
							while (reader.ready()) {
								reader.mark(0);
								String anotherLine = reader.readLine();
								if (anotherLine.startsWith("C")) {
									reader.reset();
									break;
								} else if (anotherLine.startsWith("T")) {
									reader.reset();
									errors.put(numberOfLines, "File can't end after hub");
									return false;
								} else {
									if (anotherLine.split(" ").length != 1)
										continue;
									numberOfLines++;
									numberOfNonControls++;
									if (anotherLine.split("Hub").length != 2) {
										errors.put(numberOfLines, "Hubs need an id!");
										continue;
									} else if (!isDigit(anotherLine.split("Hub")[1])) {
										errors.put(numberOfLines, "Hub ID needs to be an integer");
										continue;
									}

									hubs.add(anotherLine);
									submitQuery("INSERT INTO `hub` (`HubID`, `Active`) VALUES('"
											+ Integer.parseInt(anotherLine.split("Hub")[1]) + "', '1')");
								}
							}
							break;
						case "STATION":
							numOfStations = Integer.parseInt(line.split(" ")[2]);
							System.out.println("Number of stations: " + numOfStations);
							while (reader.ready()) {
								reader.mark(0);
								String anotherLine = reader.readLine();
								if (anotherLine.startsWith("C")) {
									reader.reset();
									break;
								} else if (anotherLine.startsWith("T")) {
									reader.reset();
									numberOfLines++;
									numberOfNonControls++;
									break;
								} else {
									numberOfLines++;
									numberOfNonControls++;
									if (anotherLine.split(" ").length != 8) {
										errors.put(numberOfLines, "Station structure is not correct");
										continue;
									} else if (anotherLine.split(" ")[1].length() != 1
											|| (!anotherLine.split(" ")[1].equalsIgnoreCase("F")
													&& !anotherLine.split(" ")[1].equalsIgnoreCase("P"))) {
										errors.put(numberOfLines, "Station type is not correct");
										continue;
									} else if (!isDigit(anotherLine.split("Station")[1].split(" ")[0])) {
										errors.put(numberOfLines, "Station id must be a number");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[2])) {
										errors.put(numberOfLines, "Number of max trains must be a number");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[3])) {
										errors.put(numberOfLines, "Lower bound range on must be a number");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[4])) {
										errors.put(numberOfLines, "Upper bound range on must be a number");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[5])) {
										errors.put(numberOfLines, "Lower bound range off must be a number");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[6])) {
										errors.put(numberOfLines, "Upper bound range off must be a number");
										continue;
									}
									int stationID = Integer.parseInt(anotherLine.split("Station")[1].split(" ")[0]);
									int isFreightTrain = (anotherLine.split(" ")[1].startsWith("P") ? 0 : 1);
									int lowerOn = Integer.parseInt(anotherLine.split(" ")[3]);
									int upperOn = Integer.parseInt(anotherLine.split(" ")[4]);
									int lowerOff = Integer.parseInt(anotherLine.split(" ")[5]);
									int upperOff = Integer.parseInt(anotherLine.split(" ")[6]);
									double ticketPrice = Double.parseDouble(anotherLine.split(" ")[7]) * 60;
									if (stationID < 0 || lowerOn < 0 || upperOn < 0 || lowerOff < 0 || upperOff < 0
											|| ticketPrice < 0.0) {
										errors.put(numberOfLines, "Values cannot be less than 0");
										continue;
									}

									stations.add(anotherLine);
									submitQuery(
											"INSERT INTO `station` (`StationID`, `Freight`, `Random_On_Min`, `Random_On_Max`, `Random_Off_Min`, `Random_Off_Max`, `Ticket_Price`, `Active`) VALUES('"
													+ stationID + "', '" + isFreightTrain + "', '" + lowerOn + "', '"
													+ upperOn + "', '" + lowerOff + "', '" + upperOff + "', '"
													+ ticketPrice + "', '1')");
								}
							}
							break;
						case "EDGE":
							numOfEdges = Integer.parseInt(line.split(" ")[2]);
							System.out.println("Number of edges: " + numOfEdges);
							int trackID = 1;
							while (reader.ready()) {
								reader.mark(0);
								String anotherLine = reader.readLine();
								if (anotherLine.startsWith("C")) {
									reader.reset();
									break;
								} else if (anotherLine.startsWith("T")) {
									reader.reset();
									break;
								} else {
									numberOfLines++;
									numberOfNonControls++;
									if (anotherLine.split(" ").length != 5) {
										errors.put(numberOfLines, "Edge structure is not correct");
										continue;
									} else if (anotherLine.split(" ")[3].length() != 5) {
										System.out.println("Yeet");
										errors.put(numberOfLines, "Start time is not correct");
										continue;
									} else if (anotherLine.split(" ")[4].length() != 5) {
										errors.put(numberOfLines, "End time is not correct");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[2])) {
										errors.put(numberOfLines, "Track length must be a number");
										continue;
									} else if (!anotherLine.split(" ")[3].contains(":")) {
										errors.put(numberOfLines, "Time needs to be formatted as 99:99");
										continue;
									} else if (!anotherLine.split(" ")[4].contains(":")) {
										errors.put(numberOfLines, "Time needs to be formatted as 99:99");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[3].split(":")[0])) {
										errors.put(numberOfLines, "Start time hour must be a number");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[3].split(":")[1])) {
										errors.put(numberOfLines, "Start time minute must be a number");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[4].split(":")[0])) {
										errors.put(numberOfLines, "End time hour must be a number");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[4].split(":")[1])) {
										errors.put(numberOfLines, "End time minute must be a number");
										continue;
									}

									/* 3 */
									int startTime = (Integer.parseInt(anotherLine.split(" ")[3].split(":")[0]) * 60)
											+ (Integer.parseInt(anotherLine.split(" ")[3].split(":")[0]));
									int endTime = (Integer.parseInt(anotherLine.split(" ")[4].split(":")[0]) * 60)
											+ (Integer.parseInt(anotherLine.split(" ")[4].split(":")[0]));
									boolean isStartAStation = anotherLine.split(" ")[0].contains("Station");
									boolean isEndAStation = anotherLine.split(" ")[1].contains("Station");
									String startName = anotherLine.split(" ")[0]
											.split((isStartAStation ? "Station" : "Hub"))[1];
									String endName = anotherLine.split(" ")[1]
											.split((isEndAStation ? "Station" : "Hub"))[1];
									if (!isDigit(startName) || !isDigit(endName)) {
										errors.put(numberOfLines, "The location id's must be a number");
										continue;
									}
									int startID = Integer.parseInt(startName);
									int endID = Integer.parseInt(endName);
									if (isStartAStation) {
										if (!doesStationExist(startID)) {
											errors.put(numberOfLines, "Starting place does not exist");
											continue;
										}
									} else {
										if (!doesHubExist(startID)) {
											errors.put(numberOfLines, "Starting place does not exist");
											continue;
										}
									}

									if (isEndAStation) {
										if (!doesStationExist(endID)) {
											errors.put(numberOfLines, "Ending place does not exist");
											continue;
										}
									} else {
										if (!doesHubExist(endID)) {
											errors.put(numberOfLines, "Ending place does not exist");
											continue;
										}
									}

									int length = Integer.parseInt(anotherLine.split(" ")[2]);
									if (length < 0) {
										errors.put(numberOfLines, "Length cannot be less than 0");
										continue;
									}

									edges.add(anotherLine);
									submitQuery("INSERT INTO `track` (`TrackID`, `"
											+ (isStartAStation ? "StartLocStationID" : "StartLocHubID") + "`, `"
											+ (isEndAStation ? "EndLocStationID" : "EndLocHubID")
											+ "`, `StartTime`, `EndTime`, `Length`, `Active`) " + "VALUES('" + trackID
											+ "', '" + startName + "', '" + endName + "', '" + startTime + "', '"
											+ endTime + "', '" + length + "', '1')");
									trackID++;
								}
							}
							break;
						case "LOCOMOTIVE":
							numOfLocomotives = Integer.parseInt(line.split(" ")[2]);
							while (reader.ready()) {
								reader.mark(1);
								String anotherLine = reader.readLine();
								if (anotherLine.startsWith("C")) {
									reader.reset();
									break;
								} else if (anotherLine.startsWith("T")) {
									numberOfLines++;
									if (anotherLine.split("T").length != 2) {
										errors.put(numberOfLines, "Trailer structure is wrong");
										return false;
									} else if (anotherLine.split(" ").length != 3) {
										errors.put(numberOfLines, "Trailer structure is wrong");
										return false;
									} else {
										if (!isDigit(anotherLine.split(" ")[1])
												|| !isDigit(anotherLine.split(" ")[2])) {
											errors.put(numberOfLines, "Trailer needs to be numbers");
											return false;
										}
										int totalEntries = Integer.parseInt(anotherLine.split(" ")[1]);
										int controlCount = Integer.parseInt(anotherLine.split(" ")[2]);
										if (numberOfNonControls == controlCount
												&& totalEntries == (numberOfNonControls + numberOfControls)
												&& numOfHubs == hubs.size() && numOfEdges == edges.size()
												&& loco.size() == numOfLocomotives
												&& numOfStations == stations.size()) {
											return true;
										} else {
											return false;
										}
									}
								} else {
									numberOfLines++;
									numberOfNonControls++;
									if (anotherLine.split(" ").length != 3) {
										errors.put(numberOfLines, "Locomotive structure is not correct");
										continue;
									} else if (anotherLine.split(" ")[2].length() != 1) {
										errors.put(numberOfLines, "Locomotive Type is not correct");
										continue;
									} else if (!isDigit(anotherLine.split(" ")[1].split("Hub")[1].split(" ")[0])) {
										errors.put(numberOfLines, "Hub id is not a number");
										continue;
									}
									int locomotiveID = Integer
											.parseInt(anotherLine.split("Locomotive")[1].split(" ")[0]);
									int isFrieght = anotherLine.split(" ")[2].charAt(0) == 'P' ? 0 : 1;
									String hub = anotherLine.split(" ")[1];
									int hubID = Integer
											.parseInt(anotherLine.split(" ")[1].split("Hub")[1].split(" ")[0]);

									if (!doesHubExist(hubID)) {
										errors.put(numberOfLines, "Hub does not exist");
										continue;
									}

									loco.add(anotherLine);
									submitQuery(
											"INSERT INTO `train` (`TrainID`, `Freight`, `HomeHubID`, `Capacity`, `TopSpeed`, `Active`) VALUES('"
													+ locomotiveID + "', '" + +isFrieght
													+ "', (SELECT `HubID` From `hub` WHERE Hub.HubId = '" + hubID
													+ "'), '0', '0', '1')");
								}
							}
						default:
							errors.put(numberOfLines, "Entity not found");
							return false;
						}
					}
				}
			}
		}
		return true;

	}

	/* Handles the input when a daily routes file is detected */
	private boolean processDailyRoutes() throws IOException {
		int prevDay = 0;
		System.out.println("Daily Routes File Detected!");
		BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
		while (reader.ready()) {
			String line = reader.readLine();
			numberOfLines++;
			System.out.println(line);
			if (numberOfLines == 1) {
				if (line.startsWith("H")) {
					if (line.split("H")[1].split(" ")[0].length() != 4) {
						errors.put(numberOfLines, "Header value needs to be formatted correctly!");
						return false;
					} else if (!isDigit(line.split("H")[1].split(" ")[0])) {
						errors.put(numberOfLines, "The file number needs to be a number");
						return false;
					}
					String fileNumberString = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumberString + " " + date);
					int fileError = checkFileNumber(Integer.parseInt(fileNumberString));
					int fileNumber = Integer.parseInt(fileNumberString);
					System.out.println(fileError);
					System.out.println(fileNumber);
					if (fileError == -1) {
						errors.put(numberOfLines, "The file number is not correct");
						return false;
					} else if ((fileError + 1) == fileNumber) {
						submitQuery("DELETE FROM `file_number` WHERE 1");
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					} else if (fileError == -555) {
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					}
				} else
					return false;
			} else {
				if (line.startsWith("C DAY")) {
					if (line.split(" ").length != 3)
						return false;
					if (!isDigit(line.split(" ")[2])) {
						errors.put(numberOfLines, "Day value is not a number");
						return false;
					}
					int day = Integer.parseInt(line.split(" ")[2]);
					if (prevDay != (day - 1)) {
						errors.put(numberOfLines, "Day value must increment by 1");
						return false;
					}
					prevDay = day;
					System.out.println("DAY #" + day);
					while (reader.ready()) {
						int stopNumber = 1;
						reader.mark(0);
						String anotherOne = reader.readLine();
						System.out.println(anotherOne);
						if (anotherOne.startsWith("C DAY")) {
							reader.reset();
							break;
						}
						numberOfLines++;
						if (anotherOne.startsWith("C")) {
							if (anotherOne.split(" ")[0].length() == 1 && anotherOne.split(" ").length == 3) {
								if (anotherOne.split(" ").length != 3 || anotherOne.split(" ")[2].length() != 3) {
									errors.put(numberOfLines, "Control line is not formatted correctly.");
									return false;
								}
								if (!isDigit(anotherOne.split(" ")[2])) {
									errors.put(numberOfLines, "Amount must be a number");
									return false;
								}
								int amt = Integer.parseInt(anotherOne.split(" ")[2]);
								int amtPerIteration = 0;
								switch (anotherOne.split(" ")[1]) {
								case "FREIGHT":
									while (reader.ready()) {
										reader.mark(0);
										String lastLine = reader.readLine();
										if (lastLine.startsWith("C")) {
											reader.reset();
											if (amt != amtPerIteration) {
												errors.put(numberOfLines,
														"The expected number of routes and actual do not match");
												return false;
											}
											amtPerIteration = 0;
											break;
										}
										numberOfLines++;
										if (lastLine.split(" ").length != 4 || lastLine.split("Station").length != 3) {
											errors.put(numberOfLines, "Freight structure is not correct");
											return false;
										} else if (!lastLine.split(" ")[2].contains(":")) {
											errors.put(numberOfLines, "Time needs to be formatted as 99:99");
											continue;
										} else if (!isDigit(lastLine.split(" ")[2].split(":")[0])) {
											errors.put(numberOfLines, "Start time hour must be a number");
											continue;
										} else if (!isDigit(lastLine.split(" ")[2].split(":")[1])) {
											errors.put(numberOfLines, "Start time minute must be a number");
											continue;
										}

										int station1 = Integer.parseInt(lastLine.split(" ")[0].split("Station")[1]);
										int station2 = Integer.parseInt(lastLine.split(" ")[1].split("Station")[1]);

										if (!doesStationExist(station1) || !doesStationExist(station2)) {
											errors.put(numberOfLines, "Both stations must exist");
											continue;
										}

										int hour;
										int minute;
										if (!lastLine.contains(":")) {
											hour = 0;
											minute = 0;
										} else {
											hour = Integer.parseInt(lastLine.split(" ")[2].split(":")[0]);
											minute = Integer.parseInt(lastLine.split(" ")[2].split(":")[1]);
										}
										amtPerIteration++;
										submitQuery(
												"INSERT INTO `Freight_Route` (`RouteID`, `StartLocId`, `EndLocID`, `StartTime`, `Repeating`, `RunDay`, `Active`) VALUES "
														+ "('" + freightRouteID
														+ "', (SELECT `StationID` From `station` WHERE Station.StationID = '"
														+ station1 + "'), "
														+ "(SELECT `StationID` From `station` WHERE Station.StationID = '"
														+ station2 + "'), '" + ((hour * 60) + minute) + "', '1', '"
														+ day + "', '1')");
										freightRouteID++;
									}
									break;
								case "PASSENGER":
									if (line.split(" ").length != 3 && line.split(" ")[2].length() != 3) {
										errors.put(numberOfLines, "Passenger control line is not formatted correctly");
										return false;
									} else if (!isDigit(line.split(" ")[2])) {
										errors.put(numberOfLines, "Passenger value is not a number");
										return false;
									}

									int amtOfPassengers = Integer.parseInt(line.split(" ")[2]);
									if (amtOfPassengers < 0) {
										errors.put(numberOfLines, "Passenger value must be greater than 0");
										return false;
									}
									while (reader.ready()) {
										reader.mark(0);
										String anotha = reader.readLine();
										System.out.println(anotha);
										if (anotha.startsWith("T")) {
											amtPerIteration = 0;
											return true;
										} else if (anotha.startsWith("C")) {
											if (anotha.split(" ").length == 3 && !anotha.startsWith("C ROUTE")) {
												reader.reset();
												break;
											}
											numberOfLines++;

											if (!isDigit(anotha.split(" ")[2])) {
												errors.put(numberOfLines, "Number of routes needs to be a number");
												return false;
											}
											int expected = Integer.parseInt(anotha.split(" ")[2]);
											if (expected < 0) {
												errors.put(numberOfLines,
														"Expected number of routes needs to be positive");
												return false;
											}
											int iteratedAmt = 0;
											while (reader.ready()) {
												reader.mark(0);
												String lastLine = reader.readLine();
												System.out.println(lastLine);
												if (lastLine.startsWith("C")) {
													if (expected != iteratedAmt) {
														errors.put(numberOfLines,
																"The expected number of routes and actual do not match");
														return false;
													}
													iteratedAmt = 0;
													stopNumber = 1;
													passengerRouteID++;
													reader.reset();
													break;
												} else if (lastLine.startsWith("T")) {
													System.out.println("yeet");
													return true;
												} else if (lastLine.split(" ").length != 2)
													return false;
												numberOfLines++;
												int hour = Integer.parseInt(lastLine.split(" ")[1].split(":")[0]);
												int minute = Integer.parseInt(lastLine.split(" ")[1].split(":")[1]);
												if (!isDigit(lastLine.split("Station")[1].split(" ")[0])) {
													errors.put(numberOfLines, "Station ID must be a digit");
													continue;
												}
												int stationID = Integer
														.parseInt(lastLine.split("Station")[1].split(" ")[0]);
												if (stationID < 0) {
													errors.put(numberOfLines, "Station ID must be above 0");
													continue;
												} else if (!doesStationExist(stationID)) {
													errors.put(numberOfLines, "Station must exist");
													continue;
												}
												iteratedAmt++;
												submitQuery(
														"INSERT INTO `passenger_route` (`RouteID`, `StopNumber`, `LocID`, `StartTime`, `Repeating`, `RunDay`, `Active`) VALUES "
																+ "('" + passengerRouteID + "', '" + stopNumber + "', '"
																+ stationID + "', '" + ((hour * 60) + minute)
																+ "', '1', '" + day + "', '1')");
												stopNumber++;
											}
										}
									}
									break;
								default:
									return false;
								}
							}
						}
					}
				} else
					return false;

			}
		}
		return false;
	}

	/* Handles the input when a repeatable routes file is detected */
	private boolean processRepeatableRoutes() throws IOException {
		System.out.println("Repeatable Routes File Detected!");
		BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
		while (reader.ready()) {
			String line = reader.readLine();
			numberOfLines++;
			if (numberOfLines == 1) {
				if (line.startsWith("H")) {
					if (line.split("H")[1].split(" ")[0].length() != 4) {
						errors.put(numberOfLines, "Header value needs to be formatted correctly!");
						return false;
					} else if (!isDigit(line.split("H")[1].split(" ")[0])) {
						errors.put(numberOfLines, "The file number needs to be a number");
						return false;
					}
					String fileNumberString = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumberString + " " + date);
					int fileError = checkFileNumber(Integer.parseInt(fileNumberString));
					int fileNumber = Integer.parseInt(fileNumberString);
					System.out.println(fileError);
					System.out.println(fileNumber);
					if (fileError == -1) {
						errors.put(numberOfLines, "The file number is not correct");
						return false;
					} else if ((fileError + 1) == fileNumber) {
						submitQuery("DELETE FROM `file_number` WHERE 1");
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					} else if (fileError == -555) {
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					}
				} else
					return false;
			} else {
				if (line.startsWith("C")) {
					numberOfControls++;
					if (line.split(" ").length != 3)
						return false;
					switch (line.split(" ")[1]) {
					case "FREIGHT":
						if (line.split(" ")[2].length() != 3) {
							errors.put(numberOfLines, "Freight control line is not correct");
							return false;
						} else if (!isDigit(line.split(" ")[2])) {
							errors.put(numberOfLines, "Freight control line is not a number");
							return false;
						}
						int amtOfFreights = Integer.parseInt(line.split(" ")[2]);
						int amtInIteration = 0;
						sumOfFreightRoutes += amtOfFreights;
						while (reader.ready()) {
							reader.mark(0);
							String anotha = reader.readLine();
							if (anotha.startsWith("C")) {
								if (amtOfFreights != amtInIteration) {
									errors.put(numberOfLines, "# of trains != actual");
									return false;
								}
								reader.reset();
								break;
							}
							numberOfNonControls++;
							numberOfLines++;
							if (anotha.split(" ").length != 5) {
								errors.put(numberOfLines, "Freight line is not properly strucutred");
								continue;
							} else if (!isDigit(anotha.split(" ")[0].split("Station")[1])) {
								errors.put(numberOfLines, "Station ID must be a number");
								continue;
							} else if (!isDigit(anotha.split(" ")[1].split("Station")[1])) {
								errors.put(numberOfLines, "Station ID must be a number");
								continue;
							} else if (anotha.split(" ")[2].length() != 1) {
								errors.put(numberOfLines, "Station type must be either F or W");
								continue;
							} else if (anotha.split(" ")[2].charAt(0) != 'F' && anotha.split(" ")[2].charAt(0) != 'W') {
								errors.put(numberOfLines, "Station type must be either F or W");
								continue;
							} else if (anotha.split(" ")[2].length() != 1) {
								errors.put(numberOfLines, "Station type must be either F or W");
								continue;
							} else if (!anotha.split(" ")[3].contains(":")
									|| anotha.split(" ")[3].split(":").length != 2) {
								errors.put(numberOfLines, "Time needs to be formatted as 99:99");
								continue;
							} else if (!isDigit(anotha.split(" ")[3].split(":")[0])) {
								errors.put(numberOfLines, "Start time hour must be a number");
								continue;
							} else if (!isDigit(anotha.split(" ")[3].split(":")[1])) {
								errors.put(numberOfLines, "Start time minute must be a number");
								continue;
							} else if (!isDigit(anotha.split(" ")[0].split("Station")[1])) {
								errors.put(numberOfLines, "Station1 must be a number");
								continue;
							} else if (!isDigit(anotha.split(" ")[1].split("Station")[1])) {
								errors.put(numberOfLines, "Station2 must be a number");
								continue;
							}

							int station1 = Integer.parseInt(anotha.split(" ")[0].split("Station")[1]);
							int station2 = Integer.parseInt(anotha.split(" ")[1].split("Station")[1]);
							char repeating = anotha.split(" ")[2].charAt(0);
							int hour = Integer.parseInt(anotha.split(" ")[3].split(":")[0]);
							int minute = Integer.parseInt(anotha.split(" ")[3].split(":")[1]);
							if (station1 < 0 || station2 < 0 || hour < 0 || minute < 0) {
								errors.put(numberOfLines, "Numbers cannot be negative");
								continue;
							}

							if (!doesStationExist(station1)) {
								errors.put(numberOfLines, "Station1 does not exist");
								continue;
							}

							if (!doesStationExist(station2)) {
								errors.put(numberOfLines, "Station2 does not exist");
								continue;
							}
							freightRoutes.add(anotha);
							amtInIteration++;
							submitQuery(
									"INSERT INTO `Freight_Route` (`RouteID`, `StartLocId`, `EndLocID`, `StartTime`, `Repeating`, `RunDay`, `Active`) VALUES "
											+ "('" + freightRouteID
											+ "', (SELECT `StationID` From `station` WHERE Station.StationID = '"
											+ station1 + "'), "
											+ "(SELECT `StationID` From `station` WHERE Station.StationID = '"
											+ station2 + "'), '" + ((hour * 60) + minute) + "', '"
											+ (repeating == 'F' ? '5' : '7') + "', '0', '1')");
							freightRouteID++;
						}
						break;
					case "PASSENGER":
						if (line.split(" ")[2].length() != 3) {
							errors.put(numberOfLines, "Passenger control line is not correct");
							return false;
						} else if (!isDigit(line.split(" ")[2])) {
							errors.put(numberOfLines, "Passenger control line is not a number");
							return false;
						}
						int amtOfPassengerRoutes = Integer.parseInt(line.split(" ")[2]);
						int stopNumber = 1;
						while (reader.ready()) {
							reader.mark(0);
							String anotha = reader.readLine();
							numberOfLines++;
							if (anotha.startsWith("T")) {
								return false;
							} else if (anotha.startsWith("C")) {
								if (anotha.split(" ").length != 4) {
									return false;
								} else if (anotha.split(" ")[3].length() != 1) {
									errors.put(numberOfLines, "Route control line must have a type of F or W");
									continue;
								} else if (anotha.split(" ")[3] != "F" && anotha.split(" ")[3] != "W") {
									errors.put(numberOfLines, "Route control line must have a type of F or W");
									continue;
								} else if (!isDigit(anotha.split(" ")[2])) {
									errors.put(numberOfLines, "Number of routes must be a number");
									continue;
								}
								passengerRoutes.put(passengerRouteID, new ArrayList<>());
								numOfRoutes++;
								int expected = Integer.parseInt(anotha.split(" ")[2]);

								numOfStopsInRoute += expected;
								System.out.println(numOfStopsInRoute);
								int repeating = (anotha.split(" ")[3].toLowerCase().charAt(0) == 'f' ? 5 : 7);
								while (reader.ready()) {
									reader.mark(0);
									String lastLine = reader.readLine();
									System.out.println(lastLine);
									if (lastLine.startsWith("C")) {
										if (expected != stopNumber - 1) {
											errors.put(numberOfLines, "# of trains != actual");
											return false;
										}
										stopNumber = 1;
										passengerRouteID++;
										numOfRoutes++;
										reader.reset();
										break;
									} else if (lastLine.startsWith("T")) {
										if (lastLine.split(" ").length != 3) {
											errors.put(numberOfLines, "Trailer structure is not correct");
											return false;
										} else if (lastLine.split(" ")[1].length() != 6) {
											errors.put(numberOfLines, "Size of first trailer value is not 6");
											return false;
										} else if (lastLine.split(" ")[2].length() != 6) {
											errors.put(numberOfLines, "Size of second trailer value is not 6");
											return false;
										} else if (!isDigit(lastLine.split(" ")[1])) {
											errors.put(numberOfLines, "First trailer number is not a number");
											return false;
										} else if (!isDigit(lastLine.split(" ")[2])) {
											errors.put(numberOfLines, "Second trailer number is not a number");
											return false;
										}
										int allLines = Integer.parseInt(lastLine.split(" ")[1]);
										int sumOfAllControlLines = Integer.parseInt(lastLine.split(" ")[2]);
										System.out.println("Number of routes: " + passengerRoutes.size());
										System.out.println("Number of passenger routes: " + amtOfPassengerRoutes);
										int sumOfRoutes = 0;
										for (int key : passengerRoutes.keySet()) {
											for (int j = 0; j < passengerRoutes.get(key).size(); j++)
												sumOfRoutes++;
										}
										System.out.println("Number of stops in routes total: " + numOfStopsInRoute);
										System.out.println("Number of stops in routes total: " + sumOfRoutes);

										System.out.println("Freight route expected: " + sumOfFreightRoutes);
										System.out.println("Freight route got: " + freightRoutes.size());
										if (allLines == numberOfLines
												&& sumOfAllControlLines == (numberOfLines - numberOfNonControls)
												&& passengerRoutes.size() == amtOfPassengerRoutes
												&& numOfStopsInRoute == sumOfRoutes
												&& sumOfFreightRoutes == freightRoutes.size())
											return true;
										else
											return false;
									}
									numberOfLines++;
									numberOfNonControls++;
									if (lastLine.split(" ").length != 2) {
										errors.put(numberOfLines, "Route station is not valid");
										continue;
									} else if (!lastLine.split(" ")[1].contains(":")) {
										errors.put(numberOfLines, "Time needs to be formatted as 99:99");
										continue;
									} else if (!isDigit(lastLine.split(" ")[1].split(":")[0])) {
										errors.put(numberOfLines, "Start time hour must be a number");
										continue;
									} else if (!isDigit(lastLine.split(" ")[1].split(":")[1])) {
										errors.put(numberOfLines, "Start time minute must be a number");
										continue;
									}

									/* Start parsing */
									String station1 = lastLine.split(" ")[0];
									if (station1.split("Station").length != 2) {
										errors.put(numberOfLines, "A station must start with the word \"Stataion\"");
										continue;
									}
									if (!isDigit(station1.split("Station")[1])) {
										errors.put(numberOfLines, "Station ID must be a number");
										continue;
									}

									int hour = Integer.parseInt(lastLine.split(" ")[1].split(":")[0]);
									int minute = Integer.parseInt(lastLine.split(" ")[1].split(":")[1]);
									passengerRoutes.get(passengerRouteID).add(lastLine);
									submitQuery(
											"INSERT INTO `passenger_route` (`RouteID`, `StopNumber`, `LocID`, `StartTime`, `Repeating`, `RunDay`, `Active`) VALUES "
													+ "('" + passengerRouteID + "', '" + stopNumber + "', '"
													+ lastLine.split("Station")[1].split(" ")[0] + "', '"
													+ ((hour * 60) + minute) + "', '" + repeating + "', '0', '1')");
									stopNumber++;
								}
							}
						}
						break;
					}
				} else
					return false;
			}
		}
		return true;

	}

	/* Handles the input when a maintenance file is detected */
	private boolean processMaintenanceFile() throws IOException {
		int prevDay = 0;
		System.out.println("Maintenance File Detected!");
		BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
		while (reader.ready()) {
			String line = reader.readLine();
			numberOfLines++;
			System.out.println(numberOfLines + " " + line);
			if (numberOfLines == 1) {
				if (line.startsWith("H")) {
					if (line.split("H")[1].split(" ")[0].length() != 4) {
						errors.put(numberOfLines, "Header value needs to be formatted correctly!");
						return false;
					} else if (!isDigit(line.split("H")[1].split(" ")[0])) {
						errors.put(numberOfLines, "The file number needs to be a number");
						return false;
					}
					String fileNumberString = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumberString + " " + date);
					int fileError = checkFileNumber(Integer.parseInt(fileNumberString));
					int fileNumber = Integer.parseInt(fileNumberString);
					System.out.println(fileError);
					System.out.println(fileNumber);
					if (fileError == -1) {
						errors.put(numberOfLines, "The file number is not correct");
						return false;
					} else if ((fileError + 1) == fileNumber) {
						submitQuery("DELETE FROM `file_number` WHERE 1");
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					} else if (fileError == -555) {
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					}
				} else
					return false;
			} else {
				if (line.startsWith("C DAY")) {
					if (line.split(" ").length != 3) {
						errors.put(numberOfLines, "Day is not formatted correctly.");
						return false;
					}
					if (!isDigit(line.split(" ")[2])) {
						errors.put(numberOfLines, "Day value is not a number");
						return false;
					}
					int day = Integer.parseInt(line.split(" ")[2]);

					System.out.println("DAY #" + day);
					if (prevDay != (day - 1)) {
						errors.put(numberOfLines, "Days are not in order");
						return false;
					}
					prevDay = day;
					while (reader.ready()) {
						reader.mark(0);
						String anotherOne = reader.readLine();
						System.out.println(anotherOne);
						System.out.println(numberOfLines + " " + anotherOne);
						if (anotherOne.startsWith("C DAY")) {
							reader.reset();
							break;
						}
						numberOfLines++;
						if (anotherOne.startsWith("C")) {
							if (anotherOne.split(" ")[0].length() == 1 && anotherOne.split(" ").length == 3) {
								if (anotherOne.split(" ").length != 3 || anotherOne.split(" ")[2].length() != 4) {
									errors.put(numberOfLines, "Control line is not formatted correctly.");
									return false;
								}
								if (!isDigit(anotherOne.split(" ")[2])) {
									errors.put(numberOfLines, "Control value needs to be a number");
									return false;
								}
								int amt = Integer.parseInt(anotherOne.split(" ")[2]);
								if (amt < 0) {
									errors.put(numberOfLines, "Amount cannot be less than 0");
									return false;
								}
								int amtInIteration = 0;
								switch (anotherOne.split(" ")[1]) {
								case "EDGE":
									while (reader.ready()) {
										reader.mark(0);
										String lastLine = reader.readLine();

										if (lastLine.startsWith("C")) {
											reader.reset();
											if (amt != amtInIteration) {
												errors.put(numberOfLines, "The number expected differed from actual");
												return false;
											}
											amtInIteration = 0;
											break;
										}
										numberOfLines++;
										System.out.println(numberOfLines + " " + lastLine);

										numberOfNonControls++;
										String loc1 = lastLine.split(" ")[0];
										String loc2 = lastLine.split(" ")[1];
										int duration = Integer.parseInt(lastLine.split(" ")[2]);
										if (duration < 0) {
											errors.put(numberOfLines, "Duration cannot be less than 0");
											continue;
										}
										boolean isLoc1Station = loc1.toLowerCase().contains("station");
										boolean isLoc2Station = loc2.toLowerCase().contains("station");

										if (!isDigit(loc1.toLowerCase().split((isLoc1Station ? "station" : "hub"))[1])
												|| !isDigit(loc2.toLowerCase()
														.split((isLoc2Station ? "station" : "hub"))[1])) {
											errors.put(numberOfLines, "The ID's need to be numbers");
											continue;
										}
										int loc1ID = Integer.parseInt(
												loc1.toLowerCase().split((isLoc1Station ? "station" : "hub"))[1]);
										int loc2ID = Integer.parseInt(
												loc2.toLowerCase().split((isLoc2Station ? "station" : "hub"))[1]);
										int trackID = 0;

										if (loc1ID < 0 || loc2ID < 0) {
											errors.put(numberOfLines, "Location ID's must be positive");
											continue;
										}

										if (isLoc1Station) {
											if (!doesStationExist(loc1ID)) {
												errors.put(numberOfLines, "Station1 does not exist");
												continue;
											}
										} else {
											if (!doesHubExist(loc1ID)) {
												errors.put(numberOfLines, "Hub1 does not exist");
												continue;
											}
										}

										if (isLoc2Station) {
											if (!doesStationExist(loc2ID)) {
												errors.put(numberOfLines, "Station2 does not exist");
												continue;
											}
										} else {
											if (!doesHubExist(loc2ID)) {
												errors.put(numberOfLines, "Hub2 does not exist");
												continue;
											}
										}

										String query = "SELECT `TrackID` FROM `track` WHERE `"
												+ (isLoc1Station ? "StartLocStationID" : "StartLocHubID") + "`= '"
												+ loc1ID + "' AND `"
												+ (isLoc2Station ? "EndLocStationID" : "EndLocHubID") + "` = '" + loc2ID
												+ "'";
										try {
											ResultSet set = connection.prepareStatement(query).executeQuery();
											while (set.next()) {
												trackID = set.getInt("TrackID");
											}
										} catch (SQLException e) {
											e.printStackTrace();
										}

										if (trackID == 0) {
											errors.put(numberOfLines, "Edge not found");
											continue;
										}
										query = "SELECT `EndDay` FROM `Maintenance` WHERE `TrackID3` = '" + trackID
												+ "'";
										int endDay = 0;
										try {
											ResultSet set = connection.prepareStatement(query).executeQuery();
											while (set.next()) {
												endDay = set.getInt("EndDay");
											}
										} catch (SQLException e) {
											e.printStackTrace();
										}
										amtInIteration++;
										query = "";
										if (endDay == 0) {
											query = "INSERT INTO `maintenance` (`MaintenanceID`, `TrackID3`, `StartDay`, `EndDay`, `Active`) VALUES ('"
													+ maintenanceID + "', '" + trackID + "', '" + day + "', '"
													+ (day + duration) + "', '0')";
											maintenanceID++;
										} else {
											query = "UPDATE `maintenance` SET `EndDay` = `EndDay` + '" + duration
													+ "' WHERE `TrackID3` = '" + trackID + "'";
										}
										try {
											connection.prepareStatement(query).execute();
										} catch (SQLException e) {
											e.printStackTrace();
										}

									}
									break;
								case "LOCOMOTIVE":
									while (reader.ready()) {
										reader.mark(0);
										String lastLine = reader.readLine();
										System.out.println(numberOfLines + " " + lastLine);
										if (lastLine.startsWith("C")) {
											reader.reset();
											if (amt != amtInIteration) {
												errors.put(numberOfLines, "The number expected differed from actual");
												return false;
											}
											amtInIteration = 0;
											break;
										}
										numberOfLines++;
										if (!isDigit(lastLine.split("Locomotive")[1].split(" ")[0])) {
											errors.put(numberOfLines, "Locomotive ID must be a number");
											continue;
										}
										numberOfNonControls++;
										int locomotiveID = Integer
												.parseInt(lastLine.split("Locomotive")[1].split(" ")[0]);

										if (!doesTrainExist(locomotiveID)) {
											errors.put(numberOfLines, "Locomotive does not exist");
											continue;
										}

										if (!isDigit(lastLine.split(" ")[1])) {
											errors.put(numberOfLines, "Duration must be a number");
											continue;
										}

										int duration = Integer.parseInt(lastLine.split(" ")[1]);
										if (duration < 0) {
											errors.put(numberOfLines, "Duration cannot be less than 0");
											continue;
										}
										String query = "SELECT `EndDay` FROM `Maintenance` WHERE `TrainID5` = '"
												+ locomotiveID + "'";
										int endDay = 0;
										try {
											ResultSet set = connection.prepareStatement(query).executeQuery();
											while (set.next()) {
												endDay = set.getInt("EndDay");
											}
										} catch (SQLException e) {
											e.printStackTrace();
										}
										amtInIteration++;
										query = "";
										if (endDay == 0) {
											query = "INSERT INTO `maintenance` (`MaintenanceID`, `TrainID5`, `StartDay`, `EndDay`, `Active`) VALUES ('"
													+ maintenanceID + "', '" + locomotiveID + "', '" + day + "', '"
													+ (day + duration) + "', '0')";
											maintenanceID++;
										} else {
											query = "UPDATE `maintenance` SET `EndDay` = `EndDay` + '" + duration
													+ "' WHERE `TrainID5` = '" + locomotiveID + "'";
										}
										try {
											connection.prepareStatement(query).execute();
										} catch (SQLException e) {
											e.printStackTrace();
										}
									}
									break;
								case "STATION":
									while (reader.ready()) {
										reader.mark(0);
										String lastLine = reader.readLine();
										System.out.println(numberOfLines + " " + lastLine);
										if (lastLine.startsWith("C")) {
											reader.reset();
											if (amt != amtInIteration) {
												errors.put(numberOfLines, "The number expected differed from actual");
												return false;
											}
											amtInIteration = 0;
											break;
										} else if (lastLine.startsWith("T")) {
											if (lastLine.split(" ").length != 4 || lastLine.split(" ")[1].length() != 6
													|| lastLine.split(" ")[2].length() != 6
													|| lastLine.split(" ")[3].length() != 6) {
												errors.put(numberOfLines, "Trailer line not structured correctly");
												return false;
											}
											int allLines = Integer.parseInt(lastLine.split(" ")[1]);
											int sumOfControls = Integer.parseInt(lastLine.split(" ")[2]);
											int numDays = Integer.parseInt(lastLine.split(" ")[3]);

											System.out.println("Number of lines: " + (numberOfLines - 1));
											System.out.println("Number of lines: " + allLines);

											System.out.println("Number of controls: " + sumOfControls);
											System.out
													.println("Number of controls: " + (allLines - numberOfNonControls));

											System.out.println("Number of days: " + numDays);
											System.out.println("Number of days: " + (day));

											if ((numberOfLines - 1) == allLines
													&& sumOfControls == (allLines - numberOfNonControls)
													&& numDays == day)
												return true;
											else
												return false;
										}

										numberOfLines++;
										numberOfNonControls++;
										if (!isDigit(lastLine.split("Station")[1].split(" ")[0])) {
											errors.put(numberOfLines, "Station ID must be a number");
											continue;
										}
										int stationID = Integer.parseInt(lastLine.split("Station")[1].split(" ")[0]);

										if (!doesStationExist(stationID)) {
											errors.put(numberOfLines, "Station does not exist");
											continue;
										}
										if (!isDigit(lastLine.split(" ")[1])) {
											errors.put(numberOfLines, "Duration must be a number");
											continue;
										}

										int duration = Integer.parseInt(lastLine.split(" ")[1]);
										if (duration < 0) {
											errors.put(numberOfLines, "Duration cannot be less than 0");
											continue;
										}
										String query = "SELECT `EndDay` FROM `Maintenance` WHERE `StationID3` = '"
												+ stationID + "'";
										int endDay = 0;
										try {
											ResultSet set = connection.prepareStatement(query).executeQuery();
											while (set.next()) {
												endDay = set.getInt("EndDay");
											}
										} catch (SQLException e) {
											e.printStackTrace();
										}
										query = "";

										amtInIteration++;
										if (endDay == 0) {
											query = "INSERT INTO `maintenance` (`MaintenanceID`, `StationID3`, `StartDay`, `EndDay`, `Active`) VALUES ('"
													+ maintenanceID + "', '" + stationID + "', '" + day + "', '"
													+ (day + duration) + "', '0')";
											maintenanceID++;
										} else {
											query = "UPDATE `maintenance` SET `EndDay` = `EndDay` + '" + duration
													+ "' WHERE `StationID3` = '" + stationID + "'";
										}
										try {
											connection.prepareStatement(query).execute();
										} catch (SQLException e) {
											e.printStackTrace();
										}

									}
									break;
								default:
									return false;
								}
							}
						}
					}
				} else
					return false;

			}
		}
		return false;
	}

	/*
	 * Handles the input when a config file is detected DONE?
	 */
	private boolean processConfigurationFile() throws IOException {
		System.out.println("Config File Detected!");
		BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
		while (reader.ready()) {
			String line = reader.readLine();
			System.out.println(line);
			if (numberOfLines == 0) {
				if (line.startsWith("H")) {
					if (line.split("H")[1].split(" ")[0].length() != 4) {
						errors.put(numberOfLines, "Header value needs to be formatted correctly!");
						return false;
					} else if (!isDigit(line.split("H")[1].split(" ")[0])) {
						errors.put(numberOfLines, "The file number needs to be a number");
						return false;
					}
					String fileNumberString = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumberString + " " + date);
					int fileError = checkFileNumber(Integer.parseInt(fileNumberString));
					int fileNumber = Integer.parseInt(fileNumberString);
					System.out.println(fileError);
					System.out.println(fileNumber);
					if (fileError == -1) {
						errors.put(numberOfLines, "The file number is not correct");
						return false;
					} else if ((fileError + 1) == fileNumber) {
						submitQuery("DELETE FROM `file_number` WHERE 1");
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					} else if (fileError == -555) {
						submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + fileNumber + "')");
					}
				} else
					return false;
			}
			if (line.startsWith("LOCOMOTIVE")) {
				numberOfLines++;
				if (line.split("LOCOMOTIVE").length != 2) {
					return false;
				} else {
					String countString = line.split(" ")[1];
					if (line.split(" ").length != 6) {
						errors.put(numberOfLines, "Locomotive structure is wrong");
						continue;
					} else if (line.split(" ")[1].length() != 1
							|| (line.split(" ")[1].charAt(0) != 'F' && line.split(" ")[1].charAt(0) != 'P')) {
						errors.put(numberOfLines, "Locomotive type is wrong");
						continue;
					} else if (!isDigit(line.split(" ")[2])) {
						errors.put(numberOfLines, "Fuel Capacity is not a number");
						continue;
					} else if (!isDigit(line.split(" ")[3])) {
						errors.put(numberOfLines, "Fuel Cost is not a number");
						continue;
					} else if (!isDigit(line.split(" ")[4])) {
						errors.put(numberOfLines, "Speed is not a number");
						continue;
					} else if (!isDigit(line.split(" ")[5])) {
						errors.put(numberOfLines, "Freight/Passenger Capacity is not a number");
						continue;
					}

					String[] splitMessage = line.split(" ");
					char type = splitMessage[1].charAt(0);
					int fuelCapacity = Integer.parseInt(splitMessage[2]);
					int fuelCost = Integer.parseInt(splitMessage[3]);
					int speed = Integer.parseInt(splitMessage[4]);
					int capacity = Integer.parseInt(splitMessage[5]);
					if (fuelCapacity < 0 || fuelCost < 0 || speed < 0 || capacity < 0) {
						errors.put(numberOfLines, "Values cannot be less than 0");
						continue;
					}

					submitQuery("UPDATE `train` SET `TopSpeed` = '" + speed + "',  `Capacity` = '" + capacity
							+ "' WHERE `Freight` = '" + (type == 'F' ? "1" : "0") + "'");
					System.out.println(type + " " + fuelCapacity + " " + fuelCost + " " + speed + " ?" + capacity);
				}
			} else if (line.startsWith("CREWS")) {
				numberOfLines++;
				if (line.split("CREWS").length != 2) {
					errors.put(numberOfLines, "Crew length != 2");
					return false;
				}

				else {
					String countString = line.split(" ")[1];
					if (line.split(" ").length != 2)
						return false;
					// int length = Integer.parseInt(countString);
					// System.out.println("Amt of crews: " + length);
				}
			} else if (line.startsWith("FUEL")) {
				numberOfLines++;
				/*
				 * if (line.split("FUEL").length != 2) {
				 * errors.put(numberOfLines, "Fuel length != 2"); return false;
				 * } else { String countString = line.split(" ")[1]; if
				 * (line.split(" ").length != 2) return false; int length =
				 * Integer.parseInt(countString);
				 * System.out.println("Amt of fuel: " + length); }
				 */
			} else if (line.startsWith("RUN")) {
				numberOfLines++;
				/*
				 * TODO: Database storage for run time, how long before it stops
				 * aka this
				 */
				if (line.split("RUN").length != 2)
					return false;
				else {
					String countString = line.split(" ")[1];
					if (line.split(" ").length != 2) {
						errors.put(numberOfLines, "Run days is not valid");
						return false;
					} else if (!isDigit(line.split(" ")[1])) {
						errors.put(numberOfLines, "Run days is not a number");
						return false;
					}
					int length = Integer.parseInt(countString);
					if (length < 0) {
						errors.put(numberOfLines, "Run days cannot be negative");
						return false;
					}
					String query = "INSERT INTO `day` (`DayID`, `DayType`, `Holiday`) VALUES ('";
					for (int i = 1; i < length; i++) {
						query += i + "', '0', '0'), ('";
					}
					query += length + "', '0', '0')";
					submitQuery(query);
				}
			} else if (line.startsWith("T")) {
				if (line.split(" ").length != 2) {
					errors.put(numberOfLines, "Trailer structure is not correct");
					return false;
				} else {
					String countString = line.split(" ")[1];
					if (countString.length() != 4) {
						errors.put(numberOfLines, "Trailer number length needs to be 4");
						return false;
					} else {
						int count = Integer.parseInt(countString);
						if (count == numberOfLines)
							return true;
						return false;
					}
				}
			}
		}
		return false;

	}

	private void printSummary() {
		System.out.println("Number of hubs: " + numOfHubs);
		System.out.println("Number of stations: " + numOfStations);
		System.out.println("Number of locomotives: " + numOfLocomotives);
		System.out.println("Number of edges: " + numOfEdges);
		System.out.println();
		System.out.println("Number of hubs: " + hubs.size());
		System.out.println("Number of stations: " + stations.size());
		System.out.println("Number of locomotives: " + loco.size());
		System.out.println("Number of edges: " + edges.size());
	}

	private static void submitQuery(String query) {
		queries.add(query);
	}

	private static void sendQueries(ArrayList<String> queries) throws SQLException {
		for (String query : queries) {
			connection.prepareStatement(query).execute();
		}
		InputReader.queries.clear();
	}

	/* Clears the database for all of the values that are not dynamic */
	private static void resetDatabase() {
		submitQuery("DELETE FROM `maintenance` WHERE 1");
		submitQuery("DELETE FROM `freight_route` WHERE 1");
		submitQuery("DELETE FROM `passenger_route` WHERE 1");
		submitQuery("DELETE FROM `track` WHERE 1");
		submitQuery("DELETE FROM `train` WHERE 1");
		submitQuery("DELETE FROM `station` WHERE 1");
		submitQuery("DELETE FROM `hub` WHERE 1");
		submitQuery("DELETE FROM `file_number` WHERE 1");
	}

	private int checkFileNumber(int fileNum) {
		String query = "SELECT `Number` FROM `file_number` WHERE 1";
		int fileNumber = -555;
		try {
			ResultSet set = connection.prepareStatement(query).executeQuery();
			while (set.next()) {
				fileNumber = set.getInt("Number");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (fileNumber == (fileNum - 1))
			return fileNumber;
		else if (fileNumber == -555)
			return fileNumber;
		return -1;
	}

	private boolean doesStationExist(int id) {
		String query = "SELECT * FROM `station` WHERE `StationID` = '" + id + "'";
		try {
			ResultSet set = connection.prepareStatement(query).executeQuery();
			while (set.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean doesHubExist(int id) {
		String query = "SELECT * FROM `hub` WHERE `HubID` = '" + id + "'";
		try {
			ResultSet set = connection.prepareStatement(query).executeQuery();
			while (set.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean doesTrainExist(int locomotiveID) {
		String query = "SELECT * FROM `train` WHERE `TrainID` = '" + locomotiveID + "'";
		boolean exists = false;
		try {
			ResultSet set = connection.prepareStatement(query).executeQuery();
			while (set.next()) {
				exists = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exists;
	}

	private boolean isDigit(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}

	}
}
