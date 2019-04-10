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

	private int lineCount = 0;
	private File fileToRead;
	private int fileNumber = 0;
	private ArrayList<String> hubs = new ArrayList<>();
	private ArrayList<String> stations = new ArrayList<>();
	private ArrayList<String> edges = new ArrayList<>();
	private ArrayList<String> loco = new ArrayList<>();
	private HashMap<Integer, String> errors = new HashMap<>();
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
	private int numOfControls = 0;
	private int numOfNonControls = 0;

	public static void main(String[] args) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cis_375_schema", "root", "");
			/* This code was to get the IDs for each route table in the database so I could continue on from
			 * the last ID that is indexed in the database
			ResultSet set = connection.prepareCall("SELECT MAX(`RouteID`) FROM `freight_route`").executeQuery();
			while (set.next()) {
				freightRouteID = set.getInt("MAX(`RouteID`)") + 1;
			}
			set = connection.prepareCall("SELECT MAX(`RouteID`) FROM `passenger_route`").executeQuery();
			while (set.next()) {
				passengerRouteID = set.getInt("MAX(`RouteID`)") + 1;
			}

			set = connection.prepareCall("SELECT MAX(`MaintenanceID`) FROM `maintenance`").executeQuery();
			while (set.next()) {
				maintenanceID = set.getInt("MAX(`MaintenanceID`)") + 1;
			}*/
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long start = System.currentTimeMillis();
		/* File path to read */
		new InputReader(new File("C:/Users/Cameron/Desktop/Config"));
		System.out.println(System.currentTimeMillis() - start);
	}
	
	public InputReader(File file) {
		fileToRead = file;
		boolean success = parseFile();
		System.out.println("Success? " + success);
		if (true) {
			for (int key : errors.keySet()) {
				System.out.println("Error on line " + key + ": " + errors.get(key));
			}
		}
		System.out.println("Number of lines: " + numberOfLines);
		System.out.println("Number of non control lines: " + numberOfNonControls);
		System.out.println("Number of control lines: " + numberOfControls);
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
							return processStructureFile();
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

	/**
	 * Parses a structure file
	 * 
	 * @return a boolean if it worked correctly or not
	 */
	private boolean processStructureFile() throws IOException {
		System.out.println("Structure File Detected!");
		BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
		lineCount = 0;
		while (reader.ready()) {
			String line = reader.readLine();
			if (lineCount == 0) {
				if (line.startsWith("H")) {
					numberOfLines++;
					if (line.split("H")[1].split(" ")[0].length() != 4) {
						errors.put(numberOfLines, "Header value needs to be formatted correctly!");
					}
					String fileNumber = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumber + " " + date);
					submitQuery("INSERT INTO `file_number` (`Number`) VALUES('" + Integer.parseInt(fileNumber) + "')");
				} else
					return false;
			} else {
				if (line.startsWith("C")) {
					numberOfControls++;
					numberOfLines++;
					if (line.split(" ").length != 3) {
						errors.put(numberOfLines, "Control line structure not correct");
						return false;
					} else {
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
									numberOfLines++;
									numberOfNonControls++;
									break;
								} else {
									numberOfLines++;
									numberOfNonControls++;
									if (anotherLine.split("Hub").length != 2) {
										errors.put(numberOfLines, "Hubs need an id!");
										continue;
									}
									submitQuery("INSERT INTO `hub` (`HubID`, `Active`) VALUES('"
											+ Integer.parseInt(anotherLine.split("Hub")[1]) + "', '1')");
								}
							}
							break;
						case "STATION":
							numOfStations = Integer.parseInt(line.split(" ")[2]);
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
									submitQuery(
											"INSERT INTO `station` (`StationID`, `Freight`, `Random_On_Min`, `Random_On_Max`, `Random_Off_Min`, `Random_Off_Max`, `Ticket_Price`, `Active`) VALUES('"
													+ Integer.parseInt(anotherLine.split("Station")[1].split(" ")[0])
													+ "', '" + (anotherLine.split(" ")[1].startsWith("P") ? "0" : "1")
													+ "', '" + Integer.parseInt(anotherLine.split(" ")[2]) + "', '"
													+ Integer.parseInt(anotherLine.split(" ")[3]) + "', '"
													+ Integer.parseInt(anotherLine.split(" ")[4]) + "', '"
													+ Integer.parseInt(anotherLine.split(" ")[5]) + "', '"
													+ Integer.parseInt(anotherLine.split(" ")[6].split("\\.")[0])
													+ "', '1')");
								}
							}
							break;
						case "EDGE":
							numOfEdges = Integer.parseInt(line.split(" ")[2]);
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
									submitQuery("INSERT INTO `track` (`TrackID`, `"
											+ (isStartAStation ? "StartLocStationID" : "StartLocHubID") + "`, `"
											+ (isEndAStation ? "EndLocStationID" : "EndLocHubID")
											+ "`, `StartTime`, `EndTime`, `Length`, `Active`) " + "VALUES('" + trackID
											+ "', '" + startName + "', '" + endName + "', '" + startTime + "', '"
											+ endTime + "', '" + Integer.parseInt(anotherLine.split(" ")[2])
											+ "', '1')");
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
									if (anotherLine.split("T").length != 2)
										return false;
									else if (anotherLine.split(" ").length != 3)
										return false;
									else {
										int totalEntries = Integer.parseInt(anotherLine.split(" ")[1]);
										int controlCount = Integer.parseInt(anotherLine.split(" ")[2]);
										if (numberOfNonControls == controlCount
												&& totalEntries == (numberOfNonControls + numberOfControls)) {
											return true;
										} else {
											return false;
										}
									}
								} else {
									numberOfLines++;
									numberOfNonControls++;
									submitQuery(
											"INSERT INTO `train` (`TrainID`, `Freight`, `HomeHubID`, `Capacity`, `TopSpeed`, `Active`) VALUES('"
													+ Integer.parseInt(anotherLine.split("Locomotive")[1].split(" ")[0])
													+ "', '"
													+ (anotherLine.split(" ")[2].toLowerCase().charAt(0) == 'p' ? "0"
															: "1")
													+ "', (SELECT `HubID` From `hub` WHERE Hub.HubId = '"
													+ Integer.parseInt(anotherLine.split("Hub")[1].split(" ")[0])
													+ "'), '0', '0', '1')");
								}
							}
						}
					}
				}
			}
			lineCount++;
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
			System.out.println(line);
			if (lineCount == 0) {
				if (line.startsWith("H")) {
					String fileNumber = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumber + " " + date);
				}
			} else {
				if (line.startsWith("C DAY")) {
					if (line.split(" ").length != 3)
						return false;
					int day = Integer.parseInt(line.split(" ")[2]);
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
						if (anotherOne.startsWith("C")) {
							if (anotherOne.split(" ")[0].length() == 1 && anotherOne.split(" ").length == 3) {
								int amt = Integer.parseInt(anotherOne.split(" ")[2]);
								switch (anotherOne.split(" ")[1]) {
								case "FREIGHT":
									while (reader.ready()) {
										reader.mark(0);
										String lastLine = reader.readLine();
										if (lastLine.startsWith("C")) {
											reader.reset();
											break;
										}
										int station1 = Integer.parseInt(lastLine.split(" ")[0].split("Station")[1]);
										int station2 = Integer.parseInt(lastLine.split(" ")[1].split("Station")[1]);
										int hour;
										int minute;
										if (!lastLine.contains(":")) {
											hour = 0;
											minute = 0;
										} else {
											hour = Integer.parseInt(lastLine.split(" ")[2].split(":")[0]);
											minute = Integer.parseInt(lastLine.split(" ")[2].split(":")[1]);
										}
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
									int amtOfPassengers = Integer.parseInt(line.split(" ")[2]);
									while (reader.ready()) {
										reader.mark(0);
										String anotha = reader.readLine();
										System.out.println(anotha);
										if (anotha.startsWith("T")) {

											return true;
										} else if (anotha.startsWith("C")) {
											if (anotha.split(" ").length == 3) {
												reader.reset();
												break;
											}
											if (anotha.split(" ").length != 4)
												return false;
											int expected = Integer.parseInt(anotha.split(" ")[2]);
											while (reader.ready()) {
												reader.mark(0);
												String lastLine = reader.readLine();
												System.out.println(lastLine);
												if (lastLine.startsWith("C")) {
													stopNumber = 1;
													passengerRouteID++;
													reader.reset();
													break;
												} else if (lastLine.startsWith("T")) {
													/* Parse here */
													return true;
												} else if (lastLine.split(" ").length != 2)
													return false;
												int hour = Integer.parseInt(lastLine.split(" ")[1].split(":")[0]);
												int minute = Integer.parseInt(lastLine.split(" ")[1].split(":")[1]);
												submitQuery(
														"INSERT INTO `passenger_route` (`RouteID`, `StopNumber`, `LocID`, `StartTime`, `Repeating`, `RunDay`, `Active`) VALUES "
																+ "('" + passengerRouteID + "', '" + stopNumber + "', '"
																+ lastLine.split("Station")[1].split(" ")[0] + "', '"
																+ ((hour * 60) + minute) + "', '1', '" + day
																+ "', '1')");
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
			lineCount++;
		}
		return false;
	}

	/* Handles the input when a repeatable routes file is detected */
	private boolean processRepeatableRoutes() throws IOException {
		System.out.println("Repeatable Routes File Detected!");
		BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
		while (reader.ready()) {
			String line = reader.readLine();
			if (lineCount == 0) {
				if (line.startsWith("H")) {
					String fileNumber = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumber + " " + date);
				}
			} else {
				if (line.startsWith("C")) {
					if (line.split(" ").length != 3)
						return false;
					switch (line.split(" ")[1]) {

					case "FREIGHT":
						int amtOfFreights = Integer.parseInt(line.split(" ")[2]);
						while (reader.ready()) {
							reader.mark(0);
							String anotha = reader.readLine();
							if (anotha.startsWith("C")) {
								reader.reset();
								break;
							}
							if (anotha.split(" ").length != 5)
								return false;
							int station1 = Integer.parseInt(anotha.split(" ")[0].split("Station")[1]);
							int station2 = Integer.parseInt(anotha.split(" ")[1].split("Station")[1]);
							char repeating = anotha.split(" ")[2].charAt(0);
							int hour = Integer.parseInt(anotha.split(" ")[3].split(":")[0]);
							int minute = Integer.parseInt(anotha.split(" ")[3].split(":")[1]);
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
						int amtOfPassengers = Integer.parseInt(line.split(" ")[2]);
						int stopNumber = 1;
						while (reader.ready()) {
							reader.mark(0);
							String anotha = reader.readLine();
							System.out.println(anotha);
							if (anotha.startsWith("T")) {

								return true;
							} else if (anotha.startsWith("C")) {
								if (anotha.split(" ").length != 4)
									return false;
								int expected = Integer.parseInt(anotha.split(" ")[2]);
								int repeating = (anotha.split(" ")[3].toLowerCase().charAt(0) == 'f' ? 5 : 7);
								while (reader.ready()) {
									reader.mark(0);
									String lastLine = reader.readLine();
									System.out.println(lastLine);
									if (lastLine.startsWith("C")) {
										stopNumber = 1;
										passengerRouteID++;
										reader.reset();
										break;
									} else if (lastLine.startsWith("T")) {
										/* Parse here */
										return true;
									} else if (lastLine.split(" ").length != 2)
										return false;
									int hour = Integer.parseInt(lastLine.split(" ")[1].split(":")[0]);
									int minute = Integer.parseInt(lastLine.split(" ")[1].split(":")[1]);
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
				} else if (line.startsWith("T")) {

					return true;
				} else
					return false;
			}
			lineCount++;
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
			System.out.println(line);
			if (lineCount == 0) {
				if (line.startsWith("H")) {
					String fileNumber = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumber + " " + date);
				}
			} else {
				if (line.startsWith("C DAY")) {
					if (line.split(" ").length != 3)
						return false;
					int day = Integer.parseInt(line.split(" ")[2]);
					System.out.println("DAY #" + day);
					/*
					 * if (day <= prevDay) return false;
					 */
					while (reader.ready()) {
						reader.mark(0);
						String anotherOne = reader.readLine();
						System.out.println(anotherOne);
						if (anotherOne.startsWith("C DAY")) {
							reader.reset();
							break;
						}
						if (anotherOne.startsWith("C")) {
							if (anotherOne.split(" ")[0].length() == 1 && anotherOne.split(" ").length == 3) {
								int amt = Integer.parseInt(anotherOne.split(" ")[2]);
								switch (anotherOne.split(" ")[1]) {
								case "EDGE":
									while (reader.ready()) {
										reader.mark(0);
										String lastLine = reader.readLine();
										if (lastLine.startsWith("C")) {
											reader.reset();
											break;
										}
										String loc1 = lastLine.split(" ")[0];
										String loc2 = lastLine.split(" ")[1];
										int duration = Integer.parseInt(lastLine.split(" ")[2]);
										boolean isLoc1Station = loc1.toLowerCase().contains("station");
										boolean isLoc2Station = loc2.toLowerCase().contains("station");
										int loc1ID = Integer.parseInt(
												loc1.toLowerCase().split((isLoc1Station ? "station" : "hub"))[1]);
										int loc2ID = Integer.parseInt(
												loc2.toLowerCase().split((isLoc2Station ? "station" : "hub"))[1]);
										int trackID = 0;
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
											System.out.println("Edge not found");
											return false;
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
										if (lastLine.startsWith("C")) {
											reader.reset();
											break;
										}
										int locomotiveID = Integer
												.parseInt(lastLine.split("Locomotive")[1].split(" ")[0]);
										int duration = Integer.parseInt(lastLine.split(" ")[1]);
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
										if (lastLine.startsWith("C")) {
											reader.reset();
											break;
										} else if (lastLine.startsWith("T")) {

											return true;
										}
										int stationID = Integer.parseInt(lastLine.split("Station")[1].split(" ")[0]);
										int duration = Integer.parseInt(lastLine.split(" ")[1]);
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
			lineCount++;
		}
		return false;
	}

	/* Handles the input when a config file is detected */
	private boolean processConfigurationFile() throws IOException {
		System.out.println("Config File Detected!");
		BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
		while (reader.ready()) {
			String line = reader.readLine();
			System.out.println(line);
			if (lineCount == 0) {
				if (line.startsWith("H")) {
					String fileNumber = line.split("H")[1].split(" ")[0];
					String date = line.split("H")[1].split(" ")[1];
					System.out.println(fileNumber + " " + date);
				} else if (line.startsWith("LOCOMOTIVE")) {
					if (line.split("LOCOMOTIVE").length != 2)
						return false;
					else {
						String countString = line.split(" ")[1];
						if (line.split(" ").length != 6)
							return false;
						String[] splitMessage = line.split(" ");
						char type = splitMessage[1].charAt(0);
						int fuelCapacity = Integer.parseInt(splitMessage[2]);
						int fuelCost = Integer.parseInt(splitMessage[3]);
						int speed = Integer.parseInt(splitMessage[4]);
						int capacity = Integer.parseInt(splitMessage[5]);
						submitQuery("UPDATE `train` SET `TopSpeed` = '" + speed + "',  `Capacity` = '" + capacity
								+ "' WHERE `Freight` = '" + (type == 'F' ? "1" : "0") + "'");
						System.out.println(type + " " + fuelCapacity + " " + fuelCost + " " + speed + " " + capacity);
					}
				} else if (line.startsWith("CREWS")) {
					if (line.split("CREWS").length != 2)
						return false;
					else {
						String countString = line.split(" ")[1];
						if (line.split(" ").length != 2)
							return false;
						int length = Integer.parseInt(countString);
						System.out.println("Amt of crews: " + length);
					}
				} else if (line.startsWith("FUEL")) {
					if (line.split("FUEL").length != 2)
						return false;
					else {
						String countString = line.split(" ")[1];
						if (line.split(" ").length != 2)
							return false;
						int length = Integer.parseInt(countString);
						System.out.println("Amt of fuel: " + length);
					}
				} else if (line.startsWith("RUN")) {
					if (line.split("RUN").length != 2)
						return false;
					else {
						String countString = line.split(" ")[1];
						if (line.split(" ").length != 2)
							return false;
						int length = Integer.parseInt(countString);
						String query = "INSERT INTO `day` (`DayID`, `DayType`, `Holiday`) VALUES ('";
						for (int i = 1; i < length; i++) {
							query += i + "', '0', '0'), ('";
						}
						query += length + "', '0', '0')";
						System.out.println(query);
						submitQuery(query);
					}
				} else if (line.startsWith("T")) {
					if (line.split("T").length != 2)
						return false;
					else {
						String countString = line.split(" ")[1];
						if (countString.length() != 4)
							return false;
						else {
							int count = Integer.parseInt(countString);
							System.out.println("Line Count: " + count);
							return true;
						}
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

	private void submitQuery(String query) {

		/*
		 * try { connection.prepareStatement(query).execute(); } catch
		 * (SQLException e) { e.printStackTrace(); }
		 */

	}

	/* Clears the database for all of the values that are not dynamic */
	private void resetDatabase() {
		submitQuery("DELETE `freight_route` WHERE true");
		submitQuery("DELETE `passenger_route` WHERE true");
		submitQuery("DELETE `station` WHERE true");
		submitQuery("DELETE `train` WHERE true");
		submitQuery("DELETE `track` WHERE true");
		submitQuery("DELETE `station` WHERE true");
		submitQuery("DELETE `hub` WHERE true");
	}

}
