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
 * Load a SOM from a ".iflo" file.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class LoadSOMCommand extends SimpleCommand {


  /**
   * Load a SOM from a ".iflo" file.
   * @param note
   */
  @Override
  public void execute(INotification note){
    //for now, we only do FCS (actually only CSV's of them)
    String filename = (String) note.getBody();
    DataSetProxy dsp = new DataSetProxy();
    dsp.loadIFlowFile(filename);
    //somehow need to check if it really did load
    
    //this seems bad practice
    sendNotification(ApplicationFacade.ADDNEWDSP, dsp, null); //Will this work?
    sendNotification(ApplicationFacade.RETURNEDSOM, dsp, null); //Will this work?


  }
}