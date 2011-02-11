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

import javafx.scene.control.Button;
import java.lang.String;
import javafx.scene.input.MouseEvent;

/**
 * The main purpose of this class is to get around the button-clicking problems in JavaFX.
 * Specifically, the "action" parameter of a button happens when the mouse presses on the button,
 * making the release of the mouse a new action happening after "action" is performed
 * (and for OK and Cancel buttons, this will occur to whatever was behind or is new in front of the dialog box).
 * @author Kyle Thayer <kthayer@emory.edu>
 */

public class ifcDialogButton extends ifcDialogItem{


  public var action:function():Void;
  public var text:String = "";

  var isMousePressedOverButton = false;
  var isMouseDragged = false;

  var btn:Button = Button{
    onMousePressed: function(e:MouseEvent){isMousePressedOverButton = true}
    onMouseReleased: mouseReleasedOnButton
    onMouseDragged: function(e:MouseEvent){isMouseDragged = true}
    onMouseClicked: mouseClickedOnButton
    text: bind text
  };

  init{
    children = btn;
  }

  function mouseReleasedOnButton(e:MouseEvent){

    if(isMousePressedOverButton){
      isMousePressedOverButton = false;
      if(isMouseDragged){ //if the mouse is not dragged, it will be a click. I need to handle this in the click
                //handler, otherwise the click will make a new event after "action" is performed.
        isMouseDragged = false;
        if(btn.contains(e.x, e.y)){
          action();
        }
      }
    }
  }

  function mouseClickedOnButton(e:MouseEvent){
    isMouseDragged = false;
    isMousePressedOverButton = false;
    action();
  }


    override public function validate () : Boolean {
        return true;
    }

    override public function getName () : String {
        return "";
    }

}
