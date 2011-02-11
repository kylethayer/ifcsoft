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
package ifcSoft.control;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import ifcSoft.ApplicationFacade;
import ifcSoft.model.DataSetProxy;

/**
 * Save the SOM to a file.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SaveSOMCommand extends SimpleCommand {


  /**
   * Save the SOM to a file. (currently disabled).
   * @param note
   */
  @Override
  public void execute(INotification note){
    //TODO: Redo
    //for now, we only do FCS (actually only CSV's of them)
    /*Object msgData[]  = (Object[]) note.getBody();
    DataSetProxy dsp = (DataSetProxy) msgData[0];
    String filename = (String) msgData[1];
    dsp.saveIFlowFile(filename);*/
  }
}
