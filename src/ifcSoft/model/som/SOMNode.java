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

import java.util.Random;

/**
 * Holds an individual SOM Node.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class SOMNode {

  private float weights[];
  
  /**
   * If not given an initial value, this will set random weights between 0
   * and 1 for each dimension.
   * @param dimensions
   */
  protected SOMNode(int dimensions, float min[], float max[]){
    weights = new float[dimensions];
    Random r = new Random();
    for(int i = 0; i < dimensions; i++){
      weights[i] = r.nextFloat() * (min[i] - max[i]) + min[i];
      
    }
  }
  
  /**
   * Creates a node with the given initial value.
   * @param dimensions
   * @param initialWeight
   */
  public SOMNode(float initialWeight[]){
    weights = new float[initialWeight.length];
    System.arraycopy(initialWeight, 0, weights, 0, initialWeight.length);
  }
  
  /**
   * This gets the weight of a specific dimension from the node.
   * @param dim
   * @return
   */
  protected float getWeight(int dim){
    return weights[dim];
  }
  
  /**
   * This gets the all the weights from the node.
   * @return
   */
  protected float[] getWeights(){
    return weights;
  }

  protected void setWeights(float newWeights[]){
    System.arraycopy(newWeights, 0, weights, 0, weights.length);
  }
  
  /**
   * This moves the weight of the node toward the newWeight given
   * by a factor of alpha (1 being replace weight and 0 being no change).
   * @param newWeight
   * @param alpha
   */
  protected void shiftWeights(float[] newWeight, float alpha){
    for(int k=0; k < weights.length; k++){
      weights[k] = (1-alpha)*weights[k] + alpha*newWeight[k];
    }
  }
}

