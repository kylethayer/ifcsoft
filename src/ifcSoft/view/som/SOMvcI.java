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
package ifcSoft.view.som;


import java.awt.image.BufferedImage;
import javafx.scene.input.MouseEvent;

/**
 * This is an interface to allow Java to call the JavaFX functions in SOMvc
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public interface SOMvcI {

  /**
   * Informs the SOMvc the the SOM has started calculating (so it can put up the progress bar).
   */
  public void SOMstarted();

  /**
   * Sets the progress of the calculating SOM.
   * @param p - progress % (0 - 1 if computing, 100 when done)
   */
  public void setProgress(int p);

  /**
   * Tells the SOMvc to draw the SOM maps in the current tab.
   */
  public void dispSOM();
  
  /**
   * When a cluster is selected, an overlay image is drawn to show what was selected,
   * this sets that image. If image is null, it means nothing is selected.
   * @param img - the overlay image
   * @param size - the total number of points selected
   * @param total - the total number of points displayed
   */
  public void setClusterImg(BufferedImage img, int size, int total);
  
  /**
   * Opens the cluster options dialog box.
   */
  public void clustOpt();


  /**
   * Opens the right-click menu.
   * @param e - the event that caused the right click menu to open
   */
  public void showRightClickMenu(MouseEvent e);
  
  /**
   * Tell the SOM tiles to update their status (point over or cluster stats).
   */
  public void updateMapStats();

  /**
   * Make sure the SOMvc is checking for changes in density maps (used when a new density map is added).
   */
  public void continueUpdatingDensities();

  /**
   * Do anything needed before swapping out the tab.
   */
  public void swapOut();

}
