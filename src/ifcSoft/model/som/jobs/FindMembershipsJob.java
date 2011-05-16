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

import ifcSoft.model.som.SOM;

/**
 * The job holder for finding membership jobs.
 * It only operates on a subset of the data points (so that each
 * data set membership job is split over multiple jobs and thus
 * multiple threads).
 *
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class FindMembershipsJob implements ThreadJob {
  
  /**
   * Which data set needs members found on.
   * -1 means the data set the SOM was made with.
   * >= 0 means that data set in the other data sets stored in the SOM.
   */
  public int dataSetNum;
  /**
   * The SOM to find the membership on.
   */
  public SOM som;
  /**
   * Starting point in the data set to find membership of.
   */
  public int firstPt;
  /**
   * Ending point in the data set to find membership of.
   */
  public int lastPt;
  /**
   * A variable that can be set to cancel the job.
   */
  public AtomicBoolean iscanceled; //the main thread can cancel a job by changing this object
  /**
   * A variable that can be set to pause the job.
   */
  public AtomicBoolean ispaused; //the main thread can cancel a job by changing this object
  
  /**
   * Create a Find Membership Job holder
   * @param dataSetNum - Which data set needs members found on.
   *          -1 means the data set the SOM was made with.
   *          >= 0 means that data set in the other data sets stored in the SOM.
   * @param som - The SOM to find the membership on.
   * @param firstPt - Starting point in the data set to find membership of.
   * @param lastPt - Ending point in the data set to find membership of.
   * @param iscanceled - A variable that can be set to pause the job.
   * @param ispaused - A variable that can be set to pause the job.
   */
  public FindMembershipsJob(int dataSetNum, SOM som, int firstPt, int lastPt, AtomicBoolean iscanceled, AtomicBoolean ispaused){
    this.dataSetNum = dataSetNum;
    this.som = som;
    this.firstPt = firstPt;
    this.lastPt = lastPt;
    this.iscanceled = iscanceled;
    this.ispaused = ispaused;
  }
  
  /**
   * It is a Find Membership Job
   * @return
   */
  @Override
  public int getJobType() {
    return ThreadJob.FINDBMUJOB;
  }

}
