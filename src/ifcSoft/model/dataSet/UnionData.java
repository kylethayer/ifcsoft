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

import ifcSoft.model.DataSetProxy;
import java.util.Arrays;

import java.util.LinkedList;


/**
 *  This will simply be a Union of several sets
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class UnionData extends DataSet {
  LinkedList<DataSetProxy> parentSets;

  private String colLabels[];
  private int dimensions;
  private int length;


  public UnionData(DataSetProxy[] datasets) throws Exception{
    LinkedList<DataSetProxy> ll = new LinkedList<DataSetProxy>();
    ll.addAll(Arrays.asList(datasets));
    combineSets(ll);
  }

  /**
   *
   * @param dataSets
   * @throws Exception
   */
  public UnionData(LinkedList<DataSetProxy> dataSets) throws Exception{
    combineSets(dataSets);
  }

  private void combineSets(LinkedList<DataSetProxy> dataSets) throws Exception{
    if(dataSets.size() < 1){
      throw new Exception("Error: attempted union of "+dataSets.size()+ "sets");
    }
    this.parentSets = dataSets;

    name = "Union: ";
    boolean firstLoop = true;
    length = 0;

    for(int i = 0; i < parentSets.size(); i++){
      DataSet set = parentSets.get(i).getData();
      set.registerChild((DataSet) this);
      length += set.length();
      if(firstLoop){
        name += set.getName();
        dimensions = set.getDimensions();
        //get column labels from the first set?
        colLabels = new String[dimensions];
        for(int j = 0; j < dimensions; j++){
          colLabels[j] = "" + set.getColLabels()[j]; //this should copy contents of string instead of ptr
        }
        firstLoop = false;
      }else{
        name += " + " + set.getName();
        if(set.getDimensions() != dimensions){
          throw new Exception ("Error: attempted union of sets with different number of dimmensions");
        }
        //compare dimensions?
      }
    }

    numValsInDim = new int[dimensions];
    mins = new float[dimensions];
    maxes = new float[dimensions];
    means = new double[dimensions];

    //just find min min, max max and do a weighted sum for mean
    findstats();


  }


  /**
   *
   * @return
   */
  @Override
  public String[] getColLabels() {
    return colLabels;
  }

  /**
   *
   * @return
   */
  @Override
  public int getDimensions() {
    return dimensions;
  }

  /**
   *
   * @return
   */
  @Override
  public int UnMaskedLength(){
    return length;
  }


  /**
   *
   * @param index
   * @return
   */
  @Override
  public float[] getUnMaskedVals(int index) {
    if(index >= length){
      return null; //bad input
    }
    //find the right Data Set
    int i = 0;
    int sofar = 0;
    while(sofar + parentSets.get(i).getData().length() <= index){
      sofar += parentSets.get(i).getData().length();
      i++;
    }
    //i is now pointing at the correct segment and sofar tells us how many points were in previous segments
    return parentSets.get(i).getData().getVals(index - sofar);
  }


  @Override
  public String getUnMaskedPointName(int index){
    if(index >= length){
      return null; //bad input
    }
    //find the right Data Set
    int i = 0;
    int sofar = 0;
    while(sofar + parentSets.get(i).getData().length() <= index){
      sofar += parentSets.get(i).getData().length();
      i++;
    }
    //i is now pointing at the correct segment and sofar tells us how many points were in previous segments
    return parentSets.get(i).getData().getPointName(index - sofar);
  }

  @Override
  public boolean hasPointNames(){
    boolean anyHaveNames = false;
    for(int i = 0; i < parentSets.size(); i++){
      if(parentSets.get(i).getData().hasPointNames()){
        anyHaveNames = true;
      }
    }
    return anyHaveNames;
  }

  /**
   *
   * @param index
   * @return
   */
  @Override
  public String getUnMaskedPointSetName(int index) {
    if(index >= length){
      return null; //bad input
    }
    //find the right Data Set
    int i = 0;
    int sofar = 0;
    while(sofar + parentSets.get(i).getData().length() <= index){
      sofar += parentSets.get(i).getData().length();
      i++;
    }
    //i is now pointing at the correct segment and sofar tells us how many points were in previous segments
    return parentSets.get(i).getData().getPointSetName(index - sofar);
  }



  /**
   *
   * @return
   */
  @Override
  public LinkedList<DataSet> getParents() {
    LinkedList<DataSet> temp = new LinkedList<DataSet>();
    for(int i = 0; i < parentSets.size(); i++){
      temp.add(parentSets.get(i).getData());
    }
    return temp;
  }

  /**
   *
   * @return
   */
  @Override
  public LinkedList<String> getRawSetNames() {
    LinkedList<String> ret = new LinkedList<String>();
    for(int i = 0; i < parentSets.size(); i++){
      LinkedList<String> temp = parentSets.get(i).getData().getRawSetNames();
      for(int j = 0; j < temp.size(); j++){
        String tempstr = temp.get(j);
        if(!ret.contains(tempstr)){ //make sure we don't put the same name in twice
          ret.add(tempstr);
        }
      }
    }
    return ret;
  }


  @Override
  public LinkedList<DataSet> getRawSets() {
    LinkedList<DataSet> ret = new LinkedList<DataSet>();
    for(int i = 0; i < parentSets.size(); i++){
      LinkedList<DataSet> temp = parentSets.get(i).getData().getRawSets();
      for(int j = 0; j < temp.size(); j++){
        DataSet tempds = temp.get(j);
        if(!ret.contains(tempds)){ //make sure we don't put the same name in twice
          ret.add(tempds);
        }
      }
    }
    return ret;
  }

  @Override
  protected void findstats() {
    for(int k = 0; k < getDimensions(); k++){
      numValsInDim[k] = 0;
      mins[k] = Float.MAX_VALUE;
      maxes[k] = Float.MIN_VALUE;
      means[k] = 0; //for now we'll use it to keep sums
    }

    //find numValsInDim of the final set
    for(int i = 0; i < parentSets.size(); i++){
      DataSet currentset = parentSets.get(i).getData();
      for(int k = 0; k < getDimensions(); k++){
        numValsInDim[k] += currentset.getNumValsInDim(k);
      }
    }


    for(int i = 0; i < parentSets.size(); i++){
      DataSet currentset = parentSets.get(i).getData();
      for(int k = 0; k < getDimensions(); k++){
        if(currentset.getNumValsInDim(k) > 0){
          means[k]+= (currentset.getNumValsInDim(k)/(double) numValsInDim[k])* currentset.means[k];
          if(currentset.mins[k] < mins[k]){
            mins[k] = currentset.mins[k];
          }
          if(currentset.maxes[k] > maxes[k]){
            maxes[k] = currentset.maxes[k];
          }
        }
      }
    }
  }

  /**
   *
   * @param dataSet
   * @param lastRemoved
   */
  @Override
  public void parentPointsRemoved(DataSet dataSet, DataSetMaskRemoved lastRemoved) {
    //find where that parent set was (by index), then all I really need to do is
    //transform the "lastRemoved" set and pass it on
    /*int sofar = 0;
    for(int i = 0; i < parentSets.size(); i++){
      if(parentSets.get(i) != dataSet){
        sofar += parentSets.get(i).getDataSize();
      }else{
        break; // we break when we get the right set, sofar is # of pts before that set
      }
    }

    DataSetMaskRemoved myRemoved = new DataSetMaskRemoved();
    myRemoved.removalType = DataSetMaskRemoved.PARENTSETREMOVED;
    for(int i = 0; i < lastRemoved.length(); i++){
      myRemoved.addPt(i+sofar);
    }

    length -= lastRemoved.length();

    findstats(); //I need to find my stats again

    DataSetMaskRemoved toPassOn = myRemoved;
    //Inform Mask if it exists
    if(myMask != null){
      toPassOn = myMask.parentPointsRemoved(myRemoved);
    }

    //inform children
    for(int i = 0; i < children.size(); i++){
      children.get(i).parentPointsRemoved((DataSet)this, toPassOn);
    }

    //throw new UnsupportedOperationException("Not supported yet.");*/
  }




}
