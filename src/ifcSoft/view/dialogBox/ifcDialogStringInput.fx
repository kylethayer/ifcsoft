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

import javafx.scene.control.TextBox;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;

/**
 * author Kyle Thayer <kthayer@emory.edu>
 */

public class ifcDialogStringInput extends ifcDialogItem{
  public-init var name:String;
  public-init var initialString:String = "";
  public-init var inputWidth:Integer = 10;
  public-init var allowEmptyInput:Boolean = false;

  var stringInput:TextBox;

  init{
    children =
      HBox{
        content:[
          Text {content: name},
          stringInput = TextBox{
            text:  initialString
            columns: inputWidth
          }
        ]
      }
  }

  public function getInput():String{
    return stringInput.text;
  }

  override function getName():String{
    return name;
  }

  override function validate():Boolean{
    if(allowEmptyInput){
      true;
    }
    if(stringInput.text.length() > 0){
      true;
    }else{
      false;
    }
  }


}
