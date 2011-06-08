/**
 *  Copyright (C) 2011   IFCSoft project
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;


public class ifcDialogProgressBar extends ifcDialogItem{
 public var progress:Float;

  init{
    children = HBox {
      content: [
        ProgressBar {
          progress: bind progress
        }
        Label {
          text: bind "{progress*100 as Integer}%"
        }

      ]
    }
  }
}