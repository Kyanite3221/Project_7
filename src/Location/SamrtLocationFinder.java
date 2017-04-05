package Location;

import Utils.MacRssiPair;
import Utils.Position;
import Utils.Utils;

import java.util.*;

/**
 * Created by thomas on 5-4-17.
 */
public class SamrtLocationFinder implements LocationFinder {

	private static int POSITION_ARRAY_LENGTH = 5;

	private HashMap<String, Position> knownLocations; //Contains the known locations of APs. The long is a MAC address.

	private LinkedList<Position> previousPositions = new LinkedList<>();

	public SamrtLocationFinder(){
		knownLocations = Utils.getKnownLocations(); //Put the known locations in our hashMap
	}

	@Override
	public Position locate(MacRssiPair[] data) {
		ArrayList<MacRssiPair> knownData = getAllKnownFromList(data);
		Position[] strongestKnownPositions = getPositionsOfStrongestKnown(knownData);
		Position calculatedLocation = LocationCalculator.weightedPositionCalculator(strongestKnownPositions);
		Position averagePosition = LocationCalculator.determineAverage(previousPositions, calculatedLocation);

		if (previousPositions.size() == 0 ||
				(! (previousPositions.getFirst().getX() == averagePosition.getX() &&
				previousPositions.getFirst().getY() == averagePosition.getY()))) {
			previousPositions.push(averagePosition);
		}

		if (previousPositions.size() > 9) {
			previousPositions.removeLast();
		}

		return averagePosition;
	}

	private ArrayList<MacRssiPair> getAllKnownFromList(MacRssiPair[] data){
		ArrayList<MacRssiPair> knownFromList = new ArrayList<>();

		for (int i = 0; i < data.length; i++) {
			if (knownLocations.containsKey(data[i].getMacAsString())) {
				knownFromList.add(data[i]);
			}
		}

		return knownFromList;
	}

	private Position[] getPositionsOfStrongestKnown(ArrayList<MacRssiPair> knownFromList) {
		Position[] positionsOfStrongestKnown = new Position[POSITION_ARRAY_LENGTH];

		Collections.sort(knownFromList, Comparator.comparingInt(a -> a.getRssi()));

		for (int i = 0; i < positionsOfStrongestKnown.length && i < knownFromList.size(); i++) {
			positionsOfStrongestKnown[i] = knownLocations.get(knownFromList.get(i).getMacAsString());
		}

		System.out.println(Arrays.toString(positionsOfStrongestKnown));
		return positionsOfStrongestKnown;
	}
}
