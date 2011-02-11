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
import javafx.scene.Group;
import javafx.scene.paint.Color;
import ifcSoft.MainApp;
import ifcSoft.model.DataSetProxy;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.paint.Paint;
import javafx.geometry.Insets;
import javafx.scene.shape.Rectangle;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.effect.MotionBlur;
import javafx.scene.Node;
import javafx.util.Math;
import javafx.scene.layout.Tile;
import com.javafx.preview.control.PopupMenu;
import com.javafx.preview.control.MenuItem;
import javafx.scene.control.Separator;
import com.javafx.preview.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.util.Sequences;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Alert;

/**
 * The view Component for the Wind Rose tab.
 * @author Kyle Thayer <kthayer@emory.edu>
 */

public class WindRoseTab extends WindRoseTabI{

  /**
  *  These variables must be initialized
  */
  public-init var app:MainApp;
  public-init var windRoseTabMediator:WindRoseTabMediator;

  postinit{
    if(app == null or windRoseTabMediator == null){
      println("HistTab initializer: not initialized fully");
    }
  }

  var unitRadius = 50.0;

  var dsp:DataSetProxy;
  




  /*********** JavaFX Display components ***********/
  var windRoseContent:Group;
  var isDisplayed:Boolean = false;

  var mainWRCs:Node;
  var rightClickMenu:PopupMenu;
  var removeTileMenuItem:MenuItem;

  var scaleTypeChoiceBox:ChoiceBox = ChoiceBox{
    items: ["Length", "Area"]
  };
  var scaleTypeBox:HBox = HBox{
    content:[
      Label{
        text:"Wedge Sizes by:  "
        textFill: Color.WHITE
      },
      scaleTypeChoiceBox
      ]
  };


  var scaleByArea:Boolean = bind (scaleTypeChoiceBox.selectedIndex == 1);



  override public function setDataSet (dsp : DataSetProxy) : Void {
    this.dsp = dsp;
  }


  override public function informNewDsp(){
    //displayTab();
  }



  override public function displayTab() {
    if(windRoseContent == null){
      var dimensions = dsp.getDimensions();

      var colors:Paint[] = [Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW,
                  Color.AQUA, Color.GREENYELLOW, Color.PURPLE,
                  Color.SKYBLUE, Color.LIGHTGREEN, Color.WHEAT,
                  Color.AZURE, Color.LAVENDER, Color.SALMON];




      /*var legend:Group = Group{
        content:
          for(dim in [0..dimensions-1]){
            Arc {
              centerX: unitRadius  centerY: unitRadius
              radiusX: unitRadius  radiusY: unitRadius
              startAngle: (dim*1.0)/dimensions * 360.0
              length: 360.0 / dimensions
              fill: colors[dim]
              type: ArcType.ROUND
            }
          }
      }*/
      var legend:PieChart = PieChart{
        clockwise:false
        data: for(dim in [0..dimensions-1]){
              PieChart.Data {
                label: dsp.getColNames()[dim]
                value: 1
                fill: colors[dim]
                strokeWidth: 0
              }
            }
        pieLabelFill: Color.WHITE
        pieLabelVisible: true
        pieValueVisible: false
        pieStrokeWidth: 0
        layoutInfo: LayoutInfo { height: unitRadius*2+200 width: unitRadius*2+200}
        pieEffect: MotionBlur { radius: 0 angle: 0 } //I want no effect
      }


      var scale:Group = Group{
        content:
          [
            Rectangle{
              x: 0  y: 0
              height: unitRadius*2
              width: unitRadius*2
            }

            for(dim in [0..dimensions-1]){
              Arc {
                centerX: unitRadius  centerY: unitRadius
                radiusX: (dim+1)*unitRadius/dimensions
                radiusY: (dim+1)*unitRadius/dimensions
                startAngle: (dim*1.0)/dimensions * 360.0
                length: 360.0 / dimensions
                fill: colors[dim]
                type: ArcType.ROUND
              }
            }
          ]
      }

      rightClickMenu = PopupMenu{
        items: [
          //MenuItem { text: "Test File Against SOM", action: function() { mediator.testFileAgainstSOM(); } }
          Separator { }
          //MenuItem { text: "Clustering Options", action: function() { mediator.clustOpt(); } }
          //Separator { }
          removeTileMenuItem = MenuItem { text: "Remove Map Tile", disable:true }
          Menu{
            text: "Add Map Tile", disable: bind notDisplayedTiles.size() ==0
            items: bind
              for(tile in notDisplayedTiles){
                MenuItem{
                  text: tile.name
                  action: function():Void{
                    if(notDisplayedTiles[indexof tile] == blankTile){
                      insert WindRoseTile{pt: -1, name:"Blank", unitRadius:unitRadius
                          onMouseClickedFunction: tileClicked, onMouseDraggedFunction: tileDragged
                          onMouseReleasedFunction: tileReleased} into displayedTiles;
                    }else{
                      insert notDisplayedTiles[indexof tile] into displayedTiles;
                      delete tile from notDisplayedTiles;
                    }
                }
              }
            }
          }
        ]
      };



      mainWRCs = makeMainWRCs(dimensions, colors, unitRadius);


      windRoseContent = Group{

        content:[
          rightClickMenu,
          VBox{
            width: bind app.contentWidth
            //layoutInfo: LayoutInfo { width: bind app.contentWidth}
            hpos: HPos.RIGHT
            nodeHPos: HPos.RIGHT
            content:[
              HBox{
                height: bind app.contentHeight
                //layoutInfo: LayoutInfo { width: bind app.contentWidth}
                nodeHPos: HPos.CENTER
                nodeVPos: VPos.CENTER
                hpos: HPos.CENTER
                spacing: 5
                padding: Insets { top: 10 right: 10 bottom: 10 left: 10}
                content:[
                  Rectangle{
                    height: app.contentHeight
                    width: 30 //TODO: bind to tab width
                  }

                  mainWRCs,
                  VBox{
                    hpos: HPos.CENTER
                    vpos: VPos.CENTER
                    spacing: 10
                    padding: Insets { top: 10 right: 25 bottom: 10 left: 10}
                    content:[scaleTypeBox, legend/*, scale*/]
                  }
                ]
              }
            ]
          }
        ]
      };
    }

    app.setMainContent(windRoseContent);
    isDisplayed = true;
  }


