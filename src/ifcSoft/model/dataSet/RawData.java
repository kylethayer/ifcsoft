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


import FloCK.FCSLoader.FCSLoader;
import FloCK.FCSLoader.Stats;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 *
 * @author Kyle Thayer <kthayer@emory.edu>
 */
public class RawData extends DataSet {

  private String fileName;
  private String[] columnLabels;
  private LinkedList<float[][]> data = new LinkedList<float[][]>();
  private int length;

  private boolean hasNames = false;
  private LinkedList<String[]> dataNames = new LinkedList<String[]>();

  private boolean didload = false;

  private int loadProgress = 0;

  private FCSLoader fcs; //needs to be an object variable so progress can be checked


  
  /**
   * Constructor that takes a file
   * I'll want to add another constructor for copy/paste into program
   * @param filename
   */
  public RawData(String filename){
    fileName = filename;

    //get the name of the file from the filename
    int lastind1 = filename.lastIndexOf('/'); //either method of directory division
    int lastind2 = filename.lastIndexOf('\\');
    if(lastind1 >= 0){
      if(lastind2 > lastind1){ //if "\" happened last
        name = filename.substring(lastind2+1);
      }else{
        name = filename.substring(lastind1+1);
      }

    }else{
      if(lastind2 >=0){
        name = filename.substring(lastind2+1);
      }else{ //neither slash was contained in filename
        name = filename;
      }
    }
  }

  /**
   * 
   * @throws Exception
   */
  public void loadFile()throws Exception{
    DataInputStream in;
    if(fileName.startsWith("http") && fileName.endsWith(".csv") ){
      URL u = new URL(fileName);
      URLConnection urlCon = u.openConnection();
      urlCon.setDoInput(true);
      in = new DataInputStream(urlCon.getInputStream());
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      readCSV(br);
    }else if(fileName.endsWith(".csv")){
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      readCSV(br);
    }else if(fileName.endsWith(".fcs")){
      fcs = new FCSLoader();
      Stats stats = fcs.ReadFCS(fileName);
      double [][] dataArray = stats.DataArray;
      columnLabels = stats.ChannelNames;
      System.out.println("data.length = "+dataArray.length + " data[0].length = "+dataArray[0].length);
      System.out.print("columnLabels:");
      for(int i = 0; i < columnLabels.length; i++){
        System.out.print(columnLabels[i] +", ");
      }
      System.out.println();


      mins = new float[columnLabels.length];
      maxes = new float[columnLabels.length];
      means = new double[columnLabels.length];
      for(int k = 0; k < columnLabels.length; k++){
        mins[k] = Float.MAX_VALUE;
        maxes[k] = Float.MIN_VALUE;
        means[k] = 0; //for now we'll use it to keep sums
      }



      length = 0;
      while(length < dataArray[0].length){
        float[][]newSeg;
        if(dataArray.length - length < DataSet.SEGSIZE){
          newSeg = new float[dataArray[0].length - length][columnLabels.length];
        }else{
          newSeg = new float[DataSet.SEGSIZE][columnLabels.length];
        }

        for(int i = 0; i < newSeg.length; i++){
          for(int k = 0; k < columnLabels.length; k++){
            newSeg[i][k] = (float) dataArray[k][length];
            if(newSeg[i][k] < mins[k]){
              mins[k] = newSeg[i][k];
            }

            if(newSeg[i][k] > maxes[k]){
              maxes[k] = newSeg[i][k];
            }
          }
          length++;
        }

        data.add(newSeg);
      }

      findstats();

      System.out.println("length="+length);
      //find actual means
      for(int k = 0; k < columnLabels.length; k++){
        System.out.println("min "+k + "= " + mins[k]);
        System.out.println("maxes "+k + "= " + maxes[k]);
        System.out.println("mean "+k + "= " + means[k]);
      }

      didload = true;
      return;
    }
  }

  /**
   *
   * @param data
   * @param length
   * @param colLabels
   * @param name
   */
  public RawData(LinkedList<float[][]> data, int length, String[] colLabels, String name) {
    this.data = data;
    this.columnLabels = colLabels;
    this.name = name;
    this.length = length;

    mins = new float[colLabels.length];
    maxes = new float[colLabels.length];
    means = new double[colLabels.length];


    this.findstats();
  }

  /**
   *
   * @return
   */
  public boolean didLoad(){
    return didload;
  }

  /**
   *
   * @return
   */
  public int getProgress(){
    if(fcs != null){
      return fcs.getProgress();
    }
    return loadProgress;
  }


