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
package ifcSoft.model.dataSet.dataSetScalar;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import ifcSoft.model.dataSet.DataSet;
import java.util.LinkedList;

/**
 *
 * @author kthayer
 */
public class PCANormalized implements DataSetScalar{
  final int MAXSAMPLEFORPCA = 5000;
  DataSetScalar dataset;
  float[] weighting;
  int dimensions;
  Matrix PCATransform;

  float mins[];
  float maxes[];
  double means[];
  double stddevs[];

  String colLabels[];

  public PCANormalized(DataSet dataset, float[] weighting){
    this.dataset = new VarianceNormalized(dataset);

    System.out.println("dataset:");
    for(int i = 0; i < dataset.getDimensions(); i++){
      System.out.println("\tmean " +i +": "+ dataset.getMean(i));
      System.out.println("\tstddev " +i +": "+ dataset.getStdDev(i));
    }

    System.out.println("VarNorm:");
    for(int i = 0; i < this.dataset.getDimensions(); i++){
      System.out.println("\tmean " +i +": "+ this.dataset.getMean(i));
      System.out.println("\tstddev " +i +": "+ this.dataset.getStdDev(i));
    }


    System.out.print("pt 1 pre:\n\t");
    float[]pt1 = dataset.getVals(0);
    for(int i = 0; i < pt1.length; i++){
      System.out.print(pt1[i] + ", ");
    }
    System.out.println();

    System.out.print("pt 1 after:\n\t");
    float[]pt2 = this.dataset.getPoint(0);
    for(int i = 0; i < pt2.length; i++){
      System.out.print(pt2[i] + ", ");
    }
    System.out.println();




    this.weighting = weighting;

    if(weighting == null || weighting.length == 0){
      this.weighting = new float[dataset.getDimensions()];
      for(int i = 0; i < this.weighting.length; i++){
        this.weighting[i] = 1;
      }
    }
    //reduce dimensions only to those not weighted to 0
    dimensions = 0;
    for(int i = 0; i < this.weighting.length; i++){
      if(this.weighting[i] > 0){
        dimensions++;
      }
    }
    System.out.println("Dimensions: "+ dimensions);

    //Do single value decomp on a reasonable subset of the data
    Matrix datamatrix = new Matrix(getSampleDataMatrix());
    System.out.print("Point 1:");
    for(int i = 0; i < datamatrix.getRowDimension(); i++){
      System.out.print(datamatrix.get(i, 0) +", ");
    }
    System.out.println();

    SingularValueDecomposition svd = new SingularValueDecomposition(datamatrix);

    //PCA = U (trans) * X
    Matrix U = svd.getU();
    System.out.println("U col = "+ U.getColumnDimension() + " row = " + U.getRowDimension());
    for(int i = 0; i < dimensions; i++){
      for(int j = 0; j < dimensions; j++){
        int val = (int) (U.get(i, j)*10);
        if(val == 0){
          System.out.print(" -   ");
        }else if (val < 0){
          System.out.print( val +"   ");
        }else{
          System.out.print(" "+ val +"   ");
        }
        
      }
      System.out.println();
    }

    //U thinks it is one column longer than it is (from some bug), this will fix it
    PCATransform = new Matrix(U.getArray(),dimensions,dimensions );

    colLabels = new String[dimensions + dataset.getDimensions()];
    for(int i = 0; i < colLabels.length; i++){
      colLabels[i] = "PC"+ (i+1);
    }
    for(int i = 0; i < dataset.getDimensions(); i++){
      colLabels[i + dimensions] = dataset.getColLabels()[i];
    }

    findstats();
    
  }

