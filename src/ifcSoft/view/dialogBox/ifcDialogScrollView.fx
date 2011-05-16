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

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollView;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.layout.LayoutInfo;
import javafx.util.Math;
import javafx.scene.control.ScrollBarPolicy;

/**
 * @author Kyle Thayer
 */

public class ifcDialogScrollView extends ifcDialogItem{
  public var node:Node;
  public var maxWidth:Float = 300;
  public var maxHeight:Float = 300;

  init{
    children =
      HBox{
        spacing: 4
        content:[
          ScrollView{
            node: Group{
              content: bind [
                Rectangle{
                  height: bind node.layoutBounds.height+15
                  width: bind node.layoutBounds.width+15
                  fill: Color.rgb(179,159,132) //lighter than the main dialog box background
                },
                node //TODO: move node a little over and down
              ]
            }

            layoutInfo:LayoutInfo{
              width: bind Math.min(maxWidth, node.layoutBounds.width+15)
              height: bind Math.min(maxHeight, node.layoutBounds.height+15)
            }

            //I do this separately, cause if I make the rectangle fit the bounds, it does both the scroll bars
            hbarPolicy: bind if(node.layoutBounds.width<= maxWidth){ScrollBarPolicy.NEVER}else{ScrollBarPolicy.ALWAYS}
            vbarPolicy: bind if(node.layoutBounds.height<= maxHeight){ScrollBarPolicy.NEVER}else{ScrollBarPolicy.ALWAYS}

          }
          
        ]
      }
  }


  override function validate():Boolean{
    if(node instanceof ifcDialogItem){
      return (node as ifcDialogItem).validate();
    }else{
      return true;
    }
  }
}
