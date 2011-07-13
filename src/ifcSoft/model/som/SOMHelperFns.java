/**
 *  Copyright (C) 2011  Kyle Thayer <kyle.thayer AT gmail.com>
 *
 *  This file is part of the IFCSoft project (http://ifcsoft.com)
 *
 *  IFCSoft is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ifcSoft.model.som;

import java.awt.Point;
import java.util.LinkedList;


/**
 *
 * @author kthayer
 */
public class SOMHelperFns {
/**
   * Find the Best Matching Unit (node of closes value to the given value).
   * @param testweight
   * @return
   */
  static protected Point findBMU(float[] testweight, SOM som){
    Point bestMatch = new Point();
    float bestMatchD = -1; //-1 means no distance yet found
    int numBest = 0; //number that are currently tied for best

    int mapWidth = som.SOMnodes.length;
    int mapHeight = som.SOMnodes[0].length; //NOTE: I might have width and height labeled backwards

    //check against every point, see if it is current best
    for(int i = 0; i < mapWidth; i++){
      for(int j = 0; j < mapHeight; j++){

        //float euclidD = 0;
        float currentW[] = som.getMapWeights(new Point(i, j));
        float euclidD = findeuclidD(testweight, currentW, som);

        //if it is a better match or the 1st match
        if(euclidD < bestMatchD || bestMatchD == -1){
          bestMatch.x =i;
          bestMatch.y =j;
          bestMatchD = euclidD;
          numBest = 1;
        }else if(euclidD == bestMatchD){
          /*If it's the same, we must pick one of those "same" values randomly */
          /*I didn't explain that well, but this is a clever little trick*/
          if(Math.random() < 1.0/numBest){
            bestMatch.x =i;
            bestMatch.y =j;
          }
          numBest++;

        }
      }
    }

    return bestMatch;
  }

  /**
   * Find the Second Best Matching Unit to the given node.
   * @param testweight
   * @return
   */
  static protected Point[] findSecondBMU(Point node, SOM som){
    Point bestMatch = new Point();
    float bestMatchD = -1; //-1 means no distance yet found
    Point secondBestMatch = new Point();
    float secondBestMatchD = -1; //-1 means no distance yet found
    int numBest = 0; //number that are currently tied for best

    int mapWidth = som.SOMnodes.length;
    int mapHeight = som.SOMnodes[0].length; //NOTE: I might have width and height labeled backwards

    //check against every point, see if it is current best
    for(int i = 0; i < mapWidth; i++){
      for(int j = 0; j < mapHeight; j++){
        if(i != node.x || j != node.y){
          //float euclidD = 0;
          float currentW[] = som.getMapWeights(new Point(i, j));
          float euclidD = findeuclidD(som.SOMnodes[node.x][node.y].getWeights(), currentW, som);

          //if it is a better match or the 1st match
          if(euclidD < bestMatchD || bestMatchD == -1){
            secondBestMatch = bestMatch;
            secondBestMatchD = bestMatchD;
            bestMatch.x =i;
            bestMatch.y =j;
            bestMatchD = euclidD;
            numBest = 1;
          }else if(euclidD == bestMatchD){
            /*If it's the same, we must pick one of those "same" values randomly */
            /*I didn't explain that well, but this is a clever little trick*/
            if(Math.random() < 1.0/numBest){
              secondBestMatch = bestMatch;
              secondBestMatchD = bestMatchD;
              bestMatch.x =i;
              bestMatch.y =j;
            }
            numBest++;
          }else{ //euclidD > bestMatchD
            if(euclidD < secondBestMatchD || secondBestMatchD == -1){
              secondBestMatch.x =i;
              secondBestMatch.y =j;
              secondBestMatchD = euclidD;
            }
          }
        }
      }
    }

    return new Point[]{bestMatch, secondBestMatch};
  }

