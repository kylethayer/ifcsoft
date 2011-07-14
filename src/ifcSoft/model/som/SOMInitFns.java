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

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import ifcSoft.model.dataSet.DataSet;
import ifcSoft.view.fileFilter.IFlowFileFilter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;

/**
 *
 * @author kthayer
 */
public class SOMInitFns {
  /***********************************************************/
  /*           Initialization Functions                      */
  /***********************************************************/

  static public void loadSOMfile(int dims, SOM som) throws FileNotFoundException, IOException{
        //linearInitialize(dims, width, height);
    JFileChooser fileChooser = new JFileChooser();
    IFlowFileFilter filter = new IFlowFileFilter();
    fileChooser.setFileFilter(filter);

    if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
      //println("approved load SOM!");
      String selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
      BufferedReader br = new BufferedReader(new FileReader(selectedFile));
      String line;
      try{
        line = br.readLine();
      }catch(IOException ex){
        System.out.println("IO Error:" + ex);
        return;
      }
      StringTokenizer st = new StringTokenizer(line, ",");
      int height = Integer.parseInt(st.nextToken());
      int width = Integer.parseInt(st.nextToken());
      som.SOMnodes = new SOMNode[width][height];
      for(int i = width-1; i >=0; i--){ //I'm off by one (on left/right shift of rows) try inverting
        for(int j = 0; j < height; j++){
          line = br.readLine();
          st = new StringTokenizer(line, ",");
          float weights[] = new float[dims];
          for(int k = 0; k < dims; k++){
            weights[k] = Float.parseFloat(st.nextToken());
          }
          som.SOMnodes[i][j] = new SOMNode(weights);
        }
      }

    }
  }

  static public void randomInitialize(int width, int height, SOM som){
    //initialize nodes with random points
    for(int i = 0; i < width ; i++){
      for(int j = 0; j < height ; j++){
        //initialize with random points from the data
        float values[] = som.datasetScalar.getPoint((int) (Math.random() * (som.datasetScalar.length() - 1)));
        for(int k=0; k < values.length; k++){
          if(Float.isNaN(values[k])){//if it's a missing value, fill it in with the avg. value
            values[k] = (float) som.datasetScalar.getMean(k);
          }
        }
        som.SOMnodes[i][j] = new SOMNode(values);
      }
    }
  }


  static public void linearInitialize(int width, int height, DataSet dataset, SOM som){
    int dims = dataset.getDimensions();
    Matrix m = findAutocorrelationMatrix(dataset);

    //since the autocorrelation Matrix may have had divide by 0s (if std deviation = 0)
    //we need to remove all row/columns that have a NAN or infinity
    m.print(8, 2);
    boolean dimsValidity[] = new boolean[dims];
    for(int i = 0; i < dims; i++){
      dimsValidity[i] = false;
    }
    for(int i = 0; i < m.getRowDimension(); i++){
      for(int j = 0; j < m.getColumnDimension(); j++){
        if(!Double.isNaN(m.get(i, j)) && !Double.isInfinite(m.get(i, j)) ){
          if(i != j){
            dimsValidity[i] = true;
            dimsValidity[j] = true;
          }
        }
      }
    }
    boolean areAnyInvalid = false;
    for(int i = 0; i < dims; i++){
      if(!dimsValidity[i]){
        areAnyInvalid = true;
      }
    }

    if(areAnyInvalid){//if some dimensions are invalid we need to remove them from the matrix
      int numValid = 0;
      for(int i = 0; i < dims; i++){
        if(dimsValidity[i]){
          numValid++;
        }
      }
      int validDims[] = new int[numValid];
      int validDimsI = 0;
      for(int i = 0; i < dims; i++){
        if(dimsValidity[i]){
          validDims[validDimsI] = i;
          validDimsI++;
        }
      }

      //make a new matrix with just the valid dimensions and replace the old matrix with it
      Matrix newM = new Matrix(numValid,numValid);
      for(int i = 0; i < numValid; i++){
        for(int j = 0; j < numValid; j++){
          newM.set(i, j, m.get(validDims[i], validDims[j]));
        }
      }

      m = newM;
      System.out.println("New matrix:");
      m.print(8, 2);
    }


    EigenvalueDecomposition test = m.eig();
    double eigenvals[] = test.getRealEigenvalues();
    Matrix eigenvectors = test.getV();

    //find indexes of largest two eigenValues
    int largestEigenIndex = 0;
    int secondLargestEigenIndex = 0;
    for(int i = 1;  i < eigenvals.length; i++){
      if(eigenvals[i] > eigenvals[largestEigenIndex]){
        secondLargestEigenIndex = largestEigenIndex;
        largestEigenIndex = i;
      }else if(eigenvals[i] > eigenvals[secondLargestEigenIndex]){
        secondLargestEigenIndex = i;
      }
    }

    double largestEigenV[] = new double[dims];
    double secondLargestEigenV[] = new double[dims];
    if(areAnyInvalid){
      int validI = 0;
      for(int k = 0; k < dims; k++){ //the columns (second index) are the eigen vectors
        if(dimsValidity[k]){
          largestEigenV[k] = eigenvectors.get(validI, largestEigenIndex);
          secondLargestEigenV[k] = eigenvectors.get(validI, secondLargestEigenIndex);
          validI++;
        }else{
          largestEigenV[k] = dataset.getMean(k);
          secondLargestEigenV[k] = dataset.getMean(k);
        }
      }
    }else{
      for(int k = 0; k < dims; k++){ //the columns (second index) are the eigen vectors
        largestEigenV[k] = eigenvectors.get(k, largestEigenIndex);
        secondLargestEigenV[k] = eigenvectors.get(k, secondLargestEigenIndex);
      }
    }

    Random r = new Random();
    int flipLargestEigen =1;
    int flipSecondEigen =1;
    if(r.nextBoolean()){
      flipLargestEigen = -1;
    }
    if(r.nextBoolean()){
      flipSecondEigen = -1;
    }
    for(int k = 0; k < dims; k++){
      largestEigenV[k] = largestEigenV[k] * eigenvals[largestEigenIndex] * flipLargestEigen;
      secondLargestEigenV[k] = secondLargestEigenV[k]* eigenvals[secondLargestEigenIndex] * flipSecondEigen;
    }

        /*PRINT**/
        for(int i = 0; i < eigenvals.length; i++){
          System.out.println(eigenvals[i]);
        }
        System.out.println("   Largest Eigen index = "+largestEigenIndex);
        System.out.println("   Second Largest Eigen index = "+secondLargestEigenIndex);
        System.out.println();
        for(int i = 0; i < eigenvectors.getColumnDimension(); i++){
          System.out.print("EigenVector: ");
          for(int j = 0; j < eigenvectors.getRowDimension(); j++){
            System.out.print(eigenvectors.get(j, i)+ ", ");
          }
          System.out.println();
        }

        System.out.println("Largest Eigen Normalized");
        for(int k = 0; k < dims; k++){
          System.out.print(largestEigenV[k]+ ", ");
        }
        System.out.println();
        System.out.println("Second Largest Eigen Normalized");
        for(int k = 0; k < dims; k++){
          System.out.print(secondLargestEigenV[k]+ ", ");
        }
        System.out.println();
        /*PRINT**/

      //scale means
    //initialize the nodes
    for(int i = 0; i < width; i++){
      for(int j=0; j < height; j++){
        float initialWeights[] = new float[dims];
        for(int k = 0; k < dims; k++){
          initialWeights[k] = (float)( (largestEigenV[k]*(i*4.0/(width-1)-2)
                      + secondLargestEigenV[k]*(j*4.0/(height-1)-2))*dataset.getStdDev(k)
                      + dataset.getMean(k));
        }


        float scaledWeights [] = som.datasetScalar.scalePoint(initialWeights); //We turn it into the goal dimension set

        som.SOMnodes[i][j] = new SOMNode(scaledWeights);
      }
    }

    System.out.println("SOM Node dims: " + som.SOMnodes[0][0].getWeights().length);
  }

  static private Matrix findAutocorrelationMatrix(DataSet data){
    Matrix autocor = new Matrix(data.getDimensions(), data.getDimensions());
    for(int i = 0; i < data.getDimensions(); i++){
      //set diagonal
      autocor.set(i, i, 1);
      for(int j = 0; j < i; j++){
        double cor = approxAutoCor(data, i, j);
        autocor.set(i, j, cor);
        autocor.set(j, i, cor);

      }
    }

    return autocor;
  }

  //if I have over 10,000 points, I'll just pick 10,000 point at random
  static private double approxAutoCor(DataSet data, int dim1, int dim2){
    double topAvg = 0;
    if(data.length() <= 10000){ //do all points
      int ptsSoFar = 0;
      for(int i = 0; i < data.length(); i++){
        if(!Float.isNaN(data.getVals(i)[dim1]) && !Double.isNaN(data.getMean(dim1))
                && !Float.isNaN(data.getVals(i)[dim2]) && !Double.isNaN(data.getMean(dim2))){
          double nextpt = (data.getVals(i)[dim1] - data.getMean(dim1))
                    *(data.getVals(i)[dim2] - data.getMean(dim2));
          //compute rolling average
          topAvg = (topAvg*ptsSoFar)/(ptsSoFar+1) + nextpt / (ptsSoFar+1);
          ptsSoFar++;
        }
      }
    }else{ //pick 10,000 random points
      int ptsSoFar = 0;
      for(int i = 0; i < 10000; i++){
        int index = (int) (Math.random() * data.length());
        //compute rolling average
        if(!Float.isNaN(data.getVals(i)[dim1]) && !Double.isNaN(data.getMean(dim1))
                && !Float.isNaN(data.getVals(i)[dim2]) && !Double.isNaN(data.getMean(dim2))){
          topAvg = (topAvg*ptsSoFar)/(ptsSoFar+1) +
                    (data.getVals(index)[dim1] - data.getMean(dim1))
                    *(data.getVals(index)[dim2] - data.getMean(dim2)) / (ptsSoFar+1);
          ptsSoFar++;
        }
      }
    }


    //for each data point
      //avg of:
        //(point(dim1) - mean(dim1)) *(point(dim2) - mean(dim2))
    //divide by: stdDev(dim1) * stdDev(dim2)
    return topAvg / data.getStdDev(dim1) / data.getStdDev(dim2);
  }


}
