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
package ifcSoft.model.thread;

import ifcSoft.model.DataSetProxy;

/**
 * The job holder for loading file jobs.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class LoadFileJob implements ThreadJob{

  /**
   * The dsp (with a filename ready) to be loaded.
   */
  public DataSetProxy dsp;


  /**
   * Create a job to load the file in the dsp.
   * @param dsp - The data set proxy to load the file into (dsp has info on file)
   */
  public LoadFileJob(DataSetProxy dsp){
    this.dsp = dsp;
  }

  @Override
  public int getJobType() {
    return LOADFILEJOB;
  }

}
