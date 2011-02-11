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
package ifcSoft.model;

import ifcSoft.model.dataSet.DataSet;
import ifcSoft.model.dataSet.RawData;
import ifcSoft.model.som.SOM;
import ifcSoft.model.som.SOMNode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * This class is to load ".iflo" files, though it is currently disabled.
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class FileIO {

  /**
   *
   * @param dsp
   * @param filename
   * @throws IOException
   */
  public static void saveIFlowFile(DataSetProxy dsp, String filename) throws IOException{
    /*BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
    bw.write("IFCSoft File Version 0.1");bw.newLine();
    bw.write("Number Tabs: 1");bw.newLine();
    bw.write("Number Data Sets: 1");bw.newLine();
    bw.newLine();

    bw.write("Tab 1:");bw.newLine();
    bw.write("Type: SOM");bw.newLine();
    int dims = dsp.getColNames().length;
    int width = dsp.getSOMwidth();
    int height = dsp.getSOMheight();
    bw.write("Width: " + width);bw.newLine();
    bw.write("Height: " + height);bw.newLine();

    bw.write("Dimensions: " + dims);bw.newLine();
    bw.write("isLogScale: " + dsp.getData().som.isLog);bw.newLine();

    //write Column labels
    bw.write("Column Labels: ");
    for(int i = 0; i < dims - 1; i++){
      bw.write(dsp.getColNames()[i]+",");
    }
    bw.write(dsp.getColNames()[dims-1]);bw.newLine();

    //write Weights
    bw.write("Column Weights: ");
    for(int i = 0; i < dims - 1; i++){
      bw.write(dsp.getWeighting()[i]+",");
    }
    bw.write(""+dsp.getWeighting()[dims-1]);bw.newLine();

    //write SOM node vals
    bw.write("SOM vals:");bw.newLine();
    for(int i = 0; i < width; i++){
      for(int j = 0; j < height; j++){
        //write node vals
        for(int k = 0; k < dims - 1; k++){
          bw.write(dsp.getData().som.getNodeVals(i, j, k)+",");
        }
        bw.write(""+dsp.getData().som.getNodeVals(i, j,dims-1));bw.newLine();
      }
    }
    bw.newLine();

    bw.write("Data Set 1:");bw.newLine();
    //if Data File
    bw.write("Type: Included");bw.newLine();
    bw.write("Name: "+dsp.getDataSetName());bw.newLine();
    bw.write("Length: "+dsp.getDataSize());bw.newLine();
    //write Column labels
    bw.write("Column Labels: ");
    for(int i = 0; i < dims - 1; i++){
      bw.write(dsp.getColNames()[i]+",");
    }
    bw.write(dsp.getColNames()[dims-1]);bw.newLine();

    //write Data Set vals
    bw.write("Data vals:");bw.newLine();
    for(int i = 0; i < dsp.getDataSize(); i++){
      //write node vals
      for(int k = 0; k < dims - 1; k++){
        bw.write(dsp.getData().dataSet.getVals(i)[k]+",");
      }
      bw.write(""+dsp.getData().dataSet.getVals(i)[dims-1]);bw.newLine();
    }
    bw.newLine();

    bw.close();*/

    //Data
  }


  /**
   *
   * @param dsp
   * @param filename
   * @throws Exception
   */
  public static void loadIFlowFile(DataSetProxy dsp, String filename) throws Exception{
    /*BufferedReader br = new BufferedReader(new FileReader(filename));
    String line = br.readLine();
    if(line.compareTo("IFCSoft File Version 0.1") != 0){
      throw new Exception("Invalid iflo 0.1 File");
    }
    line = br.readLine();
    if(line.compareTo("Number Tabs: 1") != 0){
      throw new Exception("Invalid iflo 0.1 File");
    }
    line = br.readLine();
    if(line.compareTo("Number Data Sets: 1") != 0){
      throw new Exception("Invalid iflo 0.1 File");
    }
    line = br.readLine();//blank

    line = br.readLine();
    if(line.compareTo("Tab 1:") != 0){
      throw new Exception("Invalid iflo 0.1 File");
    }
    line = br.readLine();
    if(line.compareTo("Type: SOM") != 0){
      throw new Exception("Invalid iflo 0.1 File");
    }

    line = br.readLine();
    int width = getIntField(line, "Width");

    line = br.readLine();
    int height = getIntField(line, "Height");

    line = br.readLine();
    int dims = getIntField(line, "Dimensions");

    line = br.readLine();
    boolean isLog = getBoolField(line, "isLogScale");

    System.out.println("Width = " + width + ", height="+ height+", dims="+dims);

    //get column labels
    line = br.readLine();
    if(!line.startsWith("Column Labels: ")){
      throw new Exception("Invalid iflo 0.1 File");
    }
    StringTokenizer st = new StringTokenizer(line.substring(15), ",");
    String colLabels[] = new String[dims];
    for(int i = 0; i < dims; i++){
       colLabels[i] = st.nextToken();
    }

    //get column weights
    line = br.readLine();
    if(!line.startsWith("Column Weights: ")){
      throw new Exception("Invalid iflo 0.1 File");
    }
    st = new StringTokenizer(line.substring(16), ",");
    float colWeights[] = new float[dims];
    for(int i = 0; i < dims; i++){
       colWeights[i] = Float.parseFloat(st.nextToken());
    }

    //get SOM data
    line = br.readLine();
    if(line.compareTo("SOM vals:") != 0){
      throw new Exception("Invalid iflo 0.1 File");
    }
    //TODO: probably remove access to the SOMNodes, make it transparent
    SOMNode SOMNodes[][] = new SOMNode[width][height];
    for(int i = 0; i < width; i++){
      for(int j = 0; j < height; j++){
        line = br.readLine();
        st = new StringTokenizer(line, ",");
        float nodeVals[] = new float[dims];
        for(int k = 0; k < dims; k++){
           nodeVals[k] = Float.parseFloat(st.nextToken());
        }

        SOMNodes[i][j] = new SOMNode(dims, nodeVals);
      }
    }

    System.out.println("finished Loading Som, now for Data Set 1");

    line = br.readLine();//blank

    //get Data set
    line = br.readLine();
    if(line.compareTo("Data Set 1:") != 0){
      throw new Exception("Invalid iflo 0.1 File (Data Set 1:)");
    }
    line = br.readLine();
    if(line.compareTo("Type: Included") != 0){
      throw new Exception("Invalid iflo 0.1 File (Type: Data File)");
    }

    line = br.readLine();
    String name = getStringField(line, "Name");

    line = br.readLine();
    int dataLength = getIntField(line, "Length");

    System.out.println("starting readDataSection");
    readDataSection(dsp, br, dims, dataLength, name);

    //dsp.loadDataFile(fileName);
    System.out.println("setting the som with the data");
    dsp.getData().som = new SOM(dims, width, height, isLog, colWeights, SOM.HEXSOM, dsp.getData().dataSet, SOMNodes);

    //bw.write("Width: " + getSOMwidth() + "\n");
    //bw.write("Height: " + getSOMheight() + "\n");
    //Data*/
  }

  /**
   *
   * @param line
   * @param fieldName
   * @return
   * @throws Exception
   */
  public static int getIntField(String line, String fieldName) throws Exception{
    String startStr = fieldName + ": ";
    if(!line.startsWith(startStr)){
      throw new Exception("Invalid iflo 0.1 File ["+fieldName+"]");
    }
    return Integer.parseInt(line.substring(startStr.length()));
  }

  /**
   *
   * @param line
   * @param fieldName
   * @return
   * @throws Exception
   */
  public static String getStringField(String line, String fieldName) throws Exception{
    String startStr = fieldName + ": ";
    if(!line.startsWith(startStr)){
      throw new Exception("Invalid iflo 0.1 File["+fieldName+"]");
    }
    return line.substring(startStr.length());
  }

  /**
   *
   * @param line
   * @param fieldName
   * @return
   * @throws Exception
   */
  public static boolean getBoolField(String line, String fieldName) throws Exception{
    String startStr = fieldName + ": ";
    if(!line.startsWith(startStr)){
      throw new Exception("Invalid iflo 0.1 File["+fieldName+"]");
    }
    return Boolean.parseBoolean(line.substring(startStr.length()));
  }
  
  

  static void readDataSection(DataSetProxy dsp, BufferedReader br, int dims, int length, String name) throws IOException{
    //get column labels
    String line;
    String colLabels[] = new String[dims];

    System.out.println("getting column labels");
    line = br.readLine();
    if(!line.startsWith("Column Labels: ")){
      System.out.println("Invalid iflo 0.1 File [Column Labels]");
      return;
      //throw new Exception("Invalid iflo 0.1 File [Column Labels]");
    }
    StringTokenizer st = new StringTokenizer(line.substring(15), ",");
    for(int i = 0; i < dims; i++){
       colLabels[i] = st.nextToken();
    }


    //Get the Data

    //int numsegs = (int) Math.ceil(length * 1.0 / FCSData.MAXPERSEG); //number of segs needed
    //I know the total length, but it still seems good to keep things in segments
    //so that it only deals with a segment at a time when doing things like removing outliers
    System.out.println("getting Data");
    LinkedList<float[][]> data = new LinkedList<float[][]>();
    int lengthLeft = length;

    line = br.readLine();
    if(line.compareTo("Data vals:") != 0){
      System.out.println("Invalid iflo 0.1 File [Data vals:]");
      return;
      //throw new Exception("Invalid iflo 0.1 File [SOM vals]");
    }

    while(lengthLeft > 0){
      float[][] dataSeg;
      if(lengthLeft > DataSet.SEGSIZE){
        dataSeg = new float[DataSet.SEGSIZE][];
      }else{
        dataSeg = new float[lengthLeft][];
      }

      for(int i = 0; i < dataSeg.length; i++){ //copy all the data into the seg
        line = br.readLine();
        st = new StringTokenizer(line, ",");
        dataSeg[i] = new float[dims];
        for(int k = 0; k < dims; k++){
          //if(!st.hasMoreTokens()){
          //  System.out.println("no more tokens. i="+i+", k = "+k);
          //  System.out.println("Line:"+line);
          //}
          dataSeg[i][k] = Float.parseFloat(st.nextToken());
        }
      }
      data.add(dataSeg);
      lengthLeft -= dataSeg.length;

    }
    System.out.println("creating the dataSet");

    dsp.setData(new RawData(data, length, colLabels, name));
    

  }

}
