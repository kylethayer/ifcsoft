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

import java.awt.Point;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ifcSoft.model.som.jobs.ComputeStandardSOMJob;
import ifcSoft.model.som.jobs.FindMembershipsJob;
import ifcSoft.model.thread.ThreadJob;
import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.dataSetScalar.DataSetScalar;
import ifcSoft.model.som.jobs.ComputeBatchSOMJob;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.puremvc.java.patterns.facade.Facade;

/**
 * The Self Organizing Map object
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOM {

  protected Facade facade;

  /**
   * SOM made of square nodes
   */
  public static int SQUARESOM = 1;

  /**
   * SOM made of Hexagonal Nodes
   */
  public static int HEXSOM = 2;

  /**
   * Cluster according to Edge Map
   */
  public static final int ECLUSTER = 1;

  /**
   * Cluster according to multiple edges on the Edge Map
   */
  public static final int MECLUSTER = 2;

  /**
   * Cluster according to the UMap
   */
  public static final int UCLUSTER = 3; //UMap cluster
  
  
  /**
   * The SOM map (array of nodes)
   */
  protected SOMNode SOMnodes[][];

  /**
   * In order to go back to an old SOM map, but I might not even be using this now.
   * TODO: Rather than overwrite a SOM internally, it should be replaced by a new SOM. I might already do this
   */
  protected SOMNode OldSOMnodes[][];
  protected DataSetScalar datasetScalar;
  protected float[][] UMap;
  protected int[][] DenseMap;
  protected int denseMapPlaced; //number of points placed in the density map
  protected double placedPointsError;
  protected int[][][] subsetDenseMaps;
  protected int [] subsetDenseMapPlaced;
  protected LinkedList<String> rawSetNames;

  protected int[][][] dataSetDenseMaps; //this is for other data sets (not subsets)
  protected int [] dataSetDenseMapPlaced;

  protected LinkedList<DataSetProxy> dataSets; //this is only to be accessed, not written to

  protected float[][][] UEmap;
  protected short[][] MemberArray; //col 1 = x, col 2 = y, col 3 = setNum

  protected int mapType = 0;
  /**
   * If the SOM used log weights.
   */
  //public boolean isLog = true;
  
  AtomicBoolean iscanceled = new AtomicBoolean(false);
  AtomicBoolean ispaused = new AtomicBoolean(false);
  protected LinkedList<FindMembershipsJob> pausedJobs = new LinkedList<FindMembershipsJob>();
  
  protected float SOMprogress = 0;
  
  protected float weighting[]; //weighting of the different types
  protected int channelsUsed[] = null; //if only some channels are used, this lists them (for speed boost)
  protected boolean allUsedWeightsSame = true; //if all the used weights are the same, then BMU doesn't need to use the weights
  
  //this will probably belong further up the the program and be passed to me
  protected BlockingQueue<ThreadJob> jobqueue = new LinkedBlockingQueue<ThreadJob>();
  
  
  
  /**
   * Default constructor: creates a random map to start with.
   * @param dims
   * @param width
   * @param height
   * @param mapType
   * @param weighting
   * @param isLog
   * @param dataSet
   */
  /*public SOM(int width, int height, int mapType, float[] weighting, DataSetScalar dataSet, Facade facade){
    int dims = dataSet.getDimensions();
    SOMnodes = new SOMNode[width][height];
    this.mapType = mapType;
    this.weighting = weighting;
    //this.isLog = isLog;
    this.datasetScalar = dataSet;
    this.facade = facade;
    
    if(this.weighting == null || this.weighting.length < dims){
      this.weighting = new float[dims];
      for(int i = 0; i < dims; i++){
        this.weighting[i] = 1;
      }
    }

    SOMInitFns.linearInitialize(width, height, dataSet.getDataSet(), this);
    checkWeighting();
    /*try {
      loadSOMfile(dims);
    } catch (Exception ex) {
      Logger.getLogger(SOM.class.getName()).log(Level.SEVERE, null, ex);
    }*/


    
    
    //TODO: should I just go ahead and start the threads to await jobs?
    //what happens to threads just sitting out there? I probably should have 
    //universal threads and jobQueue associated with the program, and pass it to the SOM
    //have a listener to get the threads if they exist?
    /*for(int i = 0; i < 4; i++){
      Thread newthread = new Thread(new SOMThread(jobqueue, facade));
      newthread.setPriority(newthread.getPriority() - 1);
        //drop the priority by 1 so that it runs in background and doesn't
        //interfere with the gui
      newthread.start();
    }
  }*/

  public SOM(SOMSettings somSettings, Facade facade){
    int dims = somSettings.datasetscalar.getDimensions();
    SOMnodes = new SOMNode[somSettings.width][somSettings.height];
    this.mapType = HEXSOM; //for now we default as HEX map
    this.weighting = somSettings.weights;
    //this.isLog = isLog;
    this.datasetScalar = somSettings.datasetscalar;
    this.facade = facade;

    if(this.weighting == null || this.weighting.length < dims){
      this.weighting = new float[dims];
      for(int i = 0; i < dims; i++){
        this.weighting[i] = 1;
      }
    }

    if(somSettings.initType == SOMSettings.LINEARINIT){
      SOMInitFns.linearInitialize(somSettings.width, somSettings.height, datasetScalar.getDataSet(), this);
      checkWeighting();
    }else if (somSettings.initType == SOMSettings.RANDOMINIT){
      for(int i = 0; i < SOMnodes.length; i++){
        for(int j = 0; j < SOMnodes[0].length; j++){
          //initialize with random points from the data
          SOMnodes[i][j] = new SOMNode(
              datasetScalar.getPoint((int) (Math.random() * (datasetScalar.length() - 1)))
              );
          //SOMNode[i][j] = new SOMNode(dims, datasetScalar.getMins(), .getMaxes())
        }
      }
    }else if (somSettings.initType == SOMSettings.FILEINIT){
      try {
        SOMInitFns.loadSOMfile(dims, this);
      } catch (Exception ex) {
        Logger.getLogger(SOM.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    //TODO: should I just go ahead and start the threads to await jobs?
    //what happens to threads just sitting out there? I probably should have
    //universal threads and jobQueue associated with the program, and pass it to the SOM
    //have a listener to get the threads if they exist?
    for(int i = 0; i < 4; i++){
      Thread newthread = new Thread(new SOMThread(jobqueue, facade));
      newthread.setPriority(newthread.getPriority() - 1);
        //drop the priority by 1 so that it runs in background and doesn't
        //interfere with the gui
      newthread.start();
    }
  }




  
  /**
   * Constructor that loads the specific given SOM node values.
   * @param dims
   * @param width
   * @param height
   * @param isLog
   * @param colWeights
   * @param mapType
   * @param dataSet
   * @param oldNodes
   */
  /*public SOM(int dims, int width, int height, float colWeights[], int mapType, DataSetScalar dataSet, SOMNode[][] oldNodes, Facade facade){
    SOMnodes = new SOMNode[width][height];
    this.mapType = mapType;
    this.datasetScalar = dataSet;
    this.facade = facade;
    
    if(weighting == null || weighting.length < dims){
      weighting = new float[dims];
      for(int i = 0; i < dims; i++){
        weighting[i] = colWeights[i];
      }
    }
    
    /*for now we'll do just square maps*/
    //initialize the nodes
    /*for(int i = 0; i < width; i++){
      for(int j=0; j < height; j++){
        SOMnodes[i][j] = new SOMNode(oldNodes[i][j].getWeights());
      }
    }

    initDenseMap();
    
    for(int i = 0; i < 4; i++){
      Thread newthread = new Thread(new SOMThread(jobqueue, facade));
      newthread.start();
    }

    //add job to find membership of set
    initMemberArray(dataSet.length());
    int firstpt = 0;
    while(firstpt < dataSet.length()){
      int lastpt = firstpt + 1000 - 1;
      if(lastpt >= dataSet.length()){
        lastpt = dataSet.length() - 1;
      }
      FindMembershipsJob newjob = new FindMembershipsJob(-1, this, firstpt, lastpt, iscanceled, ispaused);
      try {
        jobqueue.put(newjob);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      firstpt+= 1000;
    }
  }*/

  /**
   * This function saves information on the weighting used, allowing findBMU to run faster
   */
  private void checkWeighting() {
    //set the channelsUsed if some aren't
    int numChannelsUsed = 0;
    for(int i = 0; i <weighting.length; i++){
      if(weighting[i] != 0){
        numChannelsUsed++;
      }
    }
    if(numChannelsUsed < weighting.length){
      channelsUsed = new int[numChannelsUsed];
      int channelsUsedI = 0;
      for(int i = 0; i < weighting.length; i++){
        if(weighting[i] != 0){
          channelsUsed[channelsUsedI] = i;
          channelsUsedI++;
        }
      }

    }

    //find out if all used weights are the same
    allUsedWeightsSame = true;
    if(channelsUsed == null){
      float firstWeight = weighting[0];
      for(int i = 1; i < weighting.length; i++){
        if(weighting[i] != firstWeight){
          allUsedWeightsSame = false;
        }
      }
    }else{
      float firstWeight = weighting[channelsUsed[0]];
      for(int i = 1; i < channelsUsed.length; i++){
        if(weighting[channelsUsed[i]] != firstWeight){
          allUsedWeightsSame = false;
        }
      }
    }
  }

  String[] getDimLabels() {
    return datasetScalar.getColLabels();
  }
  
  /**
   * Calculates the SOM with the given iterations and max neighborhood size.
   * @param iterations
   * @param maxNeighborSize
   */
  public void calculateSOM(SOMSettings somSettings){
    SOMprogress = 0;
    iscanceled.set(false); //cancel anything else I was doing earlier
    //and make a new boolean cancel object for the next job
    iscanceled = new AtomicBoolean(false);
    ThreadJob newjob;
    System.out.println("calculateSOM type:" + somSettings.SOMType);
    if(somSettings.SOMType.equals(SOMSettings.CLASSICSOM)){
      newjob = new ComputeStandardSOMJob(datasetScalar,
          somSettings.classicIterations, somSettings.classicMaxNeighborhood, somSettings.classicMinNeighborhood,
          this, iscanceled); 
    }else{
      newjob = new ComputeBatchSOMJob(datasetScalar,
          somSettings.batchSteps,somSettings.batchMaxNeighborhood, somSettings.batchMinNeighborhood,
          somSettings.batchPointsPerNode, this, iscanceled);
    }

    try {
      jobqueue.put(newjob);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  /**
   * Pause the jobs on the job queue.
   */
  public synchronized void pauseJobs(){
    ispaused.set(true);
  }

  /**
   * Put a paused job on a list of paused jobs for the SOM.
   * @param fmj
   */
  public synchronized void insertPausedJob(FindMembershipsJob fmj){
    if(ispaused.get()){ //in case it got here after the jobs were unpaused
      pausedJobs.addLast(fmj);
    }else{ //put it back in the job queue where it belongs
      try {
        jobqueue.put(fmj);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Restart the jobs that are on the paused job list for the SOM.
   */
  public synchronized void restartPausedJobs(){
    ispaused.set(false);
    try {
      while(pausedJobs.size() > 0){
        jobqueue.put(pausedJobs.removeFirst());
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  /**
   * Cancel the SOM job.
   */
  public void cancelSOM() {
    iscanceled.set(true);
  }
  
  /**
   * Cancel the SOM job.
   */
  public void cancelJobs() {
    iscanceled.set(true);
  }

  
  protected float getDistance(Point p1, Point p2){
    float euclidD = 0;
    float pt1W[] = getMapWeights(p1);
    float pt2W[] = getMapWeights(p2);
    for(int k = 0; k < pt1W.length; k++){
      euclidD+= Math.pow(weighting[k]*(pt1W[k] - pt2W[k]), 2);
    }
    return (float) Math.sqrt(euclidD);
  }

  
  /**
   * Creates and clears the density map.
   * Density map is how many are members of each node.
   */
  public synchronized void initDenseMap(){
    denseMapPlaced = 0;

    DenseMap = new int[SOMnodes.length][SOMnodes[0].length];
    //I think it starts out clear, but I'll clear it just in case
    for(int i =0; i < DenseMap.length; i++){
      for(int j=0; j < DenseMap[0].length; j++){
        DenseMap[i][j]=0;
      }
    }
  }
  
  /**
   * Membership of each data point is saved as a short array (32,767 max val, but also negatives)?
   * @param length
   */
  public synchronized void initMemberArray(int length){
    MemberArray = new short[length][3]; //the map pos of each member
    
    //It should be cleared as in all belong to cell (-1,-1)
    for(int i =0; i < MemberArray.length; i++){
      for(int j=0; j < MemberArray[0].length; j++){
        MemberArray[i][j]=-1;
      }
    }
  }

  /**
   * Initialize the dense maps for the raw data set dense maps.
   */
  public synchronized void initSetDenseMaps() {
    rawSetNames = datasetScalar.getRawSetNames();
    subsetDenseMaps = new int[rawSetNames.size()][SOMnodes.length][SOMnodes[0].length];
    subsetDenseMapPlaced = new int[rawSetNames.size()];
    //I think it starts out clear, but I'll clear it just in case
    for(int k = 0; k < subsetDenseMaps.length; k++){
      subsetDenseMapPlaced[k] = 0;
      for(int i =0; i < subsetDenseMaps[0].length; i++){
        for(int j=0; j < subsetDenseMaps[0][0].length; j++){
          subsetDenseMaps[k][i][j] = 0;
        }
      }
    }
  }

  
  /**
   * Set the given data point as a member of the given node.
   * @param mem
   * @param p
   */
  public synchronized void setMember(int mem, Point node, double dist){
    placedPointsError += dist;
    String set = datasetScalar.getPointSetName(mem);
    if(set == null){
      System.out.println("Error in SOM:setMember - getPointSetName returned null for mem = "+mem);
      return;
    }
    int setNum = rawSetNames.indexOf(set);
    MemberArray[mem][0] = (short) node.x;
    MemberArray[mem][1] = (short) node.y;
    MemberArray[mem][2] = (short) setNum;

    //increment the appropriate density maps
    (DenseMap[node.x][node.y])++;
    denseMapPlaced++;
    (subsetDenseMaps[setNum][node.x][node.y])++;
    (subsetDenseMapPlaced[setNum])++;
  }

  public double getPlacedPointsError(){
    return placedPointsError;
  }

  /**
   * Set the given data point of the given data set as a member of the given node.
   * @param DataSetNum
   * @param mem
   * @param p
   */
  public synchronized void setOtherDataSetMember(int DataSetNum, int mem, Point p, double dist){
    //increment the appropriate density maps
    (this.dataSetDenseMaps[DataSetNum][p.x][p.y])++;
    this.dataSetDenseMapPlaced[DataSetNum]++;
  }
  

  /**
   * Return the number of points placed on the density map.
   * @return
   */
  public synchronized int densityMapPlaced(){
    return denseMapPlaced;
  }


  /**
   * Return the number of points that have been placed on the subset density maps.
   * @return
   */
  public synchronized int[] subsetDensityMapsPlaced(){
    int [] ret = new int[subsetDenseMapPlaced.length];
    System.arraycopy(subsetDenseMapPlaced, 0, ret, 0, subsetDenseMapPlaced.length);
    return ret;
  }

  /**
   * Return the density Map of the requested other data set.
   * @param dataSet
   * @return
   */
  synchronized int[][] getDataSetDenseMap(int dataSet) {
    if(dataSetDenseMaps == null || dataSetDenseMaps.length <= dataSet){
      return null;
    }
    return dataSetDenseMaps[dataSet];
  }

  /**
   * Returns the number of points that have been placed for the given data set.
   * @param dataSet
   * @return
   */
  synchronized int dataSetDenseMapPlaced(int dataSet) {
    if(dataSetDenseMapPlaced == null || dataSetDenseMapPlaced.length <= dataSet){
      return 0;
    }
    return dataSetDenseMapPlaced[dataSet];
  }

  /**
   * Return the number of points that have been placed on the density map for other data sets.
   */
  int getOtherDataSetsPlaced(){
    int placed = 0;
    if(dataSetDenseMapPlaced != null){
      for(int i = 0; i < dataSetDenseMapPlaced.length; i++){
        placed += dataSetDenseMapPlaced[i];
      }
    }
    return placed;
  }
  
  
  
  /**
   * Returns the weights at a given node.
   */
  protected float[] getMapWeights(Point pt) {
    return SOMnodes[pt.x][pt.y].getWeights();
  }
  


  public float[] scalePoint(float[] unweighted){
    return datasetScalar.scalePoint(unweighted);
  }
  

  /**
   * Gets the horizontal number of SOM nodes.
   * @return the horizontal number of SOM nodes.
   */
  public int getWidth() {
    return SOMnodes.length;
  }
  
  /**
   * Gets the vertical number of SOM nodes.
   * @return the vertical number of SOM nodes.
   */
  public int getHeight() {
    return SOMnodes[0].length;
  }

  /**
   * Returns the type of SOM map (ie. Square, Hexagonal , ...)
   * @return the type of SOM map (ie. Square, Hexagonal, ...)
   */
  public int getSOMType() {
    return mapType;
  }

  /**
   * Returns the weighting used to build the SOM.
   * @return
   */
  public float[] getWeighting(){
    return weighting;
  }

  /**
   * progress is fractional between 0 and 1 unless it is actually done, then it is 100
   */
  int tempProg = 0;
  /**
   * Set the progress of the SOM calculation
   * @param p
   */
  public synchronized void setProgress(float p){
    SOMprogress = p;
    if(SOMprogress > (tempProg/ 10.0)+.1){
      tempProg++;
    }
    
  }

  /**
   * progress is fractional between 0 and 1 unless it is actually done, then it is 100
   * @return
   */
  public synchronized float getProgress(){
    return SOMprogress;
  }


  /**
   * TODO: this is not thread safe yet, I'll have to do something about that.
   * This might be outdated as well from when I overwrote SOM maps rather than made new ones.
   */
  public synchronized void clearUMaps() {
    UMap = null;
    DenseMap = null;
    UEmap = null;
    
  }

  /**
   * Returns the members of the given SOM node.
   * TODO: I have no error checking here, very dangerous
   * @param p
   * @return
   */
  public synchronized int[] getCellMembers(Point p) {
    LinkedList<Integer> membersLL = new LinkedList<Integer>();
    for(int i = 0; i < MemberArray.length; i++){
      if(MemberArray[i][0] == p.x && MemberArray[i][1] == p.y){
        membersLL.add(new Integer(i));
      }
    }
    
    int[] cellMembs = new int[membersLL.size()];
    for(int i = 0; i < cellMembs.length; i++){
      cellMembs[i] = membersLL.removeFirst();
    }
    
    return cellMembs;
  }


  /**
   * Returns the values of the given SOM node.
   * @param p
   * @return
   */
  float[] getCellVals(Point p) {
    return datasetScalar.unscalePoint(getMapWeights(p));
  }


  /**
   * Returns the number of dimensions.
   * @return
   */
  public int getDimensions() {
    return weighting.length;
  }

  /**
   * Returns the data set used to build the SOM.
   * @return
   */
  public DataSet getDataSet(){
    return datasetScalar.getDataSet();
  }

  public DataSetScalar getDataSetScalar(){
    return datasetScalar;
  }

  /**
   * Returns the given other data set.
   * @param dataSetNum
   * @return
   */
  DataSet getDataSet(int dataSetNum) {
    return dataSets.get(dataSetNum).getData();
  }

  /**
   * returns the length of the data set used to build the SOM.
   * @return
   */
  int dataLength() {
    return datasetScalar.length();
  }

  /**
   * Returns the length of the other data sets combined.
   * @return
   */
  synchronized int getOtherDataSetLength(){
    int totalLength = 0;
    if(dataSetDenseMaps!= null){
      for(int i = 0; i < dataSetDenseMaps.length; i++){
        if(dataSetDenseMaps[i] != null){
          totalLength += getDataSet(i).length();
        }
      }
    }
    return totalLength;
  }

  /**
   * returns the max value of the given dimension of the data used to build the SOM.
   * @param i
   * @return
   */
  float getMax(int i) {
    return datasetScalar.getMax(i);
  }

  /**
   * returns the min value of the given dimension of the data used to build the SOM.
   * @param i
   * @return
   */
  float getMin(int i) {
    return datasetScalar.getMin(i);
  }

  /**
   * returns the name of the data set used to build the SOM.
   * @return
   */
  String getDataSetName() {
    return datasetScalar.getName();
  }

  /**
   * Returns the names of the raw data sets that comprise the data set used to
   * build the SOM.
   * @return
   */
  LinkedList<String> getRawSetNames() {
    return this.rawSetNames;
  }

  
}

