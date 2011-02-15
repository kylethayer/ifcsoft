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
package ifcSoft.view;

import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.Group;
import javafx.scene.transform.*;
import javafx.scene.text.*;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;

import javafx.scene.control.Button;
import javafx.stage.Alert;
import ifcSoft.MainApp;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogChoiceBox;


//Static variables
public def menuBorderColor=Color.LIGHTGRAY;
public def menuBackgroundColor=Color.rgb(138,123,102);
def hMenuW=700;  //for horizontal menus
def hMenuH=300;
def vMenuW=500;  //for verticle menus
def vMenuH=300;
def menuTabW=150;
package def menuTabH=20;
package def menuBevel = 10;

/**
 * This JavaFX file handles all the Menu actions.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class Menus{

  public-init var app:MainApp;

  postinit{
    if(app == null){
      println("Menus initializer: not initialized fully");
    }else{
      buildMenus();
    }

  }

  // ************constants************
  
  //Menu building information

  package def hMenuPath =  [ // Horizontal menu
     MoveTo { x: -hMenuW/2+menuBevel y: 0 },
     HLineTo { x: -menuTabW/2 - menuBevel },
     CubicCurveTo {
       controlX1: -menuTabW/2  controlY1:   0
       controlX2: -menuTabW/2  controlY2: menuTabH
       x: -menuTabW/2 + menuBevel  y:  menuTabH
     },
     HLineTo { x: menuTabW/2 - menuBevel },
     CubicCurveTo {
       controlX1: menuTabW/2  controlY1: menuTabH
       controlX2: menuTabW/2  controlY2: 0
       x: menuTabW/2 + menuBevel  y:  0
     },
     HLineTo{ x: hMenuW/2 - menuBevel},
     ArcTo { x:  hMenuW/2  y: -menuBevel  radiusX: menuBevel  radiusY: menuBevel },
     VLineTo{ y: -hMenuH},
     HLineTo{ x: -hMenuW/2},
     VLineTo{ y: -menuBevel},
     ArcTo { x:  -hMenuW/2+menuBevel  y: 0  radiusX: menuBevel  radiusY: menuBevel },
   ];
   
  package def vMenuPath =  [ //vertical menu
     MoveTo { x: -vMenuW/2+menuBevel y: 0 },
     HLineTo { x: -menuTabW/2 - menuBevel },
     CubicCurveTo {
       controlX1: -menuTabW/2  controlY1:   0
       controlX2: -menuTabW/2  controlY2: menuTabH
       x: -menuTabW/2 + menuBevel  y:  menuTabH
     },
     HLineTo { x: menuTabW/2 - menuBevel },
     CubicCurveTo {
       controlX1: menuTabW/2  controlY1: menuTabH
       controlX2: menuTabW/2  controlY2: 0
       x: menuTabW/2 + menuBevel  y:  0
     },
     HLineTo{ x: vMenuW/2 - menuBevel},
     ArcTo { x:  vMenuW/2  y: -menuBevel  radiusX: menuBevel  radiusY: menuBevel },
     VLineTo{ y: -vMenuH},
     HLineTo{ x: -vMenuW/2},
     VLineTo{ y: -menuBevel},
     ArcTo { x:  -vMenuW/2+menuBevel  y: 0  radiusX: menuBevel  radiusY: menuBevel },
   ];
   
   
   
   //  ************************ Object stuff *********************
   
   
  //menu access info
  package var fileMenu : Group;
  package var dataMenu : Group;
  package var displayMenu : Group;

  package var fileMenuOffset : Number = 0;
  package var dataMenuOffset : Number  = 0;
  package var displayMenuOffset : Number  = 0;

  var fullscreenButton:Button;
   
  package var fileMenuOpen = Timeline {
     keyFrames: [
        at (0s) {fileMenuOffset => fileMenuOffset},
        at (.3s) {fileMenuOffset => 100 tween Interpolator.EASEOUT}
     ]};
  package var fileMenuClose = Timeline {
     keyFrames: [
        at (0s) {fileMenuOffset => fileMenuOffset},
        at (.3s) {fileMenuOffset => 0 tween Interpolator.EASEOUT}
     ]};
     
  package var dataMenuOpen = Timeline {
     keyFrames: [
        at (0s) {dataMenuOffset => dataMenuOffset},
        at (.3s) {dataMenuOffset => 100 tween Interpolator.EASEOUT}
     ]};
  package var dataMenuClose = Timeline {
     keyFrames: [
        at (0s) {dataMenuOffset => dataMenuOffset},
        at (.3s) {dataMenuOffset => 0 tween Interpolator.EASEOUT}
     ]};
   
  package var displayMenuOpen = Timeline {
     keyFrames: [
        at (0s) {displayMenuOffset => displayMenuOffset},
        at (.3s) {displayMenuOffset => 100 tween Interpolator.EASEOUT}
     ]};
  package var displayMenuClose = Timeline {
     keyFrames: [
        at (0s) {displayMenuOffset => displayMenuOffset},
        at (.3s) {displayMenuOffset => 0 tween Interpolator.EASEOUT}
     ]};
  
  public var allMenus:Group;


  /**
  * Create all the menu objects and the group that containts them.
  */
  function buildMenus(){
    allMenus = Group{
      content:[
        fileMenu = Group {
          content:[
            Path{  stroke: menuBorderColor,
                   fill: menuBackgroundColor,
                elements: [vMenuPath],
                transforms: Rotate { angle: 270 }},
            Text {  x: -20  y: -4
              font: Font { size: menuTabH-5 }
              content: "File"
              fill: Color.WHITE
              transforms: Rotate { angle: 90 }
            },
            Button{
              layoutX: -90 layoutY: -150
              text: "Load Data"
              blocksMouse: false
              action:  function(){app.loadFile();}
            },
            Button{
              layoutX: -90 layoutY: -120
              text: "Load Sample\nData"
              blocksMouse: false
              action: loadSampleFile
            },
            Button{
              layoutX: -90 layoutY: 100
              text: "Help"
              blocksMouse: false
              action:  function(){displayHelp();}
            },
            Button{
              layoutX: -90 layoutY: 125
              text: "About"
              blocksMouse: false
              action:  function(){displayAbout();}
            },
            /*Button{
              layoutX: -90 layoutY: -200
              text: "Load SOM"
              blocksMouse: false
              action:  function(){app.loadSOM();}
            },
            Button{
              layoutX: -90 layoutY: -150
              text: "Save SOM"
              blocksMouse: false
              action:  function(){app.saveSOM();}
            },*/
          ]//group content
          layoutX: bind fileMenuOffset,
          layoutY: bind app.getScene().height/2,
          onMouseEntered: function(event){fileMenuClose.stop();fileMenuOpen.evaluateKeyValues();fileMenuOpen.play();}
          onMouseExited: function(event){fileMenuOpen.stop();fileMenuClose.evaluateKeyValues();fileMenuClose.play();}
          blocksMouse: true
        }
        dataMenu = Group {
          content:[
            Path{  stroke: menuBorderColor,
                   fill: menuBackgroundColor,
                elements: [vMenuPath],
                transforms: Rotate { angle: 90 }},
            Text {  x: -20  y: -4
              font: Font { size: menuTabH-5 }
              content: "Data"
              fill: Color.WHITE
              transforms: Rotate { angle: 270 }
            },
            Button{
              layoutX: 10 layoutY: -200
              text: "Load Data"
              blocksMouse: false
              action:  function(){app.loadFile();}
            },
            Button{
              layoutX: 10 layoutY: -170
              text: "Load Sample\nData"
              blocksMouse: false
              action: loadSampleFile
            },
            Button{
              layoutX: 10 layoutY: -100
              text: "Remove\nOutliers"
              blocksMouse: false
              action:  function(){app.outliersDialog(false);}
            }
            Button{
              layoutX: 10 layoutY: -50
              text: "Shrink\nData Set"
              blocksMouse: false
              action:  function(){app.shrinkDatasetDialog();}
            }
            Button{
              layoutX: 10 layoutY: 0
              text: "View\nData Set"
              blocksMouse: false
              action:  function(){app.viewDatasetDialog();}
            }
          ]//group content
          layoutX: bind app.getScene().width-1 - dataMenuOffset,
          layoutY: bind app.getScene().height/2 
          onMouseEntered: function(event){dataMenuClose.stop();dataMenuOpen.evaluateKeyValues();dataMenuOpen.play();}
          onMouseExited: function(event){dataMenuOpen.stop();dataMenuClose.evaluateKeyValues();dataMenuClose.play();}
          blocksMouse: true
        }
        displayMenu = Group {
          content:[
            Path{  stroke: menuBorderColor,
              fill: menuBackgroundColor,
              elements: [hMenuPath],
              transforms: Rotate { angle: 180 }},
            Text {  x: -25  y: -4
              font: Font { size: menuTabH-5 }
              content: "Display"
              fill: Color.WHITE
            }
            Button{
              layoutX: 0 layoutY: 50
              text: "Calculate SOM"
              blocksMouse: false
              action:  function(){app.SOM();}
            },
            /*fullscreenButton = Button{
              layoutX: 150 layoutY: 50
              text: bind{ if(app.scene.stage.fullScreen){"Exit Full Screen"}else{"Full Screen"}}
              blocksMouse: false
              action:  function(){app.scene.stage.fullScreen = not app.scene.stage.fullScreen}
            }*/

          ]//group content
          layoutX: bind app.getScene().width/2,
          layoutY: bind app.getScene().height-1  - displayMenuOffset,
          onMouseEntered: function(event){displayMenuClose.stop();displayMenuOpen.evaluateKeyValues();displayMenuOpen.play();}
          onMouseExited: function(event){displayMenuOpen.stop();displayMenuClose.evaluateKeyValues();displayMenuClose.play();}
          blocksMouse: true
        }
      ]
    }
  }
  
  /**
  * Return the group containing the menus.
  */
  public function getAllMenus():Group{
    return allMenus;
  }


  //Gives two default sample data set groups to load from the website
  var sampleFileDialog:ifcDialogBox;
  var sampleFileChoice:ifcDialogChoiceBox;
  var flowCytBeads:String = "Flow Cytometry Beads";
  var shuttleData:String = "Shuttle Data";
  function loadSampleFile():Void{
    sampleFileChoice = ifcDialogChoiceBox{
      items: [flowCytBeads, shuttleData]
    }

    sampleFileDialog = ifcDialogBox{
      name: "Load Sample Data Sets From Website"
      okAction: loadSampleFileOK
      content: [sampleFileChoice]
      cancelAction: function():Void{app.removeDialog(sampleFileDialog)}

      blocksMouse: true
    };

    app.addDialog(sampleFileDialog);
  }

  function loadSampleFileOK():Void{
    var baseurl = "http://mathcs.emory.edu/~kthayer/ifcsoft/datasets/";
    if(sampleFileChoice.selectedItem == flowCytBeads){
      app.getMainMediator().loadFile(
          ["{baseurl}BEADS_APC.csv", "{baseurl}BEADS_FITC.csv",
          "{baseurl}BEADS_PE.csv", "{baseurl}BEADS_PERCP.csv", "{baseurl}BEADS_UNSTAINED.csv", 
          "{baseurl}BEADS_TEST.csv", "{baseurl}BEADS_ALL.csv", ]);
      app.alert("This Sample Data is of Flow Cytometry test beads.\n\n"
                "The important channels are: FITC-A, PE-A, APC-A, and PERCP-A. The rest "
                "should be turned off in SOM Weighting.\n\n"
                "There should be a group of beads that is high in each of those dimensions "
                "and another group that is low in all of them.");
    }else if(sampleFileChoice.selectedItem == shuttleData){
       app.getMainMediator().loadFile(
          ["{baseurl}train-Bpv_Close.csv", "{baseurl}train-Bpv_Open.csv", "{baseurl}train-Bypass.csv",
          "{baseurl}train-Fpv_Close.csv", "{baseurl}train-Fpv_Open.csv", "{baseurl}train-High.csv",
          "{baseurl}train-Rad_Flow.csv",
          "{baseurl}test-Bpv_Close.csv", "{baseurl}test-Bpv_Open.csv", "{baseurl}test-Bypass.csv",
          "{baseurl}test-Fpv_Close.csv", "{baseurl}test-Fpv_Open.csv", "{baseurl}test-High.csv",
          "{baseurl}test-Rad_Flow.csv"]);
       app.alert("This Sample Data from the UCI Machine Learning Repository.\n\n"
                "It is best to make an SOM first of all the \"train\" sets combined.\n\n"
                "Then you should use right-click -->  \"Compare Data Set\" and add each \"test\" set one at a time.");
    }else{
      println("Somehow loadSampleFileOK got invalid sample file: {sampleFileChoice.selectedItem}");
    }

    app.removeDialog(sampleFileDialog);

  }



  function displayHelp():Void{

    Alert.inform("IFCsoft v 0.4\n"
      "Note: This is still an early test version, so it has limited functionality and sometimes gives incorrect numbers.\n\n"
      "All data sets must be in FCS format or CSV format where the first row is the column titles and all values are numeric.\n\n"
      "Self-Organizing map shortcuts:\n"
      "Select a pt/cluster of nodes: left-click on a map\n"  
      "Use right-click (or ctrl-click on a Mac) to get options on the SOM\n"
      "Draw a shape to select an area, hold the right mouse button down or alt+left mouse button down.\n"
      "Hold down shift to add next selection to the current one.\n"
      "The SOM tiles can be rearranged by dragging them to a new position.\n"
      "The names on the SOM tiles can be changed by double-clicking them, then pressing enter when done."
      );

  }

  function displayAbout():Void{

    Alert.inform("IFCSoft v 0.4\n"
      "Visualizing and analyzing multi-dimensional data sets intuitively, interactively and using novel approaches.\n"
      "http://www.ifcsoft.com\n\n"
      "This is an open-source project released under the GNU GPL v3 or later.\n\n"
      "IFCSoft  Copyright (C) 2011  Kyle Thayer. "
      "This program comes with ABSOLUTELY NO WARRANTY. "
      "This is free software, and you are welcome to redistribute it "
      "under certain conditions; see website for details.\n\n"
      "IFC Soft was originally created as part of the iFlowCyt project at Emory University."
      );
  }
  
};
