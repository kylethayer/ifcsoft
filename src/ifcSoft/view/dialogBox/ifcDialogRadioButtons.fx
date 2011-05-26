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

import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;

/**
 * @author kthayer
 */

public class ifcDialogRadioButtons extends ifcDialogItem{
  public-init var name:String;
  public-init var options:String[];
  public-init var initialCheck:Integer = 0;

  var toggleGroup= ToggleGroup {};

  var radioButtons:RadioButton[];

  init{
    children =
      VBox{
        content:[
          Label {text: name}
          HBox{
            content:[
              for(opt in options){
                radioButtons[indexof opt] = RadioButton{
                  text: opt
                  selected: indexof opt == initialCheck
                  toggleGroup:toggleGroup
                }
              }

            ]
          }
        ]
      }
  }

  public function getInput():Integer{
    var selected:Integer;
    for(button in radioButtons){
      if(button.selected){
        selected = indexof button;
      }
    }

    return selected;
  }

  override function getName():String{
    return name;
  }

}