  var displayedTiles:WindRoseTile[];

  var blankTile:WindRoseTile = WindRoseTile{
                pt: -1 name:"Blank", unitRadius: unitRadius
                onMouseClickedFunction: tileClicked
                onMouseDraggedFunction: tileDragged
                onMouseReleasedFunction: tileReleased};
  var notDisplayedTiles:WindRoseTile[] = [blankTile];

  function makeMainWRCs(dimensions:Integer, colors:Paint[], unitRadius:Number):Node{
    var numTiles = Math.min(dsp.getDataSize(), 25);
    if(numTiles < dsp.getDataSize()){
      Alert.inform("Only the first 25 of {dsp.getDataSize()} data points will be displayed.");
    }


    displayedTiles = for(pt in [0..numTiles-1]){
              WindRoseTile {
                name: dsp.getData().getPointName(pt)
                dimensions:dimensions
                colors:colors
                unitRadius:unitRadius
                pt:pt
                dsp:dsp
                onMouseClickedFunction: tileClicked
                onMouseDraggedFunction: tileDragged
                onMouseReleasedFunction: tileReleased
                scaleByArea: bind scaleByArea
              }
          };
          


    Tile{
      cache:true
      layoutInfo: LayoutInfo { width: bind app.contentWidth - unitRadius*2-300}
      hpos: HPos.CENTER
      vpos: VPos.CENTER
      hgap:15
      vgap:20
      content: bind displayedTiles

    }


  }

  var isTileBeingDragged:Boolean = false;
  var tileBeingDragged:Integer = -1;
  var isOverLeft:Boolean = false;
  var tileOver:Integer = -1;

  public function tileClicked(e:MouseEvent, tile:WindRoseTile):Void{
    if(e.button == MouseButton.SECONDARY or e.controlDown){
      var tileNum:Integer = Sequences.indexOf(displayedTiles, tile);
      removeTileMenuItem.text = "Remove {displayedTiles[tileNum].name}";
      removeTileMenuItem.disable= false;
      removeTileMenuItem.action = function(){
          if(displayedTiles[tileNum].pt != -1){
            insert displayedTiles[tileNum] into notDisplayedTiles;
          }
          delete displayedTiles[tileNum]};

      rightClickMenu.show(mainWRCs, HPos.RIGHT, VPos.BOTTOM,
        e.sceneX , e.sceneY);
    }else{
      println("close right click menu");
      rightClickMenu.hide();
    }

  }

