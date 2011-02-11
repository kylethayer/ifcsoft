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
   * Construct a scatter plot.
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
   * Get the name of the scatter plot (currently the data set name).
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
    xlowerLimits = new double[xRes+1];
    ylowerLimits = new double[yRes+1];
    DataSet ds = dsp.getData();
    double minX = ds.getMin(xDim);
    double maxX = ds.getMax(xDim);
    double minY = ds.getMin(yDim);
    double maxY = ds.getMax(yDim);
    /*if(minX == maxX){
      //Just one bar with everything
      System.out.println("Histogram: xMin == xMax");
      xRes = 1;
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
    }*/

    if(scaleType == 0){
      calculateScatterLinear(minX, maxX, minY, maxY, ds);
    }else if(scaleType == 1){
      calculateHistogramLogarithmic(minX, maxX, minY, maxY, ds);
    }else{
      System.out.println("Invalid scale option: "+ scaleType);
      return;
    }
    findMaxBarSize();

  }


  private void calculateScatterLinear(double minX, double maxX, double minY, double maxY, DataSet ds){
    double xPntWidth = (maxX - minX) / xRes;

    for(int i = 0; i < xRes; i++){
      xlowerLimits[i] = minX + xPntWidth*i;
    }
    xlowerLimits[xRes] = maxX; //In case of rounding errors, this makes max exact

    double yPntWidth = (maxY - minY) / yRes;
    for(int i = 0; i < yRes; i++){
      ylowerLimits[i] = minY + yPntWidth*i;
    }
    ylowerLimits[yRes] = maxY; //In case of rounding errors, this makes max exact


    placeDataPointsLinear(xPntWidth, yPntWidth, ds);
    findMaxBarSize();
  }

  private void calculateHistogramLogarithmic(double minX, double maxX, double minY, double maxY, DataSet ds){
    /*double logBarWidth = 1.0 / numBars; //after log scaling (scale to 1..10, then log10) I get 0 to 1
    //Math.log(max)
    System.out.println("numBars:"+numBars+" barWidth(log):"+logBarWidth+" min"+min+" max"+max);
    for(int i = 0; i < numBars; i++){ //TODO: make log scale accurate
      lowerLimits[i] = min + (max-min)*(Math.pow(10,i*logBarWidth) - 1)/9.0;
    }
    lowerLimits[numBars] = max; //In case of rounding errors, this makes max exact
    lowerLimits[0] = min;
    placeDataPointsLogarithmic(logBarWidth, ds);
    findMaxBarSize();*/

  }

  private void placeDataPointsLinear(double xPntWidth, double yPntWidth, DataSet ds){
    double minX = xlowerLimits[0];
    double maxX = xlowerLimits[xRes];

    double xWidth = xlowerLimits[1] - xlowerLimits[0];

    double minY = ylowerLimits[0];
    double maxY = ylowerLimits[yRes];

    double yWidth = ylowerLimits[1] - ylowerLimits[0];

    for(int i = 0; i < ds.length(); i++){

      int xNum = (int) ((ds.getVals(i)[xDim] - minX) / xWidth);
      int yNum = (int) ((ds.getVals(i)[yDim] - minY) / yWidth);

      if(xNum >= xRes){ //if it placed it out of our range on x
        if(ds.getVals(i)[xDim] > maxX){
          System.out.println("point "+i+" truly too large");
        }
        xNum = xRes - 1;
      }
      if(xNum < 0){
        if(ds.getVals(i)[xDim] < minX){
          System.out.println("point "+i+" truly too small");
        }
        xNum = 0;
      }
      if(yNum >= yRes){ //if it placed it out of our range on x
        if(ds.getVals(i)[yDim] > maxY){
          System.out.println("point "+i+" truly too large");
        }
        yNum = yRes - 1;
      }
      if(yNum < 0){
        if(ds.getVals(i)[yDim] < minY){
          System.out.println("point "+i+" truly too small");
        }
        yNum = 0;
      }

      pntTotalSizes[xNum][yNum]++;
      String set = ds.getPointSetName(i);
      int setNum = rawSetNames.indexOf(set);
      pntSetSizes[setNum][xNum][yNum]++;
    }
  }

  private void placeDataPointsLogarithmic(double logBarWidth, DataSet ds){
    /*double min = lowerLimits[0];
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
    }*/
  }

  private void findMaxBarSize(){
    maxPntSize = pntTotalSizes[0][0];
    for(int i = 1; i < xRes; i++){
      for(int j = 0; j < yRes; j++){
        if(pntTotalSizes[i][j] > maxPntSize){
          maxPntSize = pntTotalSizes[i][j];
        }
      }
    }
  }
}
