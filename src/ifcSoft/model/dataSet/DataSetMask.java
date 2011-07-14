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
package ifcSoft.model.dataSet;

import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class DataSetMask{

  /* The way the data is stored is that the first "length" elements in "members"
   * are the currently active elements of the original Data set. Everything 
   * past this point is junk data. For each set of removed data points, there
   * is a DataSetMaskRemoved object that contains those points and the reason
   * for their removal.
   *
   * For purpose of updating children I should just remake the members array each time
   * to keep everything in order
   */
  private LinkedList<int[]> members;
  private int length;
  private LinkedList<DataSetMaskRemoved> removedList;
  private DataSet parentSet;

  private int[] numValsInDim;
  private float[] mins;
  private float[] maxes;
  private double[] means;
  private double[] stddevs;
  //mins, maxes, stdDev

  DataSetMask(int length, DataSet parentSet){
    this.length = length;
    this.parentSet = parentSet;
    //initialize all present and none removed.
    members = new LinkedList<int[]>();
    removedList = new LinkedList<DataSetMaskRemoved>();

    //initialize the mask so it points to all children
    int sofar = 0;

    while(sofar < length){
      int segSize = DataSet.SEGSIZE;
      if(length - sofar < segSize){
        segSize = length - sofar;
      }
      int[] seg = new int[segSize];
      for(int i = 0; i < segSize; i++){
          seg[i] = i + sofar;        
      }
      members.addLast(seg);
      sofar += segSize;
    }


    //get stats from parent
    numValsInDim = new int[parentSet.getDimensions()];
    mins = new float[parentSet.getDimensions()];
    maxes = new float[parentSet.getDimensions()];
    means = new double[parentSet.getDimensions()];
    stddevs = new double[parentSet.getDimensions()];

    for(int i = 0; i < parentSet.getDimensions(); i++){
      numValsInDim[i] = parentSet.numValsInDim[i];
      mins[i] = parentSet.getMin(i);
      maxes[i] = parentSet.getMax(i);
      means[i] = parentSet.getMean(i);
      stddevs [i] = parentSet.getStdDev(i);
    }
    
  }

  /**
   *
   * @param index
   * @return
   */
  public String getPointSetName(int index){
    int parentI = parentIndex(index);
    if(parentI >=0){
      return parentSet.getUnMaskedPointSetName(parentI);
    }else{
      return null;
    }
  }

  /**
   *
   * @return
   */
  public int length() {
    return length;
  }

  int getNumValsInDim(int dimension){
    return numValsInDim[dimension];
  }

  float getMin(int dimension) {
    return mins[dimension];
  }

  float getMax(int dimension) {
    return maxes[dimension];
  }

  double getMean(int dimension) {
    return means[dimension];
  }

  double [] getStdDevs() {
    if(stddevs == null){
      findStandardDevs();
    }
    return stddevs;
  }

  float[] getVals(int index){
    int parentI = parentIndex(index);
    if(parentI >=0){
      return parentSet.getUnMaskedVals(parentI);
    }else{
      return null;
    }
  }

  String getPointName(int index){
    int parentI = parentIndex(index);
    if(parentI >=0){
      return parentSet.getUnMaskedPointName(parentI);
    }else{
      return null;
    }
  }

  private int parentIndex(int index){
    //find the right segment
    int i = 0;
    int sofar = 0;
    while(sofar + members.get(i).length <= index){
      sofar += members.get(i).length;
      i++;
      if(i >= members.size()){
        return -1;
        //throw new Exception("DSMask, getWeights("+index+") out of bounds");
      }
    }
    //i is now pointing at the correct segment and sofar tells us how many points were in previous segments
    return members.get(i)[index - sofar];
  }

  /**
   *
   * @param stdDevs
   * @return
   */
  public int removeOutliers(double stdDevs){
    //this.removedList
    DataSetMaskRemoved removed = new DataSetMaskRemoved();
    removed.removalType = "Removed Standard Devs: "+stdDevs;

    int origlength = length;

    //see what the sets are before findStandardDevs
  

    findStandardDevs();

  

    for(int i = 0; i < members.size(); i++){ /*remove the actual outliers*/
      removeSegOutliers(i, (float)stdDevs, removed);
    }


    if(origlength == length){ //if nothing was removed we don't need to do the rest
      return 0;
    }
    

    findstats(); //need to re-find mins, maxes and means
    stddevs = null; //std deviations are outdated
    System.out.println("new stats:");
    for(int k = 0; k < parentSet.getDimensions(); k++){
      System.out.println("  min "+k + "= " + mins[k]);
      System.out.println("  maxes "+k + "= " + maxes[k]);
      System.out.println("  mean "+k + "= " + means[k]);
    }
    removedList.add(removed);
    return origlength - length; 
  }

  private void removeSegOutliers(int segnum, float tolerance, DataSetMaskRemoved removed){
    int[] segData = members.get(segnum);
    //now we need to look for outliers (over "tolerance" std devs)
    int[] tempdata = new int[segData.length];
     //our new matrix will probably be a little too big, so we'll have to copy it again
    int newLength = segData.length;

    int newDataI = 0;
    for(int i = 0; i < segData.length; i++){
      double maxstddev = 0;
      double avgstddev = 0;
      int parentIndex = segData[i];
      float[] vals =parentSet.getUnMaskedVals(parentIndex);
      for(int k = 0; k < parentSet.getDimensions(); k++){
        double distToAvg = vals[k] - means[k];
        double pointStdDevs =  Math.abs(distToAvg / stddevs[k]);
        avgstddev += pointStdDevs / parentSet.getDimensions();
        if(pointStdDevs > maxstddev){
          maxstddev = pointStdDevs;
        }
      }

      //if it's less than "tolerance" standard devs, add it, otherwise skip it
      if(maxstddev <= tolerance && avgstddev <= tolerance){
        tempdata[newDataI] = parentIndex;
        newDataI++;
      }else{
        //add to the new removal set
        removed.addPt(parentIndex);
        newLength--; //our new segment length is one less since this point isn't in our set
        length--; //also we need to update the overall length count
        //numRemoved++;
      }
    }
    //now I need to copy this to a new array of size newLength
    int[] newdata = new int[newLength];
    System.arraycopy(tempdata, 0, newdata, 0, newLength);
    members.set(segnum, newdata); //replace it with our new one
  }


  private void findstats() {
    //parentSet.find
    for(int k = 0; k < parentSet.getDimensions(); k++){
      numValsInDim[k] = 0;
      mins[k] = Float.MAX_VALUE;
      maxes[k] = Float.MIN_VALUE;
      means[k] = 0; 
    }

    for(int i = 0; i < length; i++){
      for(int k = 0; k < parentSet.getDimensions(); k++){
        //compute averages(at each step it's the current avg. of pts given)
        double weight = getVals(i)[k];
        if(!Double.isNaN(weight)){ //if it is a missing value, ignore it
          means[k] = weight / (numValsInDim[k]+1) + (means[k]*numValsInDim[k])/(numValsInDim[k]+1);
          if(weight < mins[k]){
            mins[k] = (float) weight;
          }
          if(weight > maxes[k]){
            maxes[k] = (float)weight;
          }
          numValsInDim[k]++; //the dimension has one more valid value
        }
      }
    }
  }

  private void findStandardDevs() {
    stddevs = new double[parentSet.getDimensions()];

    double variance[] = new double[parentSet.getDimensions()];
    for(int k = 0; k < parentSet.getDimensions(); k++){
      variance[k] = 0;
    }

    for(int k=0; k < parentSet.getDimensions(); k++){
      //in order to save time, we'll take data from no more than DataSet.SEGSIZE points
      if(getNumValsInDim(k) <= DataSet.SEGSIZE){ //do them all
        int pointsSoFar = 0;
        for(int i = 0; i < length(); i++){
          //compute average of the squares (at each step it's the current avg. of pts given)
          float[] vals = getVals(i);
          if(!Float.isNaN(vals[k])){
            double distToAvg = vals[k] - means[k];
            variance[k] = distToAvg*distToAvg / (pointsSoFar+1) + (variance[k]*pointsSoFar)/(pointsSoFar+1);
            pointsSoFar++;
          }
        }
      }else{ //randomly pick DataSet.SEGSIZE
        int pointsSoFar = 0;
        Random r = new Random();
        while(pointsSoFar <  DataSet.SEGSIZE){
          //compute average of the squares (at each step it's the current avg. of pts given)
          float[] vals = getVals(r.nextInt(length()));
          if(!Float.isNaN(vals[k])){
            double distToAvg = vals[k] - means[k];
            variance[k] = distToAvg*distToAvg / (pointsSoFar+1) + (variance[k]*pointsSoFar)/(pointsSoFar+1);
            pointsSoFar++;
          }
        }
      }
    }

    //find actual standard devs from the variance we computed
    for(int k = 0; k < parentSet.getDimensions(); k++){
      stddevs[k] = Math.sqrt(variance[k]);
    }

  }

  DataSetMaskRemoved getLastRemoved() {
    return removedList.getLast();
  }

  /*DataSetMaskRemoved parentPointsRemoved(DataSetMaskRemoved lastRemoved) {
    //we need to go through all my points and remove them as necessary


    boolean [] removedMask = new boolean[parentSet.UnMaskedLength() + lastRemoved.length()]; //the length of the parent before the removal
    //initialize all to true (as in still present)
    for(int i = 0; i < removedMask.length; i++){
      removedMask[i] = true;
    }

    //set all removed points as false
    for(int i = 0; i < lastRemoved.length(); i++){
      removedMask[lastRemoved.getPt(i)] = false;
    }

    // I don't think I need this: int sofar = 0; //to keep track of offsets in the segments
    DataSetMaskRemoved myRemoved = new DataSetMaskRemoved();
    myRemoved.removalType = DataSetMaskRemoved.PARENTSETREMOVED;
    for(int i = 0; i < members.size(); i++ ){
      int [] segMembers = members.get(i);
      //now go through my list of points and remove those that were removed
      int[] newMembers = new int[segMembers.length];
      int newMembsI = 0; //index into the new members array

      for(int k = 0; k < segMembers.length; k++){
        int index = segMembers[k];
        if(removedMask[index] == true){ //if the points still valid, put in newMembers
          newMembers[newMembsI] = index;
          newMembsI++;
        }else{ //that point was removed, add to the removed set
          myRemoved.addPt(index);
        }
      }

      //now I need to copy this over to a new array of the correct length (not newMembsI is the correct length)
      segMembers = null; //This should also allow garbage collection of the old members array
      //System.gc();?
      segMembers = new int[newMembsI];
      System.arraycopy(newMembers, 0, segMembers, 0, newMembsI);
      members.set(i, segMembers);
    }


    return myRemoved;

  }*/


}
