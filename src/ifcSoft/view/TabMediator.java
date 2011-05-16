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
package ifcSoft.view;

/**
 * An interface that every Tab Mediator needs to implement.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public interface TabMediator {


  /**
   * Display the tab on the screen.
   */
  void display();

  /**
   * Give the tabMediator the chance to save anything it needs to before being swapped out.
   */
  void swapOutTab();

  /**
   * Returns the name of the tab.
   * @return
   */
  String getTabName();
  
  /**
   * Returns progress of a tab (eventually each tab will show some sort of progress)
   * @return  0 - 1 if in progress, 100 if done
   */
  float getTabProgress();

  /**
   * Inform the tab that a new DSP has been loaded.
   */
  public void informNewDsp();

  /**
   * Tries to close the tab
   * @return True if the tab allows itself to be closed
   */
  public boolean closeTab();


  /**
   *
   * Returns whether or not the dialog content should be blocked (for reloading tab).
   * @return if the dialog content should be blocked
   */
  public boolean isDialogContentBlocked();

}
