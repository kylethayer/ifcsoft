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
package ifcSoft.model.scatterplot;

import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.DataSet;
import java.util.LinkedList;

/**
 *
 * @author kthayer
 */
public class ScatterPlot {
	private DataSetProxy dsp;
	private int xDim;
	private int yDim;
	private int xRes;
	private int yRes;
	private int[][] pntTotalSizes;
	private int[][][] pntSetSizes;
	private double[] xlowerLimits; //note the last one is the upper limit of last bar
	private double[] ylowerLimits; //note the last one is the upper limit of last bar
	private int maxPntSize;

	private int scaleType; //0 is linear, 1 is logarithmic
	private LinkedList<String> rawSetNames;

	/**
	 * Construct a ScatterPlot.
	 * @param dsp - the data set to use
	 * @param dimension - the dimension to use
	 * @param numBars - the number of bars to split the data into
	 * @param scaleType - the scale type to use
	 */
	public ScatterPlot(DataSetProxy dsp, int xDim, int yDim, int xRes, int yRes, int scaleType){
		this.dsp = dsp;
		this.xDim = xDim;
		this.yDim = yDim;
		this.xRes = xRes;
		this.yRes = yRes;
		this.scaleType = scaleType;
		rawSetNames = dsp.getData().getRawSetNames();
		calculateScatterplot();
	}

	/**
	 * Get the name of the scatter plot(currently the data set name).
	 * @return
	 */
	public String getScatterName(){
		return dsp.getDataSetName();
	}

	/**
	 * Get the name of the X dimension being used.
	 * @return
	 */
	public String getXDimensionName(){
		return dsp.getColNames()[xDim];
	}

	/**
	 * Get the name of the Y dimension being used.
	 * @return
	 */
	public String getYDimensionName(){
		return dsp.getColNames()[yDim];
	}

	/**
	 * Get the number of bars the data was split into.
	 * @return
	 */
	public int xRes(){
		return xRes;
	}

	public int yRes(){
		return yRes;
	}

	/**
	 * Get the size (number of points in) the given bar.
	 * @param barNum
	 * @return
	 */
	public int getPntSize(int x, int y){
		return pntTotalSizes[x][y];
	}

	/**
	 * Get the size (number of points in) the given bar from the specified subset.
	 * @param setNum
	 * @param barNum
	 * @return
	 */
	public int getSetPntSize(int x, int y, int setNum){
		return pntSetSizes[setNum][x][y];
	}

	/**
	 * Get the names of the subsets that comprise the main data set.
	 * @return
	 */
	public LinkedList<String> getSetNames(){
		return rawSetNames;
	}

	/**
	 * Get the maximum size of a bar.
	 * @return
	 */
	public int getMaxPntSize(){
		return maxPntSize;
	}

	/**
	 * Get the lower limit value of the given x pixel coordinate.
	 * @param barNum
	 * @return
	 */
	public double getXLowerLim(int x){
		return xlowerLimits[x];
	}

	/**
	 * Get the upper limit value of the given x pixel coordinate.
	 * @param barNum
	 * @return
	 */
	public double getXUpperLim(int x){
		return xlowerLimits[x+1];
	}

		/**
	 * Get the lower limit value of the y pixel coordinate.
	 * @param barNum
	 * @return
	 */
	public double getYLowerLim(int y){
		return ylowerLimits[y];
	}

	/**
	 * Get the upper limit value of the given y pixel coordinate.
	 * @param barNum
	 * @return
	 */
	public double getYUpperLim(int y){
		return xlowerLimits[y+1];
	}

	private void calculateScatterplot(){
		//Todo: Add this to the job queue
		pntTotalSizes = new int[xRes][yRes];
		pntSetSizes = new int[rawSetNames.size()][xRes][yRes];
		xlowerLimits = ne
