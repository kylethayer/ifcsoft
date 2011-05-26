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

import ifcSoft.model.som.SOMProxy;
import org.puremvc.java.interfaces.IMediator;
import ifcSoft.MainApp;
import ifcSoft.model.DataSetProxy;

import ifcSoft.view.Tab;
import ifcSoft.view.MainMediator;
import java.lang.Exception;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogDataSetSelect;
import ifcSoft.view.dialogBox.ifcDialogIntInput;
import ifcSoft.view.dialogBox.ifcDialogButton;

import ifcSoft.view.dialogBox.ifcDialogFloatInput;
import ifcSoft.model.dataSet.dataSetScalar.LogScaleNormalized;
import ifcSoft.model.dataSet.dataSetScalar.VarianceNormalized;
import ifcSoft.view.dialogBox.ifcDialogChoiceBox;
import ifcSoft.model.dataSet.dataSetScalar.MinMaxNormalized;
import ifcSoft.model.dataSet.dataSetScalar.UnscaledDataSet;
import ifcSoft.model.dataSet.dataSetScalar.PCANormalized;
import ifcSoft.model.som.SOMSettings;
import javafx.util.Math;
import ifcSoft.view.dialogBox.ifcDialogItem;


/**
 * This class holds the "Calculate SOM" dialog box for giving options to
 * calculate a SOM.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class CalcSOMDialog {

  public-init var app:MainApp;
  public-init var mainMediator:MainMediator;

  postinit{
    if(app == null or mainMediator == null){
      throw new Exception("CalcSOMDialog initializer: not initialized fully");
    }
  }
  
  
  var somSettings: SOMSettings = new SOMSettings();


  var calcSOMDialog:ifcDialogBox;

  var dataSetSelect:ifcDialogDataSetSelect;
  var weightButton:ifcDialogButton;
  var advancedOptionsBtn:ifcDialogButton;

  var SOMDialogDisabled:Boolean = false;

  public function initialize(){
    dataSetSelect = ifcDialogDataSetSelect{
              mainApp:app
              openAction: function():Void{SOMDialogDisabled = true;}
              okAction: function():Void{SOMDialogDisabled = false;}
              cancelAction: function():Void{SOMDialogDisabled = false;}
              };

    weightButton = ifcDialogButton{
      text: "Choose SOM Weights"
      action: getSOMWeights
    };

    advancedOptionsBtn = ifcDialogButton{
      text: "Advanced Options"
      action: getAdvancedSettings
    };


    calcSOMDialog =  ifcDialogBox{
      name: "Make Self Organizing Map"      
      okAction: SOMOK
      content: [dataSetSelect, weightButton,  advancedOptionsBtn]
      cancelAction: function():Void{app.removeDialog(calcSOMDialog)}

      blocksMouse: true
      disable: bind SOMDialogDisabled;
    };

    app.addDialog(calcSOMDialog);
  }



  function SOMOK(){

    var datasets = (dataSetSelect.getDataSets());
    if(datasets.size() == 0){
      app.alert("No data set selected");
      app.unblockContent();
      return;
    }

    var finaldsp:DataSetProxy = mainMediator.getDataSet(dataSetSelect.getDataSets(), dataSetSelect.getSynchColumns());
    if(finaldsp == null){
      println("Error in data set combination");
      return;
    }
    somSettings.datasetproxy = finaldsp;
    //pick the correct scalar
    //var datasetscalar:DataSetScalar;
    if(somSettings.scaleType == SOMSettings.UNSCALED){
      somSettings.datasetscalar = new UnscaledDataSet(finaldsp.getData());
    }else if (somSettings.scaleType == SOMSettings.MINMAXNORM){
      somSettings.datasetscalar = new MinMaxNormalized(finaldsp.getData());
    }else if (somSettings.scaleType == SOMSettings.VARNORM){
      somSettings.datasetscalar = new VarianceNormalized(finaldsp.getData());
    }else if (somSettings.scaleType == SOMSettings.LOGSCALE){
      somSettings.datasetscalar = new LogScaleNormalized(finaldsp.getData());
    }else if (somSettings.scaleType == SOMSettings.PCACOMP){
      somSettings.datasetscalar = new PCANormalized(finaldsp.getData(), SOMWeights);
      SOMWeights = null;
      //Make weights 1 for all the PCs, then have stand ins for the original data vals set to weight 0
      var finaldims = somSettings.datasetscalar.getDimensions();
      SOMWeights = [];
      for(i in [0..finaldims-1]){
        if(i < finaldims - finaldsp.getDimensions()){
          insert 1 into SOMWeights;
        }else{ //last finaldsp.getDimensions() are 0
          insert 0 into SOMWeights;
        }
      }

    }else if (somSettings.scaleType == SOMSettings.PCACOMPDECAY){
      somSettings.datasetscalar = new PCANormalized(finaldsp.getData(), SOMWeights);
      SOMWeights = null;
      //Make weights 1 for all the PCs, then have stand ins for the original data vals set to weight 0
      var finaldims = somSettings.datasetscalar.getDimensions();
      SOMWeights = [];
      var currentweight = 1.0;
      for(i in [0..finaldims-1]){
        if(i < finaldims - finaldsp.getDimensions()){
          insert currentweight into SOMWeights;
          currentweight = currentweight *3.0/4.0;
        }else{ //last finaldsp.getDimensions() are 0
          insert 0 into SOMWeights;
        }
      }
    }
    somSettings.weights = SOMWeights as nativearray of Float;


    if(somSettings.classicMaxNeighborhood < 0){
      somSettings.classicMaxNeighborhood = Math.max(somSettings.height, somSettings.width) / 2;
    }

    if(somSettings.batchMaxNeighborhood < 0){
      somSettings.batchMaxNeighborhood = Math.max(somSettings.height, somSettings.width) / 4;
    }
    app.removeDialog(calcSOMDialog);

    var SOMmediator:SOMMediator;
    var SOMp:SOMProxy =  new SOMProxy();
    var somvc = SOMvc{};
    SOMmediator = new SOMMediator(app, somvc as SOMvcI);
    SOMmediator.setSOMprox(SOMp);//.setDSP(dsp);
    somvc.init(app, SOMmediator);

    app.registerMediator(SOMmediator as IMediator);
    app.getMainMediator().getCurrentTab().setTabMediator(SOMmediator);
    app.getMainMediator().getCurrentTab().changeMode(Tab.SOMMODE);
    SOMmediator.doSOM(somSettings);
    
  }





  /***********************************/
  /*Choose Channel Weights Dialog Box*/
  /***********************************/

  var SOMWeightsBox:ifcDialogBox;
  var SOMWeights: Float[] = null;
  var SOMWeightsTextBoxes:ifcDialogFloatInput[];

  function getSOMWeights(){
    var dataSets = dataSetSelect.getDataSets();    
    if(dataSets.size() == 0){
      app.alert("Error: No data set selected");
      return;
    }

    var SOMColNames:String[];

    if(dataSetSelect.getSynchColumns() == null){
      SOMColNames = dataSets.get(0).getColNames();
    }else{
      for(col in dataSetSelect.getSynchColumns()){
        insert col.colName into SOMColNames;
      }

    }


    

    if(SOMWeights == null or SOMColNames.size() != SOMWeights.size()){
      for (names in SOMColNames){
        insert 1 into SOMWeights; //set initial weights all to 1
      }
    }

    SOMWeightsTextBoxes =
      for(names in SOMColNames){
        ifcDialogFloatInput{
          name: names
          initialFloat: SOMWeights[indexof names]
        }
      }


    SOMWeightsBox = ifcDialogBox{
      name: "Select Channel Weights"
      content: SOMWeightsTextBoxes
      
      okAction: weightsOK
      cancelAction: function():Void{SOMDialogDisabled = false; app.removeDialog(SOMWeightsBox)}

      blocksMouse: true
      //disable: bind ReRemoveOutliersBoxDisabled;
      
    }

    SOMDialogDisabled = true;
    app.addDialog(SOMWeightsBox);
  }

  function weightsOK():Void{
    SOMWeights = for(input in SOMWeightsTextBoxes){
      input.getInput();
    }
    SOMDialogDisabled = false;
    app.removeDialog(SOMWeightsBox)
  }



  /***********************************/
  /*Advanced Settings Dialog Box     */
  /***********************************/

  var AdvancedBox:ifcDialogBox;

  var scaleTypeInput:ifcDialogChoiceBox;
  var scaleTypes:String[] = [SOMSettings.UNSCALED, SOMSettings.MINMAXNORM, SOMSettings.VARNORM,
                SOMSettings.LOGSCALE, SOMSettings.PCACOMP, SOMSettings.PCACOMPDECAY];

  var initTypeInput:ifcDialogChoiceBox;
  var initTypes:String[] = [SOMSettings.RANDOMINIT, SOMSettings.LINEARINIT, SOMSettings.FILEINIT];

  var somTypeInput:ifcDialogChoiceBox;
  var somTypes:String[] = [SOMSettings.CLASSICSOM, SOMSettings.BATCHSOM];

  var classIterInput:ifcDialogIntInput;
  var batchStepInput:ifcDialogIntInput;

  var rowsInput:ifcDialogIntInput;
  var colsInput:ifcDialogIntInput;


  var classicMaxNbrInput:ifcDialogIntInput;
  var classicMinNbrInput:ifcDialogIntInput;

  var batchMaxNbrInput:ifcDialogIntInput;
  var batchMinNbrInput:ifcDialogIntInput;
  var batchPntsPerNode:ifcDialogIntInput;


  var advancedContent:ifcDialogItem[] = bind [
        rowsInput, colsInput,
        scaleTypeInput,
        initTypeInput, somTypeInput,
        if(somTypeInput.selectedItem == somSettings.CLASSICSOM) {
          [classIterInput, classicMaxNbrInput, classicMinNbrInput]
        }else{
          [batchStepInput,batchMaxNbrInput,batchMinNbrInput, batchPntsPerNode]
        }
        ];

  function getAdvancedSettings():Void{
   
    SOMDialogDisabled = true;


    scaleTypeInput = ifcDialogChoiceBox{
      name:"Scale Type"
      items: scaleTypes
      initialSelectedItem: somSettings.scaleType
    };

    initTypeInput = ifcDialogChoiceBox{
      name:"Initialization: "
      items: initTypes
      initialSelectedItem: somSettings.initType
    };

    somTypeInput = ifcDialogChoiceBox{
      name:"SOM Type:"
      items: somTypes
      initialSelectedItem: somSettings.SOMType
    };

    classIterInput = ifcDialogIntInput{
      name:"Iterations: "
      initialInt: somSettings.classicIterations
    };

    batchStepInput = ifcDialogIntInput{
      name:"Batch Steps: "
      initialInt: somSettings.batchSteps
    };


    rowsInput = ifcDialogIntInput{
      name:"SOM Rows: "
      initialInt: somSettings.height
    };
    colsInput = ifcDialogIntInput{
      name:"SOM Columns: "
      initialInt: somSettings.width
    };

    classicMaxNbrInput = ifcDialogIntInput{
      name:"Max Neighborhood: "
      initialInt: if(somSettings.classicMaxNeighborhood == -1){
              Math.max(rowsInput.getInput()/2, colsInput.getInput()/2);
            }else{
              somSettings.classicMaxNeighborhood
            }


    };
    classicMinNbrInput = ifcDialogIntInput{
      name:"Min Neighborhood: "
      initialInt: somSettings.classicMinNeighborhood
    };


    batchMaxNbrInput = ifcDialogIntInput{
      name:"Max Neighborhood: "
      initialInt: if(somSettings.batchMaxNeighborhood == -1){
              Math.max(rowsInput.getInput()/2, colsInput.getInput()/2);
            }else{
              somSettings.batchMaxNeighborhood
            }
    };
    batchMinNbrInput = ifcDialogIntInput{
      name:"Min Neighborhood: "
      initialInt: somSettings.batchMinNeighborhood
    };
    batchPntsPerNode = ifcDialogIntInput{
      name:"Points Per Node: "
      initialInt: somSettings.batchPointsPerNode
    };

    AdvancedBox =ifcDialogBox{
      name: "Advanced Settings"
      content: bind advancedContent

      okAction: advancedOK
      cancelAction: function():Void{SOMDialogDisabled = false;
          app.removeDialog(AdvancedBox);}

      blocksMouse: true

    }

    app.addDialog(AdvancedBox);

  }

  function advancedOK():Void{
    somSettings.scaleType = scaleTypeInput.getInput() as String;
    somSettings.SOMType = somTypeInput.getInput() as String;
    somSettings.initType = initTypeInput.getInput() as String;

    somSettings.height = rowsInput.getInput();
    somSettings.width = colsInput.getInput();
    somSettings.classicIterations = classIterInput.getInput();
    somSettings.batchSteps = batchStepInput.getInput();
    if(somTypeInput.getInput() == SOMSettings.CLASSICSOM){
      somSettings.classicMaxNeighborhood = classicMaxNbrInput.getInput();
      somSettings.classicMinNeighborhood = classicMinNbrInput.getInput();
    }else{
      somSettings.batchMaxNeighborhood = batchMaxNbrInput.getInput();
      somSettings.batchMinNeighborhood = batchMinNbrInput.getInput();
      somSettings.batchPointsPerNode = batchPntsPerNode.getInput();
    }
    SOMDialogDisabled = false;
    app.removeDialog(AdvancedBox)
  }



};




