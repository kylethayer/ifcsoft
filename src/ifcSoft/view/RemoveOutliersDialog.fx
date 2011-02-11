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
package ifcSoft.view;

import ifcSoft.MainApp;
import ifcSoft.model.DataSetProxy;
import javafx.stage.Alert;
import java.util.LinkedList;
import ifcSoft.model.dataSet.UnionData;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogFloatInput;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;

/**
 * This is the JavaFX file that makes the Remove Outliers dialog box.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class RemoveOutliersDialog {
	public-init var mainMediator:MainMediator;
	public-init var mainApp:MainApp;
	

	//var ReRemoveOutliersBoxDisabled:Boolean = false;

	var stdDevInput:ifcDialogFloatInput;
	var dataSetSelect:ifcDialogDataSetSelect;
	var newRemoveDialogBoxDialog:ifcDialogBox =
		ifcDialogBox{
			name: "Remove Outliers"
			content:[
				dataSetSelect = ifcDialogDataSetSelect{mainApp:mainApp},
				stdDevInput = ifcDialogFloatInput{
					name: "Standard Devs "
					initialFloat: 6
				},
			]
			okAction: removeOutliersOK
			okName: "Remove Outliers"
			cancelAction: newRemoveOutliersCancel

			blocksMouse: true
			//disable: bind ReRemoveOutliersBoxDisabled;
		};

	function removeOutliersOK():Void{

		var finaldsp:DataSetProxy = mainMediator.getDataSet(dataSetSelect.getDataSets());

		if(finaldsp.getData().getChildren().size() > 0){ //if anything depends on it
			if(Alert.question("Warning: Other data sets (eg. Unions, Subsets) may depend on this data set.\n\n"
							"Do you wish to save make a new copy to remove outliers from?\n\n"
							"If not, all dependent data sets will be removed and you may get errors with histograms and SOMs depending on those data sets"))
									{
				var datasets = new LinkedList();
				datasets.add(finaldsp);
				finaldsp = new DataSetProxy();
				finaldsp.setData( new UnionData(datasets));
				finaldsp.setDataSetName("{(datasets.get(0) as DataSetProxy).getDataSetName()} removed outliers")
			}else{
				mainMediator.removeDataSetChildren(finaldsp.getData());
			}
		}


		mainApp.removeDialog(newRemoveDialogBoxDialog);

		mainMediator.removeOutliers(stdDevInput.getInput(), finaldsp);
	}

	function newRemoveOutliersCancel():Void{
		mainApp.removeDialog(newRemoveDialogBoxDialog);
	}


	/**
	* Create and display the Remove Outliers dialog box.
	*/
	public function outliersDialog():Void{
		if(mainMediator.getDSP(0) == null){
			mainApp.alert("No Data Set Loaded");
			return;
		}
		mainApp.addDialog(newRemoveDialogBoxDialog);
	}

}
