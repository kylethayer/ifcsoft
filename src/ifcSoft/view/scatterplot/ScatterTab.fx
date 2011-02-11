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



  /*************Histogram info************/
  var dsp:DataSetProxy;
  var scatterplot:ScatterPlot;
  /*var pntTotalSizes:Integer[][];
  var maxPntSize:Integer;

  var histBarText:String;
  var histBars:Rectangle[];
  var histBarTargets:Rectangle[];

  var histScale:Group=Group{};*/


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

  var numBars = 40; //defualt is 20*/


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

  /*var setChoiceBox:ChoiceBox;
  var setSelected: Integer = bind setChoiceBox.selectedIndex
    on replace{
      changeSet(setSelected);
    };

  var numBarsTextBox:TextBox;
  var isnumBarsTextBoxFocused = bind numBarsTextBox.focused on replace {changeBars};
  var scaleChoiceBox:ChoiceBox;*/


  /*********** JavaFX Display components ***********/
  var isDisplayed:Boolean = false;
  /*var histTabTopGroup:Group = Group{}; //Title, set chooser
  var histTabCenter:Group= Group{}; //actual histogram
  var histTabBottom:Group= Group{}; //other options

  var histContentWidth:Float = bind app.contentWidth - 60;

  var histTabNode:Node =
    HBox{
      spacing: 0
      width: bind app.contentWidth
      height: bind app.contentHeight
      content:[
        Rectangle{ //space for left tab
          fill: Color.BLACK
          height:30
          width: 30 //TODO: make dependent on tab width
        },
        VBox{
          width: bind histContentWidth
          height: bind app.contentHeight
          vpos: VPos.TOP
          nodeHPos: HPos.CENTER
          nodeVPos: VPos.CENTER
          spacing: 10
          content:[
            Rectangle{ fill: Color.BLACK height:20 width: 5}, //spacer at top
            histTabTopGroup,  //Title, set chooser
            histTabCenter, //actual histogram
            histTabBottom //other options
          ]
        },
        Rectangle{ //space for right tab
          fill: Color.BLACK
          height:30
          width: 30 //TODO: make dependent on tab width
        }
      ]


    }*/


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

  /*function changeSet(setNum: Integer){
    if(setNum != set and isDisplayed){ //if the dimmension is changed
      //if(setNum == dsp.getData())
      set = setNum;
      setBarSizes();
    }
  }*/

  /*function changeBars(){
    //try to parse the integer in the text box
    var newBars = 0;
    try{
      newBars = Integer.parseInt(numBarsTextBox.text);
    }catch(e:Exception){
      //color the text red
      //set the overlay text thing to say what the problem is
      return;
    }
    //color the text black
    if(newBars < 1){
      //color the text red
      //set the overlay text thing to say what the problem is
      return;
    }

    if(newBars != numBars){
      numBars = newBars;
      computeHistogram();
      displayHistogram();
    }
  }*/

  /*function recalc(){ //make sure bars are set as well
    var oldnumbars = numBars;
    changeBars();
    if(numBars == oldnumbars){ //if that didn't update it
      computeHistogram();
      displayHistogram();
    }
  }*/




  function computeScatter():Void{
    scatterplot = scatterMediator.calcScatter(400, 400, initialScaleType);
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

            }
          ]

        }

        ImageView{
          x: 50
          y: 60
          scaleX: 1
          scaleY: 1
          image: SwingUtils.toFXImage(scatterMediator.getScatterImage(scatterplot))
        }



      ]
    }



    app.setMainContent(scatterContent);
    isDisplayed = true;
    //scatterplot.xRes();
  /*  displayTopGroup();
    displayBottomGroup();

    scaleChoiceBox.select(initialScaleType);
    dimChoiceBox.select(dim);

    displayHistogram();

    app.setMainContent(histTabNode);
    isDisplayed = true;*/
  }

  override public function swapOutTab():Void{
    isDisplayed = false;
  }



  /*function displayTopGroup(){
    if(dimChoiceBox == null){
      dimChoiceBox = ChoiceBox{
        items:[
          for(dimName in dsp.getColNames()){
            dimName;
          }
        ]
      };
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






    histTabTopGroup.content =
    [
      HBox{
        content:[
          VBox{
            content:[
              Label {
                //x: bind app.contentWidth/2
                text: "Data Set: {dsp.getDataSetName()}"
                font: Font {name: "Arial" size: 15}
                textFill: Color.WHITE
                layoutInfo: LayoutInfo { width: app.contentWidth/2 }
              },
              dimChoiceBox,
            ]
          },
          setChoiceBox,

        ]
      }
    ];
  }*/




  /*function displayHistogram():Void{
    if(scatterplot == null){
      computeHistogram();
    }

    setBarSizes();

    histTabCenter.content =
    [
      VBox{
      content:[
        Group{ content:[
          Rectangle{ //to make sure the whole area is used to prevent centering problems
            width: bind histContentWidth
            height: 2
          },

          histBarTargets = for(i in [0..scatterplot.numBars()-1]){
            Rectangle {
              x: bind histContentWidth / scatterplot.numBars()*i
              y: bind - (app.contentHeight - 200.0)

              width: bind histContentWidth / scatterplot.numBars()//*.75
              height: bind (app.contentHeight - 200.0)

              fill: Color.BLACK
              onMouseEntered: function(e:MouseEvent):Void{barOver(i)}
              onMouseExited: function(e:MouseEvent):Void{barOff(i)}
              }
          },
          histBars = for(i in [0..scatterplot.numBars()-1]){
            Rectangle {
              x: bind histContentWidth / scatterplot.numBars()*(i + .125)
              y: bind - ((app.contentHeight - 200.0) * pntTotalSizes[i]) / maxPntSize

              width: bind histContentWidth / scatterplot.numBars()*.75
              height: bind ((app.contentHeight - 200.0) * pntTotalSizes[i]) / maxPntSize

              fill: Color.LIGHTGRAY
            }
          }
        ]},
        histScale,
        HBox{
          hpos:HPos.CENTER
          width:  bind histContentWidth
          content:[
            Rectangle{ //spacer
              x: histContentWidth/2
              y: 20
              height: 30
              width:2
            },

            Text{
              x: histContentWidth/2
              y: 20
              fill:Color.WHITE
              content: bind histBarText
            }
          ]
        }
      ]
      }
    ];
    updateScale();
  }*/

  /*function updateScale():Void{
    var axisMarks:Rectangle[];
    var axisVals:Node[];
    if(scaleType == 0){//linear scale
      var min = scatterplot.getBarLowerLim(0);
      var max = scatterplot.getBarUpperLim(numBars-1);

      var delta = max-min;
      var maxGap = delta / 5; // min of 5 labeled points
      var sigFig = Math.floor(Math.log10(maxGap));
      var finalGap = Math.pow(10, sigFig);
      while(delta / finalGap > 10){ //if more than 12 labeled ticks on an axis
        finalGap = finalGap *2;
      }
      var firstPoint = Math.ceil(min / finalGap)*finalGap;
      var currentPt = firstPoint;
      while(currentPt < max){
        var fractionOver = (currentPt - min)/(delta);
        insert Rectangle{
            x: bind fractionOver*histContentWidth
            width: 2
            height: 10
            fill: Color.RED
          } into axisMarks;

        insert Label{
            translateX: bind fractionOver*histContentWidth
            translateY: 10
            text:"{currentPt}"
            textFill: Color.WHITE
          } into axisVals;
        currentPt += finalGap;
      }
    }else if (scaleType == 1){//log scale
      var min = scatterplot.getBarLowerLim(0);
      var max = scatterplot.getBarUpperLim(numBars-1);

      var delta = max-min;
      //var logDelta = Math.log10(delta);
      var finalGap = 1.0 / 5.4;

      var currentPt = 0.0;
      while(currentPt < 1){
        var fractionOver = currentPt;
        var actualVal = min + delta*(Math.pow(10,currentPt) - 1)/9;
        insert Rectangle{
            x: bind fractionOver*histContentWidth
            width: 2
            height: 10
            fill: Color.RED
          } into axisMarks;

        insert Label{
            translateX: bind fractionOver*histContentWidth
            translateY: 10
            text:"{actualVal as Float}"
            textFill: Color.WHITE
          } into axisVals;
        currentPt += finalGap;
      }
    }

    histScale.content = [
      Rectangle{ //to make sure the whole area is used to prevent centering problems
        width: bind histContentWidth
        height: 2
      },
      axisMarks,
      axisVals,

      ];
    histScale.clip = Rectangle{ //make sure scale doesn't run off side
        width: bind histContentWidth
        height: 100
      };

  }*/



  /*function barOver(barNum:Integer):Void{
    histBars[barNum].fill = Color.WHITE;
    histBarTargets[barNum].fill = Color.color(.1,.1,.1);
    histBarText = "{pntTotalSizes[barNum]
            } points,   Bar Range: {scatterplot.getBarLowerLim(barNum) as Float
            },  {scatterplot.getBarUpperLim(barNum) as Float}";
  }*/
  /*function barOff(barNum:Integer):Void{
    histBars[barNum].fill = Color.LIGHTGRAY;
    histBarTargets[barNum].fill = Color.BLACK;
    histBarText = "";
  }*/

  /*function setBarSizes(){
    var isAll = false;
    if(set == dsp.getData().getRawSetNames().size()){ //it is the whole set
      isAll = true;
    }

    maxPntSize = 0;
    for(i in [0..scatterplot.numBars()-1]){
      if(isAll){
        if(scatterplot.getBarSize(i) > maxPntSize){
          maxPntSize = scatterplot.getBarSize(i);
        }
      }else{
        if(scatterplot.getSetBarSize(set, i) > maxPntSize){
          maxPntSize = scatterplot.getSetBarSize(set, i);
        }
      }
    }

    if(isAll){
      pntTotalSizes = [
        for(i in [0..scatterplot.numBars()-1]){
          scatterplot.getBarSize(i)
        }
      ];
    }else{
      pntTotalSizes = [
        for(i in [0..scatterplot.numBars()-1]){
          scatterplot.getSetBarSize(set, i)
        }
      ];
    }



  }*/



  /*function displayBottomGroup(){
    histTabBottom.content =
    VBox{
      content:[
        HBox{
          spacing: 5;
          content:[
            Text {
              x: 0
              y: 0
              content: bind "Bars:"
              font: Font {name: "Arial" size: 15}
              fill: Color.WHITE
            },
            numBarsTextBox = TextBox{
              text: "{numBars}"
              columns: 15
              action: function(){changeBars();}
            },
            Text {
              x: 0
              y: 50
              content: bind "Scale:"
              font: Font {name: "Arial" size: 15}
              fill: Color.WHITE
            },
            scaleChoiceBox = ChoiceBox{
              items:["Linear","Logarithmic"]
            }
            Button{
              text: "Re-Calculate"
              action: function(){recalc();}
            }
          ]
        }
      ]
    };
  }*/




}