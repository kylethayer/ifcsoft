/**
 *  Copyright (C) 2011  Kyle Thayer <kyle.thayer AT gmail.com>
 *
 *  This file is part of the IFCSoft project (http://www.ifcsoft.com)
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

package ifcSoft;

//JavaFx library imports
import javafx.stage.Stage;
import javafx.stage.Alert;

import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.text.*;


//test file chooser stuff
import javax.swing.JFileChooser;
import ifcSoft.view.fileFilter.CSVFileFilter;
import ifcSoft.view.fileFilter.FCSFileFilter;
import ifcSoft.view.fileFilter.DataFileFilter;

//My Java Imports
import ifcSoft.view.MainMediator;

//My JavaFX imports
import ifcSoft.view.Menus;
import ifcSoft.view.som.CalcSOMDialog;
import org.puremvc.java.interfaces.IMediator;
import java.io.File;
import java.lang.String;
import javafx.scene.input.MouseEvent;

import ifcSoft.view.blankTab.BlankTabI;
import ifcSoft.view.blankTab.BlankTab;

import ifcSoft.view.histogram.HistTabMediator;
import ifcSoft.view.histogram.HistTab;
import ifcSoft.view.histogram.HistTabI;
import ifcSoft.view.Tabs;
import ifcSoft.view.RemoveOutliersDialog;
import ifcSoft.view.histogram.MakeHistogramDialog;
import javafx.animation.Timeline;
import ifcSoft.model.DataSetProxy;
import javafx.animation.KeyFrame;
import ifcSoft.model.dataSet.RawData;
import javafx.util.Sequences;
import ifcSoft.view.dialogBox.ifcDialogStringInput;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogText;
import ifcSoft.view.ShrinkDataSetDialog;
import ifcSoft.view.DataSetViewer;
import ifcSoft.view.scatterplot.MakeScatterDialog;
import ifcSoft.view.scatterplot.ScatterTab;
import ifcSoft.view.scatterplot.ScatterTabMediator;
import ifcSoft.view.scatterplot.ScatterTabI;
import ifcSoft.view.windrose.WindRoseTab;
import ifcSoft.view.windrose.WindRoseTabMediator;
import ifcSoft.view.windrose.WindRoseTabI;
import ifcSoft.view.windrose.MakeWindRoseDialog;



var theMainApp:MainApp;

/**
* The initial method that starts the program.
*/
function run()
{
  theMainApp = new MainApp();
  theMainApp.start();

};

