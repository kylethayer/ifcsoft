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

package ifcSoft.view.synchDataSets;

import ifcSoft.MainApp;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogText;
import ifcSoft.view.dialogBox.ifcDialogHBox;
import ifcSoft.view.dialogBox.ifcDialogButton;
import ifcSoft.view.MainMediator;

/**
 * @author Kyle Thayer
 */

public class SynchDataSetDialog {
  public-init var mainMediator:MainMediator;
  public-init var mainApp:MainApp;
  public-init var allDimNames:String[];
  public-init var initialColNames:String[];
  public-init var okAction:function():Void;
  //input data set

  var synchTable:SynchDataTable;

  var synchDialog:ifcDialogBox =
    ifcDialogBox{
      name: "Synchronize Data Sets"
      content:[
        ifcDialogText{
          text:"The data sets selected have different dimension labels. (Note: This is only partially functional)"
        }
        //interactive table with what column names are equivalent
        synchTable = SynchDataTable{
          allDimNames:allDimNames
          initialColNames: initialColNames
        }
        //include column of unused


        //button for export synchronized
        //button for internally change orders
        //button for save new data set with these synched
        /*ifcDialogHBox{
            content:[
              ifcDialogButton{
                action: function():Void{}
                text:"Synchronize data sets"
              },
              ifcDialogButton{
                action: function():Void{}
                text:"Save new synchronized combined data set"
              },
              ifcDialogButton{
                action: function():Void{}
                text:"Export synchronized Data Sets"
              },
            ]
          }*/
      ]
      //okName: "Synchronize"
      okAction: function():Void{mainApp.removeDialog(synchDialog); okAction();}

      cancelName:"Close"
      cancelAction: function():Void{mainApp.removeDialog(synchDialog)}
    };


  /**
  * Create and display the SynchDataSetDialog
  */
  public function synchDataDialog():Void{
    if(mainMediator.getDSP(0) == null){
      mainApp.alert("No Data Set Loaded");
      return;
    }
    mainApp.addDialog(synchDialog);
  }


  public function getSynchCols():synchedColumn[]{
    var sc:synchedColumnFX[] = synchTable.getInput();
    var newCols:synchedColumn[] = [];
    for(col in sc){
      var newCol = new synchedColumn();
      newCol.colName = col.colName;
      newCol.sourceNames = col.sourceNames as nativearray of String;
      insert newCol into newCols;
    }

    println("SynchDataSetDialog returning length {newCols.size()}");
    if(newCols.size() > 0){
      return newCols;
    }else{
      return null;
    }

  }


  //var shrinkAmtInput:ifcDialogFloatInput;
  /*var dataSetSelect:ifcDialogDataSetSelect= ifcDialogDataSetSelect{
            mainApp:mainApp,
            okAction: function():Void{dataSetTable = getTable()}
            };
  var dataSetTable:ifcDialogDataTable = getTable();*/
  //var saveAsNewDS:ifcDialogRadioButtons;
  /*var dataSetViewerDlg:ifcDialogBox =
    ifcDialogBox{
      name: "Data Set Viewer"
      content:bind [
        dataSetSelect,
        ifcDialogHBox{
          content:[
            ifcDialogButton{
              text:"Save\nData Set"
              action:function(){saveDataSet();}
            },
            ifcDialogButton{
              text:"Remove\nOutliers"
              action: function(){mainApp.outliersDialog(getCurrentDataSet());}
            },
            ifcDialogButton{
              text:"Shrink\nData Set"
              action:function(){mainApp.shrinkDatasetDialog(getCurrentDataSet());}
            },
          ]
        },
        dataSetTable
        ]
      cancelName: "Close"
      cancelAction: function():Void{mainApp.removeDialog(dataSetViewerDlg)}
    };*/




  /**
  * Create and display the data Set Viewer.
  */
  /*public function dataSetViewer():Void{
    if(mainMediator.getDSP(0) == null){
      mainApp.alert("No Data Set Loaded");
      return;
    }
    mainApp.addDialog(dataSetViewerDlg);
  }*/

/*  public function getCurrentDataSet():DataSetProxy{
    var datasets = (dataSetSelect.getDataSets());
    if(datasets.size() == 0){
      mainApp.alert("No data set selected");
      mainApp.unblockContent();
      return null;
    }

    var finaldsp:DataSetProxy = mainMediator.getDataSet(dataSetSelect.getDataSets());
    if(finaldsp == null){
      println("Error in data set combination");
      return null;
    }

    return finaldsp;
  }*/



}
