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

import ifcSoft.MainAppI;
import ifcSoft.view.blankTab.BlankTabMediator;
import ifcSoft.view.histogram.HistTabMediator;
import ifcSoft.view.scatterplot.ScatterTabMediator;
import ifcSoft.view.windrose.WindRoseTabMediator;



/**
 * This class holds a Tab and allows the MainMediator to perform actions on the tab.
 *
 * NOTE: In order to prevent concurrency problems, tab switching must be done by the javafx
 * thread. Then any javafx function can assume that "currentTab" is actually the tab that was
 * active when the javafx function was triggered.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class Tab {


	//Would it be better to make all views inherit a type so that I can call one display fn?
	//Or will this have to hold extra stuff like other dialog boxes over the tab?

	//TODO: Add dialogs holder for tabs

	MainAppI app;

	//modes

	/**
	 * Blank tab (either load data button or options with the loaded data)
	 */
	public static final int BLANKMODE = 1; //if no data is loaded

	/**
	 * Self-Organizing Map
	 */
	public static final int SOMMODE = 2; //if it has a SOM graphed

	/**
	 * Histogram
	 */
	public static final int HISTMODE = 3; //if it is a histogram

	/**
	 * Scatter Plot
	 */
	public static final int SCATTERMODE = 4; //if it is a scatterplot

	public static final int WINDROSEMODE = 5; //if it is a wind rose

	private int currentMode;
	private TabMediator tabMed; //for future re-structuring

	
	/**
	 * Creates a Tab of the type "mode"
	 * @param mode
	 * @param app 
	 */
	public Tab(int mode, MainAppI app){
		this.app = app;
		this.currentMode = mode;
		switch(mode){
			case BLANKMODE:
				tabMed = new BlankTabMediator(app);
				break;
			case HISTMODE:
				tabMed = new HistTabMediator(app);
				break;
			case SCATTERMODE:
				tabMed = new ScatterTabMediator(app);
				break;
			case WINDROSEMODE:
				tabMed = new WindRoseTabMediator(app);
				break;

		}
	}

	/**
	 * Change the mode to "mode"
	 * @param mode
	 */
	public void changeMode(int mode){
		currentMode = mode;
	}

	/**
	 * Returns the current Mode
	 * @return
	 */
	public int getMode(){
		return currentMode;
	}

	/**
	 * Set the current mediator of the Tab to tm
	 * @param tm
	 */
	public void setTabMediator(TabMediator tm){
		tabMed = tm;
		app.updateTabs(); //I would update my own tab, but I don't know what number I am
	}

	/**
	 * Return the tab Mediator of the given tab.
	 * @return
	 */
	public TabMediator getTabMediator(){
		return tabMed;
	}

	//current Mode: NoData - Data, no graph - SOM - Hist - Scatter - WindRose
	//Current DSP ??? - This is saved in the SOM or whatever graph option
	//current display (SOM or other) object
	//Last SOM settings
	//undo/redo info
	//view mediators?

		//TODO: Each tab needs a thread (waiting on a job queue) so I can do tab-blocking dialogs.
			//Do I only allow one of these jobs at a time? Do I block the tab screen?
			//what about universal threads?

			//I don't really need it multi-threaded if I actually block the tab, do I?
			   //I still do if I want to return to the same point in my program logic
	/**
	 * Returns the name of the tab.
	 * @return
	 */
	public String getTabName(){
		return tabMed.getTabName();
	}

	/**
	 * Display the tab on the screen.
	 */
	public void displayTab(){
		tabMed.display();
	}

	/**
	 * Give the tabMediator the chance to save anything it needs to before being swapped out.
	 */
	public void swapOutTab(){
		tabMed.swapOutTab();
	}

	/**
	 * Tries to close the tab
	 * @return True if the tab allows itself to be closed
	 */
	public boolean closeTab(){
		return tabMed.closeTab();
	}

	/**
	 * Inform the tab that a new DSP has been loaded.
	 */
	public void informNewDsp() {
		tabMed.informNewDsp();
	}

	/**
	 *  Returns whether or not the dialog content should be blocked (for reloading tab).
	 * @return if the dialog content should be blocked
	 */
	public boolean isDialogContentBlocked(){
		return tabMed.isDialogContentBlocked();
	}
}
