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
import java.lang.UnsupportedOperationException;

/**
 * @author kthayer
 */

public class SOMTileDenseMap extends SOMTile{

	

	override public function updateDenseMap():Void{
		//main dense map
		var placed:Integer = somMaps.mediator.getDenstiyMapPlaced();
		if(placed > lastDenseDisplayed){
			var newimg:BufferedImage = somMaps.mediator.getDenstiyMapImg();
			img =newimg;
			placed = lastDenseDisplayed;
			if(somMaps.clusterImg == null){
				updatePointStats();
			}else{
				updateClusterStats();
			}
		}
	}


    override public function updatePointStats () : Void {
       var val = somMaps.mediator.getDenseCellVal();
		if(val == -1){
			setBottomText("");
		}else{
			setBottomText("{val}");
		}
    }

    override public function updateClusterStats () : Void {
        var clustMembs:Integer =  somMaps.mediator.getTotalClusterMembs();
		var totalPlaced:Float = somMaps.mediator.getDenstiyMapPlaced();
		if(totalPlaced == 0){
			setBottomText("");
		}else{
			setBottomText("{clustMembs * 100 / totalPlaced}%");
		}
    }

}
