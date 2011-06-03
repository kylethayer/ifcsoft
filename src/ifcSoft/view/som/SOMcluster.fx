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
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;

import ifcSoft.MainApp;
import ifcSoft.view.som.SOMvc;


/**
 *  @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMcluster {
  
  public-init var app:MainApp;
  public-init var mediator:SOMMediator;
  public-init var somvc:SOMvc;
  postinit{
    if(app == null or mediator == null or somvc == null){
      println("SOMcluster initializer: not initialized fully");
    }
  }

  var offsetForMakeDlgBoxVisibleHack = .1;

    
  var clusterOpt:Group;
  var toleranceScroll:ScrollBar = ScrollBar{
    min:0
    max:.5
    vertical: false
    value: 0
    width: 150
    visibleAmount: .1
    blockIncrement:.05
    clickToPosition: false
    };

  
  var clusterTypes:HBox;
  public var clusterType:Number = mediator.ECLUSTER on replace oldValue{
      mediator.changeClusterType(tolerance, clusterType);
    };
  var clusterRB1:RadioButton;
  var clusterRB2:RadioButton;
  var clusterRB3:RadioButton;
  var clustertemp1 = bind clusterRB1.selected on replace oldValue{
    if( clustertemp1 == true and oldValue == false){
      //if the value changed
      clusterType = 1;
    }
  };
  var clustertemp2 = bind clusterRB2.selected on replace oldValue{
    if( clustertemp2 == true and oldValue == false){
      //if the value changed
      clusterType = 2;
    }
  };
  
  var clustertemp3 = bind clusterRB3.selected on replace oldValue{
    if( clustertemp3 == true and oldValue == false){
      //if the value changed
      clusterType = 3;
    }
  };
  
  public var tolerance:Number = bind toleranceScroll.value on replace oldValue{
    if( tolerance != oldValue){
      //if the value changed
      mediator.changeThreshhold(tolerance, clusterType);
    }
  };


  
  public function clustOpt():Void{
    if(clusterTypes == null){ //the first time it must be initialized
      var tempE = false;
      var tempME = false;
      var tempU = false;
      if(clusterType == mediator.ECLUSTER){
        tempE = true;
      }
      if(clusterType == mediator.MECLUSTER){
        tempME = true;
      }
      if(clusterType == mediator.UCLUSTER){
        tempU = true;
      }

      var toggleGroup = ToggleGroup {};
      clusterTypes = HBox{
        content:[
          clusterRB1 = RadioButton{
            text: "Edge"
            selected: tempE
            toggleGroup:toggleGroup
          },
          clusterRB2 = RadioButton{
            text: "Multi-Edge"
            selected: tempME
            toggleGroup:toggleGroup
          },
          clusterRB3 = RadioButton{
            text: "UMap"
            selected: tempU
            toggleGroup:toggleGroup
          },
        ]
      };

    }
    
  
  
  
    if(/*clusterOpt == null*/ true){ //the first time it must be initialized
      clusterOpt = Group{
        blocksMouse: true
        layoutX: bind (app.contentWidth - 200) / 2
        layoutY: bind (app.contentHeight - 100) / 2 + offsetForMakeDlgBoxVisibleHack
        content: [
          Rectangle {
            width: 220
            height: 90
            arcWidth: 20  arcHeight: 20
            stroke: Color.LIGHTGRAY;
            fill: Color.rgb(138,123,102);
            x: 0
            y: 0
          }
          VBox{
            translateX: 10
            translateY: 10
            width: 300
            spacing: 5
            content:[
              Text{content:"Selection Tolerance"},
              toleranceScroll,
              clusterTypes,
            Button{
              blocksMouse: true
              action:  function():Void{}
              //Use clicked so that the action doesn't pass on to the background when the box closes
              onMouseClicked:function(e: MouseEvent):Void{app.removeDialog(clusterOpt);}
              text: "Close"
            }
            ]}

            
          
        ]
      };
    }
    app.addDialog(clusterOpt);

    var makeDlgVisibleHack = Timeline {
      keyFrames: [
        at (0s) {offsetForMakeDlgBoxVisibleHack => .1},
        at (.3s) {offsetForMakeDlgBoxVisibleHack => 0 tween Interpolator.EASEOUT}
      ]};
    makeDlgVisibleHack.play();
    
  }
    

};

 