  //Make a matrix of all the data points, no more than 1000 points
  private double[][] getSampleDataMatrix(){
    double[][] retmatrix;
    if(dataset.length() <= MAXSAMPLEFORPCA){ //do all points
      //for(int i = 0; i < data.length(); i++){
      retmatrix = new double[dimensions][dataset.length()];
      for(int j = 0; j < retmatrix[0].length; j++){
        float[] point = dataset.getPoint(j);
        int ptindex = 0;
        for(int i = 0; i < dataset.getDimensions(); i++){
          if(weighting[i] > 0){
            retmatrix[ptindex][j] = point[i]*weighting[i];
            ptindex++;
          }
        }
      }
    }else{
      retmatrix = new double[dimensions][MAXSAMPLEFORPCA];
      for(int j = 0; j < retmatrix[0].length; j++){
        float[] point = dataset.getPoint((int) (Math.random() * dataset.length()));
        int ptindex = 0;
        for(int i = 0; i < dataset.getDimensions(); i++){
          if(weighting[i] > 0){
            retmatrix[ptindex][j] = point[i]*weighting[i];
            ptindex++;
          }
        }
      }
    }

    return retmatrix;
  }


  private void findstats(){
    mins = new float[getDimensions()];
    maxes = new float[getDimensions()];
    means = new double[getDimensions()];

    for(int k = 0; k < getDimensions(); k++){
      mins[k] = Float.MAX_VALUE;
      maxes[k] = Float.MIN_VALUE;
      means[k] = 0; //for now we'll use it to keep sums
    }

    for(int i = 0; i < length(); i++){
      for(int k = 0; k < getDimensions(); k++){
        //compute averages(at each step it's the current avg. of pts given)
        double weight = getPoint(i)[k];
        means[k] = weight / (i+1) + (means[k]*i)/(i+1);
        if(weight < mins[k]){
          mins[k] = (float) weight;
        }
        if(weight > maxes[k]){
          maxes[k] = (float)weight;
        }
      }
    }
  }
  

  @Override
  public String getName() {
    return dataset.getName();
  }

  @Override
  public int length() {
    return dataset.length();
  }

  @Override
  public int getDimensions() {
    return dimensions + dataset.getDimensions(); //Todo: I may do less dims
  }

  @Override
  public int getUnscaledDimensions() {
    return dataset.getDimensions(); //Todo: I may do less dims
  }

  @Override
  public float getMax(int dim) {
    return maxes[dim];
  }

  @Override
  public float getMin(int dim) {
    return mins[dim];
  }

  @Override
  public double getStdDev(int dim) {
    if(stddevs == null){
      findStandardDevs();
    }
    return stddevs[dim];
  }

  @Override
  public double getMean(int dim) {
    return means[dim];
  }

  @Override
  public float[] getPoint(int index) {
    return scalePoint(dataset.getUnscaledPoint(index));
  }

  @Override
  public float[] getUnscaledPoint(int index) {
    return dataset.getUnscaledPoint(index);
  }

  @Override
  public float[] scalePoint(float[] weights) {
    float firstScaled[] = dataset.scalePoint(weights);
    float newWeights[] = new float[dimensions + weights.length];
    for(int i = 0; i < dimensions; i++){ //for each Principal Component
      double pc = 0;
      int ptindex = 0;
      for(int k = 0; k < getUnscaledDimensions(); k++){ //contribution of each dimension to the PC
        if(weighting[k] > 0){
          pc += PCATransform.get(ptindex, i) * firstScaled[k] *weighting[k];
          ptindex++;
        }
      }
      newWeights[i]= (float) pc;
    }
    //put the raw values after that (they should be weighted to 0 in the SOM.
    //Note, the maps the SOM keeps for each of these channels is theoretically
    //the same as the translation of all the PCs, though rounding could make some difference.
    for(int i = 0; i < weights.length; i++){
      newWeights[i+dimensions] = weights[i];
    }

    return newWeights;
  }

  @Override
  public float[] unscalePoint(float[] weights) {
    return weights; //if it is trying to display the value along the PC, then this
            //is correct, but if you are trying to recover the original value, this wont work
  }

  @Override
  public String[] getColLabels() {
    return colLabels;
  }

  @Override
  public LinkedList<String> getRawSetNames() {
    return dataset.getRawSetNames();
  }

  @Override
  public String getPointSetName(int index) {
    return dataset.getPointSetName(index);
  }

  @Override
  public DataSet getDataSet(){
    return dataset.getDataSet();
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
        float[] vals = getPoint(sofar);
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
}
