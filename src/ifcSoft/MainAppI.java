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



import ifcSoft.model.DataSetProxy;
import ifcSoft.view.MainMediator;
import ifcSoft.view.histogram.HistTabI;
import ifcSoft.view.histogram.HistTabMediator;
import ifcSoft.view.blankTab.BlankTabI;
import ifcSoft.view.scatterplot.ScatterTabI;
import ifcSoft.view.scatterplot.ScatterTabMediator;
import ifcSoft.view.windrose.WindRoseTabI;
import ifcSoft.view.windrose.WindRoseTabMediator;

/**
 * This is just an interface to allow Java to access the JavaFX functions of MainApp
 * since Java cannot directly access JavaFX code.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public interface MainAppI { //TODO: remove interface for letting FX files access it

  
  /**
   * This gives the MainApp a reference to its mediator.
   * @param m - the MainMediator.
   */
  public void setMainMediator(MainMediator m);

  /**
   * Saves the SOM values to a file.
   */
  //public void saveSOM();

  /**
   * Loads an SOM from a file.
   */
  //public void loadSOM();
  
  /**
   * Displays an alert with the given message.
   * @param s
   */
  public void alert(String s);
  
  
  

  /**
   * Returns the MainMediator associated with the MainApp.
   * @return
   */
  public MainMediator getMainMediator();

  /**
   * Creates an input dialog and returns it to the retObj.
   * @param retObj
   */
  //public void inputDlg(InputReturnObj retObj);

  public void nameDSP(DataSetProxy dsp, String info, String type);

  /**
   * Re-draw the tabs if needed.
   */
  public void updateTabs();


  /**
   * Redraw a specific tab if needed.
   * @param tabToUpdate
   */
  public void updateTab(int tabToUpdate);

  /**
   * Set the given tab as the current tab.
   * @param currentTab
   */
  public void setCurrentTab(int currentTab);
  
  /**
   * This function makes the blankTab JavaFX object
   *
   * To create a JavaFX object, you have to do it in a JavaFX object, so I do it here.
   * @return
   */
  public BlankTabI makeBlankTab();

  /**
   * This function makes the histTab JavaFX object
   *
   * To create a JavaFX object, you have to do it in a JavaFX object, so I do it here.
   * @param histMediator 
   * @return
   */
  public HistTabI makeHistTab(HistTabMediator histMediator);

  /**
   * This function makes the histTab JavaFX object
   *
   * To create a JavaFX object, you have to do it in a JavaFX object, so I do it here.
   * @param histMediator
   * @return
   */
  public ScatterTabI makeScatterTab(ScatterTabMediator scatterMediator);

  public WindRoseTabI makeWindRoseTab(WindRoseTabMediator windroseMediator);

  /**
   * Adds another file being loaded to the file-loading display.
   * @param dsp - Data set that has been put on the job queue
   */
  public void addFileLoading(DataSetProxy dsp);

  /**
   * Adds another file having outliers removed to the remove outliers display.
   * @param dsp - Data set that has been put on the job queue
   */
  public void addRemoveOutlier(DataSetProxy dsp);


  public java.awt.Rectangle getStageRectangle();

}
