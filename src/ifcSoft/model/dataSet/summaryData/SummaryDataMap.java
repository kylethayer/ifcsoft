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
package ifcSoft.model.dataSet.summaryData;

import ifcSoft.model.dataSet.DataSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author kthayer
 */
public class SummaryDataMap {
  //make a map of maps (allow access as a 2d map)
      // request is on [dataset+rawset] and cluster

  //how do I deal with dataset and raw dataset?
    //could I ever have a data set be a raw set and also be asked for on it's own?
    //maybe I only ever ask for the raw data sets. I'll assume that for now

  //returns null if invalid combo
  HashMap<DataSet, HashMap<String,SummaryDataPoint> > map;
      


  public SummaryDataMap(){
    map = new HashMap<DataSet, HashMap<String,SummaryDataPoint> >();
  }

  public SummaryDataPoint add(SummaryDataPoint sdp, DataSet rawdataset, String cluster){
    HashMap<String,SummaryDataPoint> map2 = map.get(rawdataset);
    if(map2 == null){
      map2 = new HashMap<String,SummaryDataPoint>();
      map.put(rawdataset, map2);
    }

    return map2.put(cluster, sdp);
  }
  

  public SummaryDataPoint get(DataSet rawdataset, String cluster){
    HashMap<String,SummaryDataPoint> map2 = map.get(rawdataset);
    if(map2 != null){
      return map2.get(cluster);
    }
    return null;
  }

  public LinkedList<String> getAllClusterNames(){
    LinkedList<String> allNames = new LinkedList<String>();


    Set<Entry<DataSet, HashMap<String,SummaryDataPoint>>> datasetList = map.entrySet();

    Iterator<Entry<DataSet, HashMap<String,SummaryDataPoint>>> mapiter = datasetList.iterator();

    while(mapiter.hasNext()){

      Set<Entry<String,SummaryDataPoint>> clusterList = mapiter.next().getValue().entrySet();

      Iterator<Entry<String,SummaryDataPoint>> clusteriter = clusterList.iterator();
      while(clusteriter.hasNext()){
        String clusterName = clusteriter.next().getKey();

        if(!allNames.contains(clusterName)){
          allNames.add(clusterName);
        }
      }
    }

    return allNames;
  }


  public LinkedList<DataSet> getAllDataSets(){
    LinkedList<DataSet> alldatasets = new LinkedList<DataSet>();

    Set<Entry<DataSet, HashMap<String,SummaryDataPoint>>> datasetList = map.entrySet();

    Iterator<Entry<DataSet, HashMap<String,SummaryDataPoint>>> mapiter = datasetList.iterator();

    while(mapiter.hasNext()){
      alldatasets.add(mapiter.next().getKey());
    }

    return alldatasets;
  }



}
