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
 * All dataSets must have one of these to be used in the SOM
 * @author kthayer
 */
public interface DataSetScalar {
  String getName();
  int length();
  int getDimensions();
  int getUnscaledDimensions(); //Since PCA changes # of dimensions

  float getMax(int dim);
  float getMin(int dim);
  double getStdDev(int dim);
  double getMean(int dim);
  
  float[] getPoint(int index);
  float[] getUnscaledPoint(int index);

  float[] scalePoint(float[] weights);
  float[] unscalePoint(float[] weights);

  String[] getColLabels();

  LinkedList<String> getRawSetNames();

  String getPointSetName(int index);

  DataSet getDataSet();

}
