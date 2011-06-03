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

package ifcSoft.view.som;

import ifcSoft.MainApp;
import ifcSoft.view.dialogBox.ifcDialogBox;
import ifcSoft.view.dialogBox.ifcDialogProgressBar;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Math;


public class SOMProgressBox {

  public-init var mainApp:MainApp;
  public-init package var mediator:SOMMediator;

  var SOMProgressDialogBox:ifcDialogBox;
  var progress: Float = 0;
  

 
  /**
  * Create and display the SOM Progress Bar dialog box.
  */
  public function SOMProgressBox():Void{
    
    SOMProgressDialogBox =
      ifcDialogBox{
        name: "SOM Progress"
        content:[
          ifcDialogProgressBar{progress: bind Math.min (progress,1) }
        ]
        cancelAction: CancelSOM
        blocksMouse: true
      };
    mainApp.addDialog(SOMProgressDialogBox);
    checkprogress();
  }

  function checkprogress():Void{
    progress = mediator.getProgress();
    if (progress<100){
      var timeline = Timeline{
        repeatCount: 1
        keyFrames: KeyFrame{
            time: 30ms
            action: function():Void{checkprogress();}
          }
      };
      timeline.play();
    }else{
      mainApp.removeDialog(SOMProgressDialogBox);
    }
  }



 function CancelSOM(){
    mediator.cancelSOM();

    mainApp.removeDialog(SOMProgressDialogBox);
  }
}
