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


/**
 * This is to hold the images and names of the SOM map tiles to be sent to the view control.
 *
 * JavaFX doesn't like arrays passed and I also want to be able to pass names and other
 * info with the SOMs, so I'm sending a class instead.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMholder {

  /**
   * The width of the images (in pxls).
   */
  public int imgwidth;
  /**
   * The height of the images (in pxls).
   */
  public int imgheight;
  /**
   * The name of the data set
   */
  public String setName;

  /**
   * The names of the raw data sets that comprise the data set
   */
  public String rawSetNames[];

  /**
   * The images for the different dimensions
   */
  public BufferedImage dimMapImages[];

  /**
   * The names of the dimensions
   */
  public String dimNames[];

  /**
   * The mins of each dimension
   */
  public float dimMins[];
  /**
   * The maxes of each dimension
   */
  public float dimMaxes[];
  
  /**
   * The uMap image
   */
  public BufferedImage uMap;

  /**
   * The min uMap value
   */
  public float uMapMin;

  /**
   * The max uMap value
   */
  public float uMapMax;

  /**
   * The Edge UMap
   */
  public BufferedImage euMap;

  /**
   * The min Edge UMap value
   */
  public float euMapMin;

  /**
   * The max Edge UMap value
   */
  public float euMapMax;

  /**
   * A blank image (for using whenever a blank is needed to be displayed).
   */
  public BufferedImage blankmap;
  

  /**
   * Add a dimension SOM Map image to the SOMholder
   * @param img The image of the map
   * @param name The name of the dimension
   * @param min
   * @param max
   */
  public void addDimImg(BufferedImage img, String name, float min, float max){
    int length = 0;
    if(dimMapImages != null){
      length = dimMapImages.length;
    }
    //make the arrays one longer
    BufferedImage newMapImages[] = new BufferedImage[length+1];
    String newNames[] = new String[length+1];
    float newMins[] = new float[length+1];
    float newMaxes[] = new float[length+1];

    if(length > 0){ //if there was something there before
      System.arraycopy(dimMapImages, 0, newMapImages, 0, length);
      System.arraycopy(dimNames, 0, newNames, 0, length);
      System.arraycopy(dimMins, 0, newMins, 0, length);
      System.arraycopy(dimMaxes, 0, newMaxes, 0, length);
    }

    newMapImages[length] = img;
    newNames[length] = name;
    newMins[length] = min;
    newMaxes[length] = max;

    dimMapImages = newMapImages;
    dimNames = newNames;
    dimMins = newMins;
    dimMaxes = newMaxes;
  }
  
}
