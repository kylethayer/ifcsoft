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

import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.dataSetScalar.DataSetScalar;
import java.awt.Point;
import java.util.LinkedList;
import org.puremvc.java.interfaces.IProxy;
import org.puremvc.java.patterns.proxy.Proxy;



/**
 * This is the pureMVC proxy that mediates access to the SOM object.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMProxy extends Proxy implements IProxy {


	/**
	 * pureMVC name.
	 */
	public static String NAME = "SOMProxy";
	/**
	 * Square SOM array.
	 */
	public static int SQUARESOM = SOM.SQUARESOM;
	/**
	 * Hexagonal SOM array.
	 */
	public static int HEXSOM = SOM.HEXSOM;
	/**
	 * Cluster by Edge UMatrix.
	 */
	public static final int ECLUSTER = SOM.ECLUSTER;
	/**
	 * Cluster by multiple edges of the Edge UMatrix.
	 */
	public static final int MECLUSTER = SOM.MECLUSTER;
	/**
	 * Cluster by the UMatrix
	 */
	public static final int UCLUSTER = SOM.UCLUSTER;
	private String mediatorName;

	/**
	 * Constructor.
	 */
	public SOMProxy(){
		super(NAME, null);
	}

	//Load / Save currently disabled
	/*public void loadIFlowFile(String filename) {

		try {
			FileIO.loadIFlowFile(this, filename);
		} catch (Exception ex) {
			facade.sendNotification(ifcSoft.ApplicationFacade.EXCEPTIONALERT, ex, null);
		}
	}*/

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
	 * Make a SOM with the given input parameters.
	 * @param width - SOM array width.
	 * @param height - SOM array height
	 * @param iterations - Number of iterations
	 * @param weights - Weights for the different channels
	 * @param isLog - whether to use log scale or not
	 * @param dataset - the data set to use to build the SOM
	 */
	public void createSOM(int width, int height, int iterations, float[] weights, DataSetScalar dataset){
		throw new UnsupportedOperationException("Outdated");
		/*int dims = dataset.getDimensions();
		setData(new SOM(dims, width, height, SOM.HEXSOM, weights, dataset, facade));
		getData().calculateSOM( iterations, height / 2);

		System.out.println("SOMProxy: Sending StartedSOM");
		facade.sendNotification(ifcSoft.ApplicationFacade.STARTEDSOM, this, mediatorName);*/
	}


	public void createSOM(SOMSettings somSettings){
		int dims = somSettings.datasetscalar.getDimensions();
		setData(new SOM(somSettings, facade));
		getData().calculateSOM(somSettings);

		System.out.println("SOMProxy: Sending StartedSOM");
		facade.sendNotification(ifcSoft.ApplicationFacade.STARTEDSOM, this, mediatorName);
	}

	/**
	 * pause the current active job.
	 */
	public void pauseJobs(){
		getData().pauseJobs();
	}

	/**
	 * Restart the jobs that were paused.
	 */
	public void restartPausedJobs(){
		getData().restartPausedJobs();
	}

	/**
	 * Find the density map.
	 * TODO: What did I do here? How does this work?
	 * @param dataSet
	 * @param dataSets
	 */
	public void findDensity(int dataSet, LinkedList<DataSetProxy> dataSets) { //TODO: Should probably just pass relevent dataSets, rathar than all
		if(getData() != null){
			SOMRetrieveFns.findDensity(dataSet, dataSets, getData());
		}
	}


	/**
	 * Cancel a SOM that is being calculated.
	 */
	public void cancelSOM() {
		getData().cancelSOM();
	}

	/**
	 * Cancel jobs that are running.
	 */
	public void cancelJobs() {
		if(getData() != null){
			getData().cancelJobs();
		}
	}

	/**
	 * Return all the SOM dimension maps
	 * @return
	 */
	public float[][][] getSOMs(){
		//get all the maps and send them back
		int dims = getData().getDimensions();
		float [][][] allmaps = new float[dims][][];
		for(int i = 0; i < dims; i++){
			allmaps[i] = SOMRetrieveFns.getMap(i, getData());
		}
		return allmaps;
	}

	/**
	 * Return the Type of SOM it is (eg. Hex or Square)
	 * @return
	 */
	public int getSOMType(){
		return getData().getSOMType();
	}

	/**
	 * Return the weighting used to build the SOM
	 * @return
	 */
	public float[] getWeighting() {
		return getData().getWeighting();
	}

	/**
	 * Return the UMatrix.
	 * @return
	 */
	public float[][] getSOMUmap() {
		return SOMRetrieveFns.getUMap(getData());
	}

	/**
	 * Return the Edge UMatrix.
	 * @return
	 */
	public float[][][] getSOMUEmap() {

		return SOMRetrieveFns.getSOMUEmap(getData());
	}


	/**
	 * Make a cluster using the given clustering method, threshold and initial points.
	 * @param cells
	 * @param threshold
	 * @param clusterType
	 * @return
	 */
	public boolean[][] makeCluster(Point cells[], float threshold, int clusterType) {
		if(getData() == null){
			return null;
		}


		boolean[][] cluster = new boolean[getSOMwidth()][getSOMheight()]; //cells marked true are in the set
		for(int i = 0; i < getSOMwidth(); i++){
			for(int j = 0; j < getSOMheight(); j++){
				cluster[i][j] = false;
			}
		}

		float[][][] UEmap;
		float[][] Umap;

		switch(clusterType){
			case ECLUSTER:
				UEmap =  SOMRetrieveFns.getSOMUEmap(getData());
					//depth first search to add points
				ClusteringFunctions.makeECluster(getData(), cells, cluster, UEmap, threshold);
				//when we get back the cluster is complete, return it
				break;
			case MECLUSTER:
				UEmap = SOMRetrieveFns.getSOMUEmap(getData());
				ClusteringFunctions.makeMECluster(getData(), cells, cluster, UEmap, threshold);
				//when we get back the cluster is complete, return it
				break;
			case UCLUSTER:
				Umap = SOMRetrieveFns.getUMap(getData());
				ClusteringFunctions.makeUCluster(getData(), cells, cluster, Umap, threshold);
				//when we get back the cluster is complete, return it
				break;

		}

		return cluster;

	}



	/**
	 * Return the current progress (0 to 1 for in progress, 100 if done).
	 * @return
	 */
	public float getProgress(){
		return getData().getProgress();
	}


	/**
	 * Return the names of the dimensions.
	 * @return
	 */
	public String[] getDimNames(){
		return getData().getDimLabels();
	}

	/**
	 * Return the width of the SOM array.
	 * @return
	 */
	public int getSOMwidth() {
		return getData().getWidth();
	}

	/**
	 * Return the height of the SOM array.
	 * @return
	 */
	public int getSOMheight() {
		return getData().getHeight();
	}

	/**
	 * Return the density Map of the SOM.
	 * @return
	 */
	public int[][] getDensityMap(){
		if(getData() == null){
			return null;
		}
		return SOMRetrieveFns.getDensityMap(getData());
	}

	/**
	 * Return the number of points that have been placed on the density map.
	 * @return
	 */
	public int densityMapPlaced(){
		return getData().densityMapPlaced();
	}

	/**
	 * Return the density Map of the requested subset of the main data.
	 * @param i
	 * @return
	 */
	public int[][] getSubsetDenseMap(int i) {
		if(getData() == null){
			return null;
		}
		return SOMRetrieveFns.getSubsetDenseMap(i, getData());
	}

	/**
	 * Return the density Map of the requested other data set.
	 * @param dataSet
	 * @return
	 */
	public int[][] getDataSetDenseMap(int dataSet){
		if(getData() == null){
			return null;
		}
		int[][] denseMap = getData().getDataSetDenseMap(dataSet);
		if(denseMap == null){
			denseMap = new int[getSOMwidth()][getSOMheight()];
		}
		return denseMap;
	}

	/**
	 * Return the number of points that have been placed on the subset density maps.
	 * @return
	 */
	public int[] SubsetsDensityMapsPlaced(){
		return getData().subsetDensityMapsPlaced();
	}

	/**
	 * Return the number of points that have been placed on the density map for other data sets.
	 * @return
	 */
	public int getOtherDataSetsPlaced(){
		return getData().getOtherDataSetsPlaced();
	}

	/**
	 * Returns the number of points that have been placed for the given data set.
	 * @param dataSet
	 * @return
	 */
	public int dataSetDensityMapPlaced(int dataSet){
		if(getData() == null){
			return -1;
		}
		return getData().dataSetDenseMapPlaced(dataSet);
	}

	/**
	 * Returns the delta of the subset density maps
	 * @return
	 */
	public float[][] getDenseMapsDelta() {
		float[][] denseMap = SOMRetrieveFns.getSetDenseMapsDelta(getData());
		if(denseMap == null){
			denseMap = new float[getSOMwidth()][getSOMheight()];
		}
		return denseMap;
	}


	/**
	 * Get the SOM dimension values of the given SOM node.
	 * @param p
	 * @return
	 */
	public float[] getCellVals(Point p){
		return getData().getCellVals(p);

	}

	/**
	 * Get the data points that are members of the given SOM node.
	 * @param p
	 * @return
	 */
	public int[] getCellMembers(Point p) {
		return getData().getCellMembers(p);
	}


	/**
	 * Return the data set that was used to build the SOM.
	 * @return
	 */
	public DataSet getDataSet(){
		return getData().getDataSet();
	}

	public DataSetScalar getDataSetScalar(){
		return getData().getDataSetScalar();
	}

	/**
	 * Return the length of the data set that was used to build the SOM.
	 * @return
	 */
	public int dataLength() {
		return getData().dataLength();
	}

	/**
	 * Return the total length of the additional data sets that have been added
	 * for their own density maps.
	 * @return
	 */
	public int getOtherDataSetLength(){
		return getData().getOtherDataSetLength();
	}

	/**
	 * Get the minimum value of the main data set of the given dimension.
	 * @param i
	 * @return
	 */
	public float getMin(int i) {
		return getData().getMin(i);
	}

	/**
	 * Get the max value of the main data set of the given dimension.
	 * @param i
	 * @return
	 */
	public float getMax(int i) {
		return getData().getMax(i);
	}

	/**
	 * Return whether it used the log scale or not.
	 * @return
	 */
	/*public boolean isLog(){
		return getData().isLog;
	}*/

	/**
	 * Return the name of the data set used to make the SOM
	 * @return
	 */
	public String getDataSetName() {
		return getData().getDataSetName();
	}

	/**
	 * Return the names of the raw data sets that are used to make the data set
	 * that was used to make the SOM.
	 * @return
	 */
	public LinkedList<String> getRawSetNames() {
		return getData().getRawSetNames();
	}


	/**
	 * pureMVC method of getting the SOM object.
	 * @return
	 */
	@Override
	public SOM getData(){
		return (SOM) super.getData();
	}

	/**
	 * pureMVC set mediator Key.
	 * @param NAME
	 */
	public void setMediatorKey(String NAME) {
		mediatorName = NAME;
	}




}
