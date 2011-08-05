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

package ifcSoft.view.synchDataSets;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Stack;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.VPos;


public class synchedColumnFX {
  public var colName: String;
  public var sourceNames:String[] on replace OldVal
                    {entryColumn = makeEntryColumn();}; // this is because the
                          //auto-binding is sometimes not catching this change
  public var boxWidth:Number;
  public var highlightBox:function(col:synchedColumnFX, e:MouseEvent):Void;
  public var moveString:function(col:synchedColumnFX, name:String, e:MouseEvent):Void;

  public var colRectangle:Rectangle;

  public var entryText:Text[];

  public var entryRectangle:Rectangle[] =  //both for detecting mouse events on entries and highlighting
    bind for (name in sourceNames){
      Rectangle{
        height: bind entryText[indexof name].boundsInLocal.height
        width: bind boxWidth - 30
        fill: Color.LIGHTGRAY
        opacity: 0
        onMouseDragged: function(e:MouseEvent):Void{
                          entryRectangle[indexof name].opacity = 1;
                          highlightBox(this, e);
                        }
        onMouseReleased: function(e:MouseEvent):Void{
                          entryRectangle[indexof name].opacity = 0;
                          highlightBox(this, null);
                          moveString(this, name, e)
                        }
      }
    }


  public var entryColumn:Stack = makeEntryColumn();

  public function makeEntryColumn():Stack{
    entryText = for(string in sourceNames){
      Text{
        content:string
        wrappingWidth:boxWidth - 30
      }
    }

    Stack{
      nodeVPos: VPos.TOP
      content: [
        colRectangle = Rectangle{
          height:200
          width: bind boxWidth
          fill: Color.rgb(179,159,132)
          stroke: Color.BLACK
        },
        VBox{
              //prevent the VBox from stretching out to fill the whole space (keeps items at top)
          height: bind sumList(for(text in entryText){text.boundsInLocal.height + 3})
          spacing: 3
          translateX:5
          content: bind for(string in sourceNames){
              Stack{
                content:[
                  entryRectangle[indexof string],
                  entryText[indexof string]
                ]
              }
          }
        }
      ]
    };

  }

  function sumList(list:Number[]){
    var sum:Number = 0;
    for(num in list){
      sum+= num;
    }
    return sum;
  }



  var testStack:Stack = Stack{};
  //var test = testStack.managed; //possibly set to false
  //var test = testStack.

  //var testBox:VBox = VBox{};
  //var temp = testBox.
    


}
