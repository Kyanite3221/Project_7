package Location;

import Utils.MacRssiPair;
import Utils.Position;

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

    /*public static Position fairPositionCalculator(MacRssiPair[] givenPairs){
        Position[] givens = new Position[givenPairs.length];
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
    }*/



    public static Position determineAverage(Position first, Position second) {
        return new Position((first.getX()+second.getY())/2.0,(first.getY()+second.getY())/2.0);
    }
}
