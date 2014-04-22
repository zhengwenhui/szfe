package com.huige.tzfe;

import android.util.Log;

public class GameManager {
	Grid grid = null;
	//int size;
	int startTiles;
	int score;
	boolean over;
	boolean won;
	boolean keepPlaying;
	String TAG = "tzfe";
	
	GameManager() {
		//this.size = size; // Size of the grid
		startTiles  = 2;
		Log.i(TAG, "GameManager startTiles:"+startTiles);
		setup();
	}

	// Restart the game
	void restart() {
		//clearGameState();
		//actuator.continueGame(); // Clear the game won/lost message
		setup();
	};


	// Keep playing after winning (allows going over 2048)
	void keepPlaying() {
		this.keepPlaying = true;
		//this.actuator.continueGame(); // Clear the game won/lost message
	};

	// Return true if the game is lost, or has won and the user hasn't kept playing
	boolean isGameTerminated() {
		return over || (this.won && !this.keepPlaying);
	};


	// Get the vector representing the chosen direction
	Point getVector(int direction) {
		// Vectors representing tile movement
		int[][] map = {
				{ -1, 0 }, // Up
				{ 0, 1 },  // Right
				{ 1, 0 },  // Down
				{ 0, -1 }, // Left
		};

		return new Point(map[direction][0], map[direction][1]);
	}
	
	// Get the vector representing the chosen direction
	int[] getParameter(int direction) {
		// Vectors representing tile movement
		int[][] map = {
				{ 1,					1,	0, 					1	}, // Up
				{ 0,					1,	grid.width_size-2,	-1	}, // Right
				{ grid.height_size-2 ,	-1, 0, 					1	},  // Down
				{ 0, 					1,	1,					1	},  // Left
		};
		
		return map[direction];
	}

	// Save all tile positions and remove merger info
	void prepareTiles() {
		Tile tile;
		for(int height = 0; height < grid.height_size; height++){
			for(int width = 0; width < grid.width_size; width++){
				tile = grid.cells[height][width];
				tile.mergedFrom = null;
				tile.savePosition();
			}
		}
	}

	// Set up the game
	void setup() {
		this.grid        = new Grid();
		this.score       = 0;
		this.over        = false;
		this.won         = false;
		this.keepPlaying = false;

		// Add the initial tiles
		Log.i(TAG, "setup");
		
		addStartTiles();
		// Update the actuator
		//actuate();
	};


	// Adds a tile in a random position
	void addRandomTile() {
		Log.i(TAG, "addRandomTile");
		
		Tile tile = grid.randomAvailableCell();

		if ( null != tile ) {
			int value = Math.random() < 0.9 ? 2 : 4;
			tile.value = value;
			Log.e(TAG, "addRandomTile tile.value:"+tile.value);
		}
	}

	// Set up the initial tiles to start the game with
	void addStartTiles() {
		Log.i(TAG, "addStartTiles");
		for (int i = 0; i < startTiles; i++) {
			addRandomTile();
		}
	};

	// Move a tile and its representation
	void moveTile(Tile tile, Tile cell) {
		cell.value += tile.value;
		tile.value = 0;
		//cell.mergedFrom = tile;
		//tile = cell;
	}
	
	// Move tiles on the grid in the specified direction
	void Move(int direction){
		// 0: up, 1: right, 2: down, 3: left
		Tile tile, other;
		int tempH, tempW;
		
		if (this.isGameTerminated()) return;
		
		// Save the current tile positions and remove merger information
		prepareTiles();
		
		Point point = getVector(direction);
		int []param = getParameter(direction);
		boolean moved = false;

		for(int height = param[0]; grid.withinHeight(height); height+=param[1]){
			for(int width = param[2]; grid.withinHeight(width); width+= param[3]){
				tile = grid.cells[height][width];
				
				if( 0 == tile.value ){
					continue;
				}
				
				tempH = height + point.heigth;
				tempW = width + point.width;
 				
				while(grid.withinBounds(tempH,tempW)){
					other = grid.cells[tempH][tempW];
					
					if( 0 == other.value ){
						moveTile(tile, other);
						moved = true;
						tile = other;
						tempH += point.heigth;
						tempW += point.width;
					}
					else if( tile.value == other.value && null == other.mergedFrom ){
						moveTile(tile, other);
						moved = true;
						other.mergedFrom = new Point(tile.heigth, tile.width);
						score += other.value;
						if (other.value == 2048) won = true;
						break;
					}
					else{
						break;
					}
				}
			}
		}

		if (moved) {
			addRandomTile();

			if (!movesAvailable()) {
				this.over = true; // Game over!
			}
		}
	}

	boolean movesAvailable() {
		return grid.cellsAvailable() > 0 || this.tileMatchesAvailable();
	};

	// Check for available matches between tiles (more expensive check)
	boolean tileMatchesAvailable() {
		Tile tile, leftTile, topTile;

		for(int height = 0; height < grid.height_size; height++){
			for(int width = 0; width < grid.width_size; width++){
				tile = grid.cells[height][width];

				if( height > 1 ){
					topTile = grid.cells[height-1][width];
					if( tile.value == topTile.value ){
						return true;
					}
				}
				
				if( width > 1 ){
					leftTile = grid.cells[height][width-1];
					if( tile.value == leftTile.value ){
						return true;
					}
				}
			}
		}
		return false;
	};

	boolean positionsEqual(Tile first, Tile second){
		return first.heigth == second.heigth && first.width == second.width;
	}
	
	boolean positionsEqual(Point first, Point second){
		return first.heigth == second.heigth && first.width == second.width;
	}
	
	class Point{
		Point(int heigth, int width){
			this.heigth = heigth;
			this.width = width;
		}
		int heigth;
		int width;
	}
}
