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
package ifcSoft.model;

import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.dataSet.RawData;
import org.puremvc.java.interfaces.IProxy;
import org.puremvc.java.patterns.proxy.Proxy;

/**
 * This is the pureMVC proxy for the data set class.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class DataSetProxy extends Proxy implements IProxy {
  
  /**
   * The pureMVC name.
   */
  public static String NAME = "DataSetProxy";

  String filePath;

  private boolean isRemovingOutliers = false;
  private int lastOultiersRemoved = 0;
  
  
  /**
   * pureMVC constructor.
   */
  public DataSetProxy(){
    super(NAME, null);
  }
  

  /**
   * Set this to be the data set belonging to the proxy.
   * @param newDataSet
   */
  public void setDataSet(DataSet newDataSet) {
    this.setData(newDataSet);
    
  }
  
  /**
   * Load a file into this data set (note: you must have already set the filepath).
   */
  public void loadDataFile(){
    if(filePath == null){
      System.out.println("DSP tried to load file, but no filepath set");
      return;
    }
    try{
      //setData(new RawData(filePath));
      ((RawData)getData()).loadFile();

    }catch(Exception ex){
      facade.sendNotification(ifcSoft.ApplicationFacade.EXCEPTIONALERT, ex, null);
    }
    
  }

  /**
   * Return how many data points have been loaded so far in the file.
   * @return
   */
  public int getFileProgress(){
    if(getData() == null){
      return 0;
    }else{
      return ((RawData)getData()).getProgress();
    }
  }

  /**
   * Return the name of the file.
   * @return
   */
  public String getFileName(){
    if(filePath == null){
      return "";
    }
    String name;
    //get the name of the file from the filename
    int lastind1 = filePath.lastIndexOf('/'); //either method of directory division
    int lastind2 = filePath.lastIndexOf('\\');
    if(lastind1 >= 0){
      if(lastind2 > lastind1){ //if "\" happened last
        name = filePath.substring(lastind2+1);
      }else{
        name = filePath.substring(lastind1+1);
      }

    }else{
      if(lastind2 >=0){
        name = filePath.substring(lastind2+1);
      }else{ //neither slash was contained in filename
        name = filePath;
      }
    }
    return name;
  }

  /**
   * Set the file to be loaded.
   * @param filePath
   */
  public void setFile(String filePath){
    setData(new RawData(filePath));
    this.filePath = filePath;
  }
  /**
   * Load a file from an ".iflo" file (currently not used).
   * @param filename
   */
  public void loadIFlowFile(String filename) {

    try {
      FileIO.loadIFlowFile(this, filename);
    } catch (Exception ex) {
      facade.sendNotification(ifcSoft.ApplicationFacade.EXCEPTIONALERT, ex, null);
    }
  }

  /*public void saveIFlowFile(String filename) {
    if(getData().som == null || getData().som.getProgress() != 100){
      String msg = "SOM not in a state to be saved.";
      facade.sendNotification(ifcSoft.ApplicationFacade.STRINGALERT, msg, null);
      return;
    }

    try {
      FileIO.saveIFlowFile(this, filename);

    } catch (IOException ex) {
      facade.sendNotification(ifcSoft.ApplicationFacade.EXCEPTIONALERT, ex, null);
    }
  }*/

  /**
   * Mark this data set as currently removing outliers.
   */
  public void setRemoveOutliers(){
    isRemovingOutliers = true;
  }

  /**
   * Returns whether or not the data set is removing outliers.
   * @return
   */
  public boolean isRemovingOutliers(){
    return isRemovingOutliers;
  }

  /**
   * Returns the progress of the removal outliers progress (currently only returns 0).
   * @return
   */
  public float getRemoveOutlierProgress(){
    return 0;
  }

  /**
   * Remove outliers from the data set.
   * @param stdDevs
   */
  public void removeOutliers(double stdDevs) {
    int originalSize = getDataSize();
    lastOultiersRemoved = getData().removeOutliers(stdDevs);
    String msg = lastOultiersRemoved+ " of " +originalSize + " cells removed as outliers";
    facade.sendNotification(ifcSoft.ApplicationFacade.STRINGALERT, msg, null);
    isRemovingOutliers = false;
  }

  /**
   * Return how many outliers were removed in the last "remove outliers" call.
   * @return
   */
  public int getLastOutliersRemovied(){
    return lastOultiersRemoved;
  }
  
  /**
   * Return how many points are in the data set.
   * @return
   */
  public int getDataSize(){
    return getData().length();
  }

  /**
   * Return the number of dimensions in the data set.
   * @return
   */
  public int getDimensions() {
    return getData().getDimensions();
  }

  
  /**
   * Return the name of the data set.
   * @return
   */
  public String getDataSetName() {
    return getData().getName();
  }

  /**
   * Set the name of the data set.
   * @param name
   */
  public void setDataSetName(String name) {
    getData().setName(name);
  }

  /**
   * Return the filename of the date set.
   * @return
   */
  public String getDataSetFileName() {
    if(getData().getClass() == RawData.class ){
      return ((RawData) getData()).getFileName();
    }else{
      //throw exception?
      return "Error, wrong type of data set";
    }
  }

  
  
  /**
   * Return the names of the dimensions.
   * @return
   */
  public String[] getColNames(){
    return getData().getColLabels();
  }


  /**
   * pureMVC getData function.
   * @return
   */
  @Override
  public DataSet getData(){
    return (DataSet) super.getData();
  }







  
}