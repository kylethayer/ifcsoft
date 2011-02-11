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
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;
import ifcSoft.view.dialogBox.ifcDialogDataTable;

/**
 * @author kthayer
 */

public class DataSetViewer {
  public-init var mainMediator:MainMediator;
  public-init var mainApp:MainApp;


  //var shrinkAmtInput:ifcDialogFloatInput;
  var dataSetSelect:ifcDialogDataSetSelect= ifcDialogDataSetSelect{
            mainApp:mainApp,
            okAction: function():Void{dataSetTable = getTable()}
            };
  var dataSetTable:ifcDialogDataTable = getTable();
  //var saveAsNewDS:ifcDialogRadioButtons;
  var dataSetViewerDlg:ifcDialogBox =
    ifcDialogBox{
      name: "Data Set Viewer"
      content:bind [
        dataSetSelect,
        dataSetTable
        ]
      cancelName: "Close"
      cancelAction: function():Void{mainApp.removeDialog(dataSetViewerDlg)}
    };

  function  getTable():ifcDialogDataTable{
    return ifcDialogDataTable{
          dataset: dataSetSelect.getDataSets()[0].getData()
          }
  }


  /**
  * Create and display the data Set Viewer.
  */
  public function dataSetViewer():Void{
    if(mainMediator.getDSP(0) == null){
      mainApp.alert("No Data Set Loaded");
      return;
    }
    mainApp.addDialog(dataSetViewerDlg);
  }
}
