package Location;

import Utils.MacRssiPair;
import Utils.Position;
import Utils.Utils;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Created by thomas on 5-4-17.
 */
public class SecondLocationFinder implements LocationFinder {

	private static final double FREQUENCY_IN_GHZ = 5200;
	private LinkedList<Position> previousPositions = new LinkedList<>();
	private static final int PREVIOUS_POSITIONS_LENGTH = 7;

	public SecondLocationFinder() {

	}

	@Override
	public Position locate(MacRssiPair[] data) {
		MacRssiPair[] filteredPairs = filterKnownData(data);

		if (filteredPairs.length == 0) {
			if (previousPositions.size()>0) {
				return LocationCalculator.pureAverage(previousPositions);
			} else {
				return new Position(-1,-1);
			}
		}
		Position[] centers = getPositions(filteredPairs);

		System.out.println(centers);
		Rectangle[] rectangles = new Rectangle[centers.length];

		for (int i = 0; i < centers.length; i++) {
			double distance = calculateDistanceToRouter(filteredPairs[i].getRssi()) * 10;

			System.out.println(distance);

			int topLeftX = (int) (centers[i].getX() - distance);
			int topLeftY = (int) (centers[i].getY() - distance);

			int bottomRightX = (int) (centers[i].getX() + distance);
			int bottomRightY = (int) (centers[i].getY() + distance);


			Rectangle rect = new Rectangle(topLeftX, topLeftY, (int) distance*2, (int) distance*2);
			rectangles[i] = rect;
		}

		Rectangle estimatedRectangle = (Rectangle) rectangles[0].clone();
		for (int i = 1; i < rectangles.length; i++) {
			estimatedRectangle = estimatedRectangle.intersection(rectangles[i]);
		}

		double x = estimatedRectangle.getX() + estimatedRectangle.getWidth()/2;
		double y = estimatedRectangle.getY() + estimatedRectangle.getHeight()/2;

		Position calculatedPosition = new Position(x,y);

		if (! (calculatedPosition.getX() == -1 &&
				calculatedPosition.getY() == -1)) {
			previousPositions.push(calculatedPosition);
		}

		if (previousPositions.size() >= PREVIOUS_POSITIONS_LENGTH) {
			previousPositions.removeLast();
		}

		return LocationCalculator.determineAverage(previousPositions, calculatedPosition);
	}

	private static Position[] getPositions(MacRssiPair[] filteredPairs) {
		Position[] positions = new Position[filteredPairs.length];
		for (int i = 0; i < filteredPairs.length; i++) {
			positions[i] = Utils.getKnownLocations5GHz().get(filteredPairs[i].getMacAsString());
		}
		return positions;
	}

	private static MacRssiPair[] filterKnownData(MacRssiPair[] data) {
		ArrayList<MacRssiPair> foundKnownData = new ArrayList<>(Arrays.asList(data));
		Iterator<MacRssiPair> iterator = foundKnownData.iterator();
		while (iterator.hasNext()) {
			if (!Utils.getKnownLocations5GHz().containsKey(iterator.next().getMacAsString())) {
				iterator.remove();
			}
		}
		MacRssiPair[] val = new MacRssiPair[foundKnownData.size()];
		foundKnownData.toArray(val);
		return val;
	}

	public static double calculateDistanceToRouter(int rssi) {
		double exponent = (27.55 - 20*Math.log10(FREQUENCY_IN_GHZ) - rssi) / 20;
		return Math.pow(10, exponent);
	}

	private static Position addPosition(Position pos1, Position pos2) {
		double x = pos1.getX() + pos2.getX();
		double y = pos1.getY() + pos2.getY();
		return new Position(x, y);
	}

	public static void main(String[] args) {
		System.out.println(calculateDistanceToRouter(-45));
		System.out.println(calculateDistanceToRouter(-59));
		System.out.println(calculateDistanceToRouter(-67));
	}
}
