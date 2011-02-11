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
import ifcSoft.view.dialogBox.ifcDialogFloatInput;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogRadioButtons;
import ifcSoft.model.DataSetProxy;

/**
 * @author kthayer
 */

public class ShrinkDataSetDialog {
  public-init var mainMediator:MainMediator;
  public-init var mainApp:MainApp;

  var shrinkAmtInput:ifcDialogFloatInput;
  var dataSetSelect:ifcDialogDataSetSelect;
  var saveAsNewDS:ifcDialogRadioButtons;
  var shrinkDatasetBoxDialog:ifcDialogBox =
    ifcDialogBox{
      name: "Shrink Data Set(s)"
      content:[
        dataSetSelect = ifcDialogDataSetSelect{mainApp:mainApp},
        shrinkAmtInput = ifcDialogFloatInput{
          name: "Keep what percent? "
          initialFloat: 10
        },
        /*saveAsNewDS = ifcDialogCheckBox{
          name: "Save As New Data Set"
          initialCheck: false
          defined: bind (dataSetSelect.getDataSets().size() == 1)
        }*/


      ]
      okAction: shrinkDatsetOK
      okName: "Shrink Data Set"
      cancelAction: shrinkDatsetCancel

      blocksMouse: true
      //disable: bind ReRemoveOutliersBoxDisabled;
    };

  

  function shrinkDatsetOK():Void{
    var finaldsp:DataSetProxy = mainMediator.getDataSet(dataSetSelect.getDataSets());

    mainMediator.shrinkDatset(finaldsp, shrinkAmtInput.getInput());

    mainApp.removeDialog(shrinkDatasetBoxDialog);
  }

  function shrinkDatsetCancel():Void{
  

    mainApp.removeDialog(shrinkDatasetBoxDialog);
  }


  /**
  * Create and display the Shrink Dataset dialog box.
  */
  public function shrinkDatasetDialog():Void{
    if(mainMediator.getDSP(0) == null){
      mainApp.alert("No Data Set Loaded");
      return;
    }
    mainApp.addDialog(shrinkDatasetBoxDialog);
  }

  
}
