package Location;

import Utils.MacRssiPair;
import Utils.Position;
import Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by thomas on 5-4-17.
 */
public class SamrtLocationFinder implements LocationFinder {

	private HashMap<String, Position> knownLocations; //Contains the known locations of APs. The long is a MAC address.

	public SamrtLocationFinder(){
		knownLocations = Utils.getKnownLocations(); //Put the known locations in our hashMap
	}

	@Override
	public Position locate(MacRssiPair[] data) {
		return null;
	}

	private ArrayList<MacRssiPair> getAllKnownFromList(MacRssiPair[] data){
		ArrayList<MacRssiPair> knownFromList = new ArrayList<>();

		for (int i = 0; (i < data.length) || (knownFromList.size() >= 5); i++) {
			if (knownLocations.containsKey(data[i].getMacAsString())) {
				knownFromList.add(data[i]);
			}
		}

		return knownFromList;
	}
}