  /**
   *
   * @return
   */
  @Override
  public int getDimensions(){
    return columnLabels.length;
  }



  /**
   *
   * @return
   */
  @Override
  public int UnMaskedLength(){
    return length;
  }



  /**
   *
   * @param index
   * @return
   */
  @Override
  public float[] getUnMaskedVals(int index) {
    //find the right segment
    int i = 0;
    int sofar = 0;
    while(sofar + data.get(i).length <= index){
      sofar += data.get(i).length;
      i++;
    }
    //i is now pointing at the correct segment and sofar tells us how many points were in previous segments
    return data.get(i)[index - sofar];
  }


  @Override
  public String getUnMaskedPointName(int index){
    if(hasNames){
      //find the right segment
      int i = 0;
      int sofar = 0;
      while(sofar + data.get(i).length <= index){
        sofar += data.get(i).length;
        i++;
      }
      //i is now pointing at the correct segment and sofar tells us how many points were in previous segments
      return dataNames.get(i)[index - sofar];
    }
    return ""+index;
  }


  @Override
  public boolean hasPointNames(){
    return hasNames;
  }




  /**
   *
   * @return
   */
  @Override
  public String[] getColLabels() {
    return columnLabels;
  }




  /**
   *
   * @return
   */
  public String getFileName() {
    return fileName;
  }
  

  /**
   *
   * @param index
   * @return
   */
  @Override
  public String getUnMaskedPointSetName(int index) {
    return name;
  }

  /**
   *
   * @return
   */
  @Override
  public LinkedList<DataSet> getParents() {
    return new LinkedList<DataSet>(); //there are no parents of a raw data set;
  }

  /**
   *
   * @return
   */
  @Override
  public LinkedList<String> getRawSetNames() {
    LinkedList<String>temp = new LinkedList<String>();
    temp.add(name);
    return temp;
  }

  @Override
  public LinkedList<DataSet> getRawSets() {
    LinkedList<DataSet>temp = new LinkedList<DataSet>();
    temp.add(this);
    return temp;
  }

  /**
   *
   * @param dataSet
   * @param lastRemoved
   */
  @Override
  public void parentPointsRemoved(DataSet dataSet, DataSetMaskRemoved lastRemoved) {
    throw new UnsupportedOperationException("The program should never get here.");
  }



  /*************************************************************/
  /*        File reading stuff               */


  private void readCSV(BufferedReader br) {

    String line = null;
    LinkedList<float[]> tempData = new LinkedList<float[]>(); //it is read into here, first, then stuck in the data array
    LinkedList<String> tempNames = new LinkedList<String>();
    try{
      line = br.readLine();
    }catch(IOException ex){
      System.out.println("IO Error:" + ex);
      return;
    }

    length = 0;
    int dataRows = 0;
    String st[];
    while(line != null){
      //StringTokenizer st = new StringTokenizer(line, ",");
      st = line.split(",");
      //if the column labels aren't set, then this is the first read
      if(columnLabels == null){
        //columnLabels = new String[st.countTokens()];
        columnLabels = st;
        for(int i = 0; i < columnLabels.length; i++){
          //columnLabels[i] = st.nextToken();
          if(columnLabels[i].contentEquals("F0") || columnLabels[i].contentEquals("\"F0\"")){
                columnLabels[i] = "SSC-A";
          }
          if(columnLabels[i].contentEquals("F1") || columnLabels[i].contentEquals("\"F1\"") ){
            columnLabels[i] = "FSC-A";
          }
        }
        if(columnLabels[0].equalsIgnoreCase("name") || columnLabels[0].equalsIgnoreCase("file")){
          hasNames = true;
          String temp[] = new String[columnLabels.length-1];
          for(int i=0; i < temp.length; i++){
            temp[i] = columnLabels[i+1];
          }
          columnLabels = temp;
        }
        columnLabels = fixQuotes(columnLabels);
      }else{//the column labels have been read
        float[] thisrow = new float[columnLabels.length];
        boolean didLoadRow = true;
        String name = null;
        if(hasNames){
          //name = st.nextToken();
            name = st[0];
        }
        //if(st.countTokens() != columnLabels.length){
        if(st.length != columnLabels.length){
          System.out.println("Error reading FCS, columns in row"
              +dataRows+" didn't match");
          didLoadRow = false;
        }

        try{
          for(int i = 0; i < columnLabels.length; i++){
            //thisrow[i] = Float.parseFloat(st.nextToken());
              thisrow[i] = Float.parseFloat(st[i]);
          }
        }catch(Exception e){
          didLoadRow = false;
          System.out.println("Error reading FCS, couldn't parse floats in row"
              +dataRows);
        }

        if(didLoadRow){
          tempData.add(thisrow);
          if(hasNames){
            tempNames.add(name);
          }
          dataRows++;
          loadProgress++;
          if(dataRows == SEGSIZE){//we'll read them into groups of MAXPERSEG (to try not to crash)
            addSegToData(tempData, tempNames, dataRows);
            //reset for next dataSegment
            tempData = new LinkedList<float[]>();
            tempNames = new LinkedList<String>();
            dataRows = 0;
          }
        }
      }
      try{
        line = br.readLine();
        if(line == null){ //if end of file
          break;
        }
      }catch(IOException ex){
        //if(ex.)
        System.out.println("IO Error:" + ex);
        return;
      }

    }

    if(dataRows > 0){  //if the last pass read some data, add it
      addSegToData(tempData, tempNames, dataRows);
      //use this to clear the linked list
      tempData = null;
    }

    findstats(); //since my live rolling average failed due to rounding, I'll try

    System.out.println("length="+length);
    //find actual means
    for(int k = 0; k < columnLabels.length; k++){
      System.out.println("min "+k + "= " + mins[k]);
      System.out.println("maxes "+k + "= " + maxes[k]);
      System.out.println("mean "+k + "= " + means[k]);
    }

    didload = true;
    return;
  }