  public function tileDragged(e:MouseEvent, tile:WindRoseTile):Void{
    println("Tile being dragged");
    var tileNum:Integer = Sequences.indexOf(displayedTiles, tile);
    var x = e.x;
    var y = e.y;



    //possibly dragging a tile
    if(not isTileBeingDragged){
      if(tileBeingDragged >= 0 and tileBeingDragged < displayedTiles.size()){
        //I don't know if this is needed
        displayedTiles[tileBeingDragged].highlight = false;
      }

      tileBeingDragged = tileNum;
      isTileBeingDragged = true;
      displayedTiles[tileBeingDragged].highlight = true;
    }

    tileNum = -2;
    var isNowOverLeft:Boolean;
    var newTileOver:Integer = -2;
    for(dispTile in displayedTiles){
      var localpt = dispTile.sceneToLocal(e.sceneX,e.sceneY);
      if(dispTile.contains(localpt)){
        newTileOver = indexof dispTile;
        if(localpt.x < unitRadius){
          isNowOverLeft = true;
        }else{
          isNowOverLeft = false;
        }
      }
    }
    if(isOverLeft != isNowOverLeft or tileOver != newTileOver){ //if we need to change the display
      //turn off old
      if(isOverLeft){
        displayedTiles[tileOver].leftHighlight = false;
        if(tileOver > 0){
          displayedTiles[tileOver - 1].rightHighlight = false;
        }
      }else{
        displayedTiles[tileOver].rightHighlight = false;
        if(tileOver < displayedTiles.size() - 1){
          displayedTiles[tileOver + 1].leftHighlight = false;
        }
      }

      isOverLeft = isNowOverLeft;
      tileOver = newTileOver;

      //turn on new
      if(isOverLeft){
        if(tileOver != tileBeingDragged){
          displayedTiles[tileOver].leftHighlight = true;
        }
        if(tileOver > 0 and tileOver - 1 != tileBeingDragged){
          displayedTiles[tileOver - 1].rightHighlight = true;
        }
      }else{
        if(tileOver != tileBeingDragged){
          displayedTiles[tileOver].rightHighlight = true;
        }
        if(tileOver < displayedTiles.size() - 1 and tileOver+1 != tileBeingDragged){
          displayedTiles[tileOver + 1].leftHighlight = true;
        }
      }


    }
  }

  public function tileReleased(e:MouseEvent, tile:WindRoseTile):Void{
    println("Tile released");
    var tileNum:Integer = Sequences.indexOf(displayedTiles, tile);
    if(isTileBeingDragged){
      isTileBeingDragged = false;
      //figure out where to insert it
      //turn off old
      displayedTiles[tileBeingDragged].highlight = false;
      if(isOverLeft){
        displayedTiles[tileOver].leftHighlight = false;
        if(tileOver > 0){
          displayedTiles[tileOver - 1].rightHighlight = false;
        }
      }else{
        displayedTiles[tileOver].rightHighlight = false;
        if(tileOver < displayedTiles.size() - 1){
          displayedTiles[tileOver + 1].leftHighlight = false;
        }
      }
      //can I just take the old values or do I need to figure out the new ones (might be different than what was highlighted)?
      //diplayedTiles[tileOver]
      var newTilePos:Integer = -1;
      if(isOverLeft){
        newTilePos = tileOver - 1;
      }else{
        newTilePos = tileOver;
      }

      println("tileBeingDragged:{tileBeingDragged}  newPos:{newTilePos}");

      if(tileOver != -2 and newTilePos != tileBeingDragged and newTilePos != tileBeingDragged - 1){ //those two cases are no change
        if(newTilePos < tileBeingDragged){
          var moveTile = displayedTiles[tileBeingDragged];
          delete displayedTiles[tileBeingDragged];
          insert moveTile after displayedTiles[newTilePos];
        }else{
          var moveTile = displayedTiles[tileBeingDragged];
          delete displayedTiles[tileBeingDragged];
          insert moveTile before displayedTiles[newTilePos];
        }
      }

      //println("tile {tileBeingDragged} dropped over {tileNum}");
    }
    /*else if(e.popupTrigger or e.controlDown){
      showRightClickMenu(e, tileNum);
    }*/
  }


  override public function swapOutTab():Void{
    isDisplayed = false;
  }


}
