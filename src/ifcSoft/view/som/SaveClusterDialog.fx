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

import ifcSoft.MainApp;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogCheckBox;
import ifcSoft.view.dialogBox.ifcDialogStringInput;
import ifcSoft.view.dialogBox.ifcDialogHBox;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;
import ifcSoft.view.dialogBox.ifcDialogDirectorySelect;
import ifcSoft.view.dialogBox.ifcDialogSpacer;
import java.awt.image.BufferedImage;
import ifcSoft.view.dialogBox.ifcDialogSummDataSelect;

/**
 * @author Kyle Thayer <kthayer@emory.edu>
 */


public class SaveClusterDialog {

//have it save last settings used


//options for
  //save cluster as new data set
  //save cluster to file (with option to save filename and data point name with it)
  //save screenshot of cluster
    //http://www.rgagnon.com/javadetails/java-0489.html
  //save cluster statistics
    //allow selection of summary data set or creation of new one

  public-init var app:MainApp;
  public-init var mediator:SOMMediator;
  postinit{
    if(app == null or mediator == null){
      println("SaveClusterDialog initializer: not initialized fully");
    }else{
      initialize();
    }
  }

  var saveClusterDialog:ifcDialogBox;
  var dataSetNameBox:ifcDialogStringInput;
  var saveAsDataSetCheck:ifcDialogCheckBox;

  var saveClusterStatsCheck:ifcDialogCheckBox;
  var summDataSelect:ifcDialogSummDataSelect;

  var saveClusterToFileCheck:ifcDialogCheckBox;
  var saveClusterDirSelect:ifcDialogDirectorySelect;

  var saveScreenshotCheck:ifcDialogCheckBox;
  var saveScreenshotDirSelect:ifcDialogDirectorySelect;

  var settings:SaveClusterSettings;

  var screenshot:BufferedImage = null;


  public function initialize(){

    screenshot = mediator.getScreenshot(); //must get the screenshot before I draw the new menu

    settings = mediator.lastSaveClustSettings;
    if(settings == null){
      settings = new SaveClusterSettings()
    }

    settings.clusterName = null;


    saveClusterDialog =  ifcDialogBox{
      name: "Save Cluster"
      okAction: saveClusterOK
      content: [
        dataSetNameBox = ifcDialogStringInput{
          name: "Cluster name: "
          initialString: "Cluster"
        },
        ifcDialogSpacer{height: 10},

        saveAsDataSetCheck = ifcDialogCheckBox{
          name:"Save Cluster as new data set"
          initialCheck: settings.saveClusterAsNewDataSet
        },
        ifcDialogSpacer{height: 10},

        ifcDialogHBox{
          content:[
            saveClusterStatsCheck = ifcDialogCheckBox{
              name:"Save cluster statistics"
              initialCheck: settings.saveClusterStats
            },
            summDataSelect = ifcDialogSummDataSelect{
              mainApp: app
              dataset: settings.summDataSet
              disable: bind not saveClusterStatsCheck.ischecked
            }
          ]
        },
        ifcDialogSpacer{height: 10},

        saveClusterToFileCheck = ifcDialogCheckBox{
          name:"Save cluster to file"
          initialCheck: settings.saveClusterToFile
        }, //TODO: add options for save name, save file, etc
        saveClusterDirSelect = ifcDialogDirectorySelect{
          mainApp:app
          initialDirectory: settings.fileDirectory
          disable: bind not saveClusterToFileCheck.ischecked
        }
        ifcDialogSpacer{height: 10},

        saveScreenshotCheck = ifcDialogCheckBox{
          name:"Save screenshot"
          initialCheck: settings.saveScreenshot
        },
        saveScreenshotDirSelect = ifcDialogDirectorySelect{
          mainApp:app
          initialDirectory: settings.screenshotDirectory
          disable: bind not saveScreenshotCheck.ischecked
        },
        ifcDialogSpacer{height: 10},

      ]
      cancelAction: function():Void{app.removeDialog(saveClusterDialog)}

      blocksMouse: true
    };

    app.addDialog(saveClusterDialog);
  }

  function saveClusterOK():Void{

    if((saveClusterToFileCheck.getInput() and saveClusterDirSelect.getInput() == null)
        or (saveScreenshotCheck.getInput() and saveScreenshotDirSelect.getInput() == null)){
      app.alert("Directories not selected");
      return;
    }

    //TODO: check if cluster name is used in file, summary data, etc. before and warn user

    settings.clusterName = dataSetNameBox.getInput();
    settings.saveClusterAsNewDataSet = saveAsDataSetCheck.getInput();
    settings.saveClusterStats = saveClusterStatsCheck.getInput();
    settings.summDataSet = summDataSelect.getDataSet(); //need to be careful selecting summ set

    settings.saveClusterToFile = saveClusterToFileCheck.getInput();
    settings.fileDirectory = saveClusterDirSelect.getInput();
    //TODO: check if the directory already contains this file

    settings.saveScreenshot = saveScreenshotCheck.getInput();
    settings.screenshotDirectory = saveScreenshotDirSelect.getInput();

    mediator.lastSaveClustSettings = settings;

    //do the actual saving
    mediator.saveCluster(settings, screenshot);

    app.removeDialog(saveClusterDialog);
  }


}
