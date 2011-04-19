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

import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;

/**
 * @author kthayer
 */

public class ifcDialogRadioButton extends ifcDialogItem{
  public-init var name:String;
  public-init var initialCheck:Boolean = false;
  public-init var toggleGroup:ToggleGroup = ToggleGroup {};

  var radioButtonInput:RadioButton;
  public-read var ischecked:Boolean = bind radioButtonInput.selected; //binding wasn't working on getInput()

  init{
    children =
      HBox{
        spacing: 4
        content:[
          radioButtonInput = RadioButton{
            selected: initialCheck
            toggleGroup: toggleGroup
          },
          Label {text: name}
        ]
      }
  }

  public function getInput():Boolean{
    return radioButtonInput.selected;
  }

	public function select():Void{
		radioButtonInput.selected = true;
	}

	public function unSelect():Void{
		radioButtonInput.selected = false;
	}

  override function getName():String{
    return name;
  }

}
