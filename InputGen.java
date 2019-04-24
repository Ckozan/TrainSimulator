package simulator375.pkg2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputGen {

	private static ArrayList<String> hubs = new ArrayList<>();
	private static ArrayList<String> stations = new ArrayList<>();
	private static HashMap<String, ArrayList<String>> edges = new HashMap<>();
	private static HashMap<String, Character> locomotives = new HashMap<>();
	private static int days = 5;
	private static int header = 1;

	static BufferedWriter writer;

	public static void main(String[] args) throws IOException {

		System.out.println("STRUCTURE\n");
		writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/Structure"));
		writer.write(buildStructure());
		writer.flush();
		writer.close();
		System.out.println("CONFIG\n");
		writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/Config"));
		writer.write(buildConfiguration());
		writer.flush();
		writer.close();
		System.out.println("MAINTENENCE\n");
		writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/Maitenence"));
		writer.write(buildMaintenence(5, 10, 15, 20));
		writer.flush();
		writer.close();

		System.out.println("REPEATABLE ROUTES \n");
		writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/Daily Routes"));
		writer.write(buildDailyRoutes(5));
		writer.flush();
		writer.close();

		System.out.println("DAILY ROUTES \n");
		writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/Repeatable Routes"));
		writer.write(buildRepeatableRoutes());
		writer.flush();
		writer.close();

	}

	public static String buildConfiguration() {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(header));
		builder.append(buildLocomotiveConfig(locomotives.size()));
		builder.append(buildConfigTrailer(builder));
		return builder.toString();
	}

	public static String buildStructure() {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(header));
		builder.append(buildHubs(15));
		builder.append(buildStations(45));
		builder.append(buildEdges(100));
		builder.append(buildLocomotives(60, 45));

		builder.append(buildStructureTrailer(builder));
		return builder.toString();
	}

	public static String buildRepeatableRoutes() {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(header));
		builder.append(buildFreightRoutes(true, 7));
		builder.append(buildPassengerRoutes(10, true));
		builder.append(buildRouteTrailer(builder));
		return builder.toString();
	}

	public static String buildDailyRoutes(int days) {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(header));
		for (int i = 0; i < days; i++) {
			builder.append("C DAY ");
			for (int j = 0; j < 3 - getDigits(days); j++)
				builder.append('0');
			builder.append((i + 1) + "\n");
			builder.append(buildFreightRoutes(false, 7));
			builder.append(buildPassengerRoutes(10, false));
		}
		builder.append(buildRouteTrailer(builder));
		return builder.toString();
	}

	public static String buildFreightRoutes(boolean repeatingRoute, int numOfFreightRoutes) {
		StringBuilder builder = new StringBuilder();
		builder.append("C FREIGHT ");
		for (int i = 0; i < 3 - getDigits(numOfFreightRoutes); i++)
			builder.append('0');
		builder.append(numOfFreightRoutes + "\n");
		for (int i = 0; i < numOfFreightRoutes; i++) {
			String station1 = stations.get((int) (Math.random() * stations.size()));
			String station2 = stations.get((int) (Math.random() * stations.size()));
			char type = ((Math.random() * 100) > 12 ? 'F' : 'W');
			int startHour = ((int) (Math.random() * 24));
			int startMin = ((int) (Math.random() * 60));
			String startTime = "";
			if (getDigits(startHour) == 1) {
				startTime += "0" + startHour;
			} else {
				startTime += startHour;
			}
			startTime += ":";
			if (getDigits(startMin) == 1) {
				startTime += "0" + startMin;
			} else {
				startTime += startMin;
			}

			if (repeatingRoute) {
				builder.append(station1 + " " + station2 + " " + type + " "
						+ ((int) (Math.random() * 100) < 12 ? "0" : startTime) + " " + ((int) (Math.random() * 5000))
						+ "\n");
			} else {
				builder.append(station1 + " " + station2 + " " + ((int) (Math.random() * 100) < 12 ? "0" : startTime)
						+ " " + ((int) (Math.random() * 5000)) + "\n");
			}
		}
		return builder.toString();
	}

	public static String buildPassengerRoutes(int numOfPassengerRoutes, boolean isRepeatable) {
		StringBuilder builder = new StringBuilder();
		String station;
		String arrivalTime;
		builder.append("C PASSENGER ");
		for (int i = 0; i < 3 - getDigits(numOfPassengerRoutes); i++)
			builder.append('0');
		builder.append(numOfPassengerRoutes + "\n");
		for (int i = 0; i < numOfPassengerRoutes; i++) {
			int amtOfRoutes = ((int) (Math.random() * 10));
			String routes = "";
			for (int j = 0; j < 3 - getDigits(amtOfRoutes); j++)
				routes += '0';
			routes += amtOfRoutes;
			if(isRepeatable)
			builder.append("C ROUTE " + routes + " " + ((int) (Math.random() * 100) < 25 ? "F" : "W") + "\n");
			else 
				builder.append("C ROUTE " + routes + "\n");
			for (int j = 0; j < amtOfRoutes; j++) {
				station = stations.get((int) (Math.random() * stations.size()));
				int startHour = ((int) (Math.random() * 24));
				int startMin = ((int) (Math.random() * 60));
				String startTime = "";
				if (getDigits(startHour) == 1) {
					startTime += "0" + startHour;
				} else {
					startTime += startHour;
				}
				startTime += ":";
				if (getDigits(startMin) == 1) {
					startTime += "0" + startMin;
				} else {
					startTime += startMin;
				}

				builder.append(station + " " + startTime + "\n");
			}
		}

		return builder.toString();
	}

	public static String buildMaintenenceTrailer(StringBuilder b, int days) {
		StringBuilder builder = new StringBuilder();
		int numOfAllLines = 0;
		int numOfNonControls = 0;
		int numOfControls = 0;
		System.out.println(b.toString().split("\n").length);
		for (String line : b.toString().split("\n")) {
			if (line.toLowerCase().startsWith("t") || line.toLowerCase().startsWith("h0"))
				continue;
			numOfAllLines++;
			if (!line.toLowerCase().startsWith("c")) {
				numOfNonControls++;
			}
		}

		builder.append("T ");
		if (getDigits(numOfAllLines) < 6) {
			for (int i = 0; i < 6 - getDigits(numOfAllLines); i++) {
				builder.append('0');
			}
		}
		builder.append(numOfAllLines + " ");

		if (getDigits(numOfAllLines-numOfNonControls) < 6) {
			for (int i = 0; i < 6 - getDigits(numOfAllLines-numOfNonControls); i++) {
				builder.append('0');
			}
		}
		builder.append(numOfAllLines-numOfNonControls + " ");

		if (getDigits(days) < 6) {
			for (int i = 0; i < 6 - getDigits(days); i++) {
				builder.append('0');
			}
		}
		builder.append(days + "");
		return builder.toString();
	}

	public static String buildStructureTrailer(StringBuilder b) {
		StringBuilder builder = new StringBuilder();
		int numOfControls = 0;
		int numOfNonControls = 0;
		int sum = 0;
		System.out.println(b.toString().split("\n").length);
		for (String line : b.toString().split("\n")) {
			if (line.toLowerCase().startsWith("h0"))
				continue;
			if (!line.toLowerCase().startsWith("c")) {
				numOfNonControls++;
			} else {
				numOfNonControls++;
				numOfControls++;
			}
		}

		sum = numOfNonControls - numOfControls;
		builder.append("T ");
		if (getDigits(numOfNonControls) < 4) {
			for (int i = 0; i < 4 - getDigits(numOfNonControls); i++) {
				builder.append('0');
			}
		}
		builder.append(numOfNonControls + " ");

		if (getDigits(sum) < 4) {
			for (int i = 0; i < 4 - getDigits(sum); i++) {
				builder.append('0');
			}
		}
		builder.append(sum + "");
		return builder.toString();
	}

	public static String buildRouteTrailer(StringBuilder b) {
		StringBuilder builder = new StringBuilder();
		int numOfControls = 0;
		int numOfLines = 0;
		
		for (String line : b.toString().split("\n")) {
			if(line.startsWith("H0"))
				continue;
			if (!line.toLowerCase().startsWith("c")) {
				numOfLines++;
			} else {
				numOfControls++;
				numOfLines++;
			}
		}

		builder.append("T ");
		for (int i = 0; i < 6 - getDigits(numOfLines); i++) {
			builder.append('0');
		}
		builder.append(numOfLines + " ");

		for (int i = 0; i < 6 - getDigits(numOfControls); i++) {
			builder.append('0');
		}
		builder.append(numOfControls + "");
		return builder.toString();
	}

	public static String buildConfigTrailer(StringBuilder b) {
		StringBuilder builder = new StringBuilder();
		int numOfControls = 0;
		int numOfNonControls = 0;
		for (String line : b.toString().split("\n")) {
			if(line.startsWith("H0") || line.startsWith("T"))
				continue;
			if (!line.toLowerCase().startsWith("c ")) {
				numOfNonControls++;
			} else {
				numOfControls++;
			}
		}
		builder.append("T ");
		if (getDigits(numOfNonControls) < 4) {
			for (int i = 0; i < 4 - getDigits(numOfNonControls); i++) {
				builder.append('0');
			}
		}
		builder.append(numOfNonControls + "\n");
		return builder.toString();
	}

	public static String buildHeader(int fileNumber) {
		StringBuilder builder = new StringBuilder();
		builder.append("H");
		for (int i = 0; i < 4 - getDigits(fileNumber); i++)
			builder.append('0');
		builder.append(fileNumber);
		builder.append(' ');
		builder.append(LocalDate.now().getDayOfMonth() + "/" + LocalDate.now().getMonthValue() + "/"
				+ LocalDate.now().getYear() + "\n");
		header++;
		return builder.toString();
	}

	public static String buildLocomotiveConfig(int numOfLocomotives) {
		StringBuilder builder = new StringBuilder();
		builder.append("LOCOMOTIVE F " + (int) (Math.random() * 10000) + " " + (int) (Math.random() * 25) + " "
				+ (int) (Math.random() * 100) + " " + (int) (Math.random() * 6000) + "\n");
		builder.append("LOCOMOTIVE P " + (int) (Math.random() * 10000) + " " + (int) (Math.random() * 25) + " "
				+ (int) (Math.random() * 100) + " " + (int) (Math.random() * 3750) + "\n");

		builder.append("CREWS " + (int) (Math.random() * 6) + "\n");
		builder.append("FUEL " + (int) (Math.random() * 40000) + "\n");
		builder.append("RUN " + ((int) (Math.random() * 30) + 2) + "\n");

		return builder.toString();

	}

	public static String buildHubs(int numberOfHubs) {
		StringBuilder builder = new StringBuilder();
		builder.append("C HUB ");
		for (int i = 0; i < 4 - getDigits(numberOfHubs); i++)
			builder.append('0');
		builder.append(numberOfHubs + "\n");
		for (int i = 1; i < numberOfHubs + 1; i++) {
			hubs.add("Hub" + i);
			builder.append("Hub" + i + "\n");
		}
		return builder.toString();
	}

	public static String buildStations(int numberOfStations) {
		StringBuilder builder = new StringBuilder();
		builder.append("C STATION ");
		for (int i = 0; i < 4 - getDigits(numberOfStations); i++)
			builder.append('0');
		builder.append(numberOfStations + "\n");
		for (int i = 1; i < numberOfStations + 1; i++) {
			stations.add("Station" + i);
			int onRange = (int) (Math.random() * 500);
			int onOffset = (int) (Math.random() * 250);

			int offRange = (int) (Math.random() * 500);
			int offOffset = (int) (Math.random() * 250);

			builder.append("Station" + i + " " + (Math.random() < .5 ? "F" : "P") + " 5 " + Math.abs(onRange - onOffset)
					+ " " + Math.abs(onRange + onOffset) + " " + Math.abs(offRange - offOffset) + " "
					+ Math.abs(offRange + offOffset) + " " + Math.round((Math.random() * 20)) + ".0 \n");
		}
		return builder.toString();
	}

	public static String buildLocomotives(int numOfLocomotives, int numOfPassenger) {
		StringBuilder builder = new StringBuilder();
		builder.append("C LOCOMOTIVE ");
		for (int j = 0; j < 4 - getDigits(numOfLocomotives); j++)
			builder.append('0');
		builder.append(numOfLocomotives + "\n");
		for (int i = 0; i < numOfLocomotives; i++) {
			if (numOfPassenger > i) {
				locomotives.put("Locomotive" + i, 'P');
				builder.append("Locomotive" + i + " " + hubs.get((int) (Math.random() * hubs.size())) + " P\n");

			} else {
				locomotives.put("Locomotive" + i, 'F');
				builder.append("Locomotive" + i + " " + hubs.get((int) (Math.random() * hubs.size())) + " F\n");
			}
		}
		return builder.toString();
	}

	public static String buildEdges(int numOfEdges) {
		StringBuilder builder = new StringBuilder();
		builder.append("C EDGE ");
		for (int j = 0; j < 4 - getDigits(numOfEdges); j++)
			builder.append('0');
		builder.append(numOfEdges + "\n");
		for (int i = 0; i < numOfEdges; i++) {
			String firstBuilding = (Math.random() > .12 ? stations.get((int) (Math.random() * stations.size()))
					: hubs.get((int) (Math.random() * hubs.size())));
			String secondBuilding = (Math.random() > .12 ? stations.get((int) (Math.random() * stations.size()))
					: hubs.get((int) (Math.random() * hubs.size())));
			if (edges.get(firstBuilding) != null) {
				edges.get(firstBuilding).add(secondBuilding);
			} else {
				edges.put(firstBuilding, new ArrayList<String>());
				edges.get(firstBuilding).add(secondBuilding);
			}
			builder.append(firstBuilding + " " + secondBuilding + " " + (int) (Math.random() * 100) + " 00:00 00:00\n");
		}
		return builder.toString();
	}

	public static String buildMaintenence(int days, int maxNumEdges, int maxNumLocomotives, int maxNumStations) {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(header));

		for (int i = 1; i < days + 1; i++) {
			builder.append("C DAY ");
			for (int j = 0; j < 3 - getDigits(days); j++)
				builder.append('0');
			builder.append(i + "\n");
			int numEdges = ((int) (Math.random() * maxNumEdges));
			System.out.println(getDigits(numEdges));
			builder.append("C EDGE ");
			for (int k = 0; k < 4 - getDigits(numEdges); k++)
				builder.append('0');
			builder.append(numEdges + "\n");
			for (int j = 0; j < numEdges; j++) {
				List<String> keysAsArray = new ArrayList<String>(edges.keySet());
				int firstBuildingIndex = (int) (Math.random() * keysAsArray.size());
				String firstBuilding = keysAsArray.get(firstBuildingIndex);
				int secondBuildingIndex = (int) ((Math.random() * edges.get(firstBuilding).size()));
				String secondBuilding = edges.get(firstBuilding).get(secondBuildingIndex);
				builder.append(
						firstBuilding + " " + secondBuilding + " " + ((int) (Math.random() * InputGen.days)) + "\n");

			}
			int numLoco = ((int) (Math.random() * maxNumLocomotives));
			System.out.println(getDigits(numLoco));
			builder.append("C LOCOMOTIVE ");
			for (int k = 0; k < 4 - getDigits(numLoco); k++)
				builder.append('0');
			builder.append(numLoco + "\n");
			for (int j = 0; j < numLoco; j++) {
				List<String> keysAsArray = new ArrayList<String>(locomotives.keySet());
				builder.append(keysAsArray.get((int) (Math.random() * keysAsArray.size())) + " "
						+ ((int) (Math.random() * InputGen.days)) + "\n");
			}
			int numStations = ((int) (Math.random() * maxNumStations));
			System.out.println(getDigits(numStations));
			builder.append("C STATION ");
			for (int k = 0; k < 4 - getDigits(numStations); k++)
				builder.append('0');
			builder.append(numStations + "\n");
			for (int j = 0; j < numStations; j++) {
				builder.append(stations.get((int) (Math.random() * stations.size())) + " "
						+ ((int) (Math.random() * InputGen.days)) + "\n");
			}
		}
		builder.append(buildMaintenenceTrailer(builder, days));
		return builder.toString();
	}

	public static int getDigits(int num) {
		if (num == 0)
			return 1;
		return (int) (Math.log10((double) num) + 1);
	}
}