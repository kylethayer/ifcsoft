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
package ifcSoft.model.dataSet.summaryData;

import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.dataSet.DataSetMaskRemoved;
import java.util.LinkedList;

/**
 *
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SummaryData extends DataSet {

	//Columns are the clusters
	//each data point is a data set

	//data gets put in one cluster at a time,

	SummaryDataMap datamap;

	//since the hashmap re-orders these, I need to keep a separate list of
	//the datasets and clusters that are in order of appearing.
	LinkedList<String> clusters = new LinkedList<String>();
	LinkedList<DataSet> datasets = new LinkedList<DataSet>();

	
	public SummaryData(){
		super.name = "Summary Data";
		datamap = new SummaryDataMap();
	}

	public void addDataPt(SummaryDataPoint sdp, DataSet dataset, String cluster){
		datamap.add(sdp, dataset, cluster);

		if(!clusters.contains(cluster)){
			clusters.add(cluster);
		}

		if(!datasets.contains(dataset)){
			datasets.add(dataset);
		}

		mins = new float[getDimensions()];
		maxes = new float[getDimensions()];
		means = new double[getDimensions()];
		this.findstats();//TODO: this can be done much more efficiently
		
	}
	
	
		

	@Override
	public String[] getColLabels() {
		return clusters.toArray(new String[0]);
	}

	@Override
	public float[] getUnMaskedVals(int index) {
		DataSet ds = datasets.get(index);
		String[] colLabels = getColLabels();
		float[] weights = new float[colLabels.length];
		for(int i = 0; i < colLabels.length; i++){
			SummaryDataPoint sdp = datamap.get(ds, colLabels[i]);
			if(sdp != null){
				weights[i] = sdp.getPercent();
			}else{
				weights[i] = 0;
			}
		}

		return weights;
	}

	@Override
	public String getUnMaskedPointName(int index){
		DataSet ds = datasets.get(index);
		return ds.getName();
	}

	@Override
	public int UnMaskedLength() {
		return datasets.size();
	}

	@Override
	public int getDimensions() {
		return getColLabels().length;
	}

	@Override
	public String getUnMaskedPointSetName(int index) {
		return name;
	}

	@Override
	public LinkedList<DataSet> getParents() { //Should the parents be the data sets that are summarized here?
		return new LinkedList<DataSet>();
	}

	@Override
	public LinkedList<String> getRawSetNames() {
		LinkedList<String>temp = new LinkedList<String>();
		temp.add(name);
		return temp;
	}

	@Override
	public LinkedList<DataSet> getRawSets() {
		LinkedList<DataSet>temp = new LinkedList<DataSet>();
		temp.add(this);
		return temp;
	}

	@Override
	public void parentPointsRemoved(DataSet dataSet, DataSetMaskRemoved lastRemoved) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
