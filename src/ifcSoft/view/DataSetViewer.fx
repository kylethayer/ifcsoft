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

import ifcSoft.MainApp;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;
import ifcSoft.view.dialogBox.ifcDialogDataTable;
import ifcSoft.view.dialogBox.ifcDialogButton;
import ifcSoft.view.dialogBox.ifcDialogHBox;
import ifcSoft.model.DataSetProxy;
import javax.swing.JFileChooser;
import ifcSoft.view.fileFilter.CSVFileFilter;
import java.io.File;
import java.io.BufferedWriter;
import ifcSoft.model.dataSet.summaryData.SummaryData;
import java.io.IOException;
import java.io.FileWriter;

/**
 * @author kthayer
 */

public class DataSetViewer {
  public-init var mainMediator:MainMediator;
  public-init var mainApp:MainApp;


  //var shrinkAmtInput:ifcDialogFloatInput;
  var dataSetSelect:ifcDialogDataSetSelect= ifcDialogDataSetSelect{
            mainApp:mainApp,
            okAction: function():Void{dataSetTable = getTable()}
            };
  var dataSetTable:ifcDialogDataTable = getTable();
  //var saveAsNewDS:ifcDialogRadioButtons;
  var dataSetViewerDlg:ifcDialogBox =
    ifcDialogBox{
      name: "Data Set Viewer"
      content:bind [
        dataSetSelect,
        ifcDialogHBox{
          content:[
            ifcDialogButton{
              text:"Save\nData Set"
              action:function(){saveDataSet();}
            },
            ifcDialogButton{
              text:"Remove\nOutliers"
              action: function(){mainApp.outliersDialog(getCurrentDataSet());}
            },
            ifcDialogButton{
              text:"Shrink\nData Set"
              action:function(){mainApp.shrinkDatasetDialog(getCurrentDataSet());}
            },
          ]
        },
        dataSetTable
        ]
      cancelName: "Close"
      cancelAction: function():Void{mainApp.removeDialog(dataSetViewerDlg)}
    };

  function  getTable():ifcDialogDataTable{
    return ifcDialogDataTable{
          dataset: dataSetSelect.getDataSets()[0].getData()
          }
  }


  /**
  * Create and display the data Set Viewer.
  */
  public function dataSetViewer():Void{
    if(mainMediator.getDSP(0) == null){
      mainApp.alert("No Data Set Loaded");
      return;
    }
    mainApp.addDialog(dataSetViewerDlg);
  }

  public function getCurrentDataSet():DataSetProxy{
    var datasets = (dataSetSelect.getDataSets());
    if(datasets.size() == 0){
      mainApp.alert("No data set selected");
      mainApp.unblockContent();
      return null;
    }

    var finaldsp:DataSetProxy = mainMediator.getDataSet(dataSetSelect.getDataSets(), dataSetSelect.getSynchColumns());
    if(finaldsp == null){
      println("Error in data set combination");
      return null;
    }

    return finaldsp;
  }


  function saveDataSet():Void{
    var dsp:DataSetProxy = getCurrentDataSet();
    if(dsp == null){
      return;
    }
    var fileChooser:JFileChooser;
    if(mainApp.lastFilePath == null){
      fileChooser = new JFileChooser();
    }else{
      fileChooser = new JFileChooser(mainApp.lastFilePath);
    }
    var filter:CSVFileFilter = new CSVFileFilter();
    fileChooser.setFileFilter(filter);

    if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){

      mainApp.lastFilePath = fileChooser.getCurrentDirectory().getAbsolutePath();
      var files: File = fileChooser.getSelectedFile();
      println("saving: {files.getAbsolutePath()}");
      var newFilename =files.getAbsolutePath();

      if(not newFilename.endsWith(".csv")){
        newFilename ="{newFilename}.csv";
      }


      var bw:BufferedWriter;
      try {
        bw = new BufferedWriter(new FileWriter(newFilename));
        if(dsp.getData().hasPointNames()){
          bw.write("name,");
        }

        for(dim in dsp.getColNames()){
          bw.write(dim);
          if(indexof dim == dsp.getColNames().length-1){
            bw.newLine();
          }else{
            bw.write(",");
          }
        }


        for(i in [0..dsp.getDataSize()-1]){
          if(dsp.getData().hasPointNames()){
            bw.write("{dsp.getData().getPointName(i)},");
          }

          for(j in [0..dsp.getColNames().length-1]){
            bw.write("{dsp.getData().getVals(i)[j]}");
            if(indexof j == dsp.getColNames().length-1){
              bw.newLine();
            }else{
              bw.write(",");
            }
          }

        }
        bw.close();

      }catch(ex:IOException){
        //if(ex.)
        println("IO Error:{ex}");
        return;
      }

    }


  }

}
