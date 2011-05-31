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
package ifcSoft.view.som;

import java.awt.Point;
import java.awt.image.BufferedImage;

import ifcSoft.ApplicationFacade;
import ifcSoft.MainAppI;
import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.dataSet.SubsetData;
import ifcSoft.model.dataSet.dataSetScalar.DataSetScalar;
import ifcSoft.model.dataSet.dataSetScalar.LogScaleNormalized;
import ifcSoft.model.dataSet.summaryData.SummaryData;
import ifcSoft.model.dataSet.summaryData.SummaryDataPoint;
import ifcSoft.model.som.SOMHelperFns;
import ifcSoft.model.som.SOMProxy;
import ifcSoft.model.som.SOMSettings;
import ifcSoft.view.MainMediator;
import ifcSoft.view.Tab;
import ifcSoft.view.TabMediator;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javafx.scene.input.MouseEvent;
import javax.imageio.ImageIO;

import org.puremvc.java.interfaces.IMediator;
import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.mediator.Mediator;

/**
 * This class handles the communication between the view component (SOMvc) and the
 * rest of the program.
 * TODO: The class is large, there must be some way to break it up
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMMediator extends Mediator implements IMediator, TabMediator {

  //TODO: Find some way of deleting (unregistering) old mediators when I overwrite them


  /**
   * The name for pureMVC
   */
  public static String NAME = "SOM Mediator";

  
  //statics from others
  /**
   * Cluster by Edges of the Edge UMatrix
   */
  public static final int ECLUSTER = SOMProxy.ECLUSTER; //edge cluster
  /**
   * Cluster by multiple Edges of the Edge UMatrix
   */
  public static final int MECLUSTER = SOMProxy.MECLUSTER; //multi-edge cluster
  /**
   * Cluster by the UMatrix
   */
  public static final int UCLUSTER = SOMProxy.UCLUSTER; //UMap cluster
  
  /**
   * The SOMProxy that is associated with this view.
   */
  public SOMProxy SOMp;
  private DataSetProxy dsp;
  private DataSetScalar dss;
  private MainMediator mainMed;
  private SOMvcI somVC;

  private String key; //every Mediator has its own unique key

  //State constants
  private static final int EMPTYSTATE = 0;
  private static final int CALCULATING = 1;
  private static final int FINDINGDENSITIES = 2;
  private static final int DONEDENSE = 3;
  private static final int DONENODENSE = 4;
  private int currentState = EMPTYSTATE;//Current state (for restoring the tab)

  private boolean isTabOpen = false;

  private Point cellMouseOver = new Point(-1,-1); // the SOM node the mouse is hovering over
  private Point Clusterpts[] = new Point[0]; // the points selected to build the cluster from
  boolean[][] lastCluster;
  boolean wasLastClusterEmpty = true;

  public SaveClusterSettings lastSaveClustSettings = null;

  double clusterDimAvgs[];

  private float denseProgress = 0;


  private SummaryData sumData = null;
  int clustNum = 0;



  /*********************************************/
  /*    PureMVC Mediator Functions       */
  /*********************************************/
  
  /**
   * Make a new mediator, it needs references to the MainApp and it's SOMvc.
   * 
   * @param app - The mainApp
   * @param somVC - The SOM View Component
   */
  public SOMMediator(MainAppI app, SOMvcI somVC){
    super(NAME, app);
    mainMed = app.getMainMediator();
    key = mainMed.generateMediatorKey();
    this.somVC = somVC;
  }

  /**
   * Assigns a SOMProxy to the SOMMediator and the SOMMediator
   * then passes its key to the SOMProxy.
   * @param SOMp
   */
  public void setSOMprox(SOMProxy SOMp){
    this.SOMp = SOMp;
    SOMp.setMediatorKey(key);
  }

  /**
   * The pureMVC Notifications that SOMMediator listens for.
   * @return
   */
  @Override
  public String[] listNotificationInterests(){
    String [] temp = new String[3];
    temp[0] = ApplicationFacade.SOMPROGRESS;
    temp[1] = ApplicationFacade.STARTEDSOM;
    temp[2] = ApplicationFacade.RETURNEDSOM;
    return temp;
  }

  /**
   * Handle a pureMVC notification.
   * @param note
   */
  @Override
  public void handleNotification(INotification note){
    if(note.getType() == key){ //if the key is the same as that of this mediator
      if(note.getName().contentEquals(ApplicationFacade.STARTEDSOM)){
        currentState = CALCULATING;
        System.out.println(NAME+" "+ key+" : Started SOM");
        if(isTabOpen){
          somVC.SOMstarted();
        }
      }else if(note.getName().contentEquals(ApplicationFacade.RETURNEDSOM)){
        currentState = FINDINGDENSITIES;
        SOMp = (SOMProxy) note.getBody();
        if(isTabOpen){
          somVC.dispSOM();
        }
      }
    }
  }

  /*********************************************/
  /*      Tab Mediator Functions       */
  /*********************************************/
  
  /**
   * Displays the tab in whatever state it is.
   */
  @Override
  public void display() {
    isTabOpen = true;
    //display whatever needs to be displayed, depending on currentState
    switch(currentState){
      case EMPTYSTATE: //nothing to display
        break;
      case CALCULATING:
        somVC.SOMstarted();
        break;
      case FINDINGDENSITIES:
        somVC.dispSOM();
        //set timeline to check on density progress
        break;
      case DONEDENSE:
        somVC.dispSOM();
        break;
      case DONENODENSE:
        somVC.dispSOM();
        break; //break is not needed, but it makes it more symmetric
    }
  }

  /**
   * Save anything that needs to be saved before the tab is switched out.
   */
  @Override
  public void swapOutTab(){
    isTabOpen = false;
    somVC.swapOut();
    //TODO: Stop timelines?
  }
  /**
   * Return the name of the tab.
   * @return
   */
  @Override
  public String getTabName() {
    return "SOM"; //TODO include set name?
  }

  /**
   * Return the progress of the tab.
   * @return
   */
  @Override
  public float getTabProgress() {
    
    return denseProgress;
  }

  /**
   * Tries to close the tab.
   * @return True if the tab allows itself to be closed
   */
  @Override
  public boolean closeTab(){
    //must close jobs (Note: these do the same thing for now, but future revisions may change)
    SOMp.cancelSOM();
    SOMp.cancelJobs();
    return true; 
  }

  /**
   *  Returns whether or not the dialog content should be blocked (for reloading tab).
   *  It should be blocked if it is computing a new SOM.
   * @return false
   */
  @Override
  public boolean isDialogContentBlocked(){
    if(currentState == CALCULATING){
      return true;
    }else{
      return false;
    }
  }

  

  /*********************************************/
  /*     SOMMediator specific functions    */
  /********************************************/
  
  /**
   * This makes a new SOM with the given input properties.
   *
   * @param width - the width of the SOM in # of nodes
   * @param height - the width of the SOM in # of nodes
   * @param dsp - the data set for the SOM
   * @param iterations - the number of iterations to be done
   * @param weights - weights of
   * @param isLog
   */
  public void doSOM(int width, int height, int iterations, float weights[], DataSetScalar dsc, DataSetProxy dsp){
    this.dsp = dsp;
    this.dss = dsc;
    if(dsp.getData() == null || dsp.getDataSize() == 0){
      String msg = "Must select data set";
      facade.sendNotification(ifcSoft.ApplicationFacade.STRINGALERT, msg, null);
    }else{
      SOMp.createSOM(width, height, iterations, weights, dsc);
      mainMed.getCurrentTab().setTabMediator(this);
      mainMed.getCurrentTab().changeMode(Tab.SOMMODE);
      mainMed.getCurrentTab().getTabMediator().display(); // TODO: that's rather round-about, but I
                                //should move this section of code up the hierarchy
    }
  }

  public void doSOM(SOMSettings somSettings){
    this.dsp = somSettings.datasetproxy;
    this.dss = somSettings.datasetscalar;
    if(dsp.getData() == null || dsp.getDataSize() == 0){
      String msg = "Must select data set";
      facade.sendNotification(ifcSoft.ApplicationFacade.STRINGALERT, msg, null);
    }else{
      //SOMp.createSOM(somSettings.width, somSettings.height, iterations, weights, dsc);
      SOMp.createSOM(somSettings);
      mainMed.getCurrentTab().setTabMediator(this);
      mainMed.getCurrentTab().changeMode(Tab.SOMMODE);
      mainMed.getCurrentTab().getTabMediator().display(); // TODO: that's rather round-about, but I
                                //should move this section of code up the hierarchy
    }
  }

  /**
   * Cancels the computing SOM.
   */
  public void cancelSOM(){
    SOMp.cancelSOM();
  }

  /**
   * Make sure the SOM finds densities for these other data sets.
   * 
   * @param selectedDataSets
   */
  public void findDensities(boolean selectedDataSets[]){
    for(int i = 0; i < selectedDataSets.length; i++){
      if(selectedDataSets[i]){
        SOMp.findDensity(i, mainMed.dataSets);
      }
    }
    somVC.continueUpdatingDensities();
  }

  /**
   * Display the cluster options dialog box.
   */
  public void clustOpt(){
    somVC.clustOpt();
  }


  /**
   * Load an SOM from a file. (currently not used)
   * @param selectedFile
   */
  public void loadSOM(String selectedFile){
    sendNotification(ApplicationFacade.LOADSOM, selectedFile, null);
  }

  /**
   * Save an SOM to a file. (currently disabled)
   * @param selectedFile
   */
  public void saveSOM(String selectedFile){
    throw new UnsupportedOperationException("Not yet implemented");
    /*Object msgData[] = new Object[2];
    msgData[0] = (Object) dsp;
    msgData[1] = (Object) selectedFile;
    sendNotification(ApplicationFacade.SAVESOM, msgData, null);*/
  }


  /**
   * Pause the SOM jobs.
   */
  public void pauseJobs(){
    if(SOMp != null){
      SOMp.pauseJobs();
    }
  }

  /**
   * Restart any paused SOM jobs (for this SOM).
   */
  public void unPauseJobs(){
    if(SOMp != null){
      SOMp.restartPausedJobs();
    }
  }

  /**
   * Cancel any SOM jobs (for this SOM).
   */
  public void cancel(){
    if(SOMp != null){
      SOMp.cancelJobs();
    }
  }
  
  /**
   * Check the progress of the SOM calculation.
   */
  public void checkProgress(){
    float p = SOMp.getProgress();
    if(p == 100){ //if it is done, get the SOMs
      currentState = FINDINGDENSITIES;
      if(isTabOpen){
        somVC.dispSOM();
      }
    }else{ //set the current progress
      int prog = (int)  (p*100);
      somVC.setProgress(prog);
    }
  }
  
  

  /**
   * Return the name of the set used for the SOM.
   * @return - the set name
   */
  public String getSetName(){
    return SOMp.getDataSetName();
  }

  /**
   * Get all the SOM map tiles.
   * @return all the map tiles.
   */
  public SOMholder getSOMMaps(){
    float[][][] rawMaps = SOMp.getSOMs();
    SOMholder somholder = new SOMholder();
    somholder.setName = SOMp.getDataSetName();

    //add Edge and UEdge
    if(SOMp.getSOMType() == SOMProxy.SQUARESOM){
      throw new UnsupportedOperationException("Square SOM Not yet implemented");
    }else if (SOMp.getSOMType() == SOMProxy.HEXSOM){
      //add U-Matrices
      BufferedImage tempimg = SOMImageFns.makeSOMhexUImg(SOMp.getSOMUEmap());
      //somholder.addImg(tempimg,"Edge UMatrix",0,1);
      somholder.euMap = tempimg;
      somholder.euMapMin = 0;
      somholder.euMapMax = 1;
      tempimg =  SOMImageFns.makeSOMhexImg(SOMp.getSOMUmap());
      //somholder.addImg(tempimg,"UMatrix",0,1);
      somholder.uMap = tempimg;
      somholder.uMapMin = 0;
      somholder.uMapMax = 1;

      somholder.imgwidth = tempimg.getWidth();
      somholder.imgheight = tempimg.getHeight();

      //add dimension maps
      for(int i = 0; i < rawMaps.length; i++){
        tempimg = SOMImageFns.makeSOMhexImg(rawMaps[i]);
        somholder.addDimImg(tempimg, SOMp.getDimNames()[i], SOMp.getMin(i), SOMp.getMax(i));
      }

      LinkedList<String> rawSetNames = SOMp.getRawSetNames();
      somholder.rawSetNames = new String[rawSetNames.size()];
      for(int i = 0; i < rawSetNames.size(); i++){
        somholder.rawSetNames[i] = rawSetNames.get(i);
      }
      somholder.blankmap = blankMap(rawMaps[0]);

    }else{
      throw new UnsupportedOperationException("SOM type: "+SOMp.getSOMType()+"Not yet implemented");
    }

    return somholder;
  }

  /**
   * Returns the number of points that have been placed on the SOM.
   * @return
   */
  public int getDenstiyMapPlaced(){
    return SOMp.densityMapPlaced();
  }

  public double getAvfPlacedError(){
    return SOMp.getData().getPlacedPointsError() / SOMp.densityMapPlaced();
  }

  /**
   * Returns the number of points placed on a combo-density map
   * 
   * @param selectedSubSets - subsets of the current set that are selected
   * @param selectedDataSets - data sets that are selected
   * @return
   */
  public int getComboDenstiyMapPlaced(boolean selectedSubSets[], boolean selectedDataSets[]){
    //go through the two boolean lists summing up their placed elements
    int totalPlaced = 0;
    for(int i = 0; i < selectedSubSets.length; i++){
      if(selectedSubSets[i]){
        totalPlaced+= getSubDenstiyMapPlaced(i);
      }
    }
    
    for(int i = 0; i < selectedDataSets.length; i++){
      if(selectedDataSets[i]){
        totalPlaced+= SOMp.dataSetDensityMapPlaced(i);
      }
    }

    return totalPlaced;
  }

  /**
   * Get number of data points placed from data sets that weren't from the main SOM set.
   * @return
   */
  public int getOtherDataSetsPlaced(){
    return SOMp.getOtherDataSetsPlaced();
  }

  /**
   * Returns the length of the Data Set used for the SOM.
   * @return
   */
  public int getDataLength(){
    return SOMp.dataLength();
  }

  /**
   * Returns the length of the Data Set used for the SOM.
   * @return
   */
  public int getOtherDataSetLength(){
    return SOMp.getOtherDataSetLength();
  }


  /**
   * Set the progress of the density maps.
   * @param percent - (0-1) if computing, 100 if done
   */
  public void setDenseProgress(float percent){
    denseProgress = percent;
  }

  /**
   * Get the overall Density Map image.
   * @return
   */
  public BufferedImage getDenstiyMapImg(){
    int[][] denseMap = SOMp.getDensityMap();
    if(denseMap == null){
      return null;
    }

    float[][] newDenseMap = normalizeDenseMap(denseMap);
    return SOMImageFns.makeSOMhexImg(newDenseMap);
  }

  public BufferedImage getDenseMapsDeltaImg(){
    float[][] denseMapsDelta = SOMp.getDenseMapsDelta();
    if(denseMapsDelta == null){
      return null;
    }

    float[][] newDenseMap = normalizeDenseMap(denseMapsDelta);
    return SOMImageFns.makeSOMhexImg(newDenseMap);
  }

  /**
   * Get the number of data points placed in a particular subset.
   * @param subsetNum
   * @return
   */
  public int getSubDenstiyMapPlaced(int subsetNum){
    return  SOMp.SubsetsDensityMapsPlaced()[subsetNum];
  }

  /**
   * Get the Density map of a particular Subset.
   * @param subsetNum
   * @return
   */
  public BufferedImage getSubDenstiyMapImg(int subsetNum){
    int[][] denseMap = SOMp.getDensityMap();
    if(denseMap == null){
      return null;
    }
    float[][] newSetDenseMap = normalizeDenseMap(SOMp.getSubsetDenseMap(subsetNum));
    return SOMImageFns.makeSOMhexImg(newSetDenseMap);
  }

  /**
   * Get the Density Map of a combination of subsets and other sets.
   * @param selectedSubSets
   * @param selectedDataSets
   * @return
   */
  public BufferedImage getComboDenstiyMapImg(boolean selectedSubSets[], boolean selectedDataSets[]){
    int numMaps = 0;
    for(int i = 0; i < selectedSubSets.length; i++){
      if(selectedSubSets[i]){
        numMaps++;
      }
    }
    for(int i = 0; i < selectedDataSets.length; i++){
      if(selectedDataSets[i]){
        numMaps++;
      }
    }
    if(numMaps == 0){
      return null;
    }

    //get all the maps I need to combine
    int allDenseMaps[][][] = new int[numMaps][][];
    int index = 0;
    for(int i = 0; i < selectedSubSets.length; i++){
      if(selectedSubSets[i]){
        allDenseMaps[index] = SOMp.getSubsetDenseMap(i);
        if(allDenseMaps[index] == null){
          System.out.println("SOMp.getSubsetDenseMap("+i+") was null");
        }
        index++;
      }
    }
    for(int i = 0; i < selectedDataSets.length; i++){
      if(selectedDataSets[i]){
        allDenseMaps[index] = SOMp.getDataSetDenseMap(i);
        if(allDenseMaps[index] == null){
          System.out.println("SOMp.getDataSetDenseMap("+i+") was null");
        }
        index++;
      }
    }

    //Make a denseMap of all those combined
    int[][] denseMap = new int[SOMp.getSOMwidth()][SOMp.getSOMheight()];
    for(int i = 0; i < denseMap.length; i++){
      for(int j = 0; j < denseMap[0].length; j++){
        int nodeTotalPlaced = 0;
        for(int k = 0; k < allDenseMaps.length; k++){
          nodeTotalPlaced += allDenseMaps[k][i][j];
        }
        denseMap[i][j] = nodeTotalPlaced;
      }
    }

    float[][] newSetDenseMap = normalizeDenseMap(denseMap);

    return SOMImageFns.makeSOMhexImg(newSetDenseMap);

  }

  /**
   * Given a density map (raw values), this normalizes it on
   * a log scale so it is between 0 and 1.
   * @param denseMap
   * @return - normalized map.
   */
  private float[][] normalizeDenseMap(int[][] denseMap){

    //now we want to do the log normalizing of it
    //first find the max membership
    int totalMembers =0;
    float minMember = Float.MAX_VALUE;
    float maxMember = 0;
    for(int i =0; i < denseMap.length; i++){
      for(int j=0; j < denseMap[0].length; j++){
        totalMembers += denseMap[i][j];
        if(denseMap[i][j] > maxMember)
          maxMember = denseMap[i][j];
        if(denseMap[i][j] < minMember)
          minMember = denseMap[i][j];
      }
    }

    float[][] newMap = new float[denseMap.length][denseMap[0].length];
    //now I need to log normalize the denseMap into newMap so max Membership is 1
    for(int i = 0; i < denseMap.length; i++){
      for(int j = 0; j < denseMap[0].length; j++){
        double scaled = (denseMap[i][j] - minMember) / (maxMember - minMember);
        newMap[i][j] = (float) Math.log10((9*scaled+1)); //9*scales+1 gives range 1-10
        //newMap[i][j] = (float) (denseMap[i][j]*1.0 / maxMember);
      }
    }

    return newMap;
  }

  /**
   * Given a density map (raw values), this normalizes it on
   * a log scale so it is between 0 and 1.
   * @param denseMap - float map
   * @return - normalized map.
   */
  private float[][] normalizeDenseMap(float[][] denseMap){

    //now we want to do the log normalizing of it
    //first find the max membership
    int totalMembers =0;
    float minMember = Float.MAX_VALUE;
    float maxMember = 0;
    for(int i =0; i < denseMap.length; i++){
      for(int j=0; j < denseMap[0].length; j++){
        totalMembers += denseMap[i][j];
        if(denseMap[i][j] > maxMember)
          maxMember = denseMap[i][j];
        if(denseMap[i][j] < minMember)
          minMember = denseMap[i][j];
      }
    }

    float[][] newMap = new float[denseMap.length][denseMap[0].length];
    //now I need to log normalize the denseMap into newMap so max Membership is 1
    for(int i = 0; i < denseMap.length; i++){
      for(int j = 0; j < denseMap[0].length; j++){
        double scaled = (denseMap[i][j] - minMember) / (maxMember - minMember);
        newMap[i][j] = (float) Math.log10((9*scaled+1)); //9*scales+1 gives range 1-10
        //newMap[i][j] = (float) (denseMap[i][j]*1.0 / maxMember);
      }
    }

    return newMap;
  }

  /**
   * Set the point the mouse is over (for displaying stats on that cell).
   * @param x
   * @param y
   * @return Returns true if this is a new cell, false if same cell
   */
  public boolean setPointMouseOver(int x, int y){
    Point p = SOMImageFns.getcell(new Point(x,y));
    if(p.x >= 0 && p.x < SOMp.getSOMwidth() && p.y >= 0 && p.y < SOMp.getSOMheight()){
      //this isn't used, I should rewrite the if statement
    }else{
      p = new Point(-1,-1);
    }
    if(p.x == cellMouseOver.x && p.y == cellMouseOver.y){
      return false;
    }else{
      cellMouseOver = p;
      return true;
    }
  }

  /**
   * Set the mouse as over no node.
   * @return - False if it was already not over a node
   */
  public boolean setPointMouseOff(){
    if(-1 == cellMouseOver.x && -1 == cellMouseOver.y){
      return false;
    }else{
      cellMouseOver = new Point(-1,-1);
      return true;
    }
  }

  /**
   * Get the dimension values for the node the mouse is over.
   * @return - an array of the dimension values, or null if over no point.
   */
  public float[] getDimCellVals(){
    if(cellMouseOver.x == -1){//if over no point
      return null;
    }else{
      return SOMp.getCellVals(cellMouseOver);
    }
  }

  /**
   * Get the dimension averages of the cluster selected.
   * @return
   */
  public double[] getDimClusterVals(){
    return clusterDimAvgs;
  }

  /**
   * Get the number of data points in the node the mouse is over.
   * @return - number of data points in the node the mouse is over, or -1 if over none.
   */
  public int getDenseCellVal(){
    if(cellMouseOver.x == -1){
      return -1;
    }else{
      return SOMp.getDensityMap()[cellMouseOver.x][cellMouseOver.y];
    }
  }

  /**
   * Get the number of data points of the given subset in the node the mouse is over.
   * @param setNum
   * @return
   */
  public int getDenseCellVal(int setNum){
    if(cellMouseOver.x == -1){
      return -1;
    }else{
      return SOMp.getSubsetDenseMap(setNum)[cellMouseOver.x][cellMouseOver.y];
    }
  }

   /**
   * Gets the hit histogram for a data set (by frequency rather than hits) and
   * blurs it the given number of times.
   * @param setNum - which data set to get hit hist of
   * @param blurNum - number of passes of the blurring algorithm
   * @return
   */
  public float[][] getDenseBlurred(int setNum, int blurNum){
    int[][] hitHist = SOMp.getSubsetDenseMap(setNum);
    float numPlaced = 0;
    for(int i = 0; i < hitHist.length; i++){
      for(int j = 0; j < hitHist[0].length; j++){
        numPlaced += hitHist[i][j];
      }
    }

    float[][] newHitHist = new float[hitHist.length][hitHist[0].length];
    for(int i = 0; i < newHitHist.length; i++){
      for(int j = 0; j < newHitHist[0].length; j++){
        newHitHist[i][j] = hitHist[i][j] / numPlaced;
      }
    }

    for(int i = 0; i < blurNum; i++){
      newHitHist = blurHitHist(newHitHist);
    }
    System.out.println("Num placed was"+numPlaced);
    System.out.println("getDenseBlurred "+setNum+" "+blurNum+" (0,0) is "+newHitHist[0][0]);
    return newHitHist;
  }

  private float[][] blurHitHist(float[][] oldHitHist){
    float[][] newHitHist = new float[oldHitHist.length][oldHitHist[0].length];
    //make each node an average of itself and its neighbors
   for(int i = 0; i < newHitHist.length; i++){
      for(int j = 0; j < newHitHist[0].length; j++){
        //find all neighbors at distance of 1
        LinkedList<Point> neighborPoints = SOMHelperFns.neighborsAtDistance(new Point(i, j), 1, SOMp.getData());
        double sumVal = oldHitHist[i][j];
        int numToAvg = 1;
        while(!neighborPoints.isEmpty()){
          Point p = neighborPoints.removeFirst();
          sumVal += oldHitHist[p.x][p.y];
          numToAvg++;
        }
        newHitHist[i][j] = (float) sumVal / numToAvg;
      }
    }
    return newHitHist;
  }

  /**
   * Get the number of data points of the given dense map in the node the mouse is over.
   * @param selectedSubSets
   * @param selectedDataSets
   * @return
   */
  public int getDenseCellVal(boolean selectedSubSets[], boolean selectedDataSets[]){
    if(cellMouseOver.x == -1){
      return -1;
    }else{
      int placed = 0;
      for(int i = 0; i < selectedSubSets.length; i++){
        if(selectedSubSets[i]){
          placed+= SOMp.getSubsetDenseMap(i)[cellMouseOver.x][cellMouseOver.y];
        }
      }

      for(int i = 0; i < selectedDataSets.length; i++){
        if(selectedDataSets[i]){
          placed+= SOMp.getDataSetDenseMap(i)[cellMouseOver.x][cellMouseOver.y];
        }
      }
      return placed;
    }
  }

  /**
   * Set a cluster to be empty.
   */
  public void clearPointsSelected(){
    Clusterpts = new Point[0]; //I guess a 0 length array is ok
  }


  /**
   * Add another base point (from the given pixel) for selecting a cluster
   * @param x
   * @param y
   */
  public void addPxlSelected(int x, int y){
    Point p = SOMImageFns.getcell(new Point(x,y));
    addPointSelected(p);
  }



  /**
   * Add another base point selected for selecting a cluster.
   * @param p
   */
  public void addPointSelected(Point p){
    //TODO: I could be duplicating points, I should add a check for that
    if(p.x >= 0 && p.x < SOMp.getSOMwidth() && p.y >= 0 && p.y < SOMp.getSOMheight()){
      Point newcells[] = new Point[Clusterpts.length+1];
      System.arraycopy(Clusterpts, 0, newcells, 0, Clusterpts.length); //copy old ones
      newcells[Clusterpts.length] = p; //add new one
      Clusterpts = newcells;
    }
  }

  /**
   * Cluster around the selected points using the given clustering method and tolerance.
   * @param tolerance
   * @param clusterType
   * @param e
   */
  public void makeCluster(float tolerance, int clusterType, MouseEvent e){
    makeCluster(Clusterpts, tolerance, clusterType, e); //pass on to the main makeCluster function
  }

  /**
   *
   * @param i
   * @param j
   * @return
   */
  public LinkedList getPxlsOfCell(int i, int j){
    return SOMImageFns.getPxlsOfCell(i, j);
  }
  
  
  /**
   * Change the clustering threshhold value.
   * @param thresh
   * @param clusterType
   */
  public void changeThreshhold(float thresh,  int clusterType){
    if(Clusterpts.length > 0){ //if there are any points selected to make a cluster from
      makeCluster(Clusterpts, thresh, clusterType, null);
    }
  }
  
  /**
   * Change the clustering type.
   * @param thresh
   * @param clusterType
   */
  public void changeClusterType(float thresh, int clusterType){
    if(Clusterpts.length > 0){ //if there are any points selected to make a cluster from
      makeCluster(Clusterpts, thresh, clusterType, null);
    }
  }
  
  /**
   * Returns the names of the different channels (map tiles) of the SOM.
   * @return Names of the different channels
   */
  public String[] getColNames(){
    return SOMp.getDimNames();
  }

  public int getUnscaledDimensions(){
    return SOMp.getDataSetScalar().getUnscaledDimensions();
  }

  /**
   * Returns the number of data points that belong to the current cluster.
   * @return
   */
  public int getTotalClusterMembs(){
    //get the membership size of the cluster
    int[][] denseMap = SOMp.getDensityMap();
    //get membership of dense map, number nodes selected, and average vals of nodes
    int membSize = 0;
    for (int i = 0; i < lastCluster.length; i++) {
      for (int j = 0; j < lastCluster[0].length; j++) {
        if (lastCluster[i][j]) { //if it is in the cluster
          //dimension average
          membSize += denseMap[i][j]; //add the number we need
        }
      }
    }
    return membSize;
  }

  /**
   * Returns the number of data points of the given set that belong to the current cluster.
   * @param setNum 
   * @return
   */
  public int getSubClusterMembs(int setNum){
    //get the density maps of the various clusters
    int[][] setDenseMaps =  SOMp.getSubsetDenseMap(setNum);

    int setMembSize =0;
    for (int i = 0; i < lastCluster.length; i++) {
      for (int j = 0; j < lastCluster[0].length; j++) {
        if (lastCluster[i][j]) { //if it is in the cluster          
          setMembSize += setDenseMaps[i][j];
        }
      }
    }
    return setMembSize;
  }

  /**
   * Returns the number of data points in the combo dense map that belong to the current cluster.
   * @param selectedSubSets
   * @param selectedDataSets
   * @return
   */
  public int getTotalClusterMembs(boolean selectedSubSets[], boolean selectedDataSets[]){
    int membSize = 0;
    for(int k = 0; k < selectedSubSets.length; k++){
      if(selectedSubSets[k]){
        int[][] denseMap = SOMp.getSubsetDenseMap(k);
        for (int i = 0; i < lastCluster.length; i++) {
          for (int j = 0; j < lastCluster[0].length; j++) {
            if (lastCluster[i][j]) { //if it is in the cluster
              membSize += denseMap[i][j]; //add the number we need
            }
          }
        }
      }
    }
    for(int k = 0; k < selectedDataSets.length; k++){
      if(selectedDataSets[k]){
        int[][] denseMap = SOMp.getDataSetDenseMap(k);
        for (int i = 0; i < lastCluster.length; i++) {
          for (int j = 0; j < lastCluster[0].length; j++) {
            if (lastCluster[i][j]) { //if it is in the cluster
              membSize += denseMap[i][j]; //add the number we need
            }
          }
        }
      }
    }
    return membSize;
  }

  /**
   * Generates the cluster based on the initial point and the clustering options.
   * @param cells
   * @param tolerance
   * @param clusterType
   * @param e
   */
  private void makeCluster(Point[] cells, float tolerance, int clusterType, MouseEvent e){
    
    if(clusterType == UCLUSTER){ //if it's a UMap cluster, multiply tolerance by 2 so it sort of evens out
      tolerance = tolerance * 2;
    }
    
    
    // if it is a valid cell, send to Model for clustering
    boolean[][] cluster = SOMp.makeCluster(cells, tolerance, clusterType); //send other values as well, probably ask user first
    lastCluster = cluster;

    //get number nodes selected and average vals of nodes
    int membSize = 0;
    int numNodesSelected = 0;
    //int setMembSizes[] = new int[setDenseMaps.length];
    clusterDimAvgs = new double[SOMp.getDimNames().length];
    for(int i = 0; i < cluster.length; i++){
      for(int j = 0; j < cluster[0].length; j++){
        if(cluster[i][j]){ //if it is in the cluster
          //dimension average
          float[] cellVals = SOMp.getCellVals(new Point(i, j));
          int[][] denseMap = SOMp.getDensityMap();
          for(int k = 0; k <SOMp.getDimNames().length; k++){
            clusterDimAvgs[k] =  cellVals[k] / (numNodesSelected+1) + (clusterDimAvgs[k]*numNodesSelected)/(numNodesSelected+1);

          }
          membSize += denseMap[i][j];
          numNodesSelected++;
        }
      }
    }
    if(numNodesSelected == 0 && wasLastClusterEmpty){ //if second moved right click that didn't select anything
      if(e == null){
        System.out.println("MakeCluster wants to open right menu, but no mouse event");
      }else{
        //this was causing problems in keeping the right click menu open when you click away from it
        //somVC.showRightClickMenu(e);
      }
    }
    //display cluster
    if(SOMp.getSOMType() == SOMProxy.HEXSOM){
      if(numNodesSelected == 0){
        somVC.setClusterImg(null, membSize ,SOMp.dataLength());
        somVC.updateMapStats();
        wasLastClusterEmpty = true;
      }else{
        wasLastClusterEmpty = false;
        BufferedImage img = SOMImageFns.makeClusterHexImg(cluster);
        somVC.setClusterImg(img, membSize ,SOMp.dataLength());
        somVC.updateMapStats();
      }
    }
  }

  public void saveCluster(SaveClusterSettings settings, BufferedImage screenshot){

    //TODO: Need to wait until it is done getting density

    if(lastCluster == null){
      mainMed.getApp().alert("No cluster selected");
    }


    int[][] denseMap = SOMp.getDensityMap();
    int membSize = 0;
    for(int i = 0; i < lastCluster.length; i++){
      for(int j = 0; j < lastCluster[0].length; j++){
        if(lastCluster[i][j]){ //if it is in the cluster
          membSize += denseMap[i][j]; //add the number we need
        }
      }
    }

    int[] membMap = null;

    if(settings.saveClusterAsNewDataSet || settings.saveClusterToFile){
      membMap = new int[membSize];
      int memPos = 0;
      //add all the points
      for(int i = 0; i < lastCluster.length; i++){
        for(int j = 0; j < lastCluster[0].length; j++){
          if(lastCluster[i][j]){ //if it is in the cluster
            int cellMembs[] = SOMp.getCellMembers(new Point(i,j));
            if(cellMembs.length != denseMap[i][j]){
              System.out.println("Error: SOMofCluster, Densemap:"+denseMap[i][j]+
                  " cellMembers:"+ cellMembs.length);
            }
            for(int k = 0; k < denseMap[i][j]; k++){
              membMap[memPos] = cellMembs[k];
              memPos++;
            }
          }
        }
      }
    }

    if(membSize == 0){
      String msg = "No data points in cluster.";
      facade.sendNotification(ifcSoft.ApplicationFacade.STRINGALERT, msg, null);
      return;
    }

    if(settings.saveClusterAsNewDataSet){
      DataSet newDataSet = (DataSet) new SubsetData(SOMp.getDataSet(), membMap);
      DataSetProxy newdsp = new DataSetProxy();
      newdsp.setDataSet(newDataSet);
      newdsp.setDataSetName(settings.clusterName);
      mainMed.addNewDSP(newdsp);
    }

    if(settings.saveClusterStats){
      saveClusterStats(settings.clusterName, settings.summDataSet); //settings.summDataSet
    }
    
    if(settings.saveClusterToFile){
      saveClusterToFile(settings.clusterName, settings.fileDirectory, membMap);
    }

    if(settings.saveScreenshot){
      saveScreenshot(settings.clusterName, settings.screenshotDirectory, screenshot);
    }
  }

  public void saveClusterStats(String clustName, SummaryData sumDataSet){
    if(sumDataSet != null){
      sumData = sumDataSet;
    }
    if(lastCluster != null){
      //String clustName = "clust " + clustNum;
      clustNum++;
      if(sumData == null){
        sumData = new SummaryData();
        DataSetProxy sumDSP = new DataSetProxy();
        sumDSP.setDataSet(sumData);
        facade.sendNotification(ApplicationFacade.ADDNEWDSP, sumDSP, null);
      }
      //this seems like a cheap way of doing it, but I'm not sure if it's worth making a custom
      // object type to hold this
      System.out.println("adding cluster: " + clustName);
      for(int i = 0; i < SOMp.getRawSetNames().size(); i++){
        SummaryDataPoint sdp = new SummaryDataPoint(SOMp.getDataSet(), SOMp.getRawSetNames().get(i),
          getSubClusterMembs(i),   getSubDenstiyMapPlaced(i));
        sumData.addDataPt(sdp, SOMp.getDataSet().getRawSets().get(i), clustName);

      }


    }
  }

  /**
   * Saves the selected cluster into a file.
   */
  public void saveClusterToFile(String name, String filepath, int[] membMap ){
    if(!filepath.endsWith("/"))
      filepath+= "/";
    String selectedFile = filepath+name+".csv";
    /*if(!selectedFile.endsWith(".csv")){
      selectedFile += ".csv";
    }*/

    DataSet dataSet = SOMp.getDataSet();
    boolean saveNames = true;
    boolean savePointNames = false;
    if(dataSet.hasPointNames()){
      savePointNames = true;
    }
    BufferedWriter bw;
    try {
      bw = new BufferedWriter(new FileWriter(selectedFile));
      //write labels
      if(saveNames){
        bw.write("File,");
      }
      if(savePointNames){
        bw.write("Name,");
      }
      for(int i = 0; i < dataSet.getDimensions(); i++ ){
        bw.write(dataSet.getColLabels()[i]);
        if(i != dataSet.getDimensions() - 1){
          bw.write(",");
        }else{

          bw.newLine();
        }
      }
      //write the data

      for(int i = 0; i < membMap.length; i++){
        float[] dataPt = dataSet.getVals(membMap[i]);
        if(saveNames){
          bw.write(dataSet.getPointSetName(membMap[i])+",");
        }
        if(savePointNames){
          bw.write(dataSet.getPointName(membMap[i])+",");
        }
        for(int k = 0; k < dataSet.getDimensions(); k++){
          bw.write(""+dataPt[k]);
          if(k != dataSet.getDimensions() - 1){
            bw.write(",");
          }else{

            bw.newLine();
          }
        }
      }
      bw.close();

    } catch (IOException ex) {
      facade.sendNotification(ApplicationFacade.EXCEPTIONALERT, ex, null);
    }
  }

  public void saveScreenshot(String name, String filepath, BufferedImage screenshot){
    if(!filepath.endsWith("/"))
      filepath+= "/";
    String selectedFile = filepath+name+".jpg";
    try {
      ImageIO.write(screenshot, "png", new File(selectedFile));
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }

  public BufferedImage getScreenshot(){
    try {
      Robot robot = new Robot();
      Rectangle captureSize = mainMed.getApp().getStageRectangle();
      BufferedImage bufferedImage = robot.createScreenCapture(captureSize);
      return bufferedImage;
    }
    catch(Exception e){
    	e.printStackTrace();
      return null;
    }
  }


  /**
   * Makes a new tab with a Histogram using the dimension on the given tile number.
   * @param dimNum
   */
  public void makeHistogram(int dimNum){
    if(dimNum < 0){ //Todo: should I allow histograms of UMaps?
      return;
    }

    if(dimNum >= SOMp.getDimNames().length){
      return; //this was a density map
    }
    //this.g
    if(dss.getClass() == LogScaleNormalized.class){
      mainMed.makeHistogram(dsp, dimNum, 1);
    }else{//linear
      mainMed.makeHistogram(dsp, dimNum, 0);
    }

    
  }

  /**
   * Generates a black image
   * TODO:this is poorly designed for the moment. It only returns a hexMap sized image
   * @param rawMap
   * @return
   */
  private BufferedImage blankMap(float[][] rawMap){
    BufferedImage img = new BufferedImage(rawMap.length*8+12,
        rawMap[0].length*6+9,BufferedImage.TYPE_INT_RGB);
    return img;
  }

  /**
   * Returns the number of nodes wide the SOM is.
   * @return
   */
  public int SOMwidth(){
    return SOMp.getSOMwidth();
  }

  /**
   * Returns the number of nodes high the SOM is.
   * @return
   */
  public int SOMheight(){
    return SOMp.getSOMheight();
  }
  


  /**
   * Returns the Data Set with the given index out of the universal DSP list
   * @param index
   * @return
   */
  public DataSetProxy getDSP(int index){
    return mainMed.getDSP(index);
  }

  /**
   * Returns the number of Data Sets in the universal DSP list
   * @return
   */
  public int numDSPs(){
    return mainMed.numDSPs();
  }

  /**
   * Returns the number of data points in the given Data Set
   * @param index
   * @return
   */
  public int getDSPSize(int index){
    return mainMed.getDSPSize(index);
  }
  
  /**
   * Returns (and creates if needed) a combination of the given data sets.
   * @param selectedDataSets
   * @return
   */
  public DataSetProxy getDataSet(Boolean[] selectedDataSets){
    return mainMed.getDataSet(selectedDataSets);
    
  }

  /**
   * Returns the MainApp object
   * @return
   */
  private MainAppI getApp(){
    return (MainAppI) super.getViewComponent();
  }

  /**
   * Let the tab know that there is a new Data Set Proxy loaded
   */
  @Override
  public void informNewDsp() {
    //So far the SOM Mediator has nothing to do with this
  }


}
