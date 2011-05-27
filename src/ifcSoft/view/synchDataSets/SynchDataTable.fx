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

import ifcSoft.view.dialogBox.ifcDialogItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import ifcSoft.view.dialogBox.ifcDialogScrollView;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.layout.Stack;
import javafx.scene.text.Text;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextBox;
import javafx.scene.input.InputMethodEvent;



public class SynchDataTable extends ifcDialogItem{
  public-init var allDimNames:String[];
  public-init var initialColNames:String[];
  public var width:Integer = 600;
  var cols:synchedColumnFX[];
  var notPlaced:String[];
  //how do I store current col thingies?

  //put editable labels at top for final column titles.

  //below that have places where you can drag around the different column names
  //that match up with it

  //option needed to replace column names in data sets

  //put the whole thing in a scrollable area? have input max size thingies?

  var boxWidth:Integer = 85;
  var unusedBoxWidth:Integer = bind boxWidth;
  var spacer:Integer = 20;

  var usedBoxesSpaceLeft:Integer = bind width - unusedBoxWidth - spacer;

  var dimLabelBeingEdited:Integer = -1;

  var dimTextLabels:Text[] =
    bind for(col in cols){
      Text {
        x:5
        content:bind col.colName
        wrappingWidth:boxWidth - 10
      }
    }

  var dimInputTextBoxes:TextBox[] =
    bind for(col in cols){
      TextBox {
        text: ""
        columns: 10
        visible: false
        blocksMouse:true
        onInputMethodTextChanged: function(e:InputMethodEvent):Void{updateDimText(indexof col)}
        action:function():Void{updateDimText(indexof col)}
      }

    }

  function updateDimText(colNum:Integer):Void{
    cols[colNum].colName = dimInputTextBoxes[colNum].rawText;
    dimInputTextBoxes[colNum].visible = false;
    dimTextLabels[colNum].visible = true;
    dimLabelBeingEdited = -1;
  }

  init{
    attemptAlign();


    var colDimNameBoxes =
      bind for(col in cols){
        Stack{
          content:[
            Rectangle{
              height:30
              width: boxWidth
              fill: Color.LIGHTGRAY
              stroke: Color.BLACK
              onMouseClicked:function(e:MouseEvent):Void{
                  if(e.clickCount > 1){
                    if(dimLabelBeingEdited != 0){
                      updateDimText(dimLabelBeingEdited);
                    }
                    dimTextLabels[indexof col].visible = false;
                    dimInputTextBoxes[indexof col].visible = true;
                    dimInputTextBoxes[indexof col].text = col.colName;
                    dimLabelBeingEdited = indexof col;
                  }else if (e.controlDown) {
                    delete col from cols;
                    for(name in col.sourceNames){
                      insert name into notPlaced;
                    }

                  }
                },

            },
            dimTextLabels[indexof col],
            dimInputTextBoxes[indexof col]
          ]
        }
      };

    var colNodes = VBox{
      content:[
        HBox{ //dim name
          content: bind [
            colDimNameBoxes,
            Stack{
              content:[
                Rectangle{
                  height:30
                  width: 20
                  fill: Color.LIGHTGRAY
                  stroke: Color.BLACK
                  //action should make new column
                  onMouseClicked:addColumn
                },
                Text{
                  x:5
                  content:"+"
                }
                
              ]
            }
          ]

        },
        HBox{ //col labels associated with dimension name
          layoutInfo:LayoutInfo{hfill:false} //so it doesn't widen this
          content: bind for(col in cols){
              Stack{
                content:[
                  Rectangle{
                    height:200
                    width: boxWidth
                    fill: Color.rgb(179,159,132)
                    stroke: Color.BLACK
                    onMouseClicked: tableClicked
                  },
                  VBox{
                    translateX:5
                    content: bind for(string in col.sourceNames){
                        Text{
                          content:string
                          wrappingWidth:boxWidth - 30
                        }
                    }
                  }



                ]
              }
            }
        }

      ]
    }

    var unPlacedColNode = VBox{
      layoutInfo:LayoutInfo{vfill:false} //so it doesn't widen this
      content:[
        Stack{
          content:[
            Rectangle{
              height:20
              width: boxWidth
              fill: Color.LIGHTGRAY
              stroke: Color.BLACK
            },
            Text{
              x:5
              content:"Unused"
              wrappingWidth:boxWidth - 10
              onMouseClicked: tableClicked
            }
          ]
        },
        Stack{
          content:[
            Rectangle{
              height:200
              width: boxWidth
              fill: Color.rgb(179,159,132)
              stroke: Color.BLACK
              onMouseClicked: tableClicked
            },
            VBox{
              translateX:5
              content: bind for(string in notPlaced){
                  Text{
                    content:string
                    wrappingWidth:boxWidth - 10
                  }
              }
            }
          ]
        }
      ]
    }


    children =
      VBox{
        spacing: 4
        content:[
          //Text {content: name},
          //description line?
          //editable column labels, with ability to add new one and a place for unused
          HBox{
            content:[
              ifcDialogScrollView{
                
                maxHeight: 300;
                maxWidth: usedBoxesSpaceLeft
                node:colNodes
              },
              Rectangle{
                onMouseClicked: tableClicked
                height:1
                width: spacer
                opacity: 0
              },
              unPlacedColNode


            ]

          }

          //column names should be highlighted in red if not all files have it, or if a file has more than one

          //columns of the dimension names associated with the given columns
          //make the items in these draggable so they can be dropped in any column
            //give them a semi-transparent background, so when they are dragged, they
            //are visible
          

        ]
      }
  }



