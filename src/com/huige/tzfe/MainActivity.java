package com.huige.tzfe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements PrintInterface{

	private int direction;

	private GameManager game;

	private String TAG = "tzfe";
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	TextView messageTextView;
	TextView stepTextView;
	TextView scoreTextView;
	private TableView tableLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tableLayout = (TableView)findViewById(R.id.table);

		stepTextView = (TextView)findViewById(R.id.step);
		scoreTextView = (TextView)findViewById(R.id.score);

		game = new GameManager(MainActivity.this);

		messageTextView = (TextView)findViewById(R.id.message);
		tableLayout = (TableView) findViewById(R.id.table);

		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		tableLayout.setOnTouchListener(gestureListener);
		print();
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			messageTextView.setText("onFling v  x:"+velocityX+",y:"+velocityY);
			messageTextView.append("\n");
			messageTextView.append("e1 x:"+e1.getX()+",y:"+e1.getY());
			messageTextView.append("\n");
			messageTextView.append("e2 x:"+e2.getX()+",y:"+e2.getY());
			messageTextView.append("\n");

			float xInterval = e1.getX() - e2.getX();
			float yInterval = e1.getY() - e2.getY();

			float xIntervalAbs = Math.abs(xInterval);
			float yIntervalAbs = Math.abs(yInterval);

			String str = "null";
			direction = -1;
			if( xIntervalAbs > Util.SWIPE_MIN_DISTANCE && xIntervalAbs > yIntervalAbs*2){
				if( xInterval > 0 ){
					Log.i(TAG, "left");
					str = "left";
					direction = 3;
				}
				else{
					Log.i(TAG, "right");
					str = "right";
					direction = 1;
				}
			}

			if( yIntervalAbs > Util.SWIPE_MIN_DISTANCE && yIntervalAbs > xIntervalAbs*2){
				if( yInterval > 0 ){
					Log.i(TAG, "up");
					str = "up";
					direction = 0;
				}
				else{
					Log.i(TAG, "down");
					str = "down";
					direction = 2;
				}
			}

			if( direction >= 0){
				game.Move(direction);
				print();
			}

			messageTextView.append(str);
			messageTextView.append("\n");
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			Log.v(TAG, "onDown");
			return true;
		}
	}

	private void print(){
		Tile[][] tiles = game.grid.cells;
		tableLayout.setDraw(tiles);
		tableLayout.invalidate();
		//tableLayout.draw(tileValus);
		/*Tile tile;
		for(int i = 0; i < tileView.length; i++ ){
			tile = tileValus[i/4][i%4];
			printView(tileView[i], tile);
		}*/
	}

	/*private void printView(TextView view, Tile tile){
		if( 0 == tile.value ){
			view.setText("");
		}
		else{
			view.setText(String.valueOf(tile.value));
		}

		view.setBackgroundColor(Util.getColor(tile.value));
	}*/

	public void onClickUndo(View view){
		if( game.undo ){
			game.undoMove(direction);
			print();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void printSteps(int step) {
		// TODO Auto-generated method stub
		stepTextView.setText(String.valueOf(step));
	}

	@Override
	public void printScore(int score) {
		// TODO Auto-generated method stub
		scoreTextView.setText(String.valueOf(score));
	}

	@Override
	public void moveView(final Tile from, final Tile to) {
		messageTextView.append("from["+from.heigth+","+from.width+"]:"+from.value+" > to["+to.heigth+","+to.width+"]:"+to.value+"\n");
	}

	/*private View getViewByTile(Tile tile){
		View view = tileView[ ( tile.heigth << 2 ) + tile.width ];
		return view;
	}*/

}
