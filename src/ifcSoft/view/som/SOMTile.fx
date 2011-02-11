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

import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import javafx.scene.layout.Stack;
import javafx.ext.swing.SwingUtils;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.shape.Path;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.geometry.HPos;
import javafx.scene.text.Font;
import javafx.scene.shape.Line;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextBox;
import ifcSoft.view.som.SOMmaps;

/**
 *  @author Kyle Thayer <kthayer@emory.edu>
 */

public abstract class SOMTile {

  public-init var somMaps:SOMmaps;
  

  //public-init var blankTile:Boolean = false;
  //public-init var setDenseMapsDelta:Boolean = false;

  public var img:BufferedImage;
  public var name:String;
  public var min:Number;
  public var max:Number;
  public var highlight:Boolean = false;
  public var leftHighlight:Boolean = false;
  public var rightHighlight:Boolean = false;

  var bottomText:String;
  protected var tileNode:Node;
  var imageView:ImageView;

  protected var lastDenseDisplayed:Integer = 0;

  postinit{
    if(somMaps == null){
      println("SOMTile initializer: not initialized fully");
    }
  }

  public function getTileNode():Node{
    if(tileNode == null){
      makeTile();
    }
    return tileNode;
  }

  public function getImageView():ImageView{
    return imageView;
  }

  public function setBottomText(str:String){
    bottomText = str;
  }

  public abstract function updateDenseMap():Void;

  public abstract function updatePointStats():Void;

  public abstract function updateClusterStats():Void;






  function makeTile():Void{

    imageView = ImageView{
      cursor: Cursor.CROSSHAIR
      cache: true

      onMouseEntered: somMaps.mouseEnterSOM
      onMouseExited: somMaps.mouseExitSOM


      scaleX: bind .8* somMaps.tileWidth / img.getWidth() //both scaled the same factor (width -> tileWidth)
      scaleY: bind .8* somMaps.tileWidth /img.getWidth()
      image: bind SwingUtils.toFXImage(img);
    };

    //insert map into ImgViews;

    var mapText:String = "Min:{ min} Max:{ max}"; //"Min:{%,f min} Max:{%,f max}";?

    setBottomText(mapText);

    tileNode = Stack{
      height: bind somMaps.tileWidth* somMaps.aspectratio
      width: bind somMaps.tileWidth
      onMouseMoved: somMaps.SOMmousemove //this needs to be here so the mouse coordinates are not scaled to the image
      onMousePressed: function (e:MouseEvent){somMaps.SOMmousepressed(e, this);}
      onMouseDragged: function (e:MouseEvent){somMaps.SOMmousedragged(e, this);}
      onMouseReleased: function (e:MouseEvent){somMaps.SOMmousereleased(e, this);}
      onMouseClicked: function (e:MouseEvent){somMaps.SOMclicked(e, this);}
      content:[
        Rectangle{
          height:bind somMaps.tileWidth* somMaps.aspectratio
          width: bind somMaps.tileWidth
          fill:Color.WHITE
          opacity: bind if(highlight){.25}else{0};
        }
        imageView,
        //someMap = map, //save one of them in someMap so I have one handy for coordinate conversion
        ImageView { //the cluster Image overlay
          cursor: Cursor.CROSSHAIR
          cache: true
          scaleX: bind .8* somMaps.tileWidth / img.getWidth() //both scaled the same factor (width -> tileWidth)
          scaleY: bind .8* somMaps.tileWidth / img.getWidth()
          image: bind somMaps.clusterImg
        },
        Group{ //to try and make the path not be centered
          content:[
            Rectangle{
              height: bind somMaps.tileWidth* somMaps.aspectratio
              width: bind somMaps.tileWidth
              opacity: 0
            },
            Path{
              //fill: Color.PURPLE
              //clip:Rectangle{ height: bind tileWidth* aspectratio, width: bind tileWidth}
              stroke: Color.PURPLE
              strokeWidth: 1
              elements: bind somMaps.selectPathElements
            }
          ]
        },
        Label{
          hpos: HPos.CENTER
          translateY: bind - somMaps.tileWidth* somMaps.aspectratio / 2 + 10
          text: bind name
          font: Font {name: "Arial" size: 20}
          textFill: Color.WHITE
          visible: bind not isEditingName
        },
        nameEditBox = TextBox{
          translateY: bind - somMaps.tileWidth* somMaps.aspectratio / 2 + 10
          text: name
          visible: bind isEditingName
          action: function(){name = nameEditBox.text;isEditingName = false;}
        },
        Rectangle{ //Label double click catcher
          translateY: bind - somMaps.tileWidth* somMaps.aspectratio / 2
          width: bind somMaps.tileWidth
          height: bind .2*somMaps.tileWidth*somMaps.aspectratio
          opacity: 0
          onMouseClicked: labelClick
        }
        Label{
          hpos: HPos.CENTER
          translateY: bind  somMaps.tileWidth* somMaps.aspectratio / 2 - 15
          text: bind bottomText
          font: Font {name: "Arial" size: 11}
          textFill: Color.WHITE
        },
        Line {
          startX: 0  startY: 0
          endX: bind .8* somMaps.tileWidth  endY: 0
          visible: bind somMaps.crosshairVisible
          translateY: bind somMaps.crosshairVPos
          opacity:.5
          strokeWidth: bind somMaps.app.contentWidth / 700
        },
        Line {
          startX: 0  startY: 0
          endX: 0 endY: bind .8* somMaps.tileWidth * somMaps.aspectratio
          visible: bind somMaps.crosshairVisible
          translateX: bind somMaps.crosshairHPos
          opacity:.5
          strokeWidth: bind somMaps.app.contentWidth / 700
        },
        HBox{ //the left/right highlights
          content: [
            Rectangle{
              height:bind somMaps.tileWidth* somMaps.aspectratio
              width: bind somMaps.tileWidth / 6
              fill:Color.WHITE
              opacity: bind if(leftHighlight){.25}else{0};
            },
            Rectangle{ //spacer
                height:bind somMaps.tileWidth* somMaps.aspectratio
                width: bind somMaps.tileWidth * 2 / 3
                fill:Color.WHITE
                opacity: 0;
            },
            Rectangle{
              height:bind somMaps.tileWidth* somMaps.aspectratio
              width: bind somMaps.tileWidth / 6
              fill:Color.WHITE
              opacity: bind if(rightHighlight){.25}else{0};
            }
          ]
        }

      ]
    }
  }


  var isEditingName:Boolean = false;
  var nameEditBox:TextBox;
  function labelClick(e:MouseEvent):Void{

    if(e.clickCount > 1){
      if(isEditingName){
        println(nameEditBox.text);
        /*name = nameEditBox.text;
        isEditingName = false;*/
      }else{
        nameEditBox.text = name;
        isEditingName = true;
      }
    }

  }




  

}
