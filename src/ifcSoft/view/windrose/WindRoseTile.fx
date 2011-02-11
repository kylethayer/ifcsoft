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

import javafx.scene.CustomNode;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.paint.Paint;
import javafx.util.Math;
import ifcSoft.model.DataSetProxy;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextBox;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.geometry.HPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Stack;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.text.Font;
import javafx.scene.layout.HBox;

/**
 * @author kthayer
 */

public class WindRoseTile extends CustomNode{
  public var dimensions:Integer;
  public var colors:Paint[];
  public var unitRadius:Number;
  public var pt:Integer; //if -1, then it is blank
  public var dsp:DataSetProxy;
  public var name:String;
  public var scaleByArea:Boolean;

  public var onMouseClickedFunction:function(e:MouseEvent, wrt:WindRoseTile);
  public var onMouseDraggedFunction:function(e:MouseEvent, wrt:WindRoseTile);
  public var onMouseReleasedFunction:function(e:MouseEvent, wrt:WindRoseTile);

  package var highlight:Boolean = false;
  package var leftHighlight:Boolean = false;
  package var rightHighlight:Boolean = false;

  var explodeAmt:Number = 1.5;
  var isEditingName:Boolean = false;
  var nameEditBox:TextBox;




  init{
    var innerContent:VBox;
    if(pt== -1){
      innerContent =
          VBox{
            onMouseClicked: function(e:MouseEvent):Void{onMouseClickedFunction(e, this)}
            onMouseDragged: function (e:MouseEvent){println("blank drag");onMouseDraggedFunction(e, this);}
            onMouseReleased: function (e:MouseEvent){onMouseReleasedFunction(e, this);}
            nodeHPos: HPos.CENTER
            spacing: 5
            content:[
              Rectangle{
                height: 15 + unitRadius*2 + 10
                width: unitRadius*2
              }
            ]
          }
    }else{
      innerContent =
        VBox{
          onMouseClicked: function(e:MouseEvent):Void{onMouseClickedFunction(e, this)}
          onMouseDragged: function (e:MouseEvent){onMouseDraggedFunction(e, this);}
          onMouseReleased: function (e:MouseEvent){onMouseReleasedFunction(e, this);}
          nodeHPos: HPos.CENTER
          spacing: 5
          content:[
            Stack{
              content:[
                Rectangle{ //Label double click catcher
                  height: 15
                  width: unitRadius*2
                  onMouseClicked: labelClick
                }
                nameEditBox = TextBox{
                  text: name
                  visible: bind isEditingName
                  action: function(){name = nameEditBox.text;isEditingName = false;}
                },
                Label{
                  text: bind name
                  textFill: Color.WHITE
                  hpos: HPos.CENTER
                  layoutInfo: LayoutInfo { width: unitRadius*2 }
                  font: Font {name: "Arial" size: 14}
                  visible: bind not isEditingName
                  onMouseClicked: labelClick
                },

              ]
            },
            makeWRCCircle(),
            Rectangle{ //spacer at the the bottom of a tile
              height: 10
              width: unitRadius*2
            }

          ]
        }
      }

      children = Group{
        //onMouseClicked: function(e:MouseEvent):Void{onMouseClickedFunction(e, this)}
        //onMouseDragged: function (e:MouseEvent){onMouseDraggedFunction(e, this);}
        //onMouseReleased: function (e:MouseEvent){onMouseReleasedFunction(e, this);}
        content:[
          innerContent,
          Rectangle{
            width: unitRadius*2
            height: unitRadius*2 + 15
            fill: Color.WHITE
            opacity:  bind if(highlight){.25}else{0};
          },
          HBox{ //the left/right highlights
            content: [
              Rectangle{
                height: unitRadius*2 + 15
                width: unitRadius*2 / 6
                fill:Color.WHITE
                opacity: bind if(leftHighlight){.25}else{0};
              },
              Rectangle{ //spacer
                  height:unitRadius*2 + 15
                  width: unitRadius*2 * 2 / 3
                  opacity:0
              },
              Rectangle{
                height:unitRadius*2 + 15
                width: unitRadius*2 / 6
                fill:Color.WHITE
                opacity: bind if(rightHighlight){.25}else{0};
              }
            ]
          }

          ]

      }

  }

  function makeWRCCircle():Node[]{
    var lengths:Double[] = bind
      if(scaleByArea){
        for(dim in [0..dimensions-1]){
            Math.sqrt(dsp.getData().getVals(pt)[dim] / dsp.getData().getMax(dim))
          };
      }else{
        for(dim in [0..dimensions-1]){
            dsp.getData().getVals(pt)[dim] / dsp.getData().getMax(dim)
          };
      };


    Group{
      content:[
          Rectangle{
            x: -explodeAmt  y: -explodeAmt
            height: unitRadius*2 + 2*explodeAmt
            width: unitRadius*2 + 2*explodeAmt
          },
          for(dim in [0..dimensions-1]){
            Arc {
              centerX: unitRadius + explodeAmt * Math.cos( (dim+.5)*6.28318531/dimensions )
              centerY: unitRadius - explodeAmt * Math.sin( (dim+.5)*6.28318531/dimensions )
              radiusX: bind unitRadius*lengths[dim]
              radiusY: bind unitRadius*lengths[dim]
              startAngle: (dim*1.0)/dimensions * 360.0
              length: 360.0 / dimensions
              fill: colors[dim]
              type: ArcType.ROUND
            }
          }
        ]
      };
  }

  function labelClick(e:MouseEvent):Void{
    if(e.clickCount > 1){
      if(isEditingName){
        
      }else{
        nameEditBox.text = name;
        isEditingName = true;
      }
    }
  }

}
