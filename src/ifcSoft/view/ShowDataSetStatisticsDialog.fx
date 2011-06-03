/**
 *  Copyright (C) 2011   IFCSoft project
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
import ifcSoft.model.DataSetProxy;
import ifcSoft.view.dialogBox.ifcDialogText;
import ifcSoft.view.dialogBox.ifcDialogHBox;
import ifcSoft.view.dialogBox.ifcDialogVBox;


public class ShowDataSetStatisticsDialog {
  public-init var mainApp:MainApp;
  public-init var dsp:DataSetProxy = null;

  var ShowStatisticsBoxDialog:ifcDialogBox;

  function ShowStatisticsOK():Void{
   
    mainApp.removeDialog(ShowStatisticsBoxDialog);
  }

  /**
  * Create and display the Show Statistics dialog box.
  */
  public function ShowDataSetStatisticsDialog():Void{
    if(dsp == null){
      mainApp.alert("No Data Selected");
      return;
    }
    ShowStatisticsBoxDialog =
      ifcDialogBox{
        name: "Data Statistics"
        content:[
          ifcDialogText{text: dsp.getDataSetName()}
          ifcDialogHBox{
            content:[
              ifcDialogVBox{
                content:[
                  ifcDialogText{text: " "}
                  ifcDialogText{text: "Min"}
                  ifcDialogText{text: "Max"}
                  ifcDialogText{text: "Mean"}
                  ifcDialogText{text: "StdDev"}

                ]
              }
              for(i in [0..dsp.getDimensions()-1]){
                ifcDialogVBox{
                  content:[
                    ifcDialogText{text:dsp.getColNames()[i]},
                    ifcDialogText{text:"{dsp.getData().getMin(i)}"}
                    ifcDialogText{text:"{dsp.getData().getMax(i)}"}
                    ifcDialogText{text:"{dsp.getData().getMean(i)as Float}"}
                    ifcDialogText{text:"{dsp.getData().getStdDev(i)as Float}"}


                    ]
                }
              }
              ]
            }
          ]


        okAction: ShowStatisticsOK
        okName: "Close"

        blocksMouse: true
      };
    mainApp.addDialog(ShowStatisticsBoxDialog);
  }



}
