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
import java.util.LinkedList;

/**
 *
 * @author kthayer
 */
public class SummaryDataPoint {
	protected DataSet dataset;
	protected String rawSetName;

	int numPoints;
	int total;

	//Eventually, each point will need actual number, data set from,
	//totals for different levels (can it get this from the data set structure?)
	// SOM used (with cluster), clustering options, dataset delta map

	public SummaryDataPoint(DataSet dataset, String rawSetName, int numPoints, int total){
		this.dataset = dataset;
		this.rawSetName = rawSetName;
		this.numPoints = numPoints;
		this.total = total;
	}

	protected float getPercent(){
		return ((float)numPoints) / total;
	}
}
