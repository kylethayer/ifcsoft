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

import javafx.scene.layout.VBox;

public class ifcDialogVBox extends ifcDialogItem{
  public-init var content:ifcDialogItem[] = [];


  init{
    children =
      VBox{
        spacing: 4
        content:content
      }
  }


  override function validate():Boolean{
    for(item in content){
      if(item.validate() == false){
        return false;
      }
    }
    return true;
  }


}
