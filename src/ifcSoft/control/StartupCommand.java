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

import ifcSoft.ApplicationFacade;
import ifcSoft.MainAppI;
import ifcSoft.view.MainMediator;
import ifcSoft.view.som.SOMMediator;

import org.puremvc.java.interfaces.*;
import org.puremvc.java.patterns.command.*;

/**
 * Starts up the pureMVC system.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class StartupCommand extends SimpleCommand {

  /**
   * The process of initializing the pureMVC system.
   * @param note
   */
  @Override
  public void execute(INotification note){
    MainAppI app = (MainAppI) note.getBody();
    
    facade.registerMediator(new MainMediator(app));
    
    
    
    facade.registerCommand(ApplicationFacade.LOADDATA, LoadDataCommand.class);
    facade.registerCommand(ApplicationFacade.LOADSOM, LoadSOMCommand.class);
    facade.registerCommand(ApplicationFacade.SAVESOM, SaveSOMCommand.class);
    
    facade.sendNotification(MainMediator.NODATA, null, null);
  }
  
}
