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

import ifcSoft.ApplicationFacade;
import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.som.SOMProxy;
import ifcSoft.view.fileFilter.CSVFileFilter;
import ifcSoft.view.fileFilter.FCSFileFilter;
import ifcSoft.view.fileFilter.DataFileFilter;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javafx.stage.Alert;
import javax.swing.JFileChooser;
import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

/**
 * Save a cluster to a file.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SaveClusterToFileCommand extends SimpleCommand {
	/**
	 * Save a cluster to a file.
	 * @param note - Arg 1: DataSetProxy, Arg 2: Cluster (boolean[][])
	 */
	@Override  
	public void execute(INotification note){

		Object[] noteData = (Object []) note.getBody();
		SOMProxy SOMp = (SOMProxy) noteData[0];
		boolean[][] cluster = (boolean[][]) noteData[1];

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

		
		System.out.println("Membership size: "+ membSize);
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


		//go through the members saving them to disk
		String selectedFile;

		JFileChooser fileChooser = new JFileChooser();
		CSVFileFilter filter = new CSVFileFilter();
		//FCSFileFilter filter2 = new FCSFileFilter();
		//DataFileFilter filter3 = new DataFileFilter();
		fileChooser.setFileFilter(filter);
		//fileChooser.setFileFilter(filter2);
		//fileChooser.setFileFilter(filter3);
		System.out.println("About to start the fileChooser dialog");
		if(fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION){
			return;
		}



		System.out.println("approved!");
		selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
		if(!selectedFile.endsWith(".csv")){
			selectedFile += ".csv";
		}

		DataSet dataSet = SOMp.getDataSet();
		LinkedList<String> rawSetNames = dataSet.getRawSetNames();
		boolean saveNames = false;
		if(rawSetNames.size() > 1){
			if(Alert.question("Do you want to include which file each data point is from?")){
				saveNames = true;
			}
			//alert: "Do you want to include which file each data point is from?
			//set a boolean or something for that
		}
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(selectedFile));
			//write labels
			if(saveNames){
				bw.write("File,");
			}
			for(int i = 0; i < dataSet.getDimensions(); i++ ){
				bw.write(dataSet.getColLabels()[i]);
				if(i != dataSet.getDimensions() - 1){
					bw.write(",");
				}else{

					bw.newLine();
				}
			}
			//write the data

			for(int i = 0; i < membMap.length; i++){
				float[] dataPt = dataSet.getVals(membMap[i]);
				if(saveNames){
					bw.write(dataSet.getPointSetName(membMap[i])+",");
				}
				for(int k = 0; k < dataSet.getDimensions(); k++){
					bw.write(""+dataPt[k]);
					if(k != dataSet.getDimensions() - 1){
						bw.write(",");
					}else{
		
						bw.newLine();
					}
				}
			}
			bw.close();

		} catch (IOException ex) {
			facade.sendNotification(ApplicationFacade.EXCEPTIONALERT, ex, null);
		}


		String msg = membSize + " of "+ SOMp.getDataSet().length()+" cells saved to file";
		facade.sendNotification(ifcSoft.ApplicationFacade.STRINGALERT, msg, null);


	}
}
