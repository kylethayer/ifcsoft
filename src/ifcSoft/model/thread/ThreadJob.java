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

/**
 * This is the interface for all job objects
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public interface ThreadJob {
	
	/**
	 * A job that just yields the CPU (to attempt to let the GUI update)
	 */
	public static final int YIELDJOB = -1;

	/**
	 * A job to compute an SOM
	 */
	public static final int COMPUTESOMJOB = 1;

	/**
	 * A job to compute a batch SOM
	 */
	public static final int COMPUTEBATCHSOMJOB = 2;

	/**
	 * A job to create the density map of an SOM (BMUs for data points)
	 */
	public static final int FINDBMUJOB = 10;

	/**
	 * A job to load a file.
	 */
	public static final int LOADFILEJOB = 20;

	/**
	 * A job to remove outliers from a data set.
	 */
	public static final int REMOVEOUTLEIRSJOB = 30;
	
	/**
	 * Returns the type of job this is.
	 * @return
	 */
	public int getJobType();
}
