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
public class SubsetData extends DataSet {
	DataSet parentSet;
	int[] members;

	/**
	 *
	 * @param dataSet
	 * @param members
	 */
	public SubsetData(DataSet dataSet, int[] members){
		name = dataSet.getName() + " subset";
		this.parentSet = dataSet;
		this.members = members;

		dataSet.registerChild((DataSet) this);

		mins = new float[parentSet.getDimensions()];
		maxes = new float[parentSet.getDimensions()];
		means = new double[parentSet.getDimensions()];


		findstats();

	}


	/**
	 *
	 * @return
	 */
	@Override
	public String[] getColLabels() {
		return parentSet.getColLabels();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int getDimensions() {
		return parentSet.getDimensions();
	}



	/**
	 *
	 * @param index
	 * @return
	 */
	@Override
	public float[] getUnMaskedVals(int index) {
		return parentSet.getVals(members[index]);
	}


	/**
	 *
	 * @return
	 */
	@Override
	public int UnMaskedLength(){
		return members.length;
	}



	/**
	 *
	 * @param index
	 * @return
	 */
	@Override
	public String getUnMaskedPointSetName(int index) {
		return parentSet.getPointSetName(members[index]);
	}








	/**
	 *
	 * @return
	 */
	@Override
	public LinkedList<DataSet> getParents() {
		LinkedList<DataSet> temp = new LinkedList<DataSet>();
		temp.add(parentSet);
		return temp;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public LinkedList<String> getRawSetNames() {
		return parentSet.getRawSetNames();
	}

	@Override
	public LinkedList<DataSet> getRawSets() {
		return parentSet.getRawSets();
	}

	/**
	 *
	 * @param dataSet
	 * @param lastRemoved
	 */
	@Override
	public void parentPointsRemoved(DataSet dataSet, DataSetMaskRemoved lastRemoved) {
		/*//we need to go through all my points and remove them as necessary
		boolean [] removedMask = new boolean[dataSet.length() + lastRemoved.length()]; //the length of the parent before
		//initialize all to true (as in still present)
		for(int i = 0; i < removedMask.length; i++){
			removedMask[i] = true;
		}

		//set all removed points as false
		for(int i = 0; i < lastRemoved.length(); i++){
			removedMask[lastRemoved.getPt(i)] = false;
		}

		//now go through my list of points and remove those that were removed
		int[] newMembers = new int[members.length];
		int newMembsI = 0; //index into the new members array
		DataSetMaskRemoved myRemoved = new DataSetMaskRemoved();
		myRemoved.removalType = DataSetMaskRemoved.PARENTSETREMOVED;
		for(int i = 0; i < members.length; i++){
			int index = members[i];
			if(removedMask[index] == true){ //if the points still valid, put in newMembers
				newMembers[newMembsI] = index;
				newMembsI++;
			}else{ //that point was removed, add to the removed set
				myRemoved.addPt(index);
			}
		}

		//now I need to copy this over to a new array of the correct length (not newMembsI is the correct length)
		members = null; //This should also allow garbage collection of the old members array
		//System.gc();?
		members = new int[newMembsI];
		System.arraycopy(newMembers, 0, members, 0, newMembsI);


		DataSetMaskRemoved toPassOn = myRemoved;
		//Inform Mask if it exists
		if(myMask != null){
			toPassOn = myMask.parentPointsRemoved(myRemoved);
		}

		//inform children
		for(int i = 0; i < children.size(); i++){
			children.get(i).parentPointsRemoved((DataSet)this, toPassOn);
		}*/

	}



}
