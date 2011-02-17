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

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Sequences;

/**
 * @author kthayer
 */

public class ifcDialogChoiceBox extends ifcDialogItem{
  public-init var name:String;
  public var items:Object[];
  public-init var initialSelectedItem:Object = null;
  public-init var selectedItem: Object = bind choiceBoxInput.selectedItem;

  var choiceBoxInput:ChoiceBox;

  init{
    children =
      HBox{
        spacing: 4
        content:[
          Label {text: name}
          choiceBoxInput = ChoiceBox{
            items: bind items
            //selected: initialCheck
          },

        ]
      }
    if(initialSelectedItem != null){
      choiceBoxInput.select(Sequences.indexOf(items, initialSelectedItem));
    }else{
      choiceBoxInput.select(0);
    }

  }

  public function getInput():Object{
    return choiceBoxInput.selectedItem;
  }

  public function getInputNumber():Integer{
    return choiceBoxInput.selectedIndex;
  }

  override function getName():String{
    return name;
  }

}
