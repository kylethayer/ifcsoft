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

import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.dataSetScalar.DataSetScalar;



/**
 *
 * @author kthayer
 */
public class SOMSettings {

  public static final String UNSCALED= "Unscaled";
  public static final String MINMAXNORM = "Min/Max Normalized";
  public static final String VARNORM = "Variance Normalized";
  public static final String LOGSCALE = "Log Scale";
  public static final String PCACOMP = "Principal Components";
  public static final String PCACOMPDECAY = "Principal Components (decaying comp. weight)";

  public static final String LINEARINIT = "Linear Initialization";
  public static final String RANDOMINIT = "Random Initialization";
  public static final String FILEINIT = "Load SOM file";

  public static final String CLASSICSOM = "Incremental SOM";
  public static final String BATCHSOM = "Batch SOM";

  public static final String USEALLPOINTS = "Use All";
  public static final String HALFMISSING = "At least half the used dimensions present";
  public static final String COMPLETEPOINTS = "Only points with all dimensions";

  public DataSetProxy datasetproxy;
  public DataSetScalar datasetscalar;
  public String scaleType;
  public float[] weights;

  public String initType;

  public String SOMType;

  public String allowMissingPointsType;

  public int width;
  public int height;

  //classic options
  public int classicIterations;
  public int classicMaxNeighborhood;
  public int classicMinNeighborhood;

  //batchSOM options
  public int batchSteps;
  public int batchMaxNeighborhood;
  public int batchMinNeighborhood;
  public int batchPointsPerNode;



  public SOMSettings(){
    datasetproxy = null;
    datasetscalar = null;
    scaleType = VARNORM;
    weights = null;

    initType = LINEARINIT;

    SOMType = BATCHSOM;

    width = 15;
    height = 15;


    classicIterations = 5000;
    classicMaxNeighborhood = -1; //default is max dimension / 2
    classicMinNeighborhood = 2;

    batchSteps = 10;
    batchMaxNeighborhood = -1; //default is max dimension / 4
    batchMinNeighborhood = 2;
    batchPointsPerNode = 100;

    allowMissingPointsType = HALFMISSING;
  }
}
