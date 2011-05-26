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

import ifcSoft.MainAppI;
import ifcSoft.view.TabMediator;

/**
 * Mediator for the "blank" tab.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class BlankTabMediator implements TabMediator {

  BlankTabI btvc;

  /**
   * The constructor needs a link to the MainApp to be able to display stuff.
   * @param app
   */
  public BlankTabMediator(MainAppI app){
    btvc = app.makeBlankTab();
  }

  @Override
  public void display() {
    //if there's data do one thing, else do other
    btvc.displayTab();
  }

  @Override
  public void swapOutTab() {
    //nothing to be saved ... so far
  }

  @Override
  public String getTabName() {
    return "blank";
  }

  @Override
  public float getTabProgress() {
    return 100;
  }

  @Override
  public void informNewDsp() {
    btvc.informNewDsp();
  }

  /**
   * Tries to close the tab
   * @return True if the tab allows itself to be closed
   */
  @Override
  public boolean closeTab(){
    return true; //Nothing to be saved, so just return
  }

  /**
   *  Returns whether or not the dialog content should be blocked (for reloading tab).
   *  In this case, it never should be blocked.
   * @return false
   */
  @Override
  public boolean isDialogContentBlocked(){
    return false;
  }
}
