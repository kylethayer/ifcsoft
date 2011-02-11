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
package ifcSoft.view.som.somtile;

import ifcSoft.view.som.SOMTile;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Stack;

/**
 * @author kthayer
 */

public class SOMTileBlank extends SOMTile {

  override public function makeTile():Void{
    tileNode = Stack{
      height: bind somMaps.tileWidth* somMaps.aspectratio
      width: bind somMaps.tileWidth
      onMouseDragged: function (e:MouseEvent){if(not (e.secondaryButtonDown or e.altDown))somMaps.SOMmousedragged(e, this);}
      onMouseReleased: function (e:MouseEvent){somMaps.SOMmousereleased(e, this);}
      content:[
        Rectangle{
          height:bind somMaps.tileWidth* somMaps.aspectratio
          width: bind somMaps.tileWidth
          fill:Color.WHITE
          opacity: bind if(highlight){.25}else{0};
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


    override public function updateDenseMap () : Void {
    }

    override public function updateClusterStats () : Void {
    }

    override public function updatePointStats () : Void { 
    }


}
