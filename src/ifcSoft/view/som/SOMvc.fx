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

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.geometry.VPos;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;

import javafx.scene.control.*;
import javafx.scene.layout.Container;
import javafx.scene.layout.*;

import ifcSoft.view.som.SOMInternDialogs;
import ifcSoft.view.som.SOMcluster;
import ifcSoft.view.som.SOMmaps;

import java.awt.image.BufferedImage;


import javafx.ext.swing.*; //this is needed to convert buffered images into the JavaFx Image format
import ifcSoft.MainApp;
import java.lang.Math;
import javafx.scene.Group;

/**
* This is the central view component for SOMs.
* @author Kyle Thayer <kthayer@emory.edu>
*/

public class SOMvc extends SOMvcI  {


	package var app:MainApp;
	package var mediator:SOMMediator;

	package var SOMdlgs:SOMInternDialogs;
	package var SOMclstr:SOMcluster;
	package var SOMmps:SOMmaps;
	
	var SOMContent:VBox = null;
	var SOMTilesGroup: Container = Container{
		height: bind app.contentHeight - 60
		width: bind app.contentWidth - 70

	};

	var setName:String = "";

	var isTabOpen:Boolean = true;
	var lastTotalDenseProgress:Integer = -1;
	var lastTotalDenseDisplayed:Integer = -1;

	var denseProgress:Float = 0;

	
	/**
	* Initialize the pieces of the view component, including links to the MainApp and the SOMMediator.
	* This must be run before the SOMvc is used.
	*
	* @param ap - the MainApp object for the program
	* @param med - the SOMMediator for this view component
	*/
	public function init(ap: MainApp, med:SOMMediator):Void{
		app = ap;
		mediator = med;
		SOMdlgs = SOMInternDialogs{app:app	mediator:mediator};
		SOMclstr = SOMcluster{app:app mediator:mediator somvc:this};
		SOMmps = SOMmaps{app:app mediator:mediator SOMclstr:SOMclstr};		
	}


	public function saveSOM(selectedFile:String):Void{
		mediator.saveSOM(selectedFile);
	}

	public function loadSOM(selectedFile:String):Void{
		mediator.loadSOM(selectedFile);
	}

	/**
	 * Informs the SOMvc the the SOM has started calculating (so it can put up the progress bar).
	 */
	override public function SOMstarted(): Void{
		SOMdlgs.SOMstarted(); 
	}
	

	/**
	 * Sets the progress of the calculating SOM.
	 * @param p - progress % (0 - 1 if computing, 100 when done)
	 */
	override public function setProgress(p: Integer): Void{
		SOMdlgs.progress = p;
		SOMdlgs.checkprogress();
	}
	
	/**
	* Initialize the SOM content box and set it as the main content box.
	*/
	function initSOMContent():Void{
		SOMContent = VBox{
			width: bind app.contentWidth
			height: bind app.contentHeight
			nodeVPos: VPos.TOP
			spacing: 0
			content: [
				HBox{ //"Set" and "Time" bar
					height: 30
					width: bind app.contentWidth
					spacing: 0
					content:[
						Rectangle{
						fill: Color.BLACK
						height:30
						width: 10
						},
						Text{
							fill: Color.WHITE
							font: Font {name: "Arial" size: 14}
							content: bind setName
							x:10
						}
					]
				},
				HBox{ // side spacing and main tiles
					spacing: 0
					layoutY:0
					width: bind app.contentWidth
					height: bind app.contentHeight - 60
					nodeVPos: VPos.TOP
					vpos: VPos.TOP
					content:[
						VBox{width: 30
							content:Rectangle{
								fill: Color.BLACK
								width:30
								height:10
							}
						},
						SOMTilesGroup,
						VBox{width: 40
							content:Rectangle{
								fill: Color.BLACK
								width:40
								height:10
							}
						}
					
					]
				},
				HBox{ //bottom menu tab spacer
					height: 30
					width: bind app.contentWidth
					content:[
						infoText,
						Group{content:[pauseButton,unPauseButton]},
						Rectangle{
							fill: Color.BLACK
							height:30
							width: 10
							}
						]
				}
			]
			
		
		}
		app.setMainContent(SOMContent);
	}


	/*********************************************/
	/*Pause / Continue Button for finding densities */
	
	var enablePause:Boolean = true;
	var checkingDense:Boolean = false;
	
	var pauseButton:Button = Button{
		text: "Pause"
		visible: bind enablePause and checkingDense
		onMouseClicked: pauseButtonClick
	};

	var unPauseButton:Button = Button{
		text: "Continue"
		visible: bind not enablePause and checkingDense
		onMouseClicked: unPauseButtonClick
	};
	
