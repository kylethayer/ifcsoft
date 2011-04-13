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
package ifcSoft.view.dialogBox;

import javafx.scene.CustomNode;
import ifcSoft.model.DataSetProxy;
import java.lang.Exception;
import ifcSoft.MainApp;
import ifcSoft.view.MainMediator;
import javafx.util.Sequences;
import javafx.stage.Alert;

/**
 * @author kthayer
 */

public class ifcDialogDataSetSelectBox extends ifcDialogBox {
  public-init var mainApp:MainApp;

  public-init var initialDataSets:DataSetProxy[];

  var selectedDataSets:Boolean[];
  var dataSetCheckBoxes:ifcDialogCheckBox[];
  var selectAllButton:ifcDialogButton;

  init{
    if(mainApp == null){ //This needs mainApp to be able to ask about data sets
      throw new Exception("ifcDialogDataSetSelect: mainApp must be initialized");
    }

    var mainMed:MainMediator = mainApp.getMainMediator();

    selectedDataSets =
      for(i in [0..mainMed.numDSPs()-1]){
        if(Sequences.indexOf(initialDataSets, mainMed.getDSP(i)) >= 0){
          true;
        }else{
          false;
        }
      };


    dataSetCheckBoxes =
      for(i in [0..selectedDataSets.size()-1]){
            ifcDialogCheckBox{
              name:mainMed.getDSP(i).getDataSetName()
              initialCheck: selectedDataSets[i]
            };
        };

		selectAllButton = ifcDialogButton{
			action: selectAll
			text: "Select All"
		}


    children = ifcDialogBox{
      name:"Select Data Set(s)"
      content:[
				  selectAllButton,
          dataSetCheckBoxes
        ]
      okAction:okAction
      okName:okName
      cancelAction:cancelAction
      cancelName:cancelName
    };
  }

	function selectAll():Void{
		var someUnselected:Boolean = false;
		for(checkBox in dataSetCheckBoxes){
			if(not checkBox.getInput()){
				someUnselected = true;
			}
		}

		if(someUnselected){ //select all
			for(checkBox in dataSetCheckBoxes){
				checkBox.select();
			}
		}else{ //unselect all
			for(checkBox in dataSetCheckBoxes){
				checkBox.unSelect();
			}
		}



	}


  function okPressed():Void{
    //make sure a data set is selected before returning
    var isDataSetSelected:Boolean = false;
    for(i in [0..selectedDataSets.size()-1]){
      if(dataSetCheckBoxes[i].getInput()){
        isDataSetSelected = true;
      }
    }
    if(isDataSetSelected){
      okAction();
    }else{
      Alert.inform("No data set selected.");
      return
    }
  }


  public function getDataSets():DataSetProxy[]{
    var datasets:DataSetProxy[];
    for(i in [0..selectedDataSets.size()-1]){
      if(dataSetCheckBoxes[i].getInput()){
        insert mainApp.getMainMediator().getDSP(i) into datasets;
      }
    }
    return datasets;
  }


}
