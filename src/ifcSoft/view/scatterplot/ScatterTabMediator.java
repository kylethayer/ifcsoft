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
package ifcSoft.view.scatterplot;

import ifcSoft.MainAppI;
import ifcSoft.model.DataSetProxy;
import ifcSoft.model.scatterplot.ScatterPlot;
import ifcSoft.view.TabMediator;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This is the mediator between the Histogram object and the Histogram view.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class ScatterTabMediator implements TabMediator {

  ScatterTabI scatterTab;
  DataSetProxy dsp;
  int xDim;
  int yDim;
  ScatterPlot scatterPlot;

  /**
   * The constructor. It needs access to the MainApp so it can draw and such.
   * @param app
   */
  public ScatterTabMediator(MainAppI app){
    scatterTab = app.makeScatterTab(this);
  }

  @Override
  public void display() {
    System.out.println("dimension is "+xDim +", " + yDim);
    scatterTab.displayTab();
  }

  @Override
  public void swapOutTab() {
    scatterTab.swapOutTab();
  }

  @Override
  public String getTabName() {
    return "Scatter Plot";
  }

  @Override
  public float getTabProgress() {
    return 100; //for now I don't thread the scatter progress
  }

  @Override
  public void informNewDsp() {
    scatterTab.informNewDsp();
  }

  /**
   * Tries to close the tab
   * @return True if the tab allows itself to be closed
   */
  @Override
  public boolean closeTab(){
    return true; //As long as I'm not working on anything, I can close
  }

  /**
   *  Returns whether or not the dialog content should be blocked (for reloading tab).
   *  In this case, it never should be blocked.
   * @return false
   */
  @Override
  public boolean isDialogContentBlocked(){
    return false;
  }

  /**
   * Set the data set that the histogram is of.
   * @param dsp
   */
  public void setDataSet(DataSetProxy dsp){
    this.dsp = dsp;
    scatterTab.setDataSet(dsp);
  }

  /**
   * Set which dimension is to be drawn.
   * @param dim
   */
  public void setXDimension(int xDim){
    this.xDim = xDim;
    scatterTab.setXDimension(xDim);
  }

  /**
   * Set which dimension is to be drawn.
   * @param dim
   */
  public void setYDimension(int yDim){
    this.yDim = yDim;
    scatterTab.setYDimension(yDim);
  }

  /**
   * Set which scale type is to be used.
   * @param scaleType
   */
  public void setScaleType(int scaleType){
    scatterTab.setScaleType(scaleType);
  }





  /************  Display Functions *****************/

  /**
   * This makes a Histogram with the number of segments and scale type.
   * @param numPieces
   * @param scaleType
   * @return
   */
  public ScatterPlot calcScatter(int xRes, int yRes, int scaleType){
    System.out.println("making scatterPlot");
    scatterPlot = new ScatterPlot(dsp, xDim, yDim, xRes, yRes, scaleType);
    return scatterPlot;
  }


  /**
   *
   * @param scatterplot
   * @param set - set number or -1 for all
   * @return
   */
  public BufferedImage getScatterImage(ScatterPlot scatterplot, int set){
    BufferedImage scatterImg = new BufferedImage(scatterplot.xRes(), scatterplot.yRes(),BufferedImage.TYPE_INT_RGB);
    for(int i = 0; i < scatterplot.xRes(); i++){
      for(int j = 0; j < scatterplot.yRes(); j++){
        float amt;
        if(set == -1){
          amt = scatterplot.getPntSize(i, j); //the Y axis of the image is flipped
        }else{
          amt = scatterplot.getSetPntSize(i, j, set);
        }
        int clr;
        if(amt == 0){
          clr = Color.HSBtoRGB(1, 0, .05f); // if nothing, make it dark grey
        }else{
          float fraction;
          if(set == -1){
            fraction = amt / scatterplot.getMaxPntSize();
          }else{
            fraction = amt / scatterplot.getMaxPntSize(); //should this be done separately?
          }
          fraction = (float) Math.sqrt(Math.sqrt(fraction)); //push the scale to higher fractions
          clr = Color.HSBtoRGB(.65f*(1-fraction), //if I go all the way to 1 I get red again
          fraction*.75f+.25f,
          fraction*.5f+.5f); //make it dimmer if it's lower
        }

        scatterImg.setRGB(i, scatterplot.yRes() - j - 1, clr);
      }
    }
    return scatterImg;
  }

}
