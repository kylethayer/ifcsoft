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
package ifcSoft.view.blankTab;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import ifcSoft.MainApp;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;


/**
 * This handles a "blank tab" with file loading and basic options.
 * @author Kyle Thayer <kthayer@emory.edu>
 */

public class BlankTab extends BlankTabI{
  
  /**
   * This variable must be initialized for it to work
   */
  public var app:MainApp;

  postinit{
    if(app == null){
      println("BlankTab initializer: not initialized fully");
    }
  }

  override public function informNewDsp(){
    displayData();
  }


  override public function displayTab() {
    //if() who holds the Dsps?
    if(app.getMainMediator().dataSets.size() == 0){
      displayNoData();
    }else{
      displayData();
    }
  }

  function displayNoData(){
    var display =  Group{
      content: [
        Group{
          layoutX: bind app.contentWidth/2 - 50
          layoutY: bind app.contentHeight/2 - 20
          content: [
            Rectangle {
              width: 200
              height : 80
              x: -15 y: -35
              onMouseClicked: function(event){app.loadFile();}

            },
            Text {
              content: "< no data loaded >\nClick  to load data"
              font: Font {name: "Arial" size: 20}
              fill: Color.WHITE
            }
          ]


        }
      ]
    }
    app.setMainContent(display);
  }

  function displayData(){
    var display = Group{
      content: [
        Group{
          layoutX: bind app.contentWidth/2 - 70
          layoutY: bind app.contentHeight/2 - 50
          content: [
            VBox{
              spacing: 10
              content: [
                Text {
                  content: "Data is loaded.\n(later this will show the data sets)"
                  font: Font {name: "Arial" size: 14}
                  fill: Color.WHITE
                }
                Text {
                  content: "Options:"
                  font: Font {name: "Arial" size: 12}
                  fill: Color.WHITE
                }
                Button{
                  text: "Load Data"
                  action:  function(){}
                  //Use clicked so that the click doesn't hit the new popup window
                  onMouseClicked:function(e: MouseEvent){app.loadFile();}
                },
                Button{
                  text: "View\nData Set"
                  action:  function(){}
                  //Use clicked so that the click doesn't hit the new popup window
                  onMouseClicked:function(e: MouseEvent){app.viewDatasetDialog();}
                },
                Button{
                  text: "Calculate SOM"
                  blocksMouse: false
                  action:  function(){}
                  //Use clicked so that the click doesn't hit the new popup window
                  onMouseClicked:function(e: MouseEvent){app.SOM();}
                },
                Button{
                  text: "Make Histogram"
                  blocksMouse: false
                  action:  function(){}
                  //Use clicked so that the click doesn't hit the new popup window
                  onMouseClicked:function(e: MouseEvent){app.histogramDlg();}
                }
                Button{
                  text: "Make Scatter Plot"
                  blocksMouse: false
                  action:  function(){}
                  //Use clicked so that the click doesn't hit the new popup window
                  onMouseClicked:function(e: MouseEvent){app.scatterplotDlg();}
                }
                Button{
                  text: "Make Wind-Rose Plot"
                  blocksMouse: false
                  action:  function(){}
                  //Use clicked so that the click doesn't hit the new popup window
                  onMouseClicked:function(e: MouseEvent){app.windrosePlotDlg();}
                }
              ]
            }
          ]


        }
      ]
    }
    app.setMainContent(display);
  }
}
