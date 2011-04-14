/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ifcSoft.view.dialogBox;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;

/**
 * @author kthayer
 */

public class ifcDialogHBox extends ifcDialogItem{
  public-init var name:String = null;
  public-init var content:ifcDialogItem[] = [];


  init{
    children =
      VBox{
        spacing: 4
        content:
          if(name != null){
            [
              Text {content: name},
              HBox{
                spacing:4
                content:content
              }
            ]
          }else{
            HBox{
              spacing:4
              content:content
            }
          }

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

  override function getName():String{
    return name;
  }


}
