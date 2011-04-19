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

import ifcSoft.model.DataSetProxy;
import java.lang.Exception;
import ifcSoft.MainApp;
import ifcSoft.view.MainMediator;
import javafx.util.Sequences;
import javafx.stage.Alert;
import ifcSoft.model.dataSet.summaryData.SummaryData;
import javafx.scene.control.ToggleGroup;

/**
 * @author kthayer
 */

public class ifcSumDataSelectBox extends ifcDialogBox {
  public-init var mainApp:MainApp;

  public-init var initialDataSet:SummaryData;

  var summDataSets:SummaryData[];

  var dataSetRadioButtons:ifcDialogRadioButton[];
  var newdataSetRadioButtons:ifcDialogRadioButton;
  var newDatasetNameInput:ifcDialogStringInput;

  var dataset:SummaryData;

  init{
    if(mainApp == null){ //This needs mainApp to be able to ask about data sets
      throw new Exception("ifcDialogDataSetSelect: mainApp must be initialized");
    }

    var mainMed:MainMediator = mainApp.getMainMediator();

    var alldatasets =
      for(i in [0..mainMed.numDSPs()-1]){
          mainMed.getDSP(i).getData();
      };

    for(summdataset in alldatasets[dataset | dataset instanceof SummaryData]){
      insert summdataset as SummaryData into summDataSets;
    }


    var toggleGroup = new ToggleGroup();
    dataSetRadioButtons =
      for(dataset in summDataSets){
            ifcDialogRadioButton{
              name:dataset.getName()
              initialCheck: (dataset == initialDataSet)
              toggleGroup: toggleGroup
            };
        };


    children = ifcDialogBox{
      name:"Select Data Set(s)"
      content:[
          dataSetRadioButtons,
          ifcDialogHBox{
            content:[
              newdataSetRadioButtons = ifcDialogRadioButton{
                name:"New Data Set"
                initialCheck: (dataSetRadioButtons.size() == 0 or initialDataSet == null)
                toggleGroup: toggleGroup
              },
              newDatasetNameInput = ifcDialogStringInput{
                initialString: "Summary Data"
                
              }

            ]
          }

        ]
      okAction:okPressed
      okName:okName
      cancelAction:cancelAction
      cancelName:cancelName
    };
  }



  function okPressed():Void{
    //make sure a data set is selected before returning
    for(i in [0..dataSetRadioButtons.size()-1]){
      if(dataSetRadioButtons[i].getInput()){
        dataset = summDataSets[i];
      }
    }
    if(newdataSetRadioButtons.ischecked){
      dataset = new SummaryData();
      var dsp:DataSetProxy = new DataSetProxy();
      dsp.setDataSet(dataset);
      dsp.setDataSetName(newDatasetNameInput.getInput());
      mainApp.mainMediator.addNewDSP(dsp);
    }

    
    okAction();
  }


  public function getDataSet():SummaryData{
    return dataset;
  }


}
