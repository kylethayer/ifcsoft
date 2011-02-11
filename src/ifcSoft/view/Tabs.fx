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

import javafx.scene.shape.MoveTo;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.Group;
import javafx.scene.shape.Path;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.shape.Rectangle;

import ifcSoft.MainApp;

/**
 * This is the JavaFX file that handles the Tabs
 * @author Kyle Thayer <kthayer@emory.edu>
 */

 
/*static variables*/
def tabH=Menus.menuTabH; //height of the actual tab shape
def tabBevel = Menus.menuBevel;
def tabW=100;

def tabVOffset = 3; //the vertical space left above the tab

def tabHeight = tabH + tabVOffset; //final height of the tab without the dividing line

public def tabTotalHeight = tabHeight + 1; //final height of the tab with dividing line

def tabBorderColor=Menus.menuBorderColor;
def unSelectedTab=Menus.menuBackgroundColor;
def unSelectedTabHalf =Color.color(unSelectedTab.red /2,unSelectedTab.green /2,unSelectedTab.blue /2);


def   tabPath =  [ // Horizontal menu
  MoveTo{x:0 y:tabH}
  CubicCurveTo{
    controlX1: tabBevel  controlY1: tabH
    controlX2: tabBevel  controlY2: 0
    x: tabBevel*2    y: 0
  }
  HLineTo{x: tabW - tabBevel }
  CubicCurveTo{
    controlX1: tabW    controlY1: 0
    controlX2: tabW   controlY2: tabH
    x: tabW  + tabBevel  y: tabH
  }
];


def selectedTab=
    LinearGradient{
      startX: 0 startY:0
      endX: 0 endY: tabH
      proportional: false
      stops:[  Stop{ offset: 0.0 color: unSelectedTab  }
          Stop{ offset: 0.15 color: unSelectedTab  }
          Stop{ offset: 0.75 color: unSelectedTabHalf  }
          Stop{ offset: 1.0 color: Color.BLACK  }  ]
    }

    

public class Tabs {

  public-init var app:MainApp;
  postinit{
    if(app == null){
      println("Tabs initializer: not initialized fully");
    }
  }

  
  public var tabNames:String[];
  public var currentTab = -1; //start as -1 meaning there are not tabs yet


  public function makeTabs():Node[]{
    var nodes:Node[] =
      [
        Rectangle{
          onMouseClicked: app.newTab
          layoutX:0 layoutY:0
          height: tabHeight - 1
          width: bind app.scene.width
          fill: Color.BLACK
        }
      ];
    for(tabNum in [0..tabNames.size()-1]){
      if(tabNum != currentTab){
        insert makeTab(tabNum) after nodes[0];
      }
    }

    insert [
      Line{
        stroke: tabBorderColor
        startX:  0
        startY: tabHeight+.5
        endX: (tabW-.5*tabBevel)*currentTab + 3
        endY: tabVOffset + tabH+.5
        strokeWidth: 1.5
      },
      Line{
        stroke: Color.BLACK
        startX: (tabW-.5*tabBevel)*currentTab + 3
        startY: tabHeight+.5
        endX:  (tabW-.5*tabBevel)*currentTab + tabW + tabBevel - 1
        endY: tabHeight +.5
        strokeWidth: 1.5
      },

      Line{
        stroke: tabBorderColor
        startX: (tabW-.5*tabBevel)*currentTab + tabW + tabBevel - 1
        startY: tabHeight+.5
        endX: bind app.scene.width
        endY: tabHeight+.5
        strokeWidth: 1.5
      },
    ] into nodes;

    insert makeTab(currentTab) into nodes;
    return nodes;
  }

  function makeTab(tabNum:Integer):Group{
    return Group{
      layoutX:(tabW-.5*tabBevel)*tabNum
      layoutY:tabVOffset
      onMouseClicked: function(event){app.selectTab(tabNum)} 
      content:[
        Path{  stroke: tabBorderColor,
            fill: {if(tabNum==currentTab){selectedTab}else{unSelectedTab}},
            elements: [tabPath],

        },
        Label{
          translateX: 20
          font: Font { size: tabH-5 }
          text: tabNames[tabNum]
          textFill: Color.WHITE
          layoutInfo: LayoutInfo { width: tabW - 2*tabBevel - 17 }
        },

        Group{/*Close button*/
          onMouseClicked:function(event){closeTab(tabNum);}
          content:
          [
            Rectangle{
              x: tabW - tabBevel - 5
              y: 3
              width: 7  height: 7
              arcWidth: 3  arcHeight: 3
              fill: selectedTab
              stroke: tabBorderColor
              strokeWidth: .5
            },
            Line{
              startX: tabW - tabBevel - 5 + 7 - 1
              startY:3 + 1
              endX: tabW - tabBevel - 5 + 1
              endY: 3 + 7 - 1
              stroke:tabBorderColor
            },
            Line{
              startX: tabW - tabBevel - 5 + 1
              startY:3 + 1
              endX: tabW - tabBevel - 5 + 7  - 1
              endY: 3 + 7  - 1
              stroke: tabBorderColor
            },

          ]
        }
      ]
    }

  }


  function closeTab(tabNum:Integer){
    if(tabNum == currentTab){
      currentTab = -1; //so that when it updates, it knows it needs to display the new tab
    }

    app.mainMediator.closeTab(tabNum);

  }

}
