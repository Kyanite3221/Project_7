package Location;

import Utils.MacRssiPair;
import Utils.Position;
import Utils.Utils;

import java.util.LinkedList;

/**
 * Created by freem on 4/5/2017.
 */
public class LocationCalculator {
    public static Position weightedPositionCalculator(Position[] givens){
        int validPositions=0;
        for (int i = 0; i < 5; i++) {
            if (givens[i] != null &&
                    givens[i].getX() >= 0 &&
                    givens[i].getX() <= 200 &&
                    givens[i].getY() >= 0  &&
                    givens[i].getY() <= 200) {
                validPositions++;
            } else {
                givens[i] = null;
            }
        }

        if (validPositions == 0) {
            return new Position(-1,-1);
        }

        double finX = 0;
        double finY = 0;
        for (int i = validPositions; i > 0; i--) {
            if (givens[validPositions-i] != null){
                finX += givens[validPositions-i].getX()*i;
                finY += givens[validPositions-i].getY()*i;
            }
        }

        switch (validPositions) {
            case 1: {
                return new Position(finX, finY);
            }
            case 2: {
                return new Position(finX/3.0, finY/3.0);
            }
            case 3: {
                return new Position(finX/6.0,finY/6.0);
            }
            case 4: {
                return new Position (finX/10.0,finY/10.0);
            }
            default: {
                return new Position(finX/15.0,finY/15.0);
            }
        }
    }

    public static Position fairPositionCalculator(MacRssiPair[] givenPairs){
        int[] givens = new int[givenPairs.length];
        for (int i = 0; i < givens.length; i++) {
            givens[i] = givenPairs[i].getRssi();
        }
        int validPositions=0;
        for (int i = 0; i < 5; i++) {
            if (givenPairs[i] != null) {
                validPositions++;
            }
        }

        if (validPositions == 0) {
            return new Position(-1,-1);
        }

        double finX = Utils.getKnownLocations().get(givenPairs[0].getMac()).getX()*100.0;
        double finY = Utils.getKnownLocations().get(givenPairs[0].getMac()).getY()*100.0;
        double factor = 100.0;
        double weigth;
        double[] usedFactors = new double[validPositions];

        for (int i = 1; i < validPositions; i++) {
                Position current = Utils.getKnownLocations().get(givenPairs[validPositions-i].getMac());
                weigth = factor / calculateFactor(givenPairs[0].getRssi(),givenPairs[i].getRssi());

                finX += current.getX()*weigth;
                finY += current.getY()*weigth;
                usedFactors[i] = weigth;
        }

        double totalFactors = 0;
        for (int i = 0; i < usedFactors.length; i++) {
            totalFactors += usedFactors[i];
        }

        return new Position(finX/totalFactors,finY/totalFactors);
    }



    public static Position determineAverage(LinkedList<Position> previousPositions, Position newestCalculated) {
        if (previousPositions.size() == 0) {    //set up, list is empty, assume the first calculated value
            return newestCalculated;
        } else if (newestCalculated.getY() == -1 && newestCalculated.getX() == -1) {    //wrong calculated value, assume the last legit value
            return previousPositions.getFirst();
        }

        double finX = 0;
        double finY = 0;
        double ammountOfEntries = 1;

        for (Position location : previousPositions) {
            finX += location.getX();
            finY += location.getY();
            ammountOfEntries++;
        }

        finX += newestCalculated.getX();
        finY += newestCalculated.getY();

        return new Position(finX/ammountOfEntries, finY/ammountOfEntries);
    }


    public static double calculateFactor (int rSSI, int rSSII) {
        int difference = rSSI-rSSII;
        double result = Math.pow(2,Math.log10(difference)/Math.log10(3));
        return result;
    }
}
