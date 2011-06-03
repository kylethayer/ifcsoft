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

import ifcSoft.model.dataSet.DataSet;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

/**
 * @author kthayer
 */

public class ifcDialogDataTable extends ifcDialogItem{
  public var dataset:DataSet;


  var cols = bind dataset.getDimensions();
  var rows = bind dataset.length();
  
  init{
     makeTable();
  }

  function makeTable(){
    var numDataPnts = dataset.length();
    if(numDataPnts > 10){
      numDataPnts = 10;
    }

    children = HBox{
      spacing: 5
      content:[
        VBox{
          spacing: 3
          content:[
            Label{text: ""}
            for(j in [0..numDataPnts-1]){
              Label{text: dataset.getPointName(j)};
            }
          ]
        }
        for(i in [0..cols-1]){
          VBox{
            spacing: 3
            content:[
              Label{text:dataset.getColLabels()[i]},
              for(j in [0..numDataPnts-1]){
                Label{text: "{dataset.getVals(j)[i]}" };
              }
              ]
          }
        }
        ]
      };
  }

}
