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

import javafx.scene.CustomNode;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Alert;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;
import javafx.scene.input.MouseEvent;
import javafx.scene.Group;

/**
 * The basic class to build dialog boxes.
 * @author Kyle Thayer <kthayer@emory.edu>
 */

public class ifcDialogBox extends CustomNode {
  public-init var name:String;
  public var content:ifcDialogItem[] on replace{children = makeContent()};


  public-init var okAction:function():Void;
  public-init var okName:String = "OK";

  public-init var cancelAction:function():Void;
  public-init var cancelName:String = "Cancel";

  //public var some sort of layout bounds


  public def dialogBoxArc = 20;

  var innerContent:VBox;

  public var xpos:Float = 0; //public to allow the mainApp to move dialog box as needed
  var lastDragX:Float = 0;
  public var ypos:Float = 0;
  var lastDragY:Float = 0; //public to allow the mainApp to move dialog box as needed

  var maxWidth = 0.0;
  var maxHeight = 0.0;
  var currentwidth = bind innerContent.boundsInLocal.width on replace{
      if(currentwidth > maxWidth){
        maxWidth = currentwidth+5;
      }
    };
  var currentheight = bind innerContent.boundsInLocal.height on replace{
      if(currentheight > maxHeight){
        maxHeight = currentheight+5;
      }
    };


  var testoffset = .1; //part of a hack to get around a JavaFX bug




  function makeContent():Node[]{
    //first make the inner stuff, then make background rounded box thingy to fit.

    var innerContentNodes:Node[] = content;

    //add the OK and Cancel buttons
    if(okAction != null){
      if(cancelAction != null){ //both
        insert HBox{
            spacing:10
            padding: Insets{top:3}
            content:[
              ifcDialogButton{text:okName action: okButton},
              ifcDialogButton{text:cancelName action: cancelAction}
            ]
          }into innerContentNodes;
      }else{ //only OK
        insert ifcDialogButton{text:okName action: okButton} into innerContentNodes;
      }
    }else if(cancelAction != null){ //only Cancel
      insert ifcDialogButton{text:cancelName action: cancelAction} into innerContentNodes;
    }

    if(name != null){
      insert Text{content:name font:Font{size:14}} before innerContentNodes[0];
    }


    innerContent = VBox{
      layoutX: dialogBoxArc
      layoutY: dialogBoxArc/2
      spacing: 2
      content:[
        for(i in [0..innerContentNodes.size()-1]){
          innerContentNodes[i];
        }
      ]
    }

    var backRectangle = 
      Rectangle {
        width: bind maxWidth + 2*dialogBoxArc
        height: bind maxHeight + dialogBoxArc*3/4
        arcWidth: dialogBoxArc  arcHeight: dialogBoxArc
        stroke: Color.LIGHTGRAY;
        fill: Color.rgb(138,123,102);
        x: 0
        y: bind testoffset
        onMouseDragged: rectangleDragged
        onMousePressed: function(e:MouseEvent):Void{lastDragX = 0; lastDragY = 0;}
        blocksMouse: true
      };


    //There is a bug in JavaFX (1.3.1, jdk1.6.0_18) where it wasn't displaying the
    // dialog when it was placed on the stage, by moving it ever so slightly,
    //javaFX will display it
    var testtimeline = Timeline {
      keyFrames: [
        at (0s) {testoffset => .1},
        at (.3s) {testoffset => 0 tween Interpolator.EASEOUT}
     ]}
    testtimeline.play();
    return Group{
        content:[backRectangle, innerContent]
        layoutX: bind xpos
        layoutY: bind ypos
      };
  }

  function rectangleDragged(e:MouseEvent):Void{
    xpos += e.dragX - lastDragX;
    ypos += e.dragY - lastDragY;
    //TODO: Make sure you can't push it completely off screen

    lastDragX = e.dragX;
    lastDragY = e.dragY;
  }


  init{
    children = makeContent();
  }



  //when they press OK, validate every item in content first
  function okButton():Void{
    for(item in content){
      if(not item.validate()){
        //display some sort of error
        Alert.inform("Form not completed: {item.getName()}");
        return;
      }
    }

    okAction();
  }


}
