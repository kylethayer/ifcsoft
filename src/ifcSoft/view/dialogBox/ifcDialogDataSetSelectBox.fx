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
package ifcSoft.view.dialogBox;

import ifcSoft.model.DataSetProxy;
import java.lang.Exception;
import ifcSoft.MainApp;
import ifcSoft.view.MainMediator;
import javafx.util.Sequences;
import javafx.stage.Alert;
import ifcSoft.view.synchDataSets.SynchDataSetDialog;
import javafx.scene.control.ScrollView;
import ifcSoft.view.synchDataSets.synchedColumn;
import ifcSoft.view.synchDataSets.synchedColumnFX;

/**
 * @author Kyle Thayer
 */

public class ifcDialogDataSetSelectBox extends ifcDialogBox {
  public-init var mainApp:MainApp;

  public-init var initialDataSets:DataSetProxy[];

  var selectedDataSets:Boolean[];
  var dataSetCheckBoxes:ifcDialogCheckBox[];
  var selectAllButton:ifcDialogButton;

  var synchColumns:synchedColumn[] = null;

  init{
    if(mainApp == null){ //This needs mainApp to be able to ask about data sets
      throw new Exception("ifcDialogDataSetSelect: mainApp must be initialized");
    }

    var mainMed:MainMediator = mainApp.getMainMediator();

    selectedDataSets =
      for(i in [0..mainMed.numDSPs()-1]){
        if(Sequences.indexOf(initialDataSets, mainMed.getDSP(i)) >= 0){
          true;
        }else{
          false;
        }
      };


    dataSetCheckBoxes =
      for(i in [0..selectedDataSets.size()-1]){
            ifcDialogCheckBox{
              name:mainMed.getDSP(i).getDataSetName()
              initialCheck: selectedDataSets[i]
            };
        };

    selectAllButton = ifcDialogButton{
      action: selectAll
      text: "Select All"
    }


    children = ifcDialogBox{
      name:"Select Data Set(s)"
      content:[
          selectAllButton,
          ifcDialogScrollView{
            node:ifcDialogVBox{content:dataSetCheckBoxes}
            maxWidth:400
            maxHeight:400
          }
        ]
      okAction:okPressed
      okName:okName
      cancelAction:cancelAction
      cancelName:cancelName
    };
  }

  function selectAll():Void{
    var someUnselected:Boolean = false;
    for(checkBox in dataSetCheckBoxes){
      if(not checkBox.getInput()){
        someUnselected = true;
      }
    }

    if(someUnselected){ //select all
      for(checkBox in dataSetCheckBoxes){
        checkBox.select();
      }
    }else{ //unselect all
      for(checkBox in dataSetCheckBoxes){
        checkBox.unSelect();
      }
    }



  }


  function okPressed():Void{
    //make sure a data set is selected and compare columns before returning
    var isDataSetSelected:Boolean = false;
    var dataColumnNames:String[] = null;
    var doDataColumnsMatch:Boolean = true;
    for(i in [0..selectedDataSets.size()-1]){
      if(dataSetCheckBoxes[i].getInput()){
        isDataSetSelected = true;

        if(dataColumnNames == null){ //if first, get names
          dataColumnNames = mainApp.getMainMediator().getDSP(i).getColNames();
        }
        else//if not first, compare current names to former
        {
          var currentDCNames:String[] = mainApp.getMainMediator().getDSP(i).getColNames();
          if(dataColumnNames.size() != currentDCNames.size()){
            doDataColumnsMatch = false;
          }else{
            for(name in dataColumnNames){
              if(not name.equals(currentDCNames[indexof name])){
                doDataColumnsMatch = false;
              }
            }
          }
        }
      }
    }

    if(isDataSetSelected){
      if(doDataColumnsMatch){
        okAction();
      }else{
        var synchD:SynchDataSetDialog = SynchDataSetDialog{
          mainMediator:mainApp.getMainMediator()
          mainApp:mainApp
          allDimNames: getAllDimNames()
          initialColNames: dataColumnNames
          okAction:function():Void{
              synchColumns = synchD.getSynchCols();
              okAction();
            }
        }
        synchD.synchDataDialog();
      }
    }else{
      Alert.inform("No data set selected.");
      return
    }
  }

  public function getAllDimNames():String[]{
    var allDimNames:String[];
    for(i in [0..selectedDataSets.size()-1]){
      if(dataSetCheckBoxes[i].getInput()){
        if(allDimNames == null){ //if first, get names
          allDimNames = mainApp.getMainMediator().getDSP(i).getColNames();
        }
        else//if not first, compare current names to former
        {
          var currentDCNames:String[] = mainApp.getMainMediator().getDSP(i).getColNames();
          for(name in currentDCNames){
            var isNameAleradyThere = false;
            for(alreadyName in allDimNames){
              if(name.equals(alreadyName)){
                isNameAleradyThere = true;
              }
            }
            if(not isNameAleradyThere)
              insert name into allDimNames;
          }
          
        }
      }
    }
    return allDimNames;
  }



  public function getDataSets():DataSetProxy[]{
    var datasets:DataSetProxy[];
    for(i in [0..selectedDataSets.size()-1]){
      if(dataSetCheckBoxes[i].getInput()){
        insert mainApp.getMainMediator().getDSP(i) into datasets;
      }
    }
    return datasets;
  }

  public function getSynchColumns():synchedColumn[]{
    println("ifcDialogDataSetSelectBox: getSynchColumns, length:{synchColumns.size()}");
    return synchColumns;
  }



}
