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
package ifcSoft.model.som;

import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.som.jobs.ComputeBatchSOMJob;
import ifcSoft.model.som.jobs.ComputeStandardSOMJob;
import ifcSoft.model.som.jobs.FindMembershipsJob;
import ifcSoft.model.thread.ThreadJob;

import java.awt.Point;
import java.util.concurrent.BlockingQueue;
import org.puremvc.java.patterns.facade.Facade;

/**
 * This is a thread that does the SOM jobs.
 * TODO: Incorporate this into the universal job thread/queue system
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMThread implements Runnable {

	private BlockingQueue<ThreadJob> jobqueue;
	private Facade facade;
	
	/**
	 * Creates a new thread with a reference to the jobQueu
	 * @param jobqueue
	 */
	public SOMThread(BlockingQueue<ThreadJob> jobqueue, Facade facade){
		this.jobqueue = jobqueue;
		this.facade = facade;
	}

	/**
	 * Loops through the job queue, doing a job if there is one, blocking if not.
	 */
	@Override
	public void run() {
		while(true){
			ThreadJob job = null;
			try {
				job = jobqueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(job != null){ //in case of exception or something
				switch(job.getJobType()){
					case ThreadJob.YIELDJOB:
						Thread.yield();
						break;
					case ThreadJob.COMPUTESOMJOB:
						calculateStandardSOM((ComputeStandardSOMJob) job);
						break;
					case ThreadJob.COMPUTEBATCHSOMJOB:
						calculateBatchSOM((ComputeBatchSOMJob) job);
						break;
					case ThreadJob.FINDBMUJOB:
						findMemberships((FindMembershipsJob) job);
						break;
				}
			}
			
		}

	}

	
	private void calculateStandardSOM(ComputeStandardSOMJob job){

		SOMCalcFns.calculateSOM(job.iterations, job.som ,job.maxNeighborSize, job.minNeighborSize,
				job.SOMdata, job.iscanceled, jobqueue, facade);

	}

	private void calculateBatchSOM(ComputeBatchSOMJob job){

		SOMCalcFns.calculateBatchSOM(job);

	}

	
	

	private void findMemberships(FindMembershipsJob job) {
		if(job.iscanceled.get()){
			//paused needs to pass it to a paused list of jobs
			System.out.println("find membs canceled");
			return;
		}
		SOM som = job.som;
		if(job.ispaused.get()){
			som.insertPausedJob(job);
			return;
		}

		//DataSetScalar dataSetScalar = som.getDataSetScalar();

		DataSet dataSet;
		boolean isOtherDataSet = false;
		if(job.dataSetNum == -1){
			dataSet = som.getDataSet();
		}else{
			isOtherDataSet = true;
			dataSet = som.getDataSet(job.dataSetNum);
		}
		 
		int firstPt = job.firstPt;
		int lastPt = job.lastPt;
		
		//for each data member, find it's location (BMU)
		for(int i = firstPt; i <= lastPt; i++){
			//either do log or scaled weights
			Point bmu;
			double dist;

			float[] scaledPoint = som.scalePoint(dataSet.getVals(i));

			bmu = SOMHelperFns.findBMU(scaledPoint, som);
			dist = SOMHelperFns.findeuclidD(scaledPoint, som.SOMnodes[bmu.x][bmu.y].getWeights(), som);
			dist = Math.sqrt(dist);
		
			//update the membership array and the density Map
			if(isOtherDataSet){
				som.setOtherDataSetMember(job.dataSetNum, i, bmu, dist);
			}else{
				som.setMember(i, bmu, dist);
			}
		}
	}


	

}
