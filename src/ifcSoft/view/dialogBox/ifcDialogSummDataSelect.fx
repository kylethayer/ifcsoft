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

import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.control.OverrunStyle;
import ifcSoft.MainApp;
import java.lang.Exception;
import ifcSoft.view.MainMediator;
import ifcSoft.model.DataSetProxy;
import ifcSoft.model.dataSet.summaryData.SummaryData;
import ifcSoft.view.dialogBox.ifcDialogSummDataSelectBox.ifcSumDataSelectBox;

/**
 * @author Kyle Thayer <kthayer@emory.edu>
 */

public class ifcDialogSummDataSelect extends ifcDialogItem{
  public-init var mainApp:MainApp;
  public-init var dataset:SummaryData = null;
  
  var datasetName:String = bind if(dataset == null){""}else{"{dataset.getName()}"};

  var dataSetSelectDialog: ifcSumDataSelectBox;

  public-init var openAction:function():Void;
  public-init var okAction:function():Void;
  public-init var cancelAction:function():Void;


  init{
    if(mainApp == null){ //This needs mainApp to be able to ask about data sets and display the
      throw new Exception("ifcDialogDataSetSelect: mainApp must be initialized");
    }


    if(dataset == null){
      //var mainMed:MainMediator = mainApp.getMainMediator();
      //datasets = mainMed.getDSP(mainMed.numDSPs()-1);
    }


    children =
      HBox{
        spacing:3
        content:[
          ifcDialogButton{text:"Data Set(s)" action: selectDataSetDialog}
          Label {text: bind datasetName
              layoutInfo: LayoutInfo { width: 150 }
              textOverrun: OverrunStyle.ELLIPSES
              },
        ]
      }
  }



  function selectDataSetDialog(){
   //make datasets thingy
   dataSetSelectDialog =
      ifcSumDataSelectBox{
        mainApp:mainApp
        initialDataSet:dataset
        okAction:selectDataSetOK
        cancelAction:function():Void{mainApp.removeDialog(dataSetSelectDialog); cancelAction()}
        blocksMouse: true
        //disable: bind ReRemoveOutliersBoxDisabled;
        layoutX: bind (mainApp.contentWidth - 200) / 2
        layoutY: bind (mainApp.contentHeight - 125) / 2
      }
    mainApp.addDialog(dataSetSelectDialog);
    openAction();
  }

  function selectDataSetOK():Void{
    //get the data set
    dataset = dataSetSelectDialog.getDataSet();
    mainApp.removeDialog(dataSetSelectDialog);
    okAction();
  }


  public function getDataSet():SummaryData{
    return dataset;
  }


}
