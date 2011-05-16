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
package ifcSoft.model.som.jobs;

import ifcSoft.model.thread.ThreadJob;

/**
 * A Job to Yield the thread (the idea being to let other things get more cpu
 * time, like the GUI).
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class YieldJob  implements ThreadJob {
  /**
   * It is a Yield Job
   * @return
   */
  @Override
  public int getJobType() {
    return ThreadJob.YIELDJOB;
  }
}
