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

import java.util.concurrent.BlockingQueue;

/**
 * This thread performs the non-SOM jobs. This should eventually be combined with the SOM thread.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class jobThread implements Runnable{

	private BlockingQueue<ThreadJob> jobqueue;

	/**
	 * Create a new thread to do SOM jobs
	 * @param jobqueue
	 */
	public jobThread(BlockingQueue<ThreadJob> jobqueue){
		this.jobqueue = jobqueue;
	}

	@Override
	public void run() {
		while(true){ //We will indefinately look for new jobs
			//get next job (blocking wait)
			ThreadJob job = null;
			try {
				job = jobqueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//run job
			if(job != null){ //in case of exception or something
				if(job.getJobType() == job.LOADFILEJOB){
					LoadFileJob lfJob = (LoadFileJob) job;
					lfJob.dsp.loadDataFile();
				}
				if(job.getJobType() == job.REMOVEOUTLEIRSJOB){
					RemoveOutliersJob roJob = (RemoveOutliersJob) job;
					roJob.dsp.removeOutliers(roJob.stdDevs);
				}
			}
		}
	}

}
