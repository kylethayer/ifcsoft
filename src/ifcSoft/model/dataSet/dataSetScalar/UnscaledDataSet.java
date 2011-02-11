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
public class UnscaledDataSet implements DataSetScalar{
  private DataSet dataset;

  public UnscaledDataSet(DataSet dataset){
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
  public int getUnscaledDimensions() {
    return getDimensions();
  }

  @Override
  public float getMax(int dim) {
    return dataset.getMax(dim);
  }

  @Override
  public float getMin(int dim) {
    return dataset.getMin(dim);
  }

  @Override
  public double getStdDev(int dim) {
    return dataset.getStdDev(dim);
  }

  @Override
  public double getMean(int dim) {
    return dataset.getMean(dim);
  }

  @Override
  public float[] getPoint(int index) {
    return dataset.getVals(index);
  }

  @Override
  public float[] getUnscaledPoint(int index) {
    return dataset.getVals(index);
  }

  @Override
  public float[] scalePoint(float[] weights) {
    return weights;
  }

  @Override
  public float[] unscalePoint(float[] weights) {
    return weights;
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

  @Override
  public DataSet getDataSet(){
    return dataset;
  }
}
