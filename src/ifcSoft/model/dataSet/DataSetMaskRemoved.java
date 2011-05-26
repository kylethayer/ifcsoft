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
package ifcSoft.model.dataSet;

import java.util.LinkedList;

/**
 *
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class DataSetMaskRemoved {

  /**
   *
   */
  public static final String STDDEVREMOVED = "StdDevRemoved";
  /**
   *
   */
  public static final String PARENTSETREMOVED = "ParentSetRemoved";

  private LinkedList<int[]> removed; // the removed elements
  private int length;
  private int lastSegUsed; //the last set may not be full, this is # elements used
  //info about removal type
  String removalType;

  DataSetMaskRemoved(){
    removed = new LinkedList<int[]>();
    length = 0;
    lastSegUsed = 0;
    removalType = "";
  }

  void addPt(int index) {
    if(removed.isEmpty()){
      removed.add(new int[DataSet.SEGSIZE]);
      lastSegUsed = 0;
    }
    int[] lastSeg = removed.getLast();
    if(lastSeg.length <= lastSegUsed){ //if we need a new segment
      if(lastSeg.length > lastSegUsed){ //test here for now just to double check
        System.out.println("Error in DataSetMaskRemoved.addPt: lastSegUsed too big");
      }
      removed.add(new int[DataSet.SEGSIZE]);
      lastSegUsed = 0;
      lastSeg = removed.getLast();
    }
    //at this point we have the last segment in "lastSeg" and there is room
    //lastSegUsed points to the next free spot
    lastSeg[lastSegUsed] = index;
    lastSegUsed++;
    length++;
  }

  int getPt(int index){
    if(index >= length){
      return -1;
    }
    //find the right segment
    int i = 0;
    int sofar = 0;
    while(sofar + removed.get(i).length <= index){
      sofar += removed.get(i).length;
      i++;
    }
    //i is now pointing at the correct segment and sofar tells us how many points were in previous segments
    return removed.get(i)[index - sofar];
  }

  int length(){
    return length;
  }
}