  function addColumn(e:MouseEvent):Void{
    insert synchedColumnFX{colName: "Dim {cols.size()}"  sourceNames: []} into cols;
  }


 function tableClicked(e:MouseEvent):Void{
   if(dimLabelBeingEdited != -1){
     updateDimText(dimLabelBeingEdited);
   }

 }


  public function getInput():synchedColumnFX[]{
    println("SynchDataTable returning size {cols.size()}");
    return cols;
  }

  override function validate():Boolean{
    //check if all files will have all columns
    return true;
  }


  function attemptAlign(){
    for(colName in initialColNames){
      var newCol = synchedColumnFX{
        colName: colName
        sourceNames: colName
       };
      insert newCol into cols;
    }

    for(dimName in allDimNames){
      var hasBeenPlaced = false;
      for(synchedCol in cols){
        if(hasBeenPlaced)
          break;
        var isInCol:Boolean = false;
        for(name in synchedCol.sourceNames){
          if(dimName.equals(name)){
            hasBeenPlaced = true;
            isInCol = true;
            break; //it's already
          }else if(areAproxSame(dimName, name)){
            isInCol = true;
          }

        }
        if(isInCol and not hasBeenPlaced){ //if it is in column, but not already listed
          insert dimName into synchedCol.sourceNames;
          hasBeenPlaced = true;
        }else if (isInCol){ //in column and already listed
          hasBeenPlaced = true;
        }
      }

      if(not hasBeenPlaced){
        println("Not placed: {dimName}");
        var isInCol:Boolean = false;
        for(name in notPlaced){
          if(dimName.equalsIgnoreCase(name)){
            hasBeenPlaced = true;
            isInCol = true;
            break; //it's already
          }
        }
        if(not isInCol){
          if(notPlaced.size() < 1){
            notPlaced = [dimName];
            println("length: {notPlaced.size()} and for fun: {[dimName].size()}");
          }else{
            insert dimName into notPlaced;
          }
          println("adding: {dimName}");
        }
      }
    }

    println("All unplaced: {for(string in notPlaced){"{string}, "}}");
    println("length: {notPlaced.size()}");

  }

  function areAproxSame(s1:String, s2:String):Boolean{
    if(s1.equalsIgnoreCase(s2)){//first, normal equals
      return true;
    }
    
    if(s1.indexOf('(') > 0 and s1.indexOf(')') > 0 and s1.indexOf('(') < s1.indexOf(')') and
      s2.indexOf('(') > 0 and s2.indexOf(')') > 0 and s2.indexOf('(') < s2.indexOf(')')){
      //check if end (dim meaning) is same
      var insideParens1:String = s1.substring(s1.indexOf('(')+1, s1.indexOf(')'));
      var insideParens2:String = s2.substring(s2.indexOf('(')+1, s2.indexOf(')'));
      if(insideParens1.length() <= 3 or insideParens2.length() <= 3){ //if nothing inside parens
        //see if beginnings match:
        var start1:String = s1.substring(0,s1.indexOf('('));
        var start2:String = s2.substring(0,s2.indexOf('('));
        if(start1.equalsIgnoreCase(start2)){
            println("{s1} ~ {s2}");
          return true;
        }
        return false;
      }

      if(insideParens1.equalsIgnoreCase(insideParens2)){
          println(" {s1} ~ {s2}");
        return true;
      }


      //remove spaces
      while(insideParens1.indexOf(' ') != -1){
        insideParens1 = "{insideParens1.substring(0, insideParens1.indexOf(' '))}{
                insideParens1.substring(insideParens1.indexOf(' ')+1, insideParens1.length())}";
      }
      while(insideParens2.indexOf(' ') != -1){
        insideParens2 = "{insideParens2.substring(0, insideParens2.indexOf(' '))}{
                insideParens2.substring(insideParens2.indexOf(' ')+1, insideParens2.length())}";
      }

      //try removing '-' from each, then compare
      while(insideParens1.indexOf('-') != -1){
        insideParens1 = "{insideParens1.substring(0, insideParens1.indexOf('-'))}{
                insideParens1.substring(insideParens1.indexOf('-')+1, insideParens1.length())}";
      }
      while(insideParens2.indexOf('-') != -1){
        insideParens2 = "{insideParens2.substring(0, insideParens2.indexOf('-'))}{
                insideParens2.substring(insideParens2.indexOf('-')+1, insideParens2.length())}";
      }

      if(insideParens1.equalsIgnoreCase(insideParens2)){

          println(" {s1} ~ {s2}");
        return true;
      }
    }

		return false;
	}



}

