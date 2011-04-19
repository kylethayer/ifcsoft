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

import ifcSoft.MainApp;
import java.lang.Exception;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.control.OverrunStyle;
import javax.swing.JFileChooser;

/**
 * @author Kyle Thayer <kthayer@emory.edu>
 */

public class ifcDialogDirectorySelect extends ifcDialogItem{
  
  public-init var mainApp:MainApp;
  public-init var initialDirectory:String;

  var path:String;

  public-init var openAction:function():Void;
  public-init var okAction:function():Void;
  public-init var cancelAction:function():Void;


  init{
    if(mainApp == null){ //This needs mainApp to be able to ask about data sets and display the
      throw new Exception("ifcDialogDataSetSelect: mainApp must be initialized");
    }


    if(initialDirectory != null){
      path = initialDirectory;
    }else{
      path = mainApp.lastFilePath;
    }


    children =
      HBox{
        spacing:3
        content:[
          ifcDialogButton{text:"Select Directory" action: selectDirectory}
          Label {text: bind path
              layoutInfo: LayoutInfo { width: 250 }
              textOverrun: OverrunStyle.ELLIPSES
              },
        ]
      }
  }



  function selectDirectory(){
    var fileChooser:JFileChooser;
    if(path == null){
      fileChooser = new JFileChooser();
    }else{
      fileChooser = new JFileChooser(path);
    }
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
      path = fileChooser.getSelectedFile().toString();
      mainApp.lastFilePath = path;
    }

  }

  public function getInput():String{
    return path;
  }

  /*override function validate():Boolean{
    //see if the directory is valid (does the filechooser gurantee this?)
    try{

      //Float.parseFloat(floatInput.text);
    }catch(e:Exception){
      return false;
    }
    return true;
  }*/

}
