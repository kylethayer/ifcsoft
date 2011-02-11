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
package ifcSoft.model.som;

import java.awt.Point;
import java.util.LinkedList;

/**
 * utility functions to perform clustering (all so far are done using Depth First Search)
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class ClusteringFunctions {

	/**
	 * Given the initial points, threshold, Edge Umap and SOM, create the cluster
	 * and place it in the boolean[][] cluster object.
	 * @param som
	 * @param cells
	 * @param cluster - the cluster is returned in this object.
	 * @param uEmap
	 * @param threshhold
	 */
	public static void makeECluster(SOM som, Point cells[], boolean[][] cluster,
			float[][][] uEmap, float threshhold) {
		
		//do depth first search using "toProcess" linked list

		LinkedList<Point> toProcess = new LinkedList<Point>();

		for(int i = 0; i < cells.length; i++){ //start with adding the initial points in the cluster
			cluster[cells[i].x][cells[i].y] = true;
			toProcess.add(cells[i]);
		}

		while(toProcess.size() > 0){
			Point cellToProc = toProcess.removeFirst();
			LinkedList<Point> toAdd = getSimilarENeighbors(som, cellToProc, uEmap, threshhold);
			//go through the linked list, recursing when a new point is to be added
			while(toAdd.size() > 0){
				Point pt;
				pt = toAdd.removeFirst();

				//recurse on the point if it wasn't already marked
				if(SOMHelperFns.isPtValid(pt, som)){ //I shouldn't have to check this, I musta done something wrong
					//if the point hasn't already been added, add it to our process list
					if(cluster[pt.x][pt.y] == false){
						cluster[pt.x][pt.y] = true;
						toProcess.addLast(pt);
					}
				}
			}
		}
		
	}
	
	/**
	 * Given the initial points, threshold, Edge Umap and SOM, create the cluster
	 * and place it in the boolean[][] cluster object. (uses 3 edges in test to add cell).
	 * @param som
	 * @param cells
	 * @param cluster - the cluster is returned in this object.
	 * @param uEmap
	 * @param threshhold
	 */
	public static void makeMECluster(SOM som, Point cells[], boolean[][] cluster,
			float[][][] uEmap, float threshhold) {
		
		LinkedList<Point> toProcess = new LinkedList<Point>();

		for(int i = 0; i < cells.length; i++){ //start with adding the initial points in the cluster
			cluster[cells[i].x][cells[i].y] = true;
			toProcess.add(cells[i]);
		}

		while(toProcess.size() > 0){
			Point cellToProc = toProcess.removeFirst();
			LinkedList<Point> toAdd = getSimilarMENeighbors(som, cellToProc, uEmap, threshhold);
			//go through the linked list, recursing when a new point is to be addec
			while(toAdd.size() > 0){
				Point pt;
				pt = toAdd.removeFirst();

				//recurse on the point if it wasn't already marked
				if(SOMHelperFns.isPtValid(pt, som)){ //I shouldn't have to check this, I musta done something wrong
					//if the point hasn't already been added, add it to our process list
					if(cluster[pt.x][pt.y] == false){
						cluster[pt.x][pt.y] = true;
						toProcess.addLast(pt);
					}
				}
			}
		}
	}
	
	/**
	 * Given the initial points, threshold, Umap and SOM, create the cluster
	 * and place it in the boolean[][] cluster object.
	 * @param som
	 * @param cells
	 * @param cluster - the cluster is returned in this object.
	 * @param Umap
	 * @param threshhold
	 */
	public static void makeUCluster(SOM som, Point cells[], boolean[][] cluster,
			float[][] Umap, float threshhold) {

		LinkedList<Point> toProcess = new LinkedList<Point>();

		for(int i = 0; i < cells.length; i++){ //start with adding the initial points in the cluster
			cluster[cells[i].x][cells[i].y] = true;
			toProcess.add(cells[i]);
		}

		while(toProcess.size() > 0){
			Point cellToProc = toProcess.removeFirst();
			LinkedList<Point> toAdd = getSimilarUNeighbors(som, cellToProc, Umap, threshhold);
			//go through the linked list, recursing when a new point is to be addec
			while(toAdd.size() > 0){
				Point pt;
				pt = toAdd.removeFirst();

				//recurse on the point if it wasn't already marked
				if(SOMHelperFns.isPtValid(pt, som)){ //I shouldn't have to check this, I musta done something wrong
					//if the point hasn't already been added, add it to our process list
					if(cluster[pt.x][pt.y] == false){
						cluster[pt.x][pt.y] = true;
						toProcess.addLast(pt);
					}
				}
			}
		}
	}
	


	
	
	/*************************** Get Similar Neighbor functions ****************************/
	
	//EUmap
	private static LinkedList<Point> getSimilarENeighbors(SOM som, Point cell, float[][][] uEmap, 
				float threshhold){
		//I'm only ready to deal with Hex maps for now
		if(som.getSOMType() != SOM.HEXSOM)
			return null;
		LinkedList<Point> neighbors = new LinkedList<Point>();
		
		//look at each direction of the HexMap and add the point if it exists and is below threshhold
		
		//************ top-right ***********
		if(uEmap[cell.x][cell.y][0] < threshhold && uEmap[cell.x][cell.y][0] != -1){
			Point neighbor = new Point();
			neighbor.x = cell.x+1;
			neighbor.y = cell.y-1;
			//if row (y) is even, the row is shifted right logically, 
				//so I undo that shift by subtracting 1
			if((neighbor.y / 2) * 2 == neighbor.y)
				neighbor.x -= 1;
			
			neighbors.add(neighbor);
		}
		

		//************ right ***********
		if(uEmap[cell.x][cell.y][1] < threshhold && uEmap[cell.x][cell.y][1] != -1){
			Point neighbor = new Point();
			neighbor.x = cell.x+1;
			neighbor.y = cell.y;
			
			neighbors.add(neighbor);
		}

		//************ bottom-right ***********
		if(uEmap[cell.x][cell.y][2] < threshhold && uEmap[cell.x][cell.y][2] != -1){
			Point neighbor = new Point();
			neighbor.x = cell.x+1;
			neighbor.y = cell.y+1;
			//if row (y) is even, the row is shifted right logically, 
				//so I undo that shift by subtracting 1
			if((neighbor.y / 2) * 2 == neighbor.y)
				neighbor.x -= 1;
			
			neighbors.add(neighbor);
		}
		

		//************ bottom-left ***********
		if(uEmap[cell.x][cell.y][3] < threshhold && uEmap[cell.x][cell.y][3] != -1){
			Point neighbor = new Point();
			neighbor.x = cell.x;
			neighbor.y = cell.y+1;
			//if row (y) is even, the row is shifted right logically, 
				//so I undo that shift by subtracting 1
			if((neighbor.y / 2) * 2 == neighbor.y)
				neighbor.x -= 1;
			
			neighbors.add(neighbor);
		}
		
		
		//************ left ***********
		if(uEmap[cell.x][cell.y][4] < threshhold && uEmap[cell.x][cell.y][4] != -1){
			Point neighbor = new Point();
			neighbor.x = cell.x-1;
			neighbor.y = cell.y;
			
			neighbors.add(neighbor);
		}
		
		
		//************ top-left ***********
		if(uEmap[cell.x][cell.y][5] < threshhold && uEmap[cell.x][cell.y][5] != -1){
			Point neighbor = new Point();
			neighbor.x = cell.x;
			neighbor.y = cell.y-1;
			//if row (y) is even, the row is shifted right logically, 
				//so I undo that shift by subtracting 1
			if((neighbor.y / 2) * 2 == neighbor.y)
				neighbor.x -= 1;
			
			neighbors.add(neighbor);
		}
		
		
		return neighbors;
	}
	
	
	//multi-edge
	private static LinkedList<Point> getSimilarMENeighbors(SOM som, Point cell, float[][][] uEmap, 
				float threshhold){
		//I'm only ready to deal with Hex maps for now
		if(som.getSOMType() != SOM.HEXSOM)
			return null;
		LinkedList<Point> neighbors = new LinkedList<Point>();
		
		//look at each direction of the HexMap and add the point if it exists and is below threshhold
		
		//************ top-right ***********
		if(uEmap[cell.x][cell.y][0] < threshhold && uEmap[cell.x][cell.y][0] != -1 && //top right
				uEmap[cell.x][cell.y][1] < threshhold && uEmap[cell.x][cell.y][5] < threshhold){ //the two sides
			Point neighbor = new Point();
			neighbor.x = cell.x+1;
			neighbor.y = cell.y-1;
			//if row (y) is even, the row is shifted right logically, 
				//so I undo that shift by subtracting 1
			if((neighbor.y / 2) * 2 == neighbor.y)
				neighbor.x -= 1;
			
			neighbors.add(neighbor);
		}
		
	
		//************ right ***********
		if(uEmap[cell.x][cell.y][1] < threshhold && uEmap[cell.x][cell.y][1] != -1 && //right
				uEmap[cell.x][cell.y][2] < threshhold && uEmap[cell.x][cell.y][0] < threshhold){ //the two sides
			Point neighbor = new Point();
			neighbor.x = cell.x+1;
			neighbor.y = cell.y;
			
			neighbors.add(neighbor);
		}
	
		//************ bottom-right ***********
		if(uEmap[cell.x][cell.y][2] < threshhold && uEmap[cell.x][cell.y][2] != -1 && //bottom-right
				uEmap[cell.x][cell.y][3] < threshhold && uEmap[cell.x][cell.y][1] < threshhold){ //the two sides
			Point neighbor = new Point();
			neighbor.x = cell.x+1;
			neighbor.y = cell.y+1;
			//if row (y) is even, the row is shifted right logically, 
				//so I undo that shift by subtracting 1
			if((neighbor.y / 2) * 2 == neighbor.y)
				neighbor.x -= 1;
			
			neighbors.add(neighbor);
		}
		
	
		//************ bottom-left ***********
		if(uEmap[cell.x][cell.y][3] < threshhold && uEmap[cell.x][cell.y][3] != -1 && //bottom-left
				uEmap[cell.x][cell.y][2] < threshhold && uEmap[cell.x][cell.y][4] < threshhold){ //the two sides
			Point neighbor = new Point();
			neighbor.x = cell.x;
			neighbor.y = cell.y+1;
			//if row (y) is even, the row is shifted right logically, 
				//so I undo that shift by subtracting 1
			if((neighbor.y / 2) * 2 == neighbor.y)
				neighbor.x -= 1;
			
			neighbors.add(neighbor);
		}
		
		
		//************ left ***********
		if(uEmap[cell.x][cell.y][4] < threshhold && uEmap[cell.x][cell.y][4] != -1 && //left
				uEmap[cell.x][cell.y][3] < threshhold && uEmap[cell.x][cell.y][5] < threshhold){ //the two sides
			Point neighbor = new Point();
			neighbor.x = cell.x-1;
			neighbor.y = cell.y;
			
			neighbors.add(neighbor);
		}
		
		
		//************ top-left ***********
		if(uEmap[cell.x][cell.y][5] < threshhold && uEmap[cell.x][cell.y][5] != -1 && //top-left
				uEmap[cell.x][cell.y][4] < threshhold && uEmap[cell.x][cell.y][0] < threshhold){ //the two sides
			Point neighbor = new Point();
			neighbor.x = cell.x;
			neighbor.y = cell.y-1;
			//if row (y) is even, the row is shifted right logically, 
				//so I undo that shift by subtracting 1
			if((neighbor.y / 2) * 2 == neighbor.y)
				neighbor.x -= 1;
			
			neighbors.add(neighbor);
		}
		
		
		return neighbors;
	}

	//U-Map
	private static LinkedList<Point> getSimilarUNeighbors(SOM som, Point cell, float[][] Umap, 
				float threshhold){
		//I'm only ready to deal with Hex maps for now
		if(som.getSOMType() != SOM.HEXSOM)
			return null;
		LinkedList<Point> neighbors = new LinkedList<Point>();
		
		//look at each direction of the HexMap and add the point if it exists and is below threshhold
		
		//************ top-right ***********
		Point neighbor = new Point();
		neighbor.x = cell.x+1;
		neighbor.y = cell.y-1;
		//if row (y) is even, the row is shifted right logically, 
			//so I undo that shift by subtracting 1
		if((neighbor.y / 2) * 2 == neighbor.y)
			neighbor.x -= 1;
		
		if(SOMHelperFns.isPtValid(neighbor, som) && Umap[neighbor.x][neighbor.y] <= threshhold){
			neighbors.add(neighbor);
		}

		//************ right ***********

		neighbor = new Point();
		neighbor.x = cell.x+1;
		neighbor.y = cell.y;
		
		if(SOMHelperFns.isPtValid(neighbor, som) && Umap[neighbor.x][neighbor.y] <= threshhold){
			neighbors.add(neighbor);
		}
	
		//************ bottom-right ***********
		neighbor = new Point();
		neighbor.x = cell.x+1;
		neighbor.y = cell.y+1;
		//if row (y) is even, the row is shifted right logically, 
			//so I undo that shift by subtracting 1
		if((neighbor.y / 2) * 2 == neighbor.y)
			neighbor.x -= 1;
		
		if(SOMHelperFns.isPtValid(neighbor, som) && Umap[neighbor.x][neighbor.y] <= threshhold){
			neighbors.add(neighbor);
		}
		
	
		//************ bottom-left ***********
		neighbor = new Point();
		neighbor.x = cell.x;
		neighbor.y = cell.y+1;
		//if row (y) is even, the row is shifted right logically, 
			//so I undo that shift by subtracting 1
		if((neighbor.y / 2) * 2 == neighbor.y)
			neighbor.x -= 1;
		
		if(SOMHelperFns.isPtValid(neighbor, som) && Umap[neighbor.x][neighbor.y] <= threshhold){
			neighbors.add(neighbor);
		}
		
		
		//************ left ***********
		neighbor = new Point();
		neighbor.x = cell.x-1;
		neighbor.y = cell.y;
		
		if(SOMHelperFns.isPtValid(neighbor, som) && Umap[neighbor.x][neighbor.y] <= threshhold){
			neighbors.add(neighbor);
		}
		
		
		//************ top-left ***********
		neighbor = new Point();
		neighbor.x = cell.x;
		neighbor.y = cell.y-1;
		//if row (y) is even, the row is shifted right logically, 
			//so I undo that shift by subtracting 1
		if((neighbor.y / 2) * 2 == neighbor.y)
			neighbor.x -= 1;
		
		if(SOMHelperFns.isPtValid(neighbor, som) && Umap[neighbor.x][neighbor.y] <= threshhold){
			neighbors.add(neighbor);
		}
		
		
		
		return neighbors;
	}

}
