package Location;

import Utils.MacRssiPair;
import Utils.Position;
import Utils.Utils;

import javax.crypto.Mac;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by thomas on 5-4-17.
 */
public class SamrtLocationFinder implements LocationFinder {

	private static final int PREVIOUS_POSITIONS_LENGTH = 5;
	private static int POSITION_ARRAY_LENGTH = 5;

	private HashMap<String, Position> knownLocations; //Contains the known locations of APs. The long is a MAC address.

	private LinkedList<Position> previousPositions = new LinkedList<>();

	public SamrtLocationFinder(){
		knownLocations = Utils.getKnownLocations(); //Put the known locations in our hashMap
	}

	@Override
	public Position locate(MacRssiPair[] data) {
		MacRssiPair[] filteredData = filterKnownData(data);
		sortDataByRssi(filteredData);

		System.out.println(Arrays.toString(filteredData));
		for (MacRssiPair filteredDatum : filteredData) {
			System.out.println(knownLocations.get(filteredDatum.getMacAsString()));
		}

		Position calculatedPosition = LocationCalculator.fairPositionCalculator(filteredData);

		if (! (calculatedPosition.getX() == -1 &&
				calculatedPosition.getY() == -1)) {
			previousPositions.push(calculatedPosition);
		}

		if (previousPositions.size() >= PREVIOUS_POSITIONS_LENGTH) {
			previousPositions.removeLast();
		}

		Position averagePosition = LocationCalculator.determineAverage(previousPositions, calculatedPosition);

		return averagePosition;
	}

	private MacRssiPair[] filterKnownData(MacRssiPair[] data) {
		ArrayList<MacRssiPair> foundKnownData = new ArrayList<>(Arrays.asList(data));
		Iterator<MacRssiPair> iterator = foundKnownData.iterator();
		while (iterator.hasNext()) {
			if (!knownLocations.containsKey(iterator.next().getMacAsString())) {
				iterator.remove();
			}
		}
		MacRssiPair[] val = new MacRssiPair[foundKnownData.size()];
		foundKnownData.toArray(val);
		return val;
	}

	private void sortDataByRssi(MacRssiPair[] data) {
		Comparator<MacRssiPair> rssiComparator = Comparator.comparing(a -> a.getRssi());
		Arrays.sort(data, rssiComparator.reversed());
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
}
