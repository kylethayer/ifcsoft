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
package ifcSoft.model.som.jobs;

import ifcSoft.model.thread.ThreadJob;
import java.util.concurrent.atomic.AtomicBoolean;

import ifcSoft.model.dataSet.dataSetScalar.DataSetScalar;
import ifcSoft.model.som.SOM;

/**
 * A Compute SOM Job holder.
 * The job is to computer the SOM based on the given values.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class ComputeStandardSOMJob implements ThreadJob {
    
  /**
   * The data set to make the SOM with.
   */
  public DataSetScalar SOMdata;
  /**
   * The number of iterations to be used.
   */
  public int iterations;
  /**
   * The maximum Neighborhood size to be used.
   */
  public int maxNeighborSize;

  /**
   * The min Neighborhood size to be used.
   */
  public int minNeighborSize;
  /**
   * The SOM object to build the SOM in.
   */
  public SOM som;
  /**
   * A variable allowing the SOM building to be canceled.
   */
  public AtomicBoolean iscanceled; //the main thread can cancel a job by changing this object
  
  /**
   * Constructor for the ComputeSOMJob
   * @param SOMdata - The number of iterations to be used.
   * @param iterations - The number of iterations to be used.
   * @param maxNeighborSize - The maximum Neighborhood size to be used.
   * @param som - The SOM object to build the SOM in.
   * @param iscanceled - A variable allowing the SOM building to be canceled.
   */
  public ComputeStandardSOMJob(DataSetScalar SOMdata, int iterations, int maxNeighborSize, int minNeighborSize, SOM som, AtomicBoolean iscanceled){
    this.SOMdata = SOMdata;
    this.maxNeighborSize = maxNeighborSize;
    this.minNeighborSize = minNeighborSize;
    this.som = som;
    this.iterations = iterations;
    this.iscanceled = iscanceled;
  }
  
  /**
   * It is a Computer SOM Job
   * @return
   */
  @Override
  public int getJobType() {
    return ThreadJob.COMPUTESOMJOB;
  }

}
