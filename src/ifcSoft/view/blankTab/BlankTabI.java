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
package ifcSoft.view.blankTab;

/**
 * The Interface for the JavaFX file (since Java can't call JavaFX directly).
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public interface BlankTabI {
	/**
	 * Display the tab on the screen.
	 */
	public void displayTab();

	/**
	 * Let the view know there is a new Data Set
	 */
	public void informNewDsp();

}
