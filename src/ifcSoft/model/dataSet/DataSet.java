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


/**
 * The DataSet interface gives standard access functions to any type of DataSet,
 * whether it is the raw data or some aggregate of other data, so that any of
 * the display / computation functions can be operated on them without caring
 * what type they are.
 *
 * Every data set potentially has a single "mask" over it's data. The primary
 * reason for this is so that when you remove outliers, you don't lose the
 * data that was removed and so that you don't generate another mask over the
 * data each time you remove outliers (wastes space and adds more indirection).
 * The mask stores which points were removed during which operation so that
 * those can be presented to the user. The standard access to the data set uses
 * the mask if it's there.
 *
 * In order to allow multi-threading, there are options for locking for read or write.
 * The write lock will expire after a default time (or given time), to allow telling the
 * user to cancel whatever other threads are accessing it. A write lock only locks
 * if there are no current read locks. (Note: Not fully implemented yet).
 *
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public abstract class DataSet {

  protected String name;

	protected int[] numValsInDim; //not counting missing numbers
  protected float[] mins; //Probably bad to have it public, but for now it must be
  protected float[] maxes; //so that SummaryData can access it
  protected double[] means;
  protected double[] stddevs;

  DataSetMask myMask = null;

  int numReading = 0;
  boolean writeLock = false;

  LinkedList<DataSet> children = new LinkedList<DataSet>();

  /**
   * The data is divided into segments to try to prevent memory allocation
   * problems as well as making updates change only part of the data at a time.
   * This is a default segment size, if a segment grows to double size, I'll split it.
   */
  public static int SEGSIZE = 50000;


  /**
   * The number of data points in the data set.
   * @return
   */
  public int length(){
    if(myMask == null){
      return UnMaskedLength();
    }
    return myMask.length();
  }

  /**
   * The number of points that have been removed as outliers.
   * @return
   */
  public int getNumRemoved(){
    if(myMask == null){
      return 0;
    }
    return UnMaskedLength() - myMask.length();
  }
  /**
   * The values of the given data point.
   * @param index
   * @return
   */
  public float[] getVals(int index){
    if(myMask == null){
      return getUnMaskedVals(index);
    }
    return myMask.getVals(index);
  }

  public String getPointName(int index){
    if(myMask == null){
      return getUnMaskedPointName(index);
    }
    return myMask.getPointName(index);
  }

  public String getUnMaskedPointName(int index){
    return ""+index;
  }

  public boolean hasPointNames(){
    return false;
  }

	public int getNumValsInDim(int dimension){
		if(myMask == null){
			return numValsInDim[dimension];
		}
		return myMask.getNumValsInDim(dimension);
	}

  /**
   * The maximum in the data set of the given dimension.
   * @param dimension
   * @return
   */
  public float getMax(int dimension){
    if(myMask == null){
      return maxes[dimension];
    }
    return myMask.getMax(dimension);
  }
  /**
   * The minimum in the data set of the given dimension.
   * @param dimension
   * @return
   */
  public float getMin(int dimension){
    if(myMask == null){
      return mins[dimension];
    }
    return myMask.getMin(dimension);
  }
  /**
   * The mean in the data set of the given dimension.
   * @param dimension
   * @return
   */
  public double getMean(int dimension) {
    if(myMask == null){
      return means[dimension];
    }
    return myMask.getMean(dimension);
  }
  /**
   * The size of a standard deviation in the data set of the given dimension.
   * @param dimension
   * @return
   */
  public double getStdDev(int dimension){
    return getStdDevs()[dimension];
  }


  /**
   * The size of a standard deviations in the data set.
   * @return
   */
  public double[] getStdDevs(){
    if(myMask == null){
      //TODO: Fill in std devs if null or empty or whatever
      if(stddevs == null){
        findStandardDevs();
      }
      return stddevs;
    }
    return myMask.getStdDevs();
  }

  private void findStandardDevs() {
    stddevs = new double[getDimensions()];

    for(int i = 0; i < getDimensions(); i++){
      stddevs[i] =0;
    }

    // find the variance (avg of [dist to mean]^2)
    //do this on SegSize at a time, then average those values together, just to reduce rounding errors
    int numSegs = (int) Math.ceil(length() / (double) DataSet.SEGSIZE);
    int segLengths[] = new int[numSegs];
    for(int i = 0; i < numSegs; i++){
      if(i != numSegs-1){
        segLengths[i] = DataSet.SEGSIZE;
      }else{ //last one is remainder
        segLengths[i] = length() -  DataSet.SEGSIZE*(numSegs-1);
      }
    }


    double [][] segVars = new double[numSegs][getDimensions()];
    //0 it out
    for(int i = 0; i < numSegs; i++){
      for(int k = 0; k < getDimensions(); k++){
        segVars[i][k] = 0;
      }
    }
    int sofar = 0;
    for(int i = 0; i < numSegs; i++){
      for(int j = 0; j < segLengths[i]; j++){
        float[] vals = getVals(sofar);
        sofar++;
        for(int k = 0; k < getDimensions(); k++){
          //compute average of the squares (at each step it's the current avg. of pts given)
          double distToAvg = vals[k] - means[k];
          segVars[i][k] = distToAvg*distToAvg / (j+1) + (segVars[i][k]*j)/(j+1);
        }
      }
    }

    //compute actual varience from the segments
    double variance[] = new double[getDimensions()];
    for(int k = 0; k < getDimensions(); k++){
      variance[k] = 0;
    }
    for(int i = 0; i < numSegs; i++){
      for(int k = 0; k < getDimensions(); k++){
        variance[k] += segVars[i][k] * (segLengths[i] / (double) length());
      }
    }

    //find actual standard devs from the variance we computed
    for(int k = 0; k < getDimensions(); k++){
      stddevs[k] = Math.sqrt(variance[k]);
    }
  }

  /**
   * Find the mins, maxes and means.
   */
  protected void findstats() {
    for(int k = 0; k < getDimensions(); k++){
			numValsInDim[k] = 0;
      mins[k] = Float.MAX_VALUE;
      maxes[k] = Float.MIN_VALUE;
      means[k] = 0; //for now we'll use it to keep sums
    }

    for(int i = 0; i < length(); i++){
      for(int k = 0; k < getDimensions(); k++){
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
  


  
  /**
   * Remove points that are farther out than the given number of standard
   * deviations in any dimension.
   * @param stdDevs
   * @return
   */
  public int removeOutliers(double stdDevs){
    System.out.println("Raw Data remove outliers");
    //if no mask, create mask
    if(myMask == null){
      System.out.println("creating mask");
      DataSetMask temp = new DataSetMask(length(), this);
      //do it into a temp just to be safe with the tests
      //in other functions for if myMask is null (shouldn't be a problem, but I'll be safe)
      myMask = temp;
    }
    //remove outliers on new mask
    System.out.println("removing outliers");
    int removed = myMask.removeOutliers(stdDevs);

    if(myMask.length() == UnMaskedLength()){ //if mask is no different than original
      myMask = null; //clear the mask
    }

    if(removed > 0){ //if something was removed
      for(int i = 0; i < children.size(); i++){
        children.get(i).parentPointsRemoved((DataSet)this, myMask.getLastRemoved());
      }
    }
    return removed;
  }
  
  /**
   * Change the name of the data set.
   * @param name
   */
  public void setName(String name){
    this.name = name;
  }

  /**
   * Return the name of the data set.
   * @return
   */
  public String getName(){
    return name;
  }


  /**
   * Lock the data set for reading.
   * //TODO: Pass the object locking it, so when it unlocks it again, I know
   * I'm unlocking properly.
   * @return
   */
  public int readLock(){
    //if I do lazy update, I may have to do the write lock here
    if(writeLock){
      return -1; //for fail
    }
    //getting here means it was not write locked
    numReading++;
    return 1;
  }

  /**
   * Unlock the data set for reading.
   */
  public void readUnlock(){
    numReading--;
  }
  
  /**
   *
   * @return
   */
  public int writeLock(){ // timeout, if too long, it can prompt user to cancel other things happening
    if(numReading != 0){
      return -numReading;
    }
    writeLock = true;
    return 1;
  }
  /**
   *
   */
  public void writeUnlock(){
    writeLock = false;
  }

  /**
   *
   * @param index
   * @return
   */
  public String getPointSetName(int index){
    if(myMask == null){
      return getUnMaskedPointSetName(index);
    }
    return myMask.getPointSetName(index);
  }


  /**
   *
   * @param child
   */
  public void registerChild(DataSet child){ //how I register with a child data Set.
    children.add(child);
  }
  
  /**
   *
   * @param child
   */
  public void unregisterChild(DataSet child){ //how I unregister with a child data Set.
    children.remove(child);
  }

  /**
   *
   * @return
   */
  public LinkedList<DataSet> getChildren(){
    return children;
  }


  
  /***********************************************************************/
  /************************* Abstract Methods ****************************/
  /***********************************************************************/
  /**
   *
   * @return
   */
  public abstract String[] getColLabels();

  /**
   *
   * @param index
   * @return
   */
  public abstract float[] getUnMaskedVals(int index);

  /**
   *
   * @return
   */
  public abstract int UnMaskedLength();

  /**
   *
   * @return
   */
  public abstract int getDimensions();

  /**
   *
   * @param index
   * @return
   */
  public abstract String getUnMaskedPointSetName(int index);


  /**
   *
   * @return
   */
  public abstract LinkedList<DataSet> getParents();
  /**
   *
   * @return
   */
  public abstract LinkedList<String> getRawSetNames();

  public abstract LinkedList<DataSet> getRawSets();

  /**
   *
   * @param dataSet
   * @param lastRemoved
   */
  public abstract void parentPointsRemoved(DataSet dataSet, DataSetMaskRemoved lastRemoved);


  

}