  private void addSegToData(LinkedList<float[]> tempData, LinkedList<String> tempNames, int dataRows) {
    //I've read the data into a linked list, but I really want to access it as
    //an array, so I will now copy that over.
    //while I'm at it, I'll find the min and max of each data thingy
    float[][] dataSeg = new float[dataRows][];
    String[] nameSeg = new String[dataRows];

    if(mins == null){
      mins = new float[columnLabels.length];
      maxes = new float[columnLabels.length];
      means = new double[columnLabels.length];
      for(int k = 0; k < columnLabels.length; k++){
        mins[k] = Float.MAX_VALUE;
        maxes[k] = Float.MIN_VALUE;
        means[k] = 0; //for now we'll use it to keep sums
      }
    }


    for(int i = 0; i < dataRows; i++){
      dataSeg[i] = tempData.removeFirst();
      if(hasNames){
        nameSeg[i] = tempNames.removeFirst();
      }
      //while we're adding the data, calculate the means and average
      length ++;
      for(int k=0; k < columnLabels.length; k++){

        //means[k] += means[k]*((1.0 - length) / length) + dataSeg[i][k] / (1.0*length);
          //Note the means fails due to rounding errors
        if(dataSeg[i][k] < mins[k]){
          mins[k] = dataSeg[i][k];
        }
        if(dataSeg[i][k] > maxes[k]){
          maxes[k] = dataSeg[i][k];
        }

      }
    }

    data.add(dataSeg);
    if(hasNames){
      dataNames.add(nameSeg);
    }
    System.out.println("length after seg "+ data.size()+": "+ length);
  }

  /**
   * In a csv file, column labels may have a comma in them and be surrounded by quotes.
   * eg. name, "(0,0)", "(0,1)", etc.
   * This should deal with it in most cases, though I'm not sure what the rule is when
   * you have more complicated labels with quotes and commas next to each other
   * @param columnLabels
   * @return
   */
  private String[] fixQuotes(String[] columnLabels) {
    LinkedList<String> newLabels = new LinkedList<String>();
    String currentString = null;
    for(int i = 0; i < columnLabels.length; i++){
      if(currentString == null){
        if(columnLabels[i].startsWith("\"")){ //start of a special case
          currentString = columnLabels[i];
					if(columnLabels[i].endsWith("\"")){ //if it also ends with a "
						newLabels.add(currentString);
						currentString = null;
					}
        }else{ //normal string value
          newLabels.add(columnLabels[i]);
        }
      }else{ //we are trying to find the end of the string value (ends with ")
        if(columnLabels[i].endsWith("\"")){ //end of the string value
          currentString+= ","+columnLabels[i];
          newLabels.add(currentString);
          currentString = null;
        }else{ //continuing the middle
          currentString+= ","+columnLabels[i];
        }
      }
    }

    return (String[]) newLabels.toArray(new String[0]);
  }
}
