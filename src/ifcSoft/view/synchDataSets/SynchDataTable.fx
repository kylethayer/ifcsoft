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
import javafx.scene.paint.Color;
import javafx.scene.layout.Stack;
import javafx.scene.text.Text;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextBox;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.Alert;



public class SynchDataTable extends ifcDialogItem{
  public-init var allDimNames:String[];
  public-init var initialColNames:String[];
  public var width:Integer = 600;
  var cols:synchedColumnFX[];
  var notPlacedCol:synchedColumnFX =
    synchedColumnFX{
      colName: "Unused"
      sourceNames: []
      boxWidth:bind boxWidth
      highlightBox: highlightBox
      moveString: moveString
    };
  var scrollArea:ifcDialogScrollView;

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
                      insert name into notPlacedCol.sourceNames;
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
              col.entryColumn
            }
        }
      ]
    }

    var unPlacedColNode = VBox{
      blocksMouse:true
      layoutInfo:LayoutInfo{vfill:false} //so it doesn't widen this
      content:bind [
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
              content:notPlacedCol.colName
              wrappingWidth:boxWidth - 10
              onMouseClicked: tableClicked
            }
          ]
        },
        notPlacedCol.entryColumn
      ]
    }

    scrollArea = ifcDialogScrollView{
        maxHeight: 300;
        maxWidth: usedBoxesSpaceLeft
        node:colNodes
      }


    children =
      VBox{
        spacing: 4
        content:[
          HBox{
            content: bind[
              scrollArea,
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
    insert synchedColumnFX{
            colName: "Dim {cols.size()}"
            sourceNames: []
            boxWidth:bind boxWidth
            highlightBox: highlightBox
            moveString: moveString
        } into cols;
  }


  function tableClicked(e:MouseEvent):Void{
    if(dimLabelBeingEdited != -1){
      updateDimText(dimLabelBeingEdited);
    }
  }

  function highlightBox(sourceCol:synchedColumnFX, e:MouseEvent):Void{
    for(col in cols){
      //see if it is over the column
      if(e != null and sourceCol != col
            and scrollArea.contains(scrollArea.sceneToLocal(e.sceneX, e.sceneY))
            and col.colRectangle.contains(col.colRectangle.sceneToLocal(e.sceneX, e.sceneY))){
        col.colRectangle.fill = Color.LIGHTGRAY
      }else{
        col.colRectangle.fill = Color.rgb(179,159,132)
      }
    }

    if(e != null and sourceCol != notPlacedCol
        and notPlacedCol.colRectangle.contains(notPlacedCol.colRectangle.sceneToLocal(e.sceneX, e.sceneY))){
      notPlacedCol.colRectangle.fill = Color.LIGHTGRAY
    }else{
      notPlacedCol.colRectangle.fill = Color.rgb(179,159,132)
    }



  }

  function moveString(sourceCol:synchedColumnFX, name:String, e:MouseEvent):Void{
    var columnIn = -1;
    if(scrollArea.contains(scrollArea.sceneToLocal(e.sceneX, e.sceneY))){ // if it's in the scroll area
      for(col in cols){ //check to see if it's over any of the columns
        if(e != null and sourceCol != col
              and col.colRectangle.contains(col.colRectangle.sceneToLocal(e.sceneX, e.sceneY))){
          columnIn = indexof col;
        }
      }
    }

    if(columnIn != -1){ //if we need to move it to one of the main columns
      insert name into cols[columnIn].sourceNames;
      delete name from sourceCol.sourceNames;

    }else if(e != null and sourceCol != notPlacedCol   //if moved to not placed column
        and notPlacedCol.colRectangle.contains(notPlacedCol.colRectangle.sceneToLocal(e.sceneX, e.sceneY))){

      insert name into notPlacedCol.sourceNames;
      delete name from sourceCol.sourceNames;
    }
  }



  public function getInput():synchedColumnFX[]{
    println("SynchDataTable returning size {cols.size()}");
    return cols;
  }

  override function validate():Boolean{
    //check to make sure each column has at least one thing linked to it
    var emptyCols:synchedColumnFX[] = [];
    for(col in cols){
      if(col.sourceNames.size() == 0){
        insert col into emptyCols;
      }
    }
    if(emptyCols.size() > 0){
      Alert.inform("Error: Some Dimensions Empty: {for(col in emptyCols){"{col.colName},"}}");
      return false;
    }
    return true;
  }


  function attemptAlign(){
    for(colName in initialColNames){
      var newCol = synchedColumnFX{
        colName: colName
        sourceNames: colName
        boxWidth: bind boxWidth
        highlightBox: highlightBox
        moveString: moveString
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
        var isInCol:Boolean = false;
        for(name in notPlacedCol.sourceNames){
          if(dimName.equalsIgnoreCase(name)){
            hasBeenPlaced = true;
            isInCol = true;
            break; //it's already
          }
        }
        if(not isInCol){
          if(notPlacedCol.sourceNames.size() < 1){
            notPlacedCol.sourceNames = [dimName];
          }else{
            insert dimName into notPlacedCol.sourceNames;
          }
        }
      }
    }

  }


 /**
  * This function checks to see if two dimension names are close enough for the
  * program to guess that they are really the same. This is particularly made
  * to handle names that come with Flow Cytometry files.
  */
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

