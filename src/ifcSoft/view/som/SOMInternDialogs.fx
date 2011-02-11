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
package ifcSoft.view.som;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.Group;
import javafx.scene.text.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.HPos;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import ifcSoft.MainApp;


/**
 * These are functions for SOM dialog boxes that could be loaded when an SOM is displayed
 * This consists currently of the:
 *     SOM progress display
 *  @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMInternDialogs {
  public-init var app:MainApp;
  public-init package var mediator:SOMMediator;
  postinit{
    if(app == null or mediator == null){
      println("SOMInternDialogs initializer: not initialized fully");
    }
  }

  package var statusdialog: Group;

  public var progress = 0;
  
  public function SOMstarted(): Void{
    progress=0;
    statusdialog = 
      Group{
        blocksMouse: true
        layoutX: bind (app.contentWidth - 200) / 2
        layoutY: bind (app.contentHeight - 150) / 2
        content: [
          Rectangle {
            width: 200
            height: 100
            arcWidth: 20  arcHeight: 20
            stroke: Color.LIGHTGRAY;
            fill: Color.rgb(138,123,102);
            x: 0
            y: 0
          }
          VBox{
            hpos: HPos.CENTER
            translateX: 10
            translateY: 10
            spacing: 10
            content:[
              Text {content: bind "SOM Progress: {progress}%"}
              ProgressBar {progress: bind progress / 100.0 }
              Button{
                action:  function():Void{CancelSOM();}
                text: "Cancel"  
              }
            ]
          }
        ]
      };
              
    app.addDialog(statusdialog);
    
    checkprogress();
    
  }
  
  function CancelSOM(){
    mediator.cancelSOM();
    
    app.removeDialog(statusdialog);
  }
  
  public function checkprogress(){
    var t;
    var timeline = Timeline{
      repeatCount: 1
      keyFrames: KeyFrame{
          time: 30ms
          action: function():Void{mediator.checkProgress();}
      
        }
    };
    timeline.play();
  }
};


