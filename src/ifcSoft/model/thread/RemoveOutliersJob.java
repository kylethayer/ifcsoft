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
package ifcSoft.model.thread;

import ifcSoft.model.DataSetProxy;

/**
 * The job holder for removing outliers jobs.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class RemoveOutliersJob implements ThreadJob{

	/**
	 * The data set to remove outliers from.
	 */
	public DataSetProxy dsp;
	/**
	 * The number of standard deviations, above which points are removed.
	 */
	public double stdDevs;

	/**
	 * Create the job holder for a Remove Outliers job.
	 * @param dsp - The data set to remove outliers from.
	 * @param stdDevs - The number of standard deviations, above which points are removed.
	 */
	public RemoveOutliersJob(DataSetProxy dsp, double stdDevs){
		this.dsp = dsp;
		this.stdDevs = stdDevs;
	}

	/**
	 * Return that the job type is a "Remove Outliers" job.
	 * @return
	 */
	@Override
	public int getJobType() {
		return REMOVEOUTLEIRSJOB;
	}

}
