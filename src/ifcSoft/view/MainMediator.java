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
package ifcSoft.view;
 

import ifcSoft.ApplicationFacade;
import ifcSoft.MainAppI;
import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.dataSet.SubsetData;
import ifcSoft.model.dataSet.UnionData;
import ifcSoft.model.thread.LoadFileJob;
import ifcSoft.model.thread.RemoveOutliersJob;
import ifcSoft.model.thread.ThreadJob;
import ifcSoft.model.thread.jobThread;
import ifcSoft.view.histogram.HistTabMediator;
import ifcSoft.view.scatterplot.ScatterTabMediator;
import ifcSoft.view.windrose.WindRoseTabMediator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.puremvc.java.interfaces.IMediator;
import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.mediator.Mediator;

/**
 * 
 * This is the main mediator which holds and handles loaded data sets and active tabs.
 *
 *  NOTE: In order to prevent concurrency problems, tab switching must be done by the javafx
 * thread. Then any javafx function can assume that "currentTab" is actually the tab that was
 * active when the javafx function was triggered.
 *
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class MainMediator extends Mediator implements IMediator {

	/**
	 *
	 */
	public static String NAME = "No Data Mediator";
	/**
	 *
	 */
	public static String NODATA = "No Data";

	/**
	 *
	 */
	public LinkedList<DataSetProxy> dataSets;
	/**
	 *
	 */
	public LinkedList<Tab> tabs;
	/**
	 *
	 */
	public int currentTab; //index of current Tab
	private int currentMedNo = 0; // this is used to generate names for the mediators

	//TODO: Make All job threads run through here.
	private BlockingQueue<ThreadJob> jobqueue = new LinkedBlockingQueue<ThreadJob>();
	
	/**
	 * Initializes the MainMediator object.
	 *
	 * This is called from the StartupCommand and is passed the MainApp
	 * object, so that it can pass a reference of itself to MainApp. It also
	 * initializes the tabs and dataset objects.
	 *
	 * @param app - The MainApp object
	 */
	public MainMediator(MainAppI app){
		super(NAME, app);
		app.setMainMediator(this);
		dataSets = new LinkedList<DataSetProxy>();
		tabs = new LinkedList<Tab>();
		Tab newTab = new Tab(Tab.BLANKMODE, app); //start with a blank tab
		tabs.add(newTab);
		currentTab = 0;
		app.updateTabs();
		app.setCurrentTab(0);


		Thread newthread = new Thread(new jobThread(jobqueue));
		newthread.setPriority(newthread.getPriority() - 1);
				//drop the priority by 1 so that it runs in background and doesn't
				//interfere with the gui
		newthread.start();
		

	}
	
	/**
	 * PureMVC notifications this is interested in.
	 * @return
	 */
	@Override
	public String[] listNotificationInterests(){
		String [] temp = new String[4];
		temp[0] = NODATA;
		temp[1] = ApplicationFacade.ADDNEWDSP;
		temp[2] = ApplicationFacade.EXCEPTIONALERT;
		temp[3] = ApplicationFacade.STRINGALERT;
		return temp;
	}
	
	/**
	 * Handle a PureMVC notification
	 * @param note
	 */
	@Override
	public void handleNotification(INotification note){
		
		if(note.getName().contentEquals(NODATA)){
			//getApp().setNoData();
			
		}else if(note.getName().contentEquals(ApplicationFacade.ADDNEWDSP)){
			addNewDSP((DataSetProxy)note.getBody());
			
		}else if(note.getName().contentEquals(ApplicationFacade.EXCEPTIONALERT)){
			Exception e = (Exception) note.getBody();
			e.printStackTrace();
			getApp().alert("Exception:"+ e.getLocalizedMessage());
		}
		else if(note.getName().contentEquals(ApplicationFacade.STRINGALERT)){
			System.out.println("StringAlert: "+ (String) note.getBody());
			String s = (String) note.getBody();
			getApp().alert(s);
		}
	}

	/**
	 * Add new data set and let the tab know about it.
	 * @param dsp
	 */
	public void addNewDSP(DataSetProxy dsp){
		dataSets.add(dsp);
		getCurrentTab().informNewDsp();
	}

	/**
	 * Remove the data sets that depend on this data set (children).
	 * @param dataset
	 */
	public void removeDataSetChildren(DataSet dataset){
		LinkedList<DataSet> children = dataset.getChildren();
		//recurse on all parents
		while(children.size() > 0){
			DataSet child = children.removeFirst();
			removeDataSetAndChildren(child);
		}
	}

	/**
	 * Remove this data set and the data sets that depend on this data set (children).
	 * @param dataset
	 */
	public void removeDataSetAndChildren(DataSet dataset){
		LinkedList<DataSet> children = dataset.getChildren();
		//recurse on all parents
		while(children.size() > 0){
			DataSet child = children.removeFirst();
			removeDataSetAndChildren(child);

		}
		//remove the data set
		for(int i = 0; i < dataSets.size(); i++){
			if(dataSets.get(i).getData() == dataset){
				dataSets.remove(i);
				return;
			}
		}
	}
	
	/**
	 * Load a CSV file
	 *
	 * @param filenames
	 */
	public void loadFile(String filenames[]){
		//for now, we only do FCS (actually only CSV's of them)
		for(int i = 0; i < filenames.length; i++){
			DataSetProxy dsp = new DataSetProxy();
			dsp.setFile(filenames[i]);
			ThreadJob newJob = new LoadFileJob(dsp);
			jobqueue.add(newJob); //Add job to Job Queue
			this.getApp().addFileLoading(dsp); //tell the MainApp that there is a file loading.
		}
	}
	
	/**
	 * Remove outliers above a certain number of standard deviations from the given data set.
	 * @param stdDevs
	 * @param dsp
	 */
	public void removeOutliers(double stdDevs, DataSetProxy dsp){
		if(dsp == null || dsp.getData() == null){
			getApp().alert("No Data Set Loaded");
			return;
		}
		dsp.setRemoveOutliers();
		ThreadJob newJob = new RemoveOutliersJob(dsp, stdDevs);
		jobqueue.add(newJob); //Add job to Job Queue
		this.getApp().addRemoveOutlier(dsp); //Inform MainApp that there is a data set having outliers removed.
		
	}


	public void shrinkDatset(DataSetProxy dsp, float percent){
		int newLength = (int) (dsp.getDataSize() * percent / 100);
		int[] members = new int[newLength];

		//This should pick exactly the newLength number of points with an equal distribution
		int currentPos = 0;
		for(int i = 0; i < dsp.getDataSize(); i++){
			float numNeeded = newLength - currentPos;
			float numLeft = dsp.getDataSize() - i;

			if(Math.random() < numNeeded / numLeft){
				members[currentPos] = i;
				currentPos++;
			}
		}

		DataSet ds = new SubsetData(dsp.getData(), members);
		DataSetProxy newdsp = new DataSetProxy();
		newdsp.setData(ds);
		getApp().nameDSP(newdsp,newLength +" of "+dsp.getDataSize() + " chosen",
								"Data Set");

	}

	/**
	 * Return the currently displayed Tab
	 * @return
	 */
	public Tab getCurrentTab(){
		return tabs.get(currentTab);
	}

	/**
	 * Select the given tab
	 * @param tabNum
	 */
	public void selectTab(int tabNum){
		if(tabNum != currentTab){
			if(currentTab >= 0 && currentTab < tabs.size()){
				tabs.get(currentTab).swapOutTab();
			}
			currentTab = tabNum;
			getApp().setCurrentTab(tabNum);
		}
	}

	/**
	 * Create a new blank tab as the last tab.
	 * TODO: In future make it appear after whatever tab requested it.
	 */
	public void newTab(){
		//swap out old tab
		getCurrentTab().swapOutTab();
		//add in new
		Tab newTab = new Tab(Tab.BLANKMODE, getApp());
		tabs.add(newTab); //TODO: make it a blank tab
		currentTab = tabs.size()-1;
		getApp().updateTabs();
		getApp().setCurrentTab(currentTab);

	}

	/**
	 * Close the given tab
	 * @param tabNum
	 */
	public void closeTab(int tabNum){
		if(tabs.get(tabNum).closeTab()) //allow the tab to cancel jobs and
		{								//dissallow being closed (it can prompt user and stuff)
			tabs.remove(tabNum);
			if(currentTab > tabNum){
				currentTab--;
			}
			if(currentTab > tabs.size() - 1){
				currentTab = tabs.size() - 1;
			}
			if(tabs.size() == 0){
				//TODO: NewTab should be made to handle this
				Tab newTab = new Tab(Tab.BLANKMODE, getApp());
				tabs.add(newTab);
				currentTab = 0;
				getApp().updateTabs();
				getApp().setCurrentTab(0);
			}else{
				getApp().updateTabs();
				getApp().setCurrentTab(currentTab);
			}
		}
	}

	/**
	 * Create a new tab (after the current tab) with a histogram with the given options.
	 * @param dsp
	 * @param dimension
	 * @param scaleType
	 */
	public void makeHistogram(DataSetProxy dsp, int dimension, int scaleType) {
		getCurrentTab().swapOutTab();
		Tab newTab = new Tab(Tab.HISTMODE, getApp());
		HistTabMediator htm = (HistTabMediator) newTab.getTabMediator();
		htm.setDataSet(dsp);
		htm.setDimension(dimension);
		htm.setScaleType(scaleType); //0 is linear, 1 is logarithmic
		//TODO: send data set and dimensions to tab
		tabs.add(currentTab+1, newTab); //place it after the tab that  made the call (I assume it's the current tab)
		currentTab++;
		getApp().updateTabs();
		getApp().setCurrentTab(currentTab);
	}

	/**
	 * Create a new tab (after the current tab) with a scatter with the given options.
	 * @param dsp
	 * @param dimension
	 * @param scaleType
	 */
	public void makeScatterPlot(DataSetProxy dsp, int xDim, int yDim) {
		getCurrentTab().swapOutTab();
		Tab newTab = new Tab(Tab.SCATTERMODE, getApp());
		ScatterTabMediator spm = (ScatterTabMediator) newTab.getTabMediator();
		spm.setDataSet(dsp);
		spm.setXDimension(xDim);
		spm.setYDimension(yDim);
		spm.setScaleType(0); //0 is linear, 1 is logarithmic
		//TODO: send data set and dimensions to tab
		tabs.add(currentTab+1, newTab); //place it after the tab that  made the call (I assume it's the current tab)
		currentTab++;
		getApp().updateTabs();
		getApp().setCurrentTab(currentTab);
	}


	public void makeWindRose(DataSetProxy dsp) {
		getCurrentTab().swapOutTab();
		Tab newTab = new Tab(Tab.WINDROSEMODE, getApp());
		WindRoseTabMediator wrm = (WindRoseTabMediator) newTab.getTabMediator();
		wrm.setDataSet(dsp);
		//spm.setXDimension(xDim);
		//spm.setYDimension(yDim);
		//spm.setScaleType(0); //0 is linear, 1 is logarithmic
		//TODO: send data set and dimensions to tab
		tabs.add(currentTab+1, newTab); //place it after the tab that  made the call (I assume it's the current tab)
		currentTab++;
		getApp().updateTabs();
		getApp().setCurrentTab(currentTab);
	}


	/**
	 * Returns the Tab object for the given tab number
	 * @param tab
	 * @return Requested Tab object
	 */
	public Tab getTab(int tab){
		return tabs.get(tab);
	}

	/**
	 * Returns the number of Tabs
	 * @return number of tabs
	 */
	public int numTabs(){
		return tabs.size();
	}
	
	/**
	 * Returns the requested DataSetProxy
	 * @param index
	 * @return the requested DataSetProxy
	 */
	public DataSetProxy getDSP(int index){
		if(dataSets.size() == 0){
			return null;
		}
		return dataSets.get(index);
	}

	/**
	 * Returns the number of DataSetProxies loaded
	 * @return number of DataSetProxies
	 */
	public int numDSPs(){
		return dataSets.size();
	}

	/**
	 * Returns the size of the requested data set
	 * @param index
	 * @return
	 */
	public int getDSPSize(int index){
		return dataSets.get(index).getDataSize();
	}

	/**
	 * Returns (and creates if needed) a combination data set of the selectedDataSets.
	 * @param selectedDataSets - True for those data sets that are selected.
	 * @return
	 */
	public DataSetProxy getDataSet(Boolean[] selectedDataSets){
		LinkedList<DataSetProxy> dataSets = new LinkedList<DataSetProxy>();
		for(int i = 0; i < selectedDataSets.length; i++){
			if(selectedDataSets[i]){
				dataSets.add(getDSP(i));
			}
		}
		System.out.println(dataSets.size() +" data set selected");
		if(dataSets.size() == 0){
			return null;
		}
		if(dataSets.size() == 1){
			return dataSets.get(0);
		}
		DataSetProxy newDSP;
		try {
			newDSP = new DataSetProxy();
			newDSP.setDataSet(new UnionData(dataSets));

		} catch (Exception ex) {
			newDSP = null;
			facade.sendNotification(ApplicationFacade.EXCEPTIONALERT, ex, null);
		}

		return newDSP;

	}

	public DataSetProxy getDataSet(DataSetProxy[] selectedDataSets){
		if(selectedDataSets.length == 1){
			return selectedDataSets[0];
		}
		DataSetProxy newDSP;
		try {
			newDSP = new DataSetProxy();
			newDSP.setDataSet(new UnionData(selectedDataSets));

		} catch (Exception ex) {
			newDSP = null;
			facade.sendNotification(ApplicationFacade.EXCEPTIONALERT, ex, null);
		}

		return newDSP;
	}


	/**
	 * If the given data set is currently in the list of "loaded data sets".
	 * @param dsp
	 * @return
	 */
	public boolean isInDataSets(DataSetProxy dsp){
		return dataSets.contains(dsp);
	}

	/**
	 * This is used to provide each mediator a unique name when asked for one
	 * @return
	 */
	public synchronized String generateMediatorKey(){
		currentMedNo++;
		return ""+currentMedNo;
	}
	
	public MainAppI getApp(){
		return (MainAppI) super.getViewComponent();
	}
	
}
