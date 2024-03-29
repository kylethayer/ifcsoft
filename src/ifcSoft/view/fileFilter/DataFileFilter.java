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
package ifcSoft.view.fileFilter;
import java.io.File;
import javax.swing.filechooser.*;

/**
 * This is a file filter (for the file chooser) that only accepts .csv files.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class DataFileFilter extends FileFilter {

  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) { //so that you can change directories (I think)
      return true;
    }
    String name = f.getName();
    if(name.endsWith(".csv") || name.endsWith(".fcs")){
      return true;
    }

    return false;
  }



  @Override
  public String getDescription() {
    return "Data File (.fcs, .csv)";
  }

}
