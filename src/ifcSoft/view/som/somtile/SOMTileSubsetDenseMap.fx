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

import java.awt.image.BufferedImage;
import ifcSoft.view.som.SOMTile;

/**
 * @author kthayer
 */

public class SOMTileSubsetDenseMap extends SOMTile{

  public-init var densityMap:Integer = -1; //-1 is not a dense map, 0 is the main one, above are the subset ones

  override public function updateDenseMap():Void{

    //density subset map
    var mapNum:Integer = densityMap;
    var placed:Integer = somMaps.mediator.getSubDenstiyMapPlaced(mapNum);
    if(placed > lastDenseDisplayed){
      var newimg:BufferedImage = somMaps.mediator.getSubDenstiyMapImg(mapNum);
      img = newimg;
      placed = lastDenseDisplayed;
      if(somMaps.clusterImg == null){
        updatePointStats();
      }else{
        updateClusterStats();
      }
    }
  }

  override public function updateClusterStats () : Void {

    var mapNum = densityMap;
    var clustMembs:Integer =  somMaps.mediator.getSubClusterMembs(mapNum);
    var totalPlaced:Float = somMaps.mediator.getSubDenstiyMapPlaced(mapNum);
    if(totalPlaced == 0){
      setBottomText("");
    }else{
      setBottomText("{clustMembs * 100 / totalPlaced}%");
    }

    
    }

    override public function updatePointStats () : Void {
    var mapNum = densityMap;
    var val = somMaps.mediator.getDenseCellVal(mapNum);
    if(val == -1){
      setBottomText("");
    }else{
      setBottomText("{val}");
    }
    }



}
