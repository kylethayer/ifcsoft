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
package ifcSoft.view.histogram;

import ifcSoft.MainApp;
import ifcSoft.view.MainMediator;
import ifcSoft.model.DataSetProxy;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;

/**
 *  @author Kyle Thayer <kthayer@emory.edu>
 */

public class MakeHistogramDialog {
  public-init var app:MainApp;
  public-init var mainMediator:MainMediator;
  postinit{
    if(app == null or mainMediator == null){
      println("CalcSOMDialog initializer: not initialized fully");
    }else{
      initialize();
    }
  }

  var histDialog:ifcDialogBox;
  var dataSetSelect:ifcDialogDataSetSelect;

  public function initialize(){
    dataSetSelect = ifcDialogDataSetSelect{mainApp:app};

    histDialog =  ifcDialogBox{
      name: "Make Histogram"
      okAction: histOK
      content: [dataSetSelect]
      cancelAction: function():Void{app.removeDialog(histDialog)}

      blocksMouse: true
    };

    app.addDialog(histDialog);
  }

  function histOK():Void{
    var datasets = (dataSetSelect.getDataSets());
    if(datasets.size() == 0){
      app.alert("No data set selected");
      app.unblockContent();
      return;
    }

    var finaldsp:DataSetProxy = mainMediator.getDataSet(dataSetSelect.getDataSets());
    if(finaldsp == null){
      println("Error in data set combination");
      return;
    }

    app.removeDialog(histDialog);
    mainMediator.makeHistogram(finaldsp, 0, 1); //at the moment 1st dimmension and log scale
  }
  
}
