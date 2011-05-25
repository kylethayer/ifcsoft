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
package ifcSoft.view.scatterplot;

import ifcSoft.MainApp;
import ifcSoft.view.MainMediator;
import ifcSoft.model.DataSetProxy;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;
import ifcSoft.view.dialogBox.ifcDialogChoiceBox;

/**
 *  @author Kyle Thayer <kthayer@emory.edu>
 */

public class MakeScatterDialog {
  public-init var app:MainApp;
  public-init var mainMediator:MainMediator;
  postinit{
    if(app == null or mainMediator == null){
      println("MakeScatterDialog initializer: not initialized fully");
    }else{
      initialize();
    }
  }

  var scatterDialog:ifcDialogBox;
  var dimensions:String[];
  var dataSetSelect:ifcDialogDataSetSelect;
  var datasets:DataSetProxy[] = bind dataSetSelect.getDataSets();
  var dataset1:DataSetProxy = bind datasets[0] on replace{
    dimensions = dataset1.getColNames();
    };


  var dim1Input:ifcDialogChoiceBox;
  var dim2Input:ifcDialogChoiceBox;
  


  public function initialize(){
    dataSetSelect = ifcDialogDataSetSelect{mainApp:app};

    dim1Input = ifcDialogChoiceBox{
      name:"X-Axis"
      items: bind dimensions
    };
    dim2Input = ifcDialogChoiceBox{
      name:"Y-Axis"
      items: bind dimensions
      initialSelectedItem:dimensions[1]
    };

    scatterDialog =  ifcDialogBox{
      name: "Make Scatter Plot"
      okAction: scatterOK
      content: [dataSetSelect,dim1Input, dim2Input ]
      cancelAction: function():Void{app.removeDialog(scatterDialog)}

      blocksMouse: true
    };

    app.addDialog(scatterDialog);
  }

  function scatterOK():Void{
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

    var xDim:Integer = dim1Input.getInputNumber();
    var yDim:Integer = dim2Input.getInputNumber();

    mainMediator.makeScatterPlot(finaldsp, xDim, yDim);
  }






}