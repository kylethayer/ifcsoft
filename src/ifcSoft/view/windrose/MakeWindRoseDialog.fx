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
package ifcSoft.view.windrose;

import ifcSoft.MainApp;
import ifcSoft.view.MainMediator;
import ifcSoft.model.DataSetProxy;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;

/**
 *  @author Kyle Thayer <kthayer@emory.edu>
 */

public class MakeWindRoseDialog {
  public-init var app:MainApp;
  public-init var mainMediator:MainMediator;
  postinit{
    if(app == null or mainMediator == null){
      println("MakeWindRose initializer: not initialized fully");
    }else{
      initialize();
    }
  }

  var windroseDialog:ifcDialogBox;
  var dataSetSelect:ifcDialogDataSetSelect;


  public function initialize(){
    dataSetSelect = ifcDialogDataSetSelect{mainApp:app};

    windroseDialog =  ifcDialogBox{
      name: "Make Wind Rose Plot"
      okAction: windroseOK
      content: [dataSetSelect]
      cancelAction: function():Void{app.removeDialog(windroseDialog)}

      blocksMouse: true
    };

    app.addDialog(windroseDialog);
  }

  function windroseOK():Void{

    var finaldsp:DataSetProxy = dataSetSelect.getDataSet();
    if(finaldsp == null){
      println("Error in data set combination");
      return;
    }

    mainMediator.makeWindRose(finaldsp);
  }

}
