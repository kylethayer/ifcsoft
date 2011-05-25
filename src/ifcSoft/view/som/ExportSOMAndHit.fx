/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ifcSoft.view.som;

import ifcSoft.MainApp;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogDirectorySelect;
import ifcSoft.view.dialogBox.ifcDialogStringInput;
import ifcSoft.view.dialogBox.ifcDialogCheckBox;
import ifcSoft.view.dialogBox.ifcDialogIntInput;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import ifcSoft.ApplicationFacade;
import java.awt.Point;

/**
 * @author kthayer
 */

public class ExportSOMAndHit {
 public-init var app:MainApp;
  public-init var mediator:SOMMediator;
  postinit{
    if(app == null or mediator == null){
      println("SaveClusterDialog initializer: not initialized fully");
    }else{
      initialize();
    }
  }

  var exportSOMDialog:ifcDialogBox;
  var exportSOMDirSelect:ifcDialogDirectorySelect;
  var fileNameInput:ifcDialogStringInput;
  var includeHitHistogramInput:ifcDialogCheckBox;
  var blurrTimesInput:ifcDialogIntInput;

  public function initialize(){


    exportSOMDialog =  ifcDialogBox{
      name: "Export SOM and Hit Histogram"
      okAction: exportSOMOK
      content: [
        //string input name
        fileNameInput = ifcDialogStringInput{
          name:"File name"
        }

        exportSOMDirSelect = ifcDialogDirectorySelect{
          mainApp:app
          initialDirectory: app.lastFilePath
        }
        includeHitHistogramInput = ifcDialogCheckBox{
          name: "Output Hit Histogram"
          initialCheck: false
        }
        blurrTimesInput = ifcDialogIntInput{
          name: "Blur passes on hit histograms"
          initialInt: 0
        }

      ]
      cancelAction: function():Void{app.removeDialog(exportSOMDialog); app.unblockContent();}

      blocksMouse: true
    };

    app.addDialog(exportSOMDialog);
    app.blockContent();
  }

  function exportSOMOK():Void{
    //TODO: if file already exists, ask permission to overwrite

    var name:String = fileNameInput.getInput();
    if(name == null or name.length() == 0){
      app.alert("Error: Filename blank");
      return;
    }


    var filepath:String = exportSOMDirSelect.getInput();
    if(filepath == null){
      app.alert("Error: No directory Selected");
      return;
    }

    if(not name.endsWith(".csv"))
      name+= ".csv";


    if(not filepath.endsWith("/"))
      filepath+= "/";
    var selectedFile:String = "{filepath}{name}.csv";


    var hitHistograms:Object[] = []; //multi-dimensional arrays are hard in javaFX,
                                //I cast the 2d arrays to objects for convenience
    if(includeHitHistogramInput.getInput()){ //get the density maps
       for(i in [0..mediator.SOMp.getRawSetNames().size() - 1]){
         insert mediator.getDenseBlurred(i,blurrTimesInput.getInput()) as Object into hitHistograms;
       }
    }

    var bw:BufferedWriter;
    try {
      bw = new BufferedWriter(new FileWriter(selectedFile));
      //write width and height
      bw.write("Width,{mediator.SOMwidth()}");
      bw.newLine();
      bw.write("Height,{mediator.SOMheight()}");
      bw.newLine();

      //Column headings
      //x, y, Dim1, Dim2, ... ,Hit Hist1, Hit Hist2, ...
      bw.write("x,y");
      for(dimName in mediator.getColNames()){
        bw.write(",{dimName}");
      }
      if(includeHitHistogramInput.getInput()){
        for(hitHistName in mediator.SOMp.getRawSetNames()){
          bw.write(",{hitHistName}");
        }
      }
      bw.newLine();
      
      //now do each node

      for(x in [0..mediator.SOMwidth()-1]){
        for(y in [0..mediator.SOMheight()-1]){
          bw.write("{x},{y}");
          for(i in [0..mediator.getColNames().length-1]){
            bw.write(",{mediator.SOMp.getCellVals(new Point(x, y))[i]}");
          }
          if(includeHitHistogramInput.getInput()){
            for(i in [0..mediator.SOMp.getRawSetNames().size()-1]){
              var thisHitHist:nativearray of nativearray of Float = hitHistograms[i] as (nativearray of nativearray of Float);
              bw.write(",{thisHitHist[x][y]}");
            }
          }

          bw.newLine();
        }
      }


      
      bw.close();

    } catch (ex:IOException) {
      app.facade.sendNotification(ApplicationFacade.EXCEPTIONALERT, ex, null);
      return;
    }

    app.removeDialog(exportSOMDialog);
    app.unblockContent();
  }


  function blurHitHist(hitHistsIn:Object[], blurNum:Integer): Object[] {
    var hitHists:(nativearray of nativearray of Integer)[] = hitHistsIn as (nativearray of nativearray of Integer)[];


    return null;

  }

}
