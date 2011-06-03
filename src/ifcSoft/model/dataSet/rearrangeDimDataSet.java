/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ifcSoft.model.dataSet;

import java.util.LinkedList;

/**
 *
 * @author kthayer
 */
public class rearrangeDimDataSet extends DataSet {

  DataSet parentSet;

  String[] newColNames;

  int[] colIndeces;

  public rearrangeDimDataSet(DataSet parentSet, String[] newColNames, int[] colIndeces){
    this.parentSet = parentSet;
    this.newColNames = newColNames;
    this.colIndeces = colIndeces;

    name = parentSet.name;

    mins = new float[colIndeces.length];
    maxes= new float[colIndeces.length];
    means= new double[colIndeces.length];

    for(int i = 0; i < colIndeces.length; i++){
      mins[i] = parentSet.mins[colIndeces[i]];
      maxes[i] = parentSet.maxes[colIndeces[i]];
      means[i] = parentSet.means[colIndeces[i]];
    }


    System.out.print("newNames:");
    for(int i = 0; i < newColNames.length; i++){
      System.out.print(newColNames[i] + ", ");
    }
    System.out.println();

    System.out.print("from old:");
    for(int i = 0; i < colIndeces.length; i++){
      System.out.print(parentSet.getColLabels()[colIndeces[i]] + ", ");
    }
    System.out.println();

  }

  @Override
  public String[] getColLabels() {
    return newColNames;
  }

  @Override
  public float[] getUnMaskedVals(int index) {
    //get val then rearrange it
    float[] parentvals = parentSet.getVals(index);
    float[] newvals = new float[getDimensions()];
    for(int i = 0; i < colIndeces.length; i++){
      newvals[i] = parentvals[colIndeces[i]];
    }
    return newvals;
  }

  @Override
  public int UnMaskedLength() {
    return parentSet.length();
  }

  @Override
  public int getDimensions() {
    return newColNames.length;
  }

  @Override
  public String getUnMaskedPointName(int index){
    return parentSet.getPointName(index);
  }


  @Override
  public boolean hasPointNames(){
    return parentSet.hasPointNames();
  }

  @Override
  public String getUnMaskedPointSetName(int index) {
    return parentSet.getPointSetName(index);
  }

  @Override
  public LinkedList<DataSet> getParents() {
    return parentSet.getParents();
  }

  @Override
  public LinkedList<String> getRawSetNames() {
    return parentSet.getRawSetNames();
  }

  @Override
  public LinkedList<DataSet> getRawSets() {
    return parentSet.getRawSets();
  }

  @Override
  public void parentPointsRemoved(DataSet dataSet, DataSetMaskRemoved lastRemoved) {
    parentSet.parentPointsRemoved(dataSet, lastRemoved);
  }


}
