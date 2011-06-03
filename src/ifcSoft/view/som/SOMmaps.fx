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

import javafx.scene.layout.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.Separator;
import javafx.util.Sequences;

import javafx.geometry.HPos;
import javafx.geometry.VPos;

import ifcSoft.view.som.SOMholder;
import ifcSoft.view.som.SOMcluster;
import ifcSoft.MainApp;

import java.lang.Math;
import java.util.LinkedList;
import java.awt.Point;
import javafx.scene.input.MouseButton;

import com.javafx.preview.control.PopupMenu;
import com.javafx.preview.control.MenuItem;
import com.javafx.preview.control.Menu;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;
import ifcSoft.view.som.somtile.SOMTileConst;
import ifcSoft.view.som.somtile.SOMTileDenseMapDelta;
import ifcSoft.view.som.somtile.SOMTileSubsetDenseMap;
import ifcSoft.view.som.somtile.SOMTileDim;
import ifcSoft.view.som.somtile.SOMTileDataSetsDense;
import ifcSoft.view.som.somtile.SOMTileBlank;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogStringInput;
import ifcSoft.view.som.somtile.SOMTileDenseMap;

/**
 *  @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMmaps {

  public-init var app:MainApp;
  public-init var mediator:SOMMediator;
  public-init var SOMclstr:SOMcluster;
  postinit{
    if(app == null or mediator == null or SOMclstr == null){
      println("SOMmaps initializer: not initialized fully");
    }
  }

  var somHolder:SOMholder;

  package var crosshairHPos: Number = 0;
  package var crosshairVPos: Number = 0;
  package var crosshairVisible: Boolean = false;

  var tilesGroup:Group;

  var tileNodes:Node[] = bind for(tile in displayedTiles){
        tile.getTileNode();
      }
  
  public var clusterImg:Image = null; //the overlay for selected cluster
  var displayedTiles:SOMTile[];
  var blankTile:SOMTile;
  var somDimTiles:SOMTile[];
  var uMap:SOMTile;
  var euMap:SOMTile;
  var denseMap:SOMTile;
  var denseMaps:SOMTile[];
  var denseMapsDelta:SOMTile;
  var notDisplayedTiles:SOMTile[];

  package var selectPathElements:PathElement[] = null;

  var someMap:ImageView = bind displayedTiles[0].getImageView(); //I need this so I can convert map coordinates to it's parents in selection

  var isMouseBeingDragged:Boolean = false;

  var rightClickMenu:PopupMenu;
  var histMenuItem:MenuItem;
  var removeTileMenuItem:MenuItem;

  //height and width of the whole Map Tiles area
  var SOMTilesHeight = bind Math.max(app.contentHeight - 60, 50) on replace oldValue{
    if(oldValue != SOMTilesHeight)
        tileWidth = getWidthMax();
    };
  var SOMTilesWidth = bind Math.max(app.contentWidth - 70, 50) on replace oldValue{
    if(oldValue != SOMTilesWidth)
        tileWidth = getWidthMax();
    };
    
  public-read var aspectratio:Number = 2; //NOTE: if you change either of these two you have to call "tileWidth = getWidthMax();"
  var numTiles = bind displayedTiles.size() on replace oldValue{
    if(oldValue != numTiles)
        tileWidth = getWidthMax();
    };
    
  public-read var tileWidth = getWidthMax();

  /**
  * This function finds the maximum width of a tile such that all tiles will fit
  * given the current aspect ratio, number of tiles and size of the area (SOMTilesHeight and Width)
  */
  function getWidthMax():Double{
    var scale = Math.sqrt(SOMTilesWidth*SOMTilesHeight / numTiles / aspectratio);
    if(not(scale > 0 and scale < 100000)){
      return 1;
    }
    //decrease the tile height until the tiles fit          //while it doesn't fit enough
    while(Math.floor(SOMTilesWidth/scale)*Math.floor(SOMTilesHeight/scale/aspectratio) < numTiles){
      //reduce scale to next lowest integer height (so one more can fit vertically)
      scale = SOMTilesHeight/aspectratio/(Math.floor(SOMTilesHeight/scale/aspectratio) + 1) -.1;
    }
    
    //we now fit as best as possible vertically. Increase width until they don't fit, return last valid scale
    var lastscale = scale;
    //first maximize the current number in width
    scale = SOMTilesWidth/Math.floor(SOMTilesWidth/scale) -.1;
                                  //while it's still fits enough
    while(Math.floor(SOMTilesWidth/scale)*Math.floor(SOMTilesHeight/scale/aspectratio) >= numTiles){
      //increase scale to next heighest integer height (so one more can fit vertically)
      lastscale = scale;
      scale = SOMTilesWidth/(Math.floor(SOMTilesWidth/scale)-1) -.1;
    }

    return lastscale; //we are one past the last valid scale, so lastScale is the biggest we can use
  }

  /**
  * This function creates and returns a Group containing all the SOM map tiles.
  * @return 
  */
  public function makeSOMTilesGroup():Group{
    if(somHolder == null){
      somHolder = mediator.getSOMMaps();
      getTiles(); //I only need to do the initial tile building if I get the somHolder
      tilesGroup = null;//we're resetting
    }

    clusterImg = null;
    aspectratio = somHolder.imgheight*1.0 / somHolder.imgwidth;
    tileWidth = getWidthMax(); //reset the tile width according to the new info
    if(tilesGroup == null){ //only build it if it needs to be
      var mapTiles = Tile{
        autoSizeTiles: false
        cache:true
        height: bind SOMTilesHeight
        width: bind SOMTilesWidth
        tileHeight: bind tileWidth* aspectratio
        tileWidth: bind tileWidth
        hpos: HPos.CENTER
        vpos: VPos.CENTER
        hgap:0
        vgap:0
        content: bind tileNodes
      }

      tilesGroup = Group{ //this Group has a rectangle to size things correctly
        content:[
          Rectangle{
            fill: Color.BLACK
            width:bind SOMTilesWidth
            height:bind SOMTilesHeight
          },
          mapTiles, 
          rightClickMenu = PopupMenu{
            items: [
              MenuItem { text: "Export SOM and Hit Histograms", action: function() { ExportSOMAndHit{app:app, mediator:mediator };} }
              Separator { }
              MenuItem { text: "Clustering Options", action: function() { mediator.clustOpt(); } }
              //MenuItem { text: "Save Cluster", action: function() { SaveClusterDialog{app:app, mediator:mediator }; } //in order for the menu not to be in the screenshot, must use the other button
              Separator { }
              removeTileMenuItem = MenuItem { text: "Remove Map Tile", disable:true }
              Menu{
                text: "Add Map Tile", disable: bind notDisplayedTiles.size() ==0
                items: bind
                  for(tile in notDisplayedTiles){
                    MenuItem{
                      text: tile.name
                      action: function():Void{
                        if(notDisplayedTiles[indexof tile] == blankTile){
                          insert SOMTileBlank{somMaps:this name:"Blank Tile"} into displayedTiles;
                        }else{
                          insert notDisplayedTiles[indexof tile] into displayedTiles;
                          delete tile from notDisplayedTiles;
                          tile.updateDenseMap();}
                        }
                    }
                  }
              }
              MenuItem { text: "Compare Data Set",  action: function() {addDenseMap()}}
              histMenuItem = MenuItem { text: "Make Histogram", disable:true }

            ]
          }
        ]

      }
    }
    return tilesGroup;
  }


  /************************** Data Set Selector ******************/
  var DataSetDialog:Group;

  var selectedSubSets:Boolean[]; //Sub sets
  var selectedSubSetsCheckBox:CheckBox[];
  var selectedDataSets:Boolean[]; //other data sets
  var selectedDataSetsCheckBox:CheckBox[];

  var allDataSets:String[] = bind
    for(i in [0..mediator.numDSPs()-1]){
            mediator.getDSP(i).getDataSetName();
  };
  //var selectedString:String;
  var allDataSetsView:HBox[] = bind
    for(i in [0..selectedDataSetsCheckBox.size()-1]){
      HBox{
        content:[
          selectedDataSetsCheckBox[i],
          Label{ 
            layoutInfo: LayoutInfo { width: 100 }
            text: "{mediator.getDSP(i).getDataSetName()} "
            textOverrun: OverrunStyle.ELLIPSES
          }
        ]
      }
  };

  var rawSetNames:LinkedList;

  var allSubSetsView:HBox[] = bind
    for(i in [0..rawSetNames.size()-1]){
      HBox{
        content:[
          selectedSubSetsCheckBox[i],
          Label{
            layoutInfo: LayoutInfo { width: 100 }
            text: "{rawSetNames.get(i)} "
            textOverrun: OverrunStyle.ELLIPSES
          }
        ]
      }
  };

  var testoffset = .1;

  /**
  *
  */
  function  addDenseMap():Void{
    rawSetNames = mediator.SOMp.getDataSet().getRawSetNames();
    //initialize so none are selected
    selectedDataSets =
      for(i in [0..mediator.numDSPs()-1]){
        false;
      };
    selectedSubSets =
      for(i in [0..rawSetNames.size()-1]){
        false;
      };

    selectedDataSetsCheckBox =
      for(i in [0..selectedDataSets.size()-1]){
        CheckBox{};
      };
    selectedSubSetsCheckBox =
      for(i in [0..rawSetNames.size()-1]){
        CheckBox{};
      };

    var testtimeline = Timeline {
      keyFrames: [
        at (0s) {testoffset => .1},
        at (.3s) {testoffset => 0 tween Interpolator.EASEOUT}
     ]};
    DataSetDialog = Group{
      blocksMouse: true
      layoutX: bind (app.contentWidth - 250) / 2
      layoutY: bind (app.contentHeight - 25*(sizeof allDataSetsView) -30) / 2 + testoffset
      content: [
        Rectangle {
          width: 250
          height: 22*(sizeof allDataSetsView) +40
          arcWidth: 20  arcHeight: 20
        stroke: Color.LIGHTGRAY;
        fill: Color.rgb(138,123,102);
        x: 0
        y: 0
      }

      VBox{
        translateX: 20
        translateY: 20
        content:[
          HBox{content:[
            VBox{content:[
            Text{content: "Subsets of Current Set"}
            allSubSetsView,
            ]},
            VBox{content:[
            Text{content: "All Data Sets"}
            allDataSetsView,
            ]}
          ]}
          HBox{content:[
            Button{
              onMouseClicked:  function(event):Void{
                    acceptDataSetChecks();
                    //setSelectedString();
                    app.removeDialog(DataSetDialog);
                    //SOMDialogDisabled = false;
                  }
              text: "OK"
            }
            Button{
              onMouseClicked:  function(event):Void{
                    app.removeDialog(DataSetDialog);
                    //SOMDialogDisabled = false;
                  }
              text: "Cancel"
            }
          ]}
      ]}

      ]
    }
    //SOMDialogDisabled = true;
    app.addDialog(DataSetDialog);
    testtimeline.play();

    //show Data Set selector
          //Should have column of current subsets and column of All Data Sets
    
  }

  function acceptDataSetChecks():Void{

    var setName:String = "";
    var numSubSetSelected= 0;
    for(cbox in selectedSubSetsCheckBox){
      if(cbox.selected){
        numSubSetSelected++;
        selectedSubSets[indexof cbox] = true;
        setName += "{somHolder.rawSetNames[indexof cbox]}, ";
      }
    }

    var numDataSetSelected = 0;
    for(cbox in selectedDataSetsCheckBox){
      if(cbox.selected){
        numDataSetSelected++;
        selectedDataSets[indexof cbox] = true;
        if(mediator.getDSP(indexof cbox).getDimensions() != mediator.getUnscaledDimensions()){
          app.alert("Cannot compare data sets: {mediator.getDSP(indexof cbox).getDataSetName()} has a different number of dimensions.");
          return;
        }
        setName += "{mediator.getDSP(indexof cbox).getDataSetName()}, ";
      }
    }

    if(numSubSetSelected==0 and numDataSetSelected==0){
      app.alert("No data sets selected.");
      return;
    }

    var newTile:SOMTile;

    //TODO:How do I check if an identical map has already been placed?
    //should I put them all in a list, and iterate through each one?
    if(numDataSetSelected == 0){ //then it is comprised of the regular subsets
      //if there is only one selected, just make sure it is displayed
      if(numSubSetSelected == 1){
        var subSetSelected = -1;
        for(i in selectedSubSets where i == true){
          subSetSelected = indexof i;
        }
        for(tile in notDisplayedTiles){
          if(tile.getClass() == SOMTileSubsetDenseMap.class){
            if((tile as SOMTileSubsetDenseMap).densityMap == subSetSelected){
              delete tile from notDisplayedTiles;
              insert tile into displayedTiles;
              tile.updateDenseMap();
            }
          }
        }
      }else{ // multiple subsets selected
        newTile =
          SOMTileDataSetsDense{
            somMaps:this;
            selectedSubSets: selectedSubSets;
            img: somHolder.blankmap
            name: setName
            min: 0
            max: 1
          };
        insert newTile into displayedTiles;
        newTile.updateDenseMap();
      }
      return;
    }
    //some data sets selected
    //need to add jobs to find new densities
    mediator.findDensities(selectedDataSets);

    if(numSubSetSelected == 0){
      newTile =
        SOMTileDataSetsDense{
          somMaps:this;
          selectedDataSets: selectedDataSets;
          img: somHolder.blankmap
          name: setName
          min: 0
          max: 1
        };

    }else{ //both
      newTile =
        SOMTileDataSetsDense{
          somMaps:this;
          selectedSubSets: selectedSubSets;
          selectedDataSets: selectedDataSets;
          img: somHolder.blankmap
          name: setName
          min: 0
          max: 1
        };
    }

    insert newTile into displayedTiles;
    newTile.updateDenseMap();

  }



  /**
  *
  */
  function getTiles():Void{
    euMap = SOMTileConst{
          somMaps:this
          img: somHolder.euMap
          name: "Edge UMap"
          min: somHolder.euMapMin
          max: somHolder.euMapMax
        };
    uMap = SOMTileConst{
          somMaps:this
          img: somHolder.uMap
          name: "UMap"
          min: somHolder.uMapMin
          max: somHolder.uMapMax
        };
    somDimTiles = for(i in [0..somHolder.dimMapImages.length-1]){
      SOMTileDim{
          somMaps:this
          dim: i
          img: somHolder.dimMapImages[i]
          name: somHolder.dimNames[i]
          min: somHolder.dimMins[i]
          max: somHolder.dimMaxes[i]
        };
    }
    denseMap = SOMTileDenseMap{
          somMaps:this
          img: somHolder.blankmap
          name: "Hit Histogram"
          min: 0
          max: 1
        };

    denseMapsDelta = SOMTileDenseMapDelta{
          somMaps:this
          img: somHolder.blankmap
          name: "Dense Maps Δ"
          min: 0
          max: 1
        };

    if(somHolder.rawSetNames.length > 1){
      denseMaps =
        for(i in [0..somHolder.rawSetNames.length-1]){
          SOMTileSubsetDenseMap{
            somMaps:this
            densityMap: i //since the main dense map is 0, these ones start at 1
            img: somHolder.blankmap
            name: somHolder.rawSetNames[i]
            min: 0
            max: 1
          };
        }
    }

    blankTile = SOMTileBlank{somMaps:this name:"Blank Tile"};
    var anotherBlankTile = SOMTileBlank{somMaps:this name:"Blank Tile"};

    insert blankTile into notDisplayedTiles;

    var displaydDimTiles:SOMTile[];
    for(tile in somDimTiles){
      if(mediator.SOMp.getWeighting()[indexof tile] != 0){
        insert tile into displaydDimTiles;
      }else{
        insert tile into notDisplayedTiles;
      }

    }

    //I'll leave off uMap for now
    insert uMap into notDisplayedTiles;
    if(denseMaps.size() == 0){
      displayedTiles = [euMap, displaydDimTiles, anotherBlankTile, denseMap];
    }else{
      displayedTiles = [euMap, displaydDimTiles, anotherBlankTile, denseMap, denseMaps];
      insert denseMapsDelta into notDisplayedTiles;
    }

  }


  package function updateDenseMaps(){
    for(tile in displayedTiles){ //only update those tiles that are visible
      tile.updateDenseMap();
    }

  }

  public function updateMapStats(){
    if(clusterImg == null){
      for(tile in displayedTiles){
        tile.updatePointStats();
      }
    }else{
      for(tile in displayedTiles){
        tile.updateClusterStats();
      }
    }
  }


  package function SOMmousemove(e:MouseEvent):Void{
    crosshairHPos = e.x - e.node.layoutBounds.width / 2 - e.node.layoutBounds.minX;
    crosshairVPos = e.y  - e.node.layoutBounds.height / 2 - e.node.layoutBounds.minY;
    //get stats for cell I'm over if no cluster selected
    if(clusterImg == null){
      //get coordinates from tile to image
      var pt = someMap.parentToLocal(e.x, e.y);
      //ImgViews[i].parentToLocal(arg0) var transPoint = someMap.localToParent(point.x, point.y);
      var isNewCell:Boolean = mediator.setPointMouseOver(pt.x, pt.y);
      //var cellStats:Float[] = mediator.getCellStats(pt.x, pt.y);
      //setMapStats(cellStats);
      if(isNewCell){
        updateMapStats();
      }
    }

  }


  package function SOMmousepressed(e:MouseEvent, tile:SOMTile):Void{
    var tileNum:Integer = Sequences.indexOf(displayedTiles, tile);
    println("e.popuptrigger={e.popupTrigger}");
    if(e.button == MouseButton.SECONDARY or e.altDown){
      selectPathElements = MoveTo{x: e.x, y: e.y};

    }else if(e.popupTrigger){
      showRightClickMenu(e, tileNum);
    }

  }

  var isTileBeingDragged:Boolean = false;
  var tileBeingDragged:Integer = -1;
  var isOverLeft:Boolean = false;
  var tileOver:Integer = -1;

  public function SOMmousedragged(e:MouseEvent, tile:SOMTile):Void{
    var tileNum:Integer = Sequences.indexOf(displayedTiles, tile);
    var x = e.x;
    var y = e.y;
    
    if(e.secondaryButtonDown or e.altDown){
      isMouseBeingDragged = true;
      //make sure coordinates are within the tile
      if(x < 1)
        x = 1;
      if(x >= tileWidth)
        x = tileWidth-1;
      if(y < 1)
        y = 1;
      if(y >= tileWidth* aspectratio)
        y = tileWidth* aspectratio-1;
      if(selectPathElements == null or selectPathElements.size() == 0){
        //selectPathElements = MoveTo{x: e.x, y: e.y};
      }else{
        insert LineTo{x: x, y: y} into selectPathElements;
      }

    }else{

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
        var tileNode = dispTile.getTileNode();
        var localpt = tileNode.sceneToLocal(e.sceneX,e.sceneY);
        if(tileNode.contains(localpt)){
          newTileOver = indexof dispTile;
          if(localpt.x < tileWidth / 2){
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
  }

  public function SOMmousereleased(e:MouseEvent, tile:SOMTile):Void{
    var tileNum:Integer = Sequences.indexOf(displayedTiles, tile);
    if(isMouseBeingDragged){ //TODO: this isn't a sufficient check, I need a var to see if it is selecting a region
      isMouseBeingDragged = false;
      if(selectPathElements.size() <= 2){ //if it's only one or two points, then treat as right click
        showRightClickMenu(e, tileNum);
        return;
      }
      if(not e.shiftDown){
        mediator.clearPointsSelected();
      }
      //select cluster
      var path = Path{
        fill: Color.PURPLE
        //clip:Rectangle{ height: bind tileWidth* aspectratio, width: bind tileWidth}
        stroke: Color.PURPLE
        strokeWidth: 1
        elements: selectPathElements
      };
      //if not control, clear selected points
      for(i in [0..mediator.SOMwidth()-1]){
        for(j in [0..mediator.SOMheight()-1]){
          //get linked list of points that belong to the cell
          var isSelected = true;
          var pxls:LinkedList = mediator.getPxlsOfCell(i, j);
          while(not pxls.isEmpty()){
            var point:Point = pxls.remove() as Point;
            var transPoint = someMap.localToParent(point.x, point.y);
            if(not path.contains(transPoint)){
              isSelected = false;
              break;
            }
          }
          if(isSelected){
            mediator.addPointSelected(new Point(i, j));
          }
        }
      }
      mediator.makeCluster(SOMclstr.tolerance, SOMclstr.clusterType, e);
      //get the points that belong to all the cells to decide which belong
      selectPathElements = null;
    }else if(isTileBeingDragged){
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
    else if(e.popupTrigger or e.controlDown){
      showRightClickMenu(e, tileNum); 
    }


  }

  
  package function mouseEnterSOM(e:MouseEvent):Void{
    crosshairVisible = true;
  }
  
  package function mouseExitSOM(e:MouseEvent):Void{
    crosshairVisible = false;
    if(clusterImg == null){
      var isCellAlreadyOff:Boolean = mediator.setPointMouseOff();
      if(isCellAlreadyOff){
        updateMapStats();
      }
    }
  }
  package function SOMclicked(e:MouseEvent, tile:SOMTile):Void{
    var tileNum:Integer = Sequences.indexOf(displayedTiles, tile);
    //onMouseClicked: function (e:MouseEvent){SOMclicked(e, tileNum);}
    var pt = someMap.parentToLocal(e.x, e.y);
    //e.x = pt.x;
    //e.y = pt.y;
    if(e.button == MouseButton.PRIMARY and not e.controlDown){
      println("close right click menu");
      rightClickMenu.hide(); //The submenu causes problems with automatic hiding
      if(not e.shiftDown){
        mediator.clearPointsSelected();
      }
      mediator.addPxlSelected(pt.x, pt.y);
      mediator.makeCluster(SOMclstr.tolerance, SOMclstr.clusterType, e);
    }else if(e.popupTrigger or e.button == MouseButton.SECONDARY or e.controlDown){
      showRightClickMenu(e, tileNum);
      //rightClickMenu.show(e.node, HPos.RIGHT, VPos.BOTTOM, e.x - e.node.boundsInLocal.width, e.y - e.node.boundsInLocal.height);
    }

  }


  public function showRightClickMenu(e: MouseEvent, tileNum:Integer){
    var ptInTileGroup = tilesGroup.sceneToLocal(e.sceneX, e.sceneY);
    println("RightClickMenu: ptInTileGroup x:{ptInTileGroup.x} y:{ptInTileGroup.y}");
    if(tileNum == -1){
      histMenuItem.disable= true;
      removeTileMenuItem.disable = true;
    }else{

      var dimNum = Sequences.indexOf(somDimTiles, displayedTiles[tileNum]);
      if(dimNum != -1){
        histMenuItem.text= "Make Histogram";
        histMenuItem.disable= false;
        //TODO: TileNumber is actually not what I want. I need the dimmension #
        histMenuItem.action = function(){mediator.makeHistogram(dimNum);};
      }else{
        histMenuItem.disable= true;
      }

      removeTileMenuItem.text = "Remove {displayedTiles[tileNum].name}";
      removeTileMenuItem.disable= false;
      removeTileMenuItem.action = function(){
          if(displayedTiles[tileNum].getClass() != SOMTileBlank.class){
            insert displayedTiles[tileNum] into notDisplayedTiles;
          }
          delete displayedTiles[tileNum]};
    }

    println("RightClickMenu: TilesGroup.boundsInLocal width:{tilesGroup.boundsInLocal.width} height:{tilesGroup.boundsInLocal.height}");

    rightClickMenu.show(tilesGroup, HPos.RIGHT, VPos.BOTTOM,
        ptInTileGroup.x -tilesGroup.boundsInLocal.width , ptInTileGroup.y -tilesGroup.boundsInLocal.height);
  }
  
};
