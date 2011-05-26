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
package ifcSoft.view.scatterplot;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import ifcSoft.MainApp;
import ifcSoft.model.DataSetProxy;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ChoiceBox;
import ifcSoft.model.scatterplot.ScatterPlot;
import javafx.scene.image.ImageView;
import javafx.ext.swing.SwingUtils;
import javafx.scene.image.Image;

/**
 * The view Component for the Scatter tab.
 * @author Kyle Thayer <kthayer@emory.edu>
 */

public class ScatterTab extends ScatterTabI{

  /**
  *  These variables must be initialized
  */
  public-init var app:MainApp;
  public-init var scatterMediator:ScatterTabMediator;

  postinit{
    if(app == null or scatterMediator == null){
      println("HistTab initializer: not initialized fully");
    }
  }



  /*************Scatter Plot info************/
  var dsp:DataSetProxy;
  var scatterplot:ScatterPlot;
 

  /*************Histogram options************/
  var xDim:Integer;
  var yDim:Integer;

  var set:Integer;

  var initialScaleType = 0;
  /*var scaleType: Integer = bind scaleChoiceBox.selectedIndex  //0 is linear, 1 is logarithmic
    on replace oldValue{
      if(scaleType != oldValue and isDisplayed){
        initialScaleType = scaleType;
        computeHistogram();
        displayHistogram();
      }
    };


  /*********** Interactive Selection Elements ***********/
  var xDimChoiceBox:ChoiceBox;
  var xDimSelected: Integer = bind xDimChoiceBox.selectedIndex
    on replace{
      changeXDim(xDimSelected);
    };

  var yDimChoiceBox:ChoiceBox;
  var yDimSelected: Integer = bind yDimChoiceBox.selectedIndex
    on replace{
      changeYDim(yDimSelected);
    };

  var setChoiceBox:ChoiceBox;
  var setSelected: Integer = bind setChoiceBox.selectedIndex
    on replace{
      changeSet(setSelected);
    };



  /*********** JavaFX Display components ***********/
  var isDisplayed:Boolean = false;
  var scatterImage:Image;
 


  override public function setXDimension (Xdimension : Integer) : Void {
    xDim = Xdimension;
  }

  override public function setYDimension (Ydimension : Integer) : Void {
    yDim = Ydimension;
  }

  override public function setDataSet (dsp : DataSetProxy) : Void {
    this.dsp = dsp;
  }

  override public function setScaleType (initialScaleType:Integer) : Void {
    if(initialScaleType == 0 or initialScaleType == 1)
      this.initialScaleType = initialScaleType;
  }


  override public function informNewDsp(){
    //displayTab();
  }

  function changeXDim(dimNum: Integer){
    if(dimNum != xDim and isDisplayed){ //if the dimmension is changed
      xDim = dimNum;
      //dimName = dsp.getColNames()[dim];
      scatterMediator.setXDimension(dimNum);
      computeScatter();
      displayTab(); //TODO: Make it just update scatter plot
    }
  }

  function changeYDim(dimNum: Integer){
    if(dimNum != yDim and isDisplayed){ //if the dimmension is changed
      yDim = dimNum;
      //dimName = dsp.getColNames()[dim];
      scatterMediator.setYDimension(dimNum);
      computeScatter();
      displayTab();  //TODO: Make it just update scatter plot
    }
  }

  function changeSet(setNum: Integer){
    var newSet = setNum;
    if(newSet == dsp.getData().getRawSetNames().size()){ //it is the whole set
      newSet = -1;
    }
    if(newSet != set and isDisplayed){ //if the dimmension is changed
      //if(setNum == dsp.getData())
      set = newSet;

      scatterImage = SwingUtils.toFXImage(scatterMediator.getScatterImage(scatterplot, set));
    }
  }





  function computeScatter():Void{
    scatterplot = scatterMediator.calcScatter(600, 400, initialScaleType);
  }


  override public function displayTab() {
    if(scatterplot == null){
      computeScatter();
    }

    if(xDimChoiceBox == null){
      xDimChoiceBox = ChoiceBox{
        items:[
          for(dimName in dsp.getColNames()){
            dimName;
          }
        ]
      };
      xDimChoiceBox.select(xDim);
    }
    if(yDimChoiceBox == null){
      yDimChoiceBox = ChoiceBox{
        items:[
          for(dimName in dsp.getColNames()){
            dimName;
          }
        ]
      };
      yDimChoiceBox.select(yDim);
    }

    if(setChoiceBox == null){
      var choiceBoxStrings:String[];

      if(dsp.getData().getRawSetNames().size() > 1){
        choiceBoxStrings =
          [
            for(setName in dsp.getData().getRawSetNames()){
              setName;
            },
            dsp.getDataSetName()
          ]
      }else{
        choiceBoxStrings = dsp.getDataSetName()
      }

      for(string in choiceBoxStrings){
        if(string.length() > 18){
          choiceBoxStrings[indexof string] = "{string.substring(0, 15)}..."
        }
      }

      setChoiceBox = ChoiceBox{
        items: choiceBoxStrings
      };
    }

    scatterImage = SwingUtils.toFXImage(scatterMediator.getScatterImage(scatterplot, set));
    var scatterContent:Group = Group{
      content:[
        HBox{
          layoutX: bind app.contentWidth / 2
          layoutY: 20
          content:[
            VBox{
              content:[
                Text{content:"X Axis:" fill:Color.WHITE}
                xDimChoiceBox
              ]

            },
            VBox{
              content:[
                Text{content:"Y Axis:" fill:Color.WHITE}
                yDimChoiceBox
              ]

            },
            VBox{
              content:[
                Text{content:"Data Set displayed:" fill:Color.WHITE}
                setChoiceBox,
              ]

            },

          ]

        }

        ImageView{
          x: 50
          y: 60
          scaleX: 1
          scaleY: 1
          image: bind scatterImage
        }



      ]
    }



    app.setMainContent(scatterContent);
    isDisplayed = true;
  }

  override public function swapOutTab():Void{
    isDisplayed = false;
  }



}