  /**
   * Find the Euclidean distance (squared, since it doesn't change comparison) between different two different weights.
   *
   * I break up the loop into different cases so that it will do less computation if possible.
   *
   * In testing it, I'm not sure this gains much by this.
   *
   * @param testweight
   * @param currentW
   * @return Euclidean distance between two points
   */
  static public float findeuclidD(float[] testweight, float[] currentW, SOM som){
    float euclidD = 0;
    if(som.channelsUsed == null){ //all channels used
      int weightLength = currentW.length;
      if(som.allUsedWeightsSame){
        for(int k = 0; k < weightLength; k++){
					if(!Float.isNaN(currentW[k]) && !Float.isNaN(testweight[k])){
						euclidD+= Math.pow(currentW[k] - testweight[k], 2);
					}
        }
      }else{ //some weights are different
        for(int k = 0; k < weightLength; k++){
					if(!Float.isNaN(currentW[k]) && !Float.isNaN(testweight[k])){
						euclidD+= Math.pow(som.weighting[k]*(currentW[k] - testweight[k]), 2);
					}
        }
      }
    }else{ //only some channels used
      int channelsUsedLength = som.channelsUsed.length;
      if(som.allUsedWeightsSame){
        for(int k = 0; k < channelsUsedLength; k++){
          int index = som.channelsUsed[k];
					if(!Float.isNaN(currentW[index]) && !Float.isNaN(testweight[index])){
						euclidD+= Math.pow(currentW[index] - testweight[index], 2);
					}
        }
      }else{ //some of the used weights are different
        for(int k = 0; k < channelsUsedLength; k++){
          int index = som.channelsUsed[k];
					if(!Float.isNaN(currentW[index]) && !Float.isNaN(testweight[index])){
						euclidD+= Math.pow(som.weighting[index]*(currentW[index] - testweight[index]), 2);
					}
        }
      }
    }
    return euclidD;
  }




  /**
   * Checks if a Point is a valid index for a SOM Node
   * @param p
   * @return True if valid index of a SOM node
   */
  static public boolean isPtValid(Point p, SOM som){
    if(p.x >= 0 && p.x < som.getWidth() &&
        p.y >= 0 && p.y < som.getHeight()){
      return true;
    }
    return false;
  }

