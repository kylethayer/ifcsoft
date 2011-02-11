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

import ifcSoft.model.dataSet.DataSet;
import java.util.LinkedList;

/**
 *
 * @author kthayer
 */
public class MinMaxNormalized implements DataSetScalar {

  private DataSet dataset;
  

  public MinMaxNormalized(DataSet dataset){
    this.dataset = dataset;
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
    return dataset.getDimensions();
  }

  @Override
  public float getMax(int dim) {
    return 1;
  }

  @Override
  public float getMin(int dim) {
    return 0;
  }

  @Override
  public double getStdDev(int dim) {
    //TODO: is this valid?
    return scaleDim(dataset.getStdDev(dim), dim);
  }

  @Override
  public double getMean(int dim) {
    return scaleDim(dataset.getMean(dim), dim);
  }

  @Override
  public float[] getPoint(int index) {
    return scalePoint(dataset.getVals(index));
  }

  @Override
  public float[] getUnscaledPoint(int index) {
    return dataset.getVals(index);
  }

  @Override
  public int getUnscaledDimensions() {
    return getDimensions();
  }

  @Override
  public float[] scalePoint(float[] weights) {
    float [] scaledpt = new float[weights.length];
    for(int i = 0; i < weights.length; i++){
      scaledpt[i] = (float) scaleDim(weights[i], i);
    }
    return scaledpt;
  }

  @Override
  public float[] unscalePoint(float[] weights) {
    float [] scaledpt = new float[weights.length];
    for(int i = 0; i < weights.length; i++){
      scaledpt[i] = (float) unscaleDim(weights[i], i);
    }
    return scaledpt;
  }

  @Override
  public String[] getColLabels() {
    return dataset.getColLabels();
  }

  @Override
  public LinkedList<String> getRawSetNames() {
    return dataset.getRawSetNames();
  }

  @Override
  public String getPointSetName(int index) {
    return dataset.getPointSetName(index);
  }

  private double scaleDim(double val, int dim){
    if(dataset.getMax(dim) == dataset.getMin(dim)){
      return 0;
    }
    return (val - dataset.getMin(dim)) / (dataset.getMax(dim) - dataset.getMin(dim));
  }

  private double unscaleDim(double val, int dim){
    if(dataset.getMax(dim) == dataset.getMin(dim)){
      return dataset.getMax(dim);
    }
    return val * (dataset.getMax(dim) - dataset.getMin(dim)) + dataset.getMin(dim) ;
  }

  @Override
  public DataSet getDataSet(){
    return dataset;
  }

}
