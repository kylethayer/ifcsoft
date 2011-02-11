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

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * These are static functions that handle the image Functions.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMImageFns {
	/**
	 * Creates an image of the given hex rawMap
	 * @param rawMap - raw values (0-1) of a hex map
	 * @return
	 */
	protected static BufferedImage makeSOMhexImg(float[][] rawMap){
		BufferedImage img = new BufferedImage(rawMap.length*8+12,
				rawMap[0].length*6+9,BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < rawMap.length; i++){
			for(int j = 0; j < rawMap[0].length; j++){
				int color = getRGBColor(rawMap[i][j]);
				
				//the "root" points
				int x = i*8;
				int y = j*6;
				//if row (i) is even, move over half a space (3 pixels)
				if((j / 2) * 2 == j)
					x += 4;
				//by columns (symmetrically)
				for(int k = 0; k < 3; k++){  // first/last
					img.setRGB(x, y+3+k, color);
					img.setRGB(x+7, y+3+k, color);
				}
				for(int k = 0; k < 5; k++){
					img.setRGB(x+1, y+2+k, color);
					img.setRGB(x+6, y+2+k, color);
				}
				for(int k = 0; k < 7; k++){
					img.setRGB(x+2, y+1+k, color);
					img.setRGB(x+5, y+1+k, color);
				}
				for(int k = 0; k < 9; k++){
					img.setRGB(x+3, y+k, color);
					img.setRGB(x+4, y+k, color);
				}
				
			}
		}	
		return img;
	}
	
	/**
	 *Creates an Edge Umap image of the given hex rawUEMap
	 * @param rawMap
	 * @param rawUEMap - raw values (0-1) of a hex map
	 * @return
	 */
	protected static BufferedImage makeSOMhexUImg(float[][][] rawUEMap){
		/* Input is an array [x][y][side] where there are 6 sides for each
		 * hexagon (top-right, right, bottom-right, bottom-left, left, top-left)
		 */
		
		BufferedImage img = new BufferedImage(rawUEMap.length*8+12,
				rawUEMap[0].length*6+9,BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < rawUEMap.length; i++){
			for(int j = 0; j < rawUEMap[0].length; j++){
				
				//the "root" points
				int x = i*8;
				int y = j*6;
				//if row (j) is even, move over half a space (3 pixels)
				if((j / 2) * 2 == j)
					x += 4;
				
				//top-right
				if(rawUEMap[i][j][0] != -1){ //-1 means it is a border
					int color = getRGBColor(rawUEMap[i][j][0]);
					img.setRGB(x+4, y, color);
					img.setRGB(x+4, y+1, color);
					img.setRGB(x+5, y+1, color);
					img.setRGB(x+5, y+2, color);
					img.setRGB(x+6, y+2, color);
				}
				//right
				if(rawUEMap[i][j][1] != -1){ //-1 means it is a border
					int color = getRGBColor(rawUEMap[i][j][1]);
					img.setRGB(x+7, y+3, color);
					img.setRGB(x+7, y+4, color);
					img.setRGB(x+7, y+5, color);
					img.setRGB(x+6, y+3, color);
					img.setRGB(x+6, y+4, color);
					img.setRGB(x+6, y+5, color);
				}
				//bottom-right
				if(rawUEMap[i][j][2] != -1){ //-1 means it is a border
					int color = getRGBColor(rawUEMap[i][j][2]);
					img.setRGB(x+4, y+8, color);
					img.setRGB(x+4, y+7, color);
					img.setRGB(x+5, y+7, color);
					img.setRGB(x+5, y+6, color);
					img.setRGB(x+6, y+6, color);
				}
				//bottom-left
				if(rawUEMap[i][j][3] != -1){ //-1 means it is a border
					int color = getRGBColor(rawUEMap[i][j][3]);
					img.setRGB(x+3, y+8, color);
					img.setRGB(x+3, y+7, color);
					img.setRGB(x+2, y+7, color);
					img.setRGB(x+2, y+6, color);
					img.setRGB(x+1, y+6, color);
				}
				//left
				if(rawUEMap[i][j][4] != -1){ //-1 means it is a border
					int color = getRGBColor(rawUEMap[i][j][4]);
					img.setRGB(x, y+3, color);
					img.setRGB(x, y+4, color);
					img.setRGB(x, y+5, color);
					img.setRGB(x+1, y+3, color);
					img.setRGB(x+1, y+4, color);
					img.setRGB(x+1, y+5, color);
				}
				//top-left
				if(rawUEMap[i][j][5] != -1){ //-1 means it is a border
					int color = getRGBColor(rawUEMap[i][j][5]);
					img.setRGB(x+1, y+2, color);
					img.setRGB(x+2, y+2, color);
					img.setRGB(x+2, y+1, color);
					img.setRGB(x+3, y+1, color);
					img.setRGB(x+3, y, color);

				}
				
			}
		}	
		return img;
	}
	
	/**
	 * Creates an overlay image showing the given cluster
	 * @param cluster - the nodes that are part of a cluster are marked True
	 * @return
	 */
	protected static BufferedImage makeClusterHexImg(boolean[][] cluster) {
		BufferedImage img = new BufferedImage(cluster.length*8+12,
				cluster[0].length*6+9,BufferedImage.TYPE_INT_ARGB);
		for(int i = 0; i < cluster.length; i++){
			for(int j = 0; j < cluster[0].length; j++){
				Color c;
				if(cluster[i][j]){ //if the point is in the cluster, then white, 50%alpha
					c = new Color(1.0f, 1.0f, 1.0f, .25f);
				}else{ //if not in the cluster, clear
					c = new Color(1.0f, 1.0f, 1.0f, 0f);
				}
				//the "root" points
				int x = i*8;
				int y = j*6;
				//if row (i) is even, move over half a space (3 pixels)
				if((j / 2) * 2 == j)
					x += 4;
				//by columns (symmetrically)
				for(int k = 0; k < 3; k++){  // first/last
					img.setRGB(x, y+3+k, c.getRGB());
					img.setRGB(x+7, y+3+k, c.getRGB());
				}
				for(int k = 0; k < 5; k++){
					img.setRGB(x+1, y+2+k, c.getRGB());
					img.setRGB(x+6, y+2+k, c.getRGB());
				}
				for(int k = 0; k < 7; k++){
					img.setRGB(x+2, y+1+k, c.getRGB());
					img.setRGB(x+5, y+1+k, c.getRGB());
				}
				for(int k = 0; k < 9; k++){
					img.setRGB(x+3, y+k, c.getRGB());
					img.setRGB(x+4, y+k, c.getRGB());
				}
				
			}
		}	
		return img;
	}

	/**
	 * Given a point (by pxl) on the HexMap, return what node it was part of.
	 * @param p - the pixel
	 * @return The coordinates of the node the pixel was in
	 */
	protected static Point getcell(Point p){
		//This only deals with Hex map
		
		Point cell = new Point();
		p.y += 6; //When I did the logic on this, I confused which row was shifted right, so this
					//makes up for that error along with subtracting 1 from y whenever we set it
		
		
		
		int doublerow = p.y / 12; //breaks up by left shifted cells
		int column = p.x / 8;
		int relx = p.x - column*8;
		int rely = p.y - doublerow * 12;
		
		if(rely > 8){
			//if it is one of the horizontal connections between right-shifted cells
			cell.y = doublerow*2 + 1 - 1;
			if(relx < 4){
				//if it is the left sided one
				cell.x = column-1;
			}else{ //if the right sided one
				cell.x = column;				
			}
			return cell;
		}
		
		//getting here means it is the blocks around left shifted cells

		//the top left corner
		if(	(rely == 0 && relx < 3)	||
			(rely == 1 && relx < 2)	||
			(rely == 2 && relx < 1)	){

				cell.x = column - 1;
				cell.y = (int) doublerow*2 - 1 - 1;
				return cell;
		}
		
		//the top right corner
		if(	(rely == 0 && relx > 4)	||
			(rely == 1 && relx > 5)	||
			(rely == 2 && relx > 6)	){

				cell.x = column;
				cell.y = (int) doublerow*2 - 1 - 1;
				return cell;
		}
		
		//the bottom left corner
		if(	(rely == 6 && relx < 3)	||
			(rely == 7 && relx < 2)	||
			(rely == 8 && relx < 1)	){

				cell.x = column - 1;
				cell.y = (int) doublerow*2 + 1 - 1;
				return cell;
		}
		
		//the bottom right corner
		if(	(rely == 6 && relx > 4)	||
			(rely == 7 && relx > 5)	||
			(rely == 8 && relx > 6)	){

				cell.x = column;
				cell.y = (int) doublerow*2 + 1 - 1;
				return cell;
		}
		
		
		//if we get here, it is the cell that it is centered on
		cell.x = column;
		cell.y = (int) doublerow*2 - 1;
		
		return cell;
	}

	/**
	 * Returns the pxls that belong to the given node.
	 * @param i - x coordinate of node
	 * @param j - y coordinate of node
	 * @return
	 */
	static LinkedList getPxlsOfCell(int i, int j) {
		//This only deals with Hex map
		LinkedList<Point> list = new LinkedList<Point>();

		//note, this code is modified from MakeSomHexImage

		//the "root" points
		int x = i*8;
		int y = j*6;
		//if row (i) is even, move over half a space (3 pixels)
		if((j / 2) * 2 == j)
			x += 4;
		//by columns (symmetrically)
		for(int k = 0; k < 3; k++){  // first/last
			list.add(new Point(x, y+3+k));
			list.add(new Point(x+7, y+3+k));
		}
		for(int k = 0; k < 5; k++){
			list.add(new Point(x+1, y+2+k));
			list.add(new Point(x+6, y+2+k));
		}
		for(int k = 0; k < 7; k++){
			list.add(new Point(x+2, y+1+k));
			list.add(new Point(x+5, y+1+k));
		}
		for(int k = 0; k < 9; k++){
			list.add(new Point(x+3, y+k));
			list.add(new Point(x+4, y+k));
		}
		return list;
	}
	
	/**
	 * Produces a color based on the input value.
	 * @param val - floate from 0 to 1
	 * @return
	 */
	private static int getRGBColor(float val){
		/*Color c;
		if(val <= .5){ //blue to Green
			float temp = 2*val;
			if(temp < 0 || temp > 1){
				System.out.println("val out of range:"+ temp);
			}
			 c = new Color(0f, temp, 1 - temp);
			 
			 
		}else{ //Green to red
			val -= .5;
			c = new Color(val*2, 1 - val*2 ,0f);
		}
		
		return c.getRGB();*/
		return Color.HSBtoRGB(.65f*(1-val), //if I go all the way to 1 I get red again
				val*.75f+.25f,
				val*.75f+.25f); //make it dimmer if it's lower
	}


	
}
