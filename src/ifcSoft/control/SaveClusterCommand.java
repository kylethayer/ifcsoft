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
package ifcSoft.control;

import java.awt.Point;

import ifcSoft.MainAppI;
import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.SubsetData;
import ifcSoft.model.som.SOMProxy;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

/**
 * Save a cluster as a new data set.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SaveClusterCommand  extends SimpleCommand{
	
	
	/**
	 * Save a cluster as a new data set.
	 * @param note - Arg 1: DataSetProxy, Arg 2: Cluster (boolean[][])
	 */
	@Override 
	public void execute(INotification note){
		
		Object[] noteData = (Object []) note.getBody();
		MainAppI mainApp = (MainAppI) noteData[0];
		SOMProxy SOMp = (SOMProxy) noteData[1];
		boolean[][] cluster = (boolean[][]) noteData[2];
		
		//Need to wait until it is done getting density
		
		int[][] denseMap = SOMp.getDensityMap();
		int membSize = 0;
		for(int i = 0; i < cluster.length; i++){
			for(int j = 0; j < cluster[0].length; j++){
				if(cluster[i][j]){ //if it is in the cluster
					membSize += denseMap[i][j]; //add the number we need
				}
			}
		}
		
		
		if(membSize == 0){
			String msg = "No data points in cluster.";
			facade.sendNotification(ifcSoft.ApplicationFacade.STRINGALERT, msg, null);
			return;
		}


		int[] membMap = new int[membSize];
		int memPos = 0; 
		//add all the points
		for(int i = 0; i < cluster.length; i++){
			for(int j = 0; j < cluster[0].length; j++){
				if(cluster[i][j]){ //if it is in the cluster
					int cellMembs[] = SOMp.getCellMembers(new Point(i,j));
					if(cellMembs.length != denseMap[i][j]){
						System.out.println("Error: SOMofCluster, Densemap:"+denseMap[i][j]+
								" cellMembers:"+ cellMembs.length);
					}
					for(int k = 0; k < denseMap[i][j]; k++){
						membMap[memPos] = cellMembs[k];
						memPos++;
					}
				}
			}
		}
		
		
		//membSize

		String msg = membSize + " of "+ SOMp.getDataSet().length()+" data points selected";
		//facade.sendNotification(ifcSoft.ApplicationFacade.STRINGALERT, msg, null);
		
		//we should now have the membership map
		DataSet newDataSet = (DataSet) new SubsetData(SOMp.getDataSet(), membMap);
		DataSetProxy newdsp = new DataSetProxy();
		newdsp.setDataSet(newDataSet);

		mainApp.nameDSP(newdsp, msg, "Cluster");
		

	}
}