	function pauseButtonClick(e:MouseEvent):Void{
		mediator.pauseJobs(); 
		enablePause = false;
	};

	function unPauseButtonClick(e:MouseEvent):Void{
		mediator.unPauseJobs();
		enablePause = true;
	};
	
	public function cancelJobs():Void{
		mediator.cancel();
	}
	
	var clusterSize = 0;
	var TotalSize = 0;
	var infoText:Text = Text{
		fill: Color.WHITE
		font: Font {name: "Arial" size: 14}
		content: ""
		x:10
	};

	/**
	 * Tells the SOMvc to draw the SOM maps in the current tab.
	 */
	override public function dispSOM():Void{
		isTabOpen = true;
		if(SOMContent == null){
			initSOMContent();
		}
		setName = mediator.getSetName();

		SOMTilesGroup.content = SOMmps.makeSOMTilesGroup();

		//start the loop of checking the density progress
	   	checkDenseProgress();

		app.clearDialog();
		app.unblockContent();
		
	}


	/**
	 * Do anything needed before swapping out the tab.
	 */
	override public function swapOut():Void{
		isTabOpen = false;
		SOMContent = null;
	}

	
	var denseProgTimeline:Timeline;

	/**
	* Wait for half a second, then see if there has been any progress on the density maps.
	*/
	function checkDenseProgress():Void{
		denseProgTimeline = Timeline{
			repeatCount: 1
			keyFrames: KeyFrame{
					time: 500ms //2 times per second
					action: updateDensityProgress
				}
		};
		denseProgTimeline.play();
	}

	/**
	* If progress has been made, update them. If it is not finished, call checkDenseProgress().
	*/
	function updateDensityProgress():Void{
		var placed:Integer = mediator.getDenstiyMapPlaced() + mediator.getOtherDataSetsPlaced(); 
		var dataLength:Integer = mediator.getDataLength() + mediator.getOtherDataSetLength();

		var avgPlacedError = mediator.getAvfPlacedError(); 

		
		if(placed > lastTotalDenseProgress){
			var newPercent:Float;
			if(dataLength == placed){
				newPercent = 100;
			}else{
				newPercent = (placed as Float) / dataLength;
			}
			//display progress stuff
			//set percent
			mediator.setDenseProgress(newPercent);
			denseProgress = newPercent;
			lastTotalDenseProgress = placed;
		}

		if(placed > lastTotalDenseDisplayed){
			//update display
			if(isTabOpen){
				SOMmps.updateDenseMaps();
				lastTotalDenseDisplayed = placed;
			}
		}

		if(denseProgress != 100){
			checkDenseProgress();
			infoText.content = "Calculating membership: { Math.round(denseProgress*100)}% Data pt Error: {avgPlacedError}";
			checkingDense = true;
		}else{
			checkingDense = false;
			if(SOMmps.clusterImg!= null){
				//need to recalculate size
				infoText.content = "{clusterSize} of {TotalSize} chosen";
			}else{
				infoText.content = "Done. Data pt Error: {avgPlacedError}";
			}
		}
	}

	/**
	 * Make sure the SOMvc is checking for changes in density maps (used when a new density map is added).
	 */
	override public function continueUpdatingDensities():Void{
		if(checkingDense == false){
			checkingDense = true;
			checkDenseProgress();
		}

	}

	/**
	 * Opens the cluster options dialog box.
	 */
	override public function clustOpt():Void{
		SOMclstr.clustOpt();
	}
	
	/**
	 * When a cluster is selected, an overlay image is drawn to show what was selected,
	 * this sets that image. If image is null, it means nothing is selected.
	 * @param img - the overlay image
	 * @param size - the total number of points selected
	 * @param total - the total number of points displayed
	 */
	override public function setClusterImg(img: BufferedImage, size:Integer, total:Integer): Void{
		if(img == null){
			SOMmps.clusterImg = null;
		}else{
			SOMmps.clusterImg = SwingUtils.toFXImage(img);
		}
		clusterSize = size;
		TotalSize = total;
		infoText.content = "{clusterSize} of {TotalSize} chosen";
	}

	/**
	 * Opens the right-click menu.
	 * @param e - the event that caused the right click menu to open
	 */
	override public function showRightClickMenu (e : MouseEvent) : Void {
		SOMmps.showRightClickMenu(e, -1); //-1 means not on a Tile (it is on one, but I don't have the info here)
	}

	/**
	 * Tell the SOM tiles to update their status (point over or cluster stats).
	 */
	override public function updateMapStats():Void{
		SOMmps.updateMapStats();
	}

	
	
};
