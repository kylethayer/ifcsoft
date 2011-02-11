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
package ifcSoft.model.histogram;

import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.DataSet;
import java.util.LinkedList;

/**
 * Holds the histogram data (bar limits and sizes etc.).
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class Histogram {

	
	private DataSetProxy dsp;
	private int dimension;
	private int numBars;
	private int[] barTotalSizes;
	private int[][] barSetSizes;
	private double[] lowerLimits; //note the last one is the upper limit of last bar
	private int maxBarSize;
	private int scaleType; //0 is linear, 1 is logarithmic
	private LinkedList<String> rawSetNames;

	/**
	 * Construct a histogram.
	 * @param dsp - the data set to use
	 * @param dimension - the dimension to use
	 * @param numBars - the number of bars to split the data into
	 * @param scaleType - the scale type to use
	 */
	public Histogram(DataSetProxy dsp, int dimension, int numBars, int scaleType){
		this.dsp = dsp;
		this.dimension = dimension;
		this.numBars = numBars;
		this.scaleType = scaleType;
		rawSetNames = dsp.getData().getRawSetNames();
		calculateHistogram();
	}

	/**
	 * Get the name of the histogram (currently the data set name).
	 * @return
	 */
	public String getHistName(){
		return dsp.getDataSetName();
	}

	/**
	 * Get the name of the dimension being used.
	 * @return
	 */
	public String getDimensionName(){
		return dsp.getColNames()[dimension];
	}

	/**
	 * Get the number of bars the data was split into.
	 * @return
	 */
	public int numBars(){
		return numBars;
	}

	/**
	 * Get the size (number of points in) the given bar.
	 * @param barNum
	 * @return
	 */
	public int getBarSize(int barNum){
		return barTotalSizes[barNum];
	}

	/**
	 * Get the size (number of points in) the given bar from the specified subset.
	 * @param setNum
	 * @param barNum
	 * @return
	 */
	public int getSetBarSize(int setNum, int barNum){
		return barSetSizes[setNum][barNum];
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
	public int getMaxBarSize(){
		return maxBarSize;
	}

	/**
	 * Get the lower limit value of the given bar.
	 * @param barNum
	 * @return
	 */
	public double getBarLowerLim(int barNum){
		return lowerLimits[barNum];
	}

	/**
	 * Get the upper limit value of the given bar.
	 * @param barNum
	 * @return
	 */
	public double getBarUpperLim(int barNum){
		return lowerLimits[barNum+1];
	}

	private void calculateHistogram(){
		//Todo: Add this to the job queue
		barTotalSizes = new int[numBars];
		barSetSizes = new int[rawSetNames.size()][numBars];
		lowerLimits = new double[numBars+1];
		DataSet ds = dsp.getData();
		double min = ds.getMin(dimension);
		double max = ds.getMax(dimension);
		if(min == max){
			//Just one bar with everything
			System.out.println("Histogram: Min == Max");
			numBars = 1;
			barTotalSizes = new int[1];
			for(int i = 0; i < rawSetNames.size(); i++){
				barSetSizes[i] = new int[1];
				barSetSizes[i][0] = 1; // ds.
			}

			lowerLimits = new double[2];
			barTotalSizes[0] = ds.length();
			lowerLimits[0] = min;
			lowerLimits[1] = max;
			return;
		}

		if(scaleType == 0){
			calculateHistogramLinear(min, max, ds);
		}else if(scaleType == 1){
			calculateHistogramLogarithmic(min, max, ds);
		}else{
			System.out.println("Invalid scale option: "+ scaleType);
			return;
		}
		findMaxBarSize();

	}

	private void calculateHistogramLinear(double min, double max, DataSet ds){
		double barWidth = (max - min) / numBars;
		System.out.println("numBars:"+numBars+" barWidth:"+barWidth+" min"+min+" max"+max);
		for(int i = 0; i < numBars; i++){
			lowerLimits[i] = min + barWidth*i;
		}
		lowerLimits[numBars] = max; //In case of rounding errors, this makes max exact

		placeDataPointsLinear(barWidth, ds);
		findMaxBarSize();
	}

	private void calculateHistogramLogarithmic(double min, double max, DataSet ds){
		double logBarWidth = 1.0 / numBars; //after log scaling (scale to 1..10, then log10) I get 0 to 1
		//Math.log(max)
		System.out.println("numBars:"+numBars+" barWidth(log):"+logBarWidth+" min"+min+" max"+max);
		for(int i = 0; i < numBars; i++){ //TODO: make log scale accurate
			lowerLimits[i] = min + (max-min)*(Math.pow(10,i*logBarWidth) - 1)/9.0;
		}
		lowerLimits[numBars] = max; //In case of rounding errors, this makes max exact
		lowerLimits[0] = min;
		placeDataPointsLogarithmic(logBarWidth, ds);
		findMaxBarSize();

	}

	private void placeDataPointsLinear(double barWidth, DataSet ds){
		double min = lowerLimits[0];
		double max = lowerLimits[numBars];
		for(int i = 0; i < ds.length(); i++){
			int barNum = (int) ((ds.getVals(i)[dimension] - min) / barWidth);

			if(barNum >= numBars){ //if it placed it out of our range
				if(ds.getVals(i)[dimension] > max){
					System.out.println("point "+i+" truly too large");
				}
				barNum = numBars - 1;
			}
			if(barNum < 0){
				if(ds.getVals(i)[dimension] < min){
					System.out.println("point "+i+" truly too small");
				}
				barNum = 0;
			}

			barTotalSizes[barNum]++;
			String set = ds.getPointSetName(i);
			int setNum = rawSetNames.indexOf(set);
			barSetSizes[setNum][barNum]++;
		}
	}

	private void placeDataPointsLogarithmic(double logBarWidth, DataSet ds){
		double min = lowerLimits[0];
		double max = lowerLimits[numBars];
		for(int i = 0; i < ds.length(); i++){
			int barNum = (int) (
					Math.log10(
						(ds.getVals(i)[dimension] - min)*9.0 / (max - min) + 1
					) 
				/ logBarWidth);
			if(barNum >= numBars){ //if it placed it out of our range
				if(ds.getVals(i)[dimension] > max){
					System.out.println("point "+i+" truly too large");
				}
				barNum = numBars - 1;
			}
			if(barNum < 0){
				if(ds.getVals(i)[dimension] < min){
					System.out.println("point "+i+" truly too small");
				}
				barNum = 0;
			}

			barTotalSizes[barNum]++;
			String set = ds.getPointSetName(i);
			int setNum = rawSetNames.indexOf(set);
			barSetSizes[setNum][barNum]++;
		}
	}

	private void findMaxBarSize(){
		maxBarSize = barTotalSizes[0];
		for(int i = 1; i < numBars; i++){
			if(barTotalSizes[i] > maxBarSize){
				maxBarSize = barTotalSizes[i];
			}
		}
	}

}
