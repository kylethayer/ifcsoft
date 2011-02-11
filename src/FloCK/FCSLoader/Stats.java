/**
 *  Copyright (C) 2011  Tjibbe Donker
 *
 *  This file originally came from the FloCK program
 *  (http://theory.bio.uu.nl/tjibbe/flock/) and is now
 *  part of the IFCSoft project (http://ifcsoft.com)
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
package FloCK.FCSLoader;

//import fcs.*;
import javax.swing.*;

public class Stats {
    public double DataArray[][];
    public char[] ClusterNumbers; 
  public boolean Trash[]; 
  
  public boolean[] ClustersLeft;
    public boolean[] DisplayClusters;
  int NumClusters;
     
  public double min[],max[];
    public boolean logScale[];
    public float decades[];
    public float offsets[];
    public int ranges[];
    public String[] ChannelNames;
    
    public int NumEvents=1;
    public int NumDataPoints=1;
    public int NumChannels=1;
    int SubSet;
    public short NormScale = 1024;
    public int EventsNotTrashed=0;
    public int EventsTrashed=0;
    
    public boolean UseRaw=false;    
    public String CreatorMachine;
    public String DateCreated;
    public String DateExport;
    public String VersionS;
    public float VersionF;
    public char DataType;

    public boolean loaded=false;
    boolean DEBUG = false;
    
    
    public void resetClusterNumbers(){
   ClusterNumbers=new char[NumEvents];   
      
    }
    
    public void retrash(){
     for(int i=0;i<ClustersLeft.length;i++)
       if(!ClustersLeft[i]) {
         TrashCluster(i,false);
         System.out.println("Trash "+i+" is true");
       } else
         System.out.println("Trash "+i+" is false");
       
    }

    public void setClustersLeft(boolean[] cl){
   System.out.println("Reloading clusters left and trashed");
       ClustersLeft=cl;   
   System.out.println("Retrashing the clusters");
       
     retrash();
    }
    
    public int getNClusters(){
     return(NumClusters);   
    }
   
    public void setNumClusters(int n){
      NumClusters=n;
       DisplayClusters = new boolean[n];
     ClustersLeft = new boolean[n];
     if(n>0)
       for(int i=0; i<n;i++){
         DisplayClusters[i]=true;
         ClustersLeft[i]=true;
         
       }
   }   
 
   
     public boolean[] getTrash(){
    boolean[] tr = new boolean[ClustersLeft.length];
    for(int i=0;i<tr.length;i++)
      if(ClustersLeft[i])tr[i]=false;else tr[i]=true;
    return(tr);
  }   
      
    public Stats(int Channels, int num){
  DataArray = new double[Channels][num];
  Trash = new boolean[num];
  for(int i=0;i<num;i++) Trash[i]=false;
  NumChannels = Channels;
  NumEvents = num;
  NumDataPoints = Channels*num;
  min = new double[NumChannels];
  max = new double[NumChannels];
  logScale = new boolean[NumChannels];
  decades= new float[NumChannels];
  offsets= new float[NumChannels];
  EventsNotTrashed=num;
  EventsTrashed=0;
  loaded=false;
    }   
    
    public int SetSubSet(int require){
  int Counter=0;
  int UpToNum=0;
  for(int j=0;j<NumEvents&&Counter<require;j++){
    if(!Trash[j])Counter++;
    UpToNum++;
  }
  SubSet=UpToNum;
  return Counter;
    }

    public void setTrash(){
     // boolean tr[] = new boolean[DataSet.ClusterNumbers.length];
      int counter=0;
      for(int i=0;i<ClusterNumbers.length;i++){
        if(ClusterNumbers[i]==0||ClusterNumbers[i]==-1) {
          Trash[i]=true;
          counter++;
        }else{
        Trash[i]=false;
      }
      }
  } 
    
    public int TheSubSet(){
  return SubSet;
    }
    
    public boolean UnTrashAll(boolean ask){

  int value=0;
  if(ask){
      Object options[] = { "Yes", "No" };
      
      value = JOptionPane.showOptionDialog(null, "This will untrash all events. Are you sure?", "Untrash events", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
  }
  if(value==0){

      for(int j=0;j<NumEvents;j++) Trash[j]=false; 
      Object[] options2 = {"OK"};
      EventsNotTrashed=NumEvents;
      EventsTrashed=0;
      if(ask)JOptionPane.showOptionDialog(null, "Untrashed all events", "Untrash events", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options2, options2[0]);    
      return true;
  } else return false;    
    }

    public void MinAndMax(boolean atZero){
  
  for(int c=0;c<NumChannels;c++){
      if(atZero==true) min[c]=0; else min[c]=DataArray[c][0];
      max[c]=DataArray[c][0];
       for(int i=0;i<NumEvents;i++) 
     if(Trash[i]==false){
         if(min[c]>DataArray[c][i]) min[c]=DataArray[c][i];
         if(max[c]<DataArray[c][i]) max[c]=DataArray[c][i];
     }
     if(min[c]==max[c])max[c]=(min[c]+1);
      } 
    } 
    
    public void CompOverRating(){
      MinAndMax(false);
      for(int c=0;c<NumChannels;c++){
        if(max[c]>ranges[c]&&(max[c]-min[c])-1<ranges[c]){
         for(int i=0;i<NumEvents;i++)
          DataArray[c][i]=(DataArray[c][i]-min[c]);
          if(DEBUG) System.out.println("Channel "+c+" was compensated for overrating");
        }
      
      }
      
    }
    public void NegativesToZero(){
  int counter=0;
      for(int c=0;c<NumChannels;c++){
       
    for(int i=0;i<NumEvents;i++)
    if(DataArray[c][i]<0) {
      DataArray[c][i]=0;
      counter++;
    }  
      }
  if(DEBUG) System.out.println(counter+" values were set to zero");      
    }
    
    public void TrashCluster(boolean[] tr){
   for(int i=0;i<tr.length;i++)
     if(tr[i])TrashCluster(i,false);   
    }
    
    public void TrashCluster(int C,boolean ask){
  
    Object options[] = { "Yes", "No" };
    Object options2[] = { "OK" };
    int value=0;
    
    if(ask) value = JOptionPane.showOptionDialog(null, "This will trash all events in selected clusters. Are you sure?", "Trash events", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    
    if(value==0){
        int CountTrash=0;           
        if(ClustersLeft[C])
        for(int j=0;j<NumEvents;j++){
        if(ClusterNumbers[j]==C+1) {
            Trash[j]=true; 
            CountTrash++;
        }
        }
      ClustersLeft[C]=false;
      EventsNotTrashed=EventsNotTrashed-CountTrash;
      EventsTrashed=EventsTrashed+CountTrash;
    }  
    }

    
    
}
