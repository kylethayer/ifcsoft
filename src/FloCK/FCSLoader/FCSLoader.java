/**
 *  Copyright (C) 2011  Tjibbe Donker
 *
 *  This file originally came from the FloCK program
 *  (http://theory.bio.uu.nl/tjibbe/flock/) and is now
 *  part of the IFCSoft project (http://ifcsoft.com)
 *
 *  This program is free software: you can redistribute it and/or modify
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

//import FloCK.FCSLoader.stats.*;
//import fcs.matrix.*;
import java.io.FileReader; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.FileInputStream ;
import java.io.File ;
import java.io.InputStream ;
import java.io.DataInputStream ;
import javax.swing.*;
import java.awt.event.*;
//import components.*;

import Jama.Matrix;
import java.io.BufferedInputStream;



public class FCSLoader {
	int progress = 0; //Added by Kyle Thayer for a reporting progress

    boolean BigDEBUG = false;
    boolean memDEBUG = false;
    boolean DEBUG = false;
    public File FCSFile;
    int length=2;
    int StartText=0;
    int StopText=0;
    int StartData=0;
    int StopData=0;
    int NumTextElements=1;
    int NumBytes=1;
    int ByteOrd[];
    char DataType='*';
    String CreatorMachine;
    String DateCreated;
    String DateExport;
    String VersionS;
    float VersionF;
    String ChannelNames[];
    boolean LogNorm[];

    boolean NeedCompensation = false;
    Matrix CompensationMatrix;
	Matrix invCompensationMatrix;
    int NumMChan;
    int CompensationChannels[];
    int NormScale = 1024;
    static JFrame frame;
    
    volatile boolean busy;
  
    public FCSLoader(){
	length=2;
	StartText=0;
	StopText=0;
	StartData=0;
	StopData=0;
	NumTextElements=1;

	NumBytes=1;
	ByteOrd = new int[4];
    }
       
    public void resetDataSet(){
		length=2;
		StartText=0;
		StopText=0;
		StartData=0;
		StopData=0;
		NumTextElements=1;
		NumBytes=1;
		ByteOrd = new int[4];
    }


public float arr2float (byte[] arr, int start) { 

//http://www.captain.at/howto-java-convert-binary-data.php
	int i = 0; 
	int len = 4; 
	byte[] tmp = new byte[len]; 
	
	for (i = start; i < (start + len); i++) 
		tmp[ByteOrd[i-start]] = arr[i]; 
 

	int accum = 0; 
	i = 0; 
	for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) { 
		accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy; i++; 
	} 
	return Float.intBitsToFloat(accum); 
} 

public int arr2int (byte[] arr, int len) {
    //  int len=2;
    int myMin=4;
    int myMax=0;
    int myInt=0;

    byte[] tmp = new byte[len];

    for(int i=0;i<len;i++){
	if(myMin>ByteOrd[i]) myMin=ByteOrd[i];
	if(myMax<ByteOrd[i]) myMax=ByteOrd[i];
    }
    if(myMax-myMin==len-1) {
	for(int i=0;i<len;i++)
	    myInt+=((arr[ByteOrd[i]-myMin]&0xff)<<(8*i));
    } else myInt=-1;
   
    return myInt;
}

class CompareScreen extends JPanel implements ActionListener{
	JButton okButton;
	
	public CompareScreen(){
		okButton = new JButton("OK");
		add(okButton);
		okButton.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {

	   
	    
	    if(e.getSource()==okButton){
		    synchronized(this){
		    notifyAll();
		    }
	    }
	 }
}


     void CreateCompareScreen() {
        //Create and set up the window.
        JFrame NumClusInf = new JFrame("Cluster Number Information");
        NumClusInf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        CompareScreen ComparePane = new CompareScreen();
        ComparePane.setOpaque(true); //content panes must be opaque
        NumClusInf.setContentPane(ComparePane);

        //Display the window.
        NumClusInf.pack();
        NumClusInf.setVisible(true);
    }

	public Stats ReadFCS(String Filename)throws IOException{
		return ReadFCS(Filename,true);
	}
	
	// Convert the string containing the spillover matrix to a real matrix
	public void LoadSpillmatrix(String loadedString){
		
		int CurIndex=0;

		int TransToMatrix[] = new int[ChannelNames.length];
		for(int j=0;j<ChannelNames.length;j++) TransToMatrix[j]=-1;
		int NewIndex=loadedString.indexOf(",",CurIndex);
		String str = loadedString.substring(CurIndex,NewIndex);
		NumMChan=(int)Integer.valueOf(str.trim()).intValue() ;
		double vals[][] = new double[NumMChan][NumMChan];
		if(DEBUG) System.out.println(loadedString);
		if(DEBUG) System.out.println("Number of channels in matrix: "+NumMChan);
		for(int i=0;i<NumMChan;i++){	
			CurIndex=NewIndex;
			NewIndex=loadedString.indexOf(",",CurIndex+1);
			str = loadedString.substring(CurIndex+1,NewIndex).trim();
			int NameLink=-1;			
			for(int j=0;j<ChannelNames.length;j++){
				if(ChannelNames[j].indexOf(str,0)!=-1)NameLink=j;
			}
			TransToMatrix[NameLink]=i;
			if(DEBUG) System.out.println(str+" at position "+NameLink);	
		}
		for(int i=0;i<NumMChan;i++){
		for(int j=0;j<NumMChan;j++){
			CurIndex=NewIndex;
			NewIndex=loadedString.indexOf(",",CurIndex+1);
			if(NewIndex<0) NewIndex=loadedString.length();
			if(DEBUG) System.out.print(CurIndex+" to "+NewIndex);

			str = loadedString.substring(CurIndex+1,NewIndex).trim();
			
			vals[j][i]=Double.valueOf(str.trim()).doubleValue();
			//if(i!=j) vals[i][j]=-vals[i][j];
			if(DEBUG) System.out.println(" gives "+vals[i][j]);
		}}
		CompensationChannels=TransToMatrix;
		CompensationMatrix=new Matrix(vals);

		//System.out.println(CompensationMatrix);
		if(DEBUG){
			System.out.println("..");
			System.out.println(" TODO: Make it dump matrix");
			//CompensationMatrix.print();
			System.out.println("..");
		}
	    invCompensationMatrix= CompensationMatrix.inverse();
		
		if(DEBUG){
			System.out.println(" TODO: Make it dump matrix");
			System.out.println("..");
		}
		if(DEBUG) for(int j=0;j<ChannelNames.length;j++)System.out.println(j+": "+TransToMatrix[j]);		
	}

	//////////////////////////////////////////////////////////////////////////////////
	//																				//
	// 			This is the main FCS file reader:									//
	//																				//
	//////////////////////////////////////////////////////////////////////////////////

	 public Stats ReadFCS(String Filename, boolean renorm) throws IOException { 
		FileReader inputStream = null; 
		FileWriter outputStream = null; 
		FCSFile = new File(Filename);
		int baseLines[];
		boolean mBool=false;
		int Events,MyChannels;
		Stats DA=new Stats(0,0);	;	
		resetDataSet();
				    
		System.gc();	
		Events=0;
		try { 
			inputStream = new FileReader(Filename); 
			File file = new File(Filename); 
			InputStream is = new BufferedInputStream(new FileInputStream(file)); //Added buffering to make it load files much, much faster
			DataInputStream dis = new DataInputStream( is ); 


			float c =0;
			char sep;
			String str;	

			//Read the "FCS"
			char incomingData[] = new char[58];
			inputStream.read(incomingData);	
		
			str = new String(incomingData,0,3);
			str.toUpperCase();
			if(str.indexOf("FCS")==-1){
			    if(DEBUG){
				System.out.println("!--Warning--!");
				System.out.println("First check failed, Probably not an FCS file");
				System.out.println("Fatal error, exiting");
				System.out.println("!-----------!");
			    }
			    mBool=false;
			} else {
			str = new String(incomingData,3,3);
			if(DEBUG) System.out.println(str);
			c=(float)Float.valueOf(str).floatValue();
			VersionF=c;
			VersionS=str;
			if(c!=2.0)
			    if(DEBUG){
				System.out.println("!--Warning--!");
				System.out.println("FCS version"+(int)c+"not supported");
				System.out.println("This might cause problems");
				System.out.println("!-----------!");
				}
			
			//Read Start of text

			str = new String(incomingData,10,8);
			StartText=(int)Integer.valueOf(str.trim()).intValue();
			if(DEBUG) System.out.println(StartText);

			//Read End of text

			str = new String(incomingData,18,8);
			StopText=(int)Integer.valueOf(str.trim()).intValue() ;
			if(DEBUG) System.out.println(StopText);

			str = new String(incomingData,26,8);
			StartData=(int)Integer.valueOf(str.trim()).intValue() ;
			if(DEBUG) System.out.println(StartData);

			//Read End of text

			str = new String(incomingData,34,8);
			StopData=(int)Integer.valueOf(str.trim()).intValue() ;
			if(DEBUG) System.out.println(StopData);


			/////////////////////////////////////////////////			
			// Start reading the text part of the FCS file //
			/////////////////////////////////////////////////
			
			//Read text from file
			char TextPart[] = new char[(StopText-StartText)];
			inputStream.skip(StartText-58);
			inputStream.read(TextPart);

			//Put it into String *Is this needed?*		
			int l=TextPart.length;
			str = new String(TextPart,0,l);
			if(DEBUG) System.out.println(l);
			
			//Determine the separator and count the number of records
			sep=TextPart[0];
			if(DEBUG) System.out.println("Separator was defined as: "+sep);
			int NumBreak=0;
			for(int i=0; i<l; i++){
				if(TextPart[i]==sep) NumBreak++;
			}	
			NumTextElements = (int)(NumBreak/2);
			if(DEBUG) System.out.println(NumTextElements);
			
			//Translating text part to array
			String TextArray[][] = new String[NumTextElements][2];
			int CurIndex = 0;
			int NewIndex;
			int SecondIndex;
		
			for(int i=0;i<NumTextElements;i++){
				NewIndex=str.indexOf(sep,CurIndex+1);
				SecondIndex=str.indexOf(sep,NewIndex+1);
				TextArray[i][0]=str.substring(CurIndex+1,NewIndex);
				
				if(SecondIndex!=-1){	
					TextArray[i][1]=str.substring(NewIndex+1,SecondIndex);
				} else {
					TextArray[i][1]=str.substring(NewIndex+1);
				}
				CurIndex=SecondIndex;
			}

			//Reading the needed variables from array
			//First the variables independent of number of channels
			 
			for(int b=0;b<4;b++)
			    ByteOrd[b]=b;
			MyChannels=-1;
			DataType='I';
			String TMPstr;
			for(int i=0;i<NumTextElements;i++){
			    if(StartData==0){
				if(TextArray[i][0].indexOf("BEGINDATA")!=-1){StartData=(int)Integer.valueOf(TextArray[i][1].trim()).intValue();}
				if(TextArray[i][0].indexOf("ENDDATA")!=-1){StopData=(int)Integer.valueOf(TextArray[i][1].trim()).intValue();}
			    }
			    if(TextArray[i][0].indexOf("$PAR")==0){ // ==0 was !=-1
					MyChannels=(int)Integer.valueOf(TextArray[i][1].trim()).intValue();
					if(DEBUG) System.out.println("Number of channels found: "+MyChannels);
			    }
			    if(TextArray[i][0].indexOf("DATATYPE")!=-1){DataType=TextArray[i][1].charAt(0);}
			    if((TextArray[i][0].indexOf("P1B")!=-1)&&(TextArray[i][0].indexOf("P1BS")==-1)){
					NumBytes=(int)((int)Integer.valueOf(TextArray[i][1].trim()).intValue()/8);
				}
			    if(TextArray[i][0].indexOf("CREATOR")!=-1) CreatorMachine=TextArray[i][1];
			    if(TextArray[i][0].indexOf("$DATE")!=-1) DateCreated=TextArray[i][1];
			    if(TextArray[i][0].indexOf("EXPORT TIME")!=-1) DateExport=TextArray[i][1];
			    if(TextArray[i][0].indexOf("TOT")!=-1){Events=(int)Integer.valueOf(TextArray[i][1].trim()).intValue();}
			    if(TextArray[i][0].indexOf("APPLY COMPENSATION")!=-1&&TextArray[i][1].toUpperCase().indexOf("TRUE")!=-1)	
				{NeedCompensation=true;}
			    if(TextArray[i][0].indexOf("BYTEORD")!=-1){
				    if(TextArray[i][1].indexOf(",")!=-1){
						for(int b=0;b<4;b++)
						    ByteOrd[b]=(int)Integer.valueOf(TextArray[i][1].substring((b*2),(b*2)+1)).intValue()-1;
					} else {
					    for(int b=0;b<4;b++)
						    ByteOrd[b]=b;
				    }
					if(DEBUG) System.out.println("ByteOrd = "+ByteOrd[0]+ByteOrd[1]+ByteOrd[2]+ByteOrd[3]);
				}
			}

			//If the number of channels (given by $PAR) is not found, use this 
			//emergency procedure to detect the number of channels.
			if(MyChannels==-1){
			    MyChannels=1;
			    boolean foundC=true;
			    while(foundC==true){
				TMPstr = "$P"+Integer.toString(MyChannels)+"N";
				foundC=false;
				for(int i=0;i<NumTextElements;i++){
				    if(TextArray[i][0].indexOf(TMPstr)!=-1){
					foundC=true;
					MyChannels++;
				    }
				}  
			    }
			    MyChannels--;
			}

	
		 
		//Try to free some memory because after a short while we're going to load the data
		//This Needs some tweaking
 	
		Object[] options = { "Yes", "No" };
			System.gc();
			Runtime rt = Runtime.getRuntime();
			
			long avMem=(int)((rt.maxMemory()-rt.totalMemory())*0.75);
			long needMem=(Events*(1+(MyChannels*6)+(27*4)));//+(MyChannels*(63+(25*20)))+1024;  //63+(25*20)
			if(memDEBUG){
			System.out.println("----------------Before Loading--------------------------");
			System.out.println("Total memory allocated to VM: " + (int)(rt.totalMemory()/1024));
			System.out.println("Max memory allocated to VM: " + (int)(rt.maxMemory()/1024));
			System.out.println("Memory currently available: " + (int)(rt.freeMemory()/1024));
			System.out.println("Memory currently available: " + (int)(avMem/1024));
			
			System.out.println("Collecting garbage");
			System.gc();
			System.out.println("Memory currently available: " + (int)(rt.freeMemory()/1024));
			System.out.println("Estimated memory needed: "+(int)(needMem/1024));
			System.out.println("--------------------------------------------------------");
			} else {System.gc();}
		
			needMem=0; //temporary solution
			if(avMem<needMem)
			    {
				int myNumEvents=(int)((avMem-1024-(MyChannels*(63+(20*25))))/(1+(MyChannels*6)+(27*4)));
	
				JOptionPane.showMessageDialog(null, "File too large.\n Memory available: "+
						Long.toString(avMem)+
						" KB, Needed: "+
						Long.toString(needMem)+
						" KB\nSetting events to "+
					Integer.toString(myNumEvents), 
					"Memory Test", 
					JOptionPane.WARNING_MESSAGE 
					);
				Events=myNumEvents;
			    } 

			 //   Events = 300;//TEMPORARY
			 int skippoints=0;
			 int zeropoints=0;
			 //int NewCN=MyChannels;

			 /////////////////Loading channel dependent variables///////////////
			 // This next bit loads the:
			 // - Names
			 // - Scales
			 // - decades
			 // - offsets
			 // - lognorms
			 // - ranges
			 ///////////////////////////////////////////////////////////////////
			 System.out.println("Using "+Events+" events and "+MyChannels+" channels");
			DA = new Stats(MyChannels,Events);		
			LogNorm = new boolean[MyChannels];
			DA.logScale = new boolean[MyChannels];
			DA.decades = new float[MyChannels];
			DA.offsets = new float[MyChannels];
			
			for(int j=1;j<MyChannels+1;j++) LogNorm[j-1]=false;
			baseLines = new int[MyChannels];
			
			String tChannelNames[] = new String[MyChannels];
			boolean tLogNorm[] = new boolean[MyChannels];
			boolean tlogScale[] = new boolean[MyChannels];
			float tdecades[] = new float[MyChannels];
			float toffsets[] = new float[MyChannels]; 
			int tranges[] = new int[MyChannels];
			int tbaseLines[] = new int[MyChannels];
			
			for(int j=1;j<MyChannels+1;j++) tLogNorm[j-1]=false;
				
			for(int i=0;i<NumTextElements;i++){

			    if(TextArray[i][0].indexOf("$P")!=-1){
				for(int j=1;j<MyChannels+1;j++) {
				    TMPstr = "$P"+Integer.toString(j)+"N";
				    if(TextArray[i][0].indexOf(TMPstr)!=-1){
					tChannelNames[j-1]=TextArray[i][1];
				    }
				    TMPstr = "$P"+Integer.toString(j)+"E";
				    if(TextArray[i][0].indexOf(TMPstr)!=-1){
					
					if(TextArray[i][1].indexOf(",")!=-1){
					    tdecades[j-1]=(float)Float.valueOf(TextArray[i][1].substring(0,TextArray[i][1].indexOf(","))).floatValue();
					    toffsets[j-1]=(float)Float.valueOf(TextArray[i][1].substring(TextArray[i][1].indexOf(",")+1)).floatValue();
					    if(tdecades[j-1]>0) tlogScale[j-1]=true; else tlogScale[j-1]=false;
					} else {
					    if(DEBUG) System.out.println("No seperation between decades and offset found");
					}
					if(DEBUG) System.out.println("$P"+j+"E found: "+tdecades[j-1]+toffsets[j-1]);   
				    }
				    TMPstr = "$P"+Integer.toString(j)+"R";
				    if(TextArray[i][0].indexOf(TMPstr)!=-1){
					tranges[j-1]=(int)Integer.valueOf(TextArray[i][1]).intValue();
				    }
				}
			    }

			    if(TextArray[i][0].indexOf("P")!=-1){
				for(int j=1;j<MyChannels+1;j++) {
				    TMPstr = "P"+Integer.toString(j)+"DISPLAY";
				    if(TextArray[i][0].indexOf(TMPstr)!=-1){
					if(TextArray[i][1].indexOf("LOG")!=-1){
					    if(DEBUG) System.out.println("Log transform");
					    tLogNorm[j-1]=true;
					    tlogScale[j-1]=true;
					}
				    }
				    TMPstr = "P"+Integer.toString(j)+"BS";
				    if(TextArray[i][0].indexOf(TMPstr)!=-1)
					tbaseLines[j-1]=(int)Integer.valueOf(TextArray[i][1]).intValue();
				}
			    }
			}
			for(int i=0;i<NumTextElements;i++){
				if(TextArray[i][0].indexOf("$P")!=-1){
					for(int j=1;j<MyChannels+1;j++) {
						TMPstr = "$P"+Integer.toString(j)+"S";
						if(TextArray[i][0].indexOf(TMPstr)!=-1){
							tChannelNames[j-1]=tChannelNames[j-1]+" ( "+TextArray[i][1]+" )";
						}
					}
				}
			}
			for(int j=0;j<MyChannels;j++) 
			    if(tLogNorm[j]&&tdecades[j]==0){
				tdecades[j]=(float)(Math.log(tranges[j])/Math.log(10));
				if(DEBUG) System.out.println("Channel: "+j+", decades: "+tdecades[j]);
			    }

			// If the number of channels are equal between datasets, the order is assumed to be the same
			// If not, then use the matcher to order the channels to match the previous dataset. 
			    			
			int[] ChannelOrder;
			ChannelOrder = new int[MyChannels];
			System.out.println("ChannelOrderLength = "+MyChannels);
			for(int i=0;i<MyChannels;i++)
					 ChannelOrder[i]=i; 
			 ChannelOrder = new int[MyChannels];
			 for(int i=0;i<MyChannels;i++)
				 ChannelOrder[i]=i;
				 
				 
			LogNorm = tLogNorm;
			DA.logScale = tlogScale;
			DA.decades = tdecades;
			DA.offsets = toffsets;
			DA.ranges = tranges;
			baseLines = tbaseLines;
			ChannelNames = tChannelNames;
			 //}
			//Now that the names are known, load the Spillover Matrix
			for(int i=0;i<NumTextElements;i++){
				if(TextArray[i][0].indexOf("SPILL")!=-1){LoadSpillmatrix(TextArray[i][1]);}
			}

			if(BigDEBUG) 
			for(int i=0;i<TextArray.length;i++){
				System.out.println(TextArray[i][0]+": "+TextArray[i][1]);
			}
			if(DEBUG) {
				for(int i=0;i<ChannelOrder.length;i++) System.out.println("nr "+i+" is "+ChannelOrder[i]);
				System.out.println("DataType is "+DataType);
				System.out.println("Data Part from "+StartData+" to "+StopData+" (Total "+DA.NumDataPoints+" datapoints in "+DA.NumEvents+" events and "+DA.NumChannels+" channels)");
			}  
			// Start loading the data  
			    is.skip(StartData);
			    float f=0;
			    int myI=0;
			    byte[] ch = new byte[NumBytes];
			    int CountE=0;
			    int CountT=0;
			    for(int j=0;j<DA.NumEvents;j++){
					progress++; //Added by Kyle Thayer for a reporting progress
					CountE++;
					for(int i=0;i<MyChannels;i++){
						DA.DataArray[i][j]=0;
					}
					for(int i=0;i<MyChannels;i++){
						is.read(ch);

						if(ChannelOrder[i]<MyChannels&&ChannelOrder[i]>-1){

						if(DataType=='F') myI=(int)arr2float(ch, 0);
						if(DataType=='I') myI=arr2int(ch,NumBytes);
						DA.DataArray[ChannelOrder[i]][j]=myI;
						}
						CountT++;
					}

					mBool=true;
			    }
			    
			    if(DEBUG) System.out.println("Data loaded. Events: "+CountE+", Total: "+CountT);
			    if(memDEBUG){
				    rt = Runtime.getRuntime();
				    System.out.println("----------------After Loading----------------------------");
				    System.out.println("Total memory allocated to VM: " + (int)(rt.totalMemory()/(1024)));
				    System.out.println("Max memory allocated to VM: " + (int)(rt.maxMemory()/(1024)));
				    System.out.println("Memory currently available: " + (int)(rt.freeMemory()/(1024)));
				    System.out.println("Collecting garbage");
				    System.gc();
				    System.out.println("Memory currently available: " + (int)(rt.freeMemory()/(1024)));
				    System.out.println("Estimated memory needed: "+(int)(needMem/1024));
				    System.out.println("--------------------------------------------------------");
			    }
			    DA.MinAndMax(false);
			    if(DEBUG)
				for(int i=0;i<MyChannels;i++){
					System.out.println("Channel "+i+": "+DA.min[i]+" to "+DA.max[i]);
				}

				DA.ChannelNames=ChannelNames;
			    DA.CreatorMachine=CreatorMachine;
     			DA.DateCreated=DateCreated;
     			DA.DateExport=DateExport;
     			DA.VersionS=VersionS;
          		DA.VersionF=VersionF;	
          		DA.DataType=DataType;	    
			    
			    DA.CompOverRating();
			    DA.NegativesToZero();
			    DA.MinAndMax(false);
			    DA.loaded=true;
			    DA.ClusterNumbers=new char[DA.NumEvents];
			}
		} finally { 
		    if (inputStream != null) { 
			inputStream.close(); 
		    } 
		    if (outputStream != null) { 
			outputStream.close(); 
		    } 
	}
		if(!mBool) DA = new Stats(0,0);		
			
		return DA;
	} 



	public int getProgress(){ //Added by Kyle Thayer for a reporting progress
		return progress;
	}

}
