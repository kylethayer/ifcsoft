/**
 *  Copyright (C) 2011  Kyle Thayer <kyle.thayer AT gmail.com>
 *
 *  This file is part of the IFCSoft project (http://www.ifcsoft.com)
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

package ifcSoft;

import org.puremvc.java.interfaces.*;
import org.puremvc.java.patterns.facade.*;

import ifcSoft.control.StartupCommand;


/**
 *
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class ApplicationFacade extends Facade implements IFacade{


	//notification name constants
	/**
	 *
	 */
	public static String STARTUP = "startup";
	/**
	 *
	 */
	public static String LOGIN = "login";
	/**
	 *
	 */
	public static String LOADDATA = "loaddata";
	/**
	 *
	 */
	public static String LOADSOM = "loadsom";
	/**
	 *
	 */
	public static String SAVESOM = "savesom";
	/**
	 *
	 */
	public static String FILELOADED = "fileloaded";
	/**
	 *
	 */
	public static String RETURNEDSOM = "returnedsom";
	/**
	 *
	 */
	public static String STARTEDSOM = "startedsom";
	/**
	 * 
	 */
	public static String GETSOMPROGRESS = "getsomprogress";
	/**
	 *
	 */
	public static String RETURNEDSOMPROGRESS = "returnedsomprogress";
	/**
	 *
	 */
	public static String SOMPROGRESS = "somprogress";
	/**
	 *
	 */
	public static String GETPROGRESS = "getprogress";
	/**
	 *
	 */
	public static String EXCEPTIONALERT = "exceptionalert";
	/**
	 *
	 */
	public static String STRINGALERT = "stringalert";
	/**
	 *
	 */
	public static String CHOOSECLUSTER = "choosecluster";
	/**
	 *
	 */
	public static String SAVECLUSTERTOFILE = "saveclustertofile";
	/**
	 *
	 */
	public static String ADDNEWDSP = "addnewdsp";


	
	/**
	 * Returns the instance of ApplicationFacade (and creates it if needed).
	 * @return The instance ApplicationFacade
	 */
	public static ApplicationFacade getInstance(){
		if(instance == null){
			instance = new ApplicationFacade();
		}
		return (ApplicationFacade) instance;
	}
	
	/**
	 * pureMVC initialize controller.
	 */
	@Override
	protected void initializeController(){
		super.initializeController();
		registerCommand(STARTUP, StartupCommand.class);
	}
	
	/**
	 * pureMVC startup.
	 * @param app
	 */
	public void startup(MainAppI app){
		sendNotification(STARTUP, app, null);
	}
	
	
	
}