/**
 * This is the Main Application file. 
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class MainApp extends MainAppI {

  var facade:ApplicationFacade;
  public var mainMediator:MainMediator;
  
  public var scene:Scene;

  var tabHeight = Tabs.tabTotalHeight; 

  var mainContent:Group = Group{layoutX:0 layoutY:tabHeight autoSizeChildren:false managed:false};

  public var contentHeight = bind scene.height - tabHeight;
  public var contentWidth = bind scene.width;

  var tabBox:Group = Group{layoutX:0 layoutY:0 autoSizeChildren:false managed:false};

  var contentBlock:Rectangle;
  var isContentBlocked:Boolean = false;
  var dialogsContent:Group = Group{};
  var everythingBlock:Rectangle;
  var isEverythingBlocked: Boolean = false;
  var blockingDialogContent:Group = Group{};
  public var menus:Menus;
  var tabs:Tabs;
  public var isShiftDownb:Boolean = false;

  public var lastFilePath:String = null;
  

  var stage:Stage = Stage {
    title: "IFCSoft Version 0.4"
    scene:  scene = Scene {
      width: 750
      height: 600
      fill: Color.BLACK
      content: [ 
        Text {
          x:50 y:14
          content: "Loading..."
          font: Font {name: "Arial" size: 14}
         }
      ]
    }
  };

  /**
  * Initialize the application.
  */
  public function start(){
    menus = Menus{app:this};

    tabs = Tabs{app:this};

    facade = ApplicationFacade.getInstance();
    stage;
    facade.startup(this);

    contentBlock = Rectangle{
      height: bind scene.height
      width: bind scene.width
      fill: Color.GREY
      opacity: .25
      blocksMouse: true
      visible: bind isContentBlocked
    };

    everythingBlock = Rectangle{
      height: bind scene.height
      width: bind scene.width
      fill: Color.GREY
      opacity: .25
      blocksMouse: true
      visible: bind isEverythingBlocked
    };

    scene.content = [
      mainContent,
      contentBlock,
      dialogsContent,
      menus.allMenus,
      //these next two are only for dialogs that block everything
      //this may not work out well in the long run (I might want a tab-block instead)
      everythingBlock,
      blockingDialogContent,
      tabBox,
      //should there be another block to block tabs as well?
    ];
  }

  /**
   * Re-draw the tabs if needed.
   */
  override public function updateTabs(){
    //Todo: Make it so that it puts the tab in the right place
    tabs.tabNames =
      for(i in [0..mainMediator.numTabs()-1]){
        mainMediator.getTab(i).getTabName();
      };
    drawTabs();
  }

  /**
   * Redraw a specific tab if needed.
   * @param tabToUpdate
   */
  override public function updateTab(tabToUpdate:Integer){
    tabs.tabNames[tabToUpdate] =  mainMediator.getTab(tabToUpdate).getTabName();
    drawTabs();
  }

  /**
   * Set the given tab as the current tab.
   * @param currentTab
   */
  override public function setCurrentTab(currentTab:Integer){
    if(tabs.currentTab != currentTab){
      tabs.currentTab = currentTab;
      drawTabs();
      clearDialog();
      if(mainMediator.getCurrentTab().isDialogContentBlocked()){
        blockContent();
      }else{
        unblockContent();
      }
      setMainContent(null);
      mainMediator.getCurrentTab().displayTab();
    }
  }



  function drawTabs():Void{
    // fill in the tabBox
    tabBox.content = tabs.makeTabs();

  }

  /**
  * This is the function called when you click on a tab.
  */
  public function selectTab(i:Integer){
    mainMediator.selectTab(i);
  }

  /**
  * This creates a new blank tab. (from double clicking on the tab bar).
  * @param me - the mouseEvent that caused the new tab to be displayed.
  */
  public function newTab(me:MouseEvent):Void{
    if(me.clickCount >= 2){
      mainMediator.newTab();
    }
  }

  /**
  * Returns the main scene object (useful for binding to scene properties).
  */
  public function getScene(): Scene{
    return scene;
  }

  /**
   * This gives the MainApp a reference to its mediator.
   * @param m - the MainMediator.
   */
  override public function setMainMediator(m:MainMediator){
    mainMediator = m;
  }
  
  /**
   * Returns the MainMediator associated with the MainApp.
   * @return
   */
  override public function getMainMediator () : MainMediator {
    return mainMediator;
  }

  /**
  * Turns on the content block (tab contents, not tab dialogs).
  */
  public function blockContent():Void{
    isContentBlocked = true;
  }

  /**
  * Turns off the content block (tab contents, not tab dialogs).
  */
  public function unblockContent():Void{
    isContentBlocked = false;

  }

  /**
  * Set the given node as the main content (of the tab).
  * @param n - The node to set as the tab content.
  */
  public function setMainContent(n:Node): Void{
    mainContent.content = n;
  }


  public function addDialog(n:ifcDialogBox): Void{
    n.xpos = contentWidth / 2 - n.layoutBounds.width;
    n.ypos = contentHeight / 2 - n.layoutBounds.height;
    if(dialogsContent.content == null or dialogsContent.content.size() == 0){
      dialogsContent.content = [n];
    }else{
      insert n into dialogsContent.content;
    }
  }

  /**
  * Add the given dialog box (node) to the tab.
  * @param n - The dialog box.
  */
  public function addDialog(n:Group): Void{
    if(dialogsContent.content == null or dialogsContent.content.size() == 0){
      dialogsContent.content = [n];
    }else{
      insert n into dialogsContent.content;
    }
  }

  /**
  * Remove the given dialog box (node) from the tab.
  * @param n - The dialog box.
  */
  public function removeDialog(n:Node): Void{
    delete n from dialogsContent.content;
  }

  /**
  * Remove all dialog boxes from the tab.
  */
  public function clearDialog(): Void{
    delete dialogsContent.content;
  }
  

  /**
  * Display the SOM Dialog box.
  */
  public function SOM(): Void{
    //TODO: This shouldn't reset everything every time

    //TODO: get last SOM settings to send to new dialog box

    var calcSOMDlg = CalcSOMDialog{app:this mainMediator:mainMediator};
    calcSOMDlg.initialize();

  }

  /**
  * Display the histogram dialog box.
  */
  public function histogramDlg():Void{
    var histDlg = MakeHistogramDialog{app:this mainMediator:mainMediator};
  }

  /**
  * Display the scatterplot dialog box.
  */
  public function scatterplotDlg():Void{
    var scatterDlg = MakeScatterDialog{app:this mainMediator:mainMediator};
    //var scatterDlg = MakeWindRoseDialog{app:this mainMediator:mainMediator};
  }

  /**
  * Display the windrose dialog box.
  */
  public function windrosePlotDlg():Void{
    //var scatterDlg = MakeScatterDialog{app:this mainMediator:mainMediator};
    var windRoseDlg = MakeWindRoseDialog{app:this mainMediator:mainMediator};
  }

  
  //override function saveSOM():Void{
    /*if(SOMview == null){
      alert("No SOM loaded");
      return;
    }
    //should I have a check to see if a SOM is loaded?
    cancelJobs();
    var selectedFile: String;

    var fileChooser:JFileChooser = new JFileChooser();
    var filter:IFlowFileFilter = new IFlowFileFilter();
    fileChooser.setFileFilter(filter);

    if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){

      println("approved!");
      selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
      if(not selectedFile.endsWith(".iflo")){
        selectedFile += ".iflo";
      }
      SOMview.saveSOM(selectedFile);
    }*/
  //}

  //override function loadSOM():Void{
  //  cancelJobs();
    /*var selectedFile: String;

    var fileChooser:JFileChooser = new JFileChooser();
    var filter:IFlowFileFilter = new IFlowFileFilter();
    fileChooser.setFileFilter(filter);

    if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){

      println("approved load SOM!");
      selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
      //init SOM view
      SOMview = new SOMvc();
      var dsp =  mainMediator.getDSP();
      var SOMMed = new SOMMediator(this);
      SOMMed.setDSP(dsp);
      SOMview.init(this, SOMMed);

      SOMview.loadSOM(selectedFile);
    }*/
  //}

  /**
  * Load a data file.
  */
  public function loadFile(){
    //cancelJobs();

    var fileChooser:JFileChooser;
    if(lastFilePath == null){
      fileChooser = new JFileChooser();
    }else{
      fileChooser = new JFileChooser(lastFilePath);
    }
    var filter:CSVFileFilter = new CSVFileFilter();
    var filter2:FCSFileFilter = new FCSFileFilter();
    var filter3:DataFileFilter = new DataFileFilter();
    fileChooser.setFileFilter(filter);
    fileChooser.setFileFilter(filter2);
    fileChooser.setFileFilter(filter3);
    fileChooser.setMultiSelectionEnabled(true);

    if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){

      lastFilePath = fileChooser.getCurrentDirectory().getAbsolutePath();
      var files: File[] = fileChooser.getSelectedFiles();
      var fileNames:String[];
      for(i in [0..files.size()-1]){
        //selectedFiles.add(files[i].getAbsolutePath());
        println("loading: {files[i].getAbsolutePath()}");
        insert files[i].getAbsolutePath() into fileNames;
        //mainMediator.loadFile(files[i].getAbsolutePath());
      }
      mainMediator.loadFile(fileNames); 
    }
  }

  var loadingFileDialog:ifcDialogBox = null;
  var filesBeingLoaded:DataSetProxy[];
  var fileProgresses:Integer[];
  var fileLoadingTexts:String[] = bind
    for(dataset in filesBeingLoaded){
      "{dataset.getFileName()}: {fileProgresses[indexof dataset]} Data points";
    };

  /**
  * Add the given dsp as a file being loaded and display the file loading dialog box.
  */
  override public function addFileLoading(dsp:DataSetProxy):Void{
    insert dsp into filesBeingLoaded;
    insert 0 into fileProgresses;
    
    if(loadingFileDialog == null){
      loadingFileDialog = ifcDialogBox{
        name: "Loading Files:"
        content: bind [
              for(text in fileLoadingTexts){
                ifcDialogText{
                  text: text
                }
              }
            ]
        layoutX: bind (contentWidth - 200) / 2
        layoutY: bind (contentHeight - 125) / 2
      }
    }

    if(Sequences.indexOf(blockingDialogContent.content,  loadingFileDialog) == -1){
      insert loadingFileDialog into blockingDialogContent.content;
      checkFileProgress();
    }

    //Make sure dialog is open, make sure clock is running to check on file progress and add them when done
  }

  function checkFileProgress():Void{
    var timeline = Timeline{
      repeatCount: 1
      keyFrames: KeyFrame{
          time: 300ms
          action: function():Void{updateFileProgress();}
        }
    };
    timeline.play();
  }


  function updateFileProgress(){
    var setsDone:Boolean[] = [
        for(i in [0..filesBeingLoaded.size()-1]){
          false;
        }
    ];
    //go through until none new left to add
    //this makes sure they load in order (otherwise
    //the finishing loading and removing from list could be
    //changing together).
    var areAnyNew:Boolean = true;
    while(areAnyNew){
      areAnyNew = false;
      for(i in [0..setsDone.size()-1]){
        var dataset = filesBeingLoaded[i];
        if((dataset.getData() as RawData).didLoad()){
          if(setsDone[i] == false){
            setsDone[i] = true;
            areAnyNew = true;
          }
        }else{
          fileProgresses[i] = dataset.getFileProgress();
        }
      }
    }

    var index = 0;
    while(index < setsDone.size()){
      if(setsDone[index] == true){
         var dataset = filesBeingLoaded[index];
         delete setsDone[index];
         delete fileProgresses[index];
         delete filesBeingLoaded[index];
         mainMediator.addNewDSP(dataset);
      }else{
        index++;
      }
    }

    if(filesBeingLoaded.size() == 0){
      delete loadingFileDialog from blockingDialogContent.content;
    }else{
      checkFileProgress();
    }
  }

  //make removeOutliers dialog stuff

  var removingOutlierDialog:ifcDialogBox = null;
  var dspsRemovingOutliers:DataSetProxy[];
  var removingOutlierProgresses:Float[];
  var removingOutlierTexts:String[] = bind
    for(dataset in dspsRemovingOutliers){
      "{dataset.getDataSetName()}";/*{removingOutlierProgresses[indexof dataset]*100}%*/
    };

  /**
   * Adds another file having outliers removed to the remove outliers display.
   * @param dsp - Data set that has been put on the job queue
   */
  override public function addRemoveOutlier(dsp:DataSetProxy):Void{
    insert dsp into dspsRemovingOutliers;
    insert 0 into removingOutlierProgresses;

    if(removingOutlierDialog == null){
      removingOutlierDialog = ifcDialogBox{
        name: "Removing Outliers:"
        content: bind [
              for(text in removingOutlierTexts){
                ifcDialogText{
                  text: text
                }
              }
            ]
        layoutX: bind (contentWidth - 200) / 2
        layoutY: bind (contentHeight - 125) / 2
      }
    }
    if(Sequences.indexOf(blockingDialogContent.content,  removingOutlierDialog) == -1){
      insert removingOutlierDialog into blockingDialogContent.content;
      checkRemovingOutliersProgress();
    }

    //Make sure dialog is open, make sure clock is running to check on file progress and add them when done
  }

  function checkRemovingOutliersProgress():Void{
    var timeline = Timeline{
      repeatCount: 1
      keyFrames: KeyFrame{
          time: 300ms
          action: function():Void{updateOutliersProgress();}
        }
    };
    timeline.play();
  }


  function updateOutliersProgress(){
    for(dataset in dspsRemovingOutliers){
      if(not dataset.isRemovingOutliers()){
        delete removingOutlierProgresses[indexof dataset];
        delete dspsRemovingOutliers[indexof dataset];
        //mainMediator.addNewDSP(dataset);
        //if dsp not in current dsps, then we need to add it and give it a name
        if(dataset.getLastOutliersRemovied() >0){//if more than 0 outliers removed, call the getName thingy and the add newDSP
          if(not mainMediator.isInDataSets(dataset) ){
            nameDSP(dataset,"{dataset.getLastOutliersRemovied()} of {dataset.getLastOutliersRemovied()+dataset.getDataSize()} removed",
                "Data Set");
          }
        }  
      }else{
        removingOutlierProgresses[indexof dataset] = dataset.getRemoveOutlierProgress();
      }
    }
    if(dspsRemovingOutliers.size() == 0){
      delete removingOutlierDialog from blockingDialogContent.content;
    }else{
      checkRemovingOutliersProgress();
    }
  }

  override function nameDSP(dsp:DataSetProxy, info:String, type:String):Void{
    var dlg:ifcDialogBox;
    var strIn: ifcDialogStringInput;
    dlg = ifcDialogBox{
      blocksMouse: true
      name: "Choose Name for {type}"
      content:[
        ifcDialogText{text:info},
        strIn = ifcDialogStringInput{
          name:"{type} Name: "
          initialString:dsp.getDataSetName()
          inputWidth: 20
        }
      ]
      okAction: function():Void{chooseDataSetNameOK(dlg, strIn, dsp)}
      cancelAction: function():Void{
          removeDialog(dlg);
          //TODO: dataset.delete();
        }
    }
    this.addDialog(dlg); //TODO: send to blocking content
  }


  function chooseDataSetNameOK(dlg: ifcDialogBox, strIn: ifcDialogStringInput, dsp:DataSetProxy):Void{
    dsp.setDataSetName(strIn.getInput());
    facade.sendNotification(ApplicationFacade.ADDNEWDSP, dsp, null);
    removeDialog(dlg);

  }



  /**
  * Display the Remove Outliers dialog box.
  */
  public function outliersDialog(dsp:DataSetProxy):Void{
    var rmvOutliersDlg = RemoveOutliersDialog{
                mainMediator: mainMediator
                mainApp: this
                dsp:dsp
                }
    rmvOutliersDlg.outliersDialog();

  }

  public function shrinkDatasetDialog(dsp:DataSetProxy):Void{
    var shrinkDatasetDlg = ShrinkDataSetDialog{
                mainMediator: mainMediator
                mainApp: this
                dsp:dsp
                }
    shrinkDatasetDlg.shrinkDatasetDialog();

  }

  public function viewDatasetDialog():Void{
    var datasetViewDlg = DataSetViewer{
                mainMediator: mainMediator
                mainApp: this
                }
    datasetViewDlg.dataSetViewer();
  }

  /**
   * Displays an alert with the given message.
   * @param s
   */
  override public function alert(s:String):Void{
    Alert.inform(s);
  }

  /**
  * Register the given mediator in pureMVC.
  * @param med - the mediator to be put in the pureMVC framework.
  */
  public function registerMediator(med:IMediator){
    facade.registerMediator(med);
  }

  /**
   * This function makes the blankTab JavaFX object
   *
   * To create a JavaFX object, you have to do it in a JavaFX object, so I do it here.
   * @return
   */
  override function makeBlankTab():BlankTabI{
    var bt:BlankTab = BlankTab{app: this};
    return bt as BlankTabI;
  }

  /**
   * This function makes the histTab JavaFX object
   *
   * To create a JavaFX object, you have to do it in a JavaFX object, so I do it here.
   * @param histMediator
   * @return
   */
  override function  makeHistTab(histMediator:HistTabMediator): HistTabI{
    var ht = HistTab{app:this  histMediator:histMediator};
    return ht as HistTabI;
  } 

  /**
   * This function makes the scatterTab JavaFX object
   *
   * To create a JavaFX object, you have to do it in a JavaFX object, so I do it here.
   * @param histMediator
   * @return
   */
  override function  makeScatterTab(scatterMediator:ScatterTabMediator): ScatterTabI{
    var st = ScatterTab{app:this  scatterMediator:scatterMediator};
    return st as ScatterTabI;
  }

  override function  makeWindRoseTab(windroseMediator:WindRoseTabMediator): WindRoseTabI{
    var st = WindRoseTab{app:this  windRoseTabMediator:windroseMediator};
    return st as WindRoseTabI;
  }

};