  /**
   * Returns a Linked List of the neighbors of the current node at the given distance
   * @param currentpt
   * @param distance
   * @return
   */
  static public LinkedList<Point> neighborsAtDistance(Point currentpt, int distance, SOM som){
    LinkedList<Point> neighbors = new LinkedList<Point>();
    if(distance == 0){
      neighbors.add(currentpt);
      return neighbors;
    }

    if(distance == 1){
      int x = 1; //I need something to put a breakpoint on
    }

    /* If it is a square map */
    if(som.mapType == SOM.SQUARESOM){
      for(int i = currentpt.x - distance; i <= currentpt.x + distance; i++){
        if(i >= 0 && i < som.SOMnodes.length){
          Point pt1 = new Point(i, currentpt.y + distance);
          Point pt2 = new Point(i, currentpt.y - distance);
          //add the points if they are valid points
          if(pt1.y >= 0 && pt1.y < som.SOMnodes[0].length){
            neighbors.add(pt1);
          }
          if(pt2.y >= 0 && pt2.y < som.SOMnodes[0].length){
            neighbors.add(pt2);
          }
        }
      }
      for(int j = currentpt.y-distance+1; j <= currentpt.y+distance-1; j++){
        if(j >= 0 && j < som.SOMnodes[0].length){
          Point pt1 = new Point(currentpt.x + distance, j);
          Point pt2 = new Point(currentpt.x - distance, j);
          //add the points if they are valid points
          if(pt1.x >= 0 && pt1.x < som.SOMnodes.length){
            neighbors.add(pt1);
          }
          if(pt2.x >= 0 && pt2.x < som.SOMnodes.length){
            neighbors.add(pt2);
          }
        }
      }

      //Hexagonal Map
    }else if (som.mapType == SOM.HEXSOM){
      boolean yeven = false;
      if((currentpt.y / 2) * 2 == currentpt.y){
        yeven = true;
      }
      //x, y, dist
      //those in the same row (currentpt.y)
      Point pt1 = new Point(currentpt.x + distance, currentpt.y);
      Point pt2 = new Point(currentpt.x - distance, currentpt.y);
      if(pt1.x >= 0 && pt1.x < som.SOMnodes.length){
        neighbors.add(pt1);
      }
      if(pt2.x >= 0 && pt2.x < som.SOMnodes.length){
        neighbors.add(pt2);
      }
      //in row y, cols x+d, x-d

      //rest of rows
      int k = 2*distance-1;
      int deltay = 1;
      Point pt3;
      Point pt4;
      while(/*k-1*/ k > distance/*distance+1*/){
        //in row y +- deltay
        //different if y was even or odd
        if(yeven){
          pt1 = new Point(currentpt.x + (new Double(Math.ceil(k/2.0))).intValue(),
              currentpt.y + deltay);
          pt2 = new Point(currentpt.x + (new Double(Math.ceil(k/2.0))).intValue(),
              currentpt.y - deltay);
          pt3 = new Point(currentpt.x - (new Double(Math.floor(k/2.0))).intValue(),
              currentpt.y + deltay);
          pt4 = new Point(currentpt.x - (new Double(Math.floor(k/2.0))).intValue(),
              currentpt.y - deltay);
          //rows x+ciel(k/2), x-floor(k/2)
        }else{
          //rows x+floor(k/2), x-ciel(k/2)
          pt1 = new Point(currentpt.x + (new Double(Math.floor(k/2.0))).intValue(),
              currentpt.y + deltay);
          pt2 = new Point(currentpt.x + (new Double(Math.floor(k/2.0))).intValue(),
              currentpt.y - deltay);
          pt3 = new Point(currentpt.x - (new Double(Math.ceil(k/2.0))).intValue(),
              currentpt.y + deltay);
          pt4 = new Point(currentpt.x - (new Double(Math.ceil(k/2.0))).intValue(),
              currentpt.y - deltay);
        }


        if(pt1.x >= 0 && pt1.x < som.SOMnodes.length &&
            pt1.y >= 0 && pt1.y < som.SOMnodes[0].length){
          neighbors.add(pt1);
        }
        if(pt2.x >= 0 && pt2.x < som.SOMnodes.length &&
            pt2.y >= 0 && pt2.y < som.SOMnodes[0].length){
          neighbors.add(pt2);
        }
        if(pt3.x >= 0 && pt3.x < som.SOMnodes.length &&
            pt3.y >= 0 && pt3.y < som.SOMnodes[0].length){
          neighbors.add(pt3);
        }
        if(pt4.x >= 0 && pt4.x < som.SOMnodes.length &&
            pt4.y >= 0 && pt4.y < som.SOMnodes[0].length){
          neighbors.add(pt4);
        }

        k = k-1;
        deltay = deltay + 1;

      }

      //add top/bottom rows
      //in rows y +- deltay,
      if(yeven){
        //    all from x+ceil(k/2) to x-floor(k/2)
        for(int i = currentpt.x - (new Double(Math.floor(k/2.0))).intValue();
            i <= currentpt.x + (new Double(Math.ceil(k/2.0))).intValue(); i++){

          pt1 = new Point(i, currentpt.y + deltay);
          pt2 = new Point(i, currentpt.y - deltay);
          if(pt1.x >= 0 && pt1.x < som.SOMnodes.length &&
              pt1.y >= 0 && pt1.y < som.SOMnodes[0].length){
            neighbors.add(pt1);
          }
          if(pt2.x >= 0 && pt2.x < som.SOMnodes.length &&
              pt2.y >= 0 && pt2.y < som.SOMnodes[0].length){
            neighbors.add(pt2);
          }
        }
      }else{
        //  if(y odd)
        //    all from- x+floor(k/2) to x-ciel(k/2)
        for(int i = currentpt.x - (new Double(Math.ceil(k/2.0))).intValue();
            i <= currentpt.x + (new Double(Math.floor(k/2.0))).intValue(); i++){

          pt1 = new Point(i, currentpt.y + deltay);
          pt2 = new Point(i, currentpt.y - deltay);
          if(pt1.x >= 0 && pt1.x < som.SOMnodes.length &&
              pt1.y >= 0 && pt1.y < som.SOMnodes[0].length){
            neighbors.add(pt1);
          }
          if(pt2.x >= 0 && pt2.x < som.SOMnodes.length &&
              pt2.y >= 0 && pt2.y < som.SOMnodes[0].length){
            neighbors.add(pt2);
          }
        }
      }
    }
    return neighbors;
  }

}
