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
package ifcSoft.view.histogram;

import ifcSoft.model.histogram.Histogram;
import ifcSoft.MainAppI;
import ifcSoft.model.DataSetProxy;
import ifcSoft.view.TabMediator;

/**
 * This is the mediator between the Histogram object and the Histogram view.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class HistTabMediator implements TabMediator {

	HistTabI histTab;
	DataSetProxy dsp;
	int dim;
	Histogram hist;

	/**
	 * The constructor. It needs access to the MainApp so it can draw and such.
	 * @param app
	 */
	public HistTabMediator(MainAppI app){
		histTab = app.makeHistTab(this);
	}

	@Override
	public void display() {
		System.out.println("dimension is "+dim);
		histTab.displayTab();
	}

	@Override
	public void swapOutTab() {
		histTab.swapOutTab();
	}

	@Override
	public String getTabName() {
		return "Histogram";
	}

	@Override
	public float getTabProgress() {
		return 100; //for now I don't thread the histogram progress
	}

	@Override
	public void informNewDsp() {
		histTab.informNewDsp();
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
		histTab.setDataSet(dsp);
	}

	/**
	 * Set which dimension is to be drawn.
	 * @param dim
	 */
	public void setDimension(int dim){
		this.dim = dim;
		histTab.setDimension(dim);
	}

	/**
	 * Set which scale type is to be used.
	 * @param scaleType
	 */
	public void setScaleType(int scaleType){
		histTab.setScaleType(scaleType);
	}





	/************  Display Functions *****************/

	/**
	 * This makes a Histogram with the number of segments and scale type.
	 * @param numPieces
	 * @param scaleType
	 * @return
	 */
	public Histogram divideHistogram(int numPieces, int scaleType){
		System.out.println("making histogram");
		hist = new Histogram(dsp, dim, numPieces, scaleType);
		return hist;
	}

}
