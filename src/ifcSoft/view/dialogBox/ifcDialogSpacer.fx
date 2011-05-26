/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ifcSoft.view.dialogBox;

import javafx.scene.shape.Rectangle;

/**
 * @author kthayer
 */

public class ifcDialogSpacer extends ifcDialogItem{
  public-init var height:Float = 1;
  public-init var width:Float = 1;

  init{
    children =
      Rectangle{
        height: height
        width:width
        opacity:0
      }

  }

}
