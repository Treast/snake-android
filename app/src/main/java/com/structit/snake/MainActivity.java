package com.structit.snake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.structit.snake.view.SnakeOnTouchListener;
import com.structit.snake.view.SnakeView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final int DIRECTION_NONE = 0;
    public static final int DIRECTION_DOWN = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_RIGHT = 3;
    public static final int DIRECTION_UP = 4;

    private final String LOG_TAG = MainActivity.class.getName();
    private final int POINT_OFFSET = 4;
    private final int REDRAW_INTERVAL_MS = 500;

    private SnakeView mSnakeView;
    private RedrawHandler mHandler;
    private Snake mSnake;
    private Point mFood;
    private int mDirectionPlayer;
    private Menu mMenu;
    private boolean mIsStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mSnakeView = findViewById(R.id.snake);

        SnakeOnTouchListener listener = new SnakeOnTouchListener(this);
        this.mSnakeView.setOnTouchListener(listener);

        this.mHandler = new RedrawHandler(this);
    }

    @Override
    protected void onStart() {
        Log.d(LOG_TAG, "Starting...");

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.mMenu = menu;
        this.mIsStopped = true;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isSelected;

        switch (item.getItemId()){
            case R.id.start:
                if(this.mIsStopped == true) {
                    this.mMenu.getItem(0).setIcon(R.drawable.ic_stop);
                    this.mIsStopped = false;

                    newGame();
                } else {
                    this.mMenu.getItem(0).setIcon(R.drawable.ic_play);
                    this.mIsStopped = true;
                }

                isSelected = true;
                break;

            default:
                isSelected = super.onOptionsItemSelected(item);
                break;
        }

        return isSelected;
    }

    private void newGame() {
        Log.d(LOG_TAG, "Starting new game...");

        this.generateSnake();
        this.generateFood();
        this.mDirectionPlayer = DIRECTION_NONE;

        this.mHandler.setInterval(REDRAW_INTERVAL_MS);
        this.mHandler.request();
    }

    private void generateSnake() {
        Random random = new Random();

        this.mSnake = new Snake(POINT_OFFSET +
                random.nextInt(this.mSnakeView.getNbTileX() - 2*POINT_OFFSET),
                POINT_OFFSET +
                        random.nextInt(this.mSnakeView.getNbTileY() - 2*POINT_OFFSET));
    }

    private void generateFood() {
        Random random = new Random();
        Boolean isSnakePosition = false;
        Point food;

        do
        {
            isSnakePosition = false;

            food = new Point(POINT_OFFSET +
                    random.nextInt(this.mSnakeView.getNbTileX() - 2*POINT_OFFSET),
                    POINT_OFFSET +
                            random.nextInt(this.mSnakeView.getNbTileY()- 2*POINT_OFFSET));

            for(int i=0; i < this.mSnake.getLength(); i++)
            {
                if(this.mSnake.getPart(i).equals(food))
                {
                    isSnakePosition = true;
                    break;
                }
                //Else do nothing
            }
        }
        while (isSnakePosition);

        this.mFood = food;
    }

    public void update() {
        switch (this.mDirectionPlayer)
        {
            case DIRECTION_DOWN:
                if (this.mSnake.isBaby() ||
                        this.mSnake.getPart(0).getY() ==
                                this.mSnake.getPart(1).getY())
                    this.mSnake.setDirection(DIRECTION_DOWN);
                break;

            case DIRECTION_RIGHT:
                if (this.mSnake.isBaby() ||
                        this.mSnake.getPart(0).getX() ==
                                this.mSnake.getPart(1).getX())
                    this.mSnake.setDirection(DIRECTION_RIGHT);
                break;

            case DIRECTION_LEFT:
                if (this.mSnake.isBaby() ||
                        this.mSnake.getPart(0).getX() ==
                                this.mSnake.getPart(1).getX())
                    this.mSnake.setDirection(DIRECTION_LEFT);
                break;

            case DIRECTION_UP:
                if (this.mSnake.isBaby() ||
                        this.mSnake.getPart(0).getY() ==
                                this.mSnake.getPart(1).getY())
                    this.mSnake.setDirection(DIRECTION_UP);
                break;

            case DIRECTION_NONE:
            default:
                break;
        }

        if (this.mSnake.getPart(0).equals(this.mFood))
        {
            this.mSnake.Update(true);
            this.generateFood();
        }
        else
        {
            this.mSnake.Update(false);
        }

        if (this.mSnake.getPart(0).getX() < 1 ||
                this.mSnake.getPart(0).getX() > this.mSnakeView.getNbTileX()-2 ||
                this.mSnake.getPart(0).getY() < 1 ||
                this.mSnake.getPart(0).getY() > this.mSnakeView.getNbTileY()-2 ||
                this.mSnake.isBitting())
        {
            this.mMenu.getItem(0).setIcon(R.drawable.ic_play);
        }
        else if(this.mIsStopped) {
            //Refresh the canvas
            this.mSnakeView.clearTiles();
            this.mSnakeView.invalidate();
        }
        else
        {
            //Refresh the canvas
            this.mSnakeView.clearTiles();
            this.mSnakeView.updateFood(this.mFood);
            this.mSnakeView.updateSnake(this.mSnake);
            this.mSnakeView.invalidate();

            this.mHandler.request();
        }
    }

    public void updateDirectionPlayer(float rawX, float rawY) {
        double x = this.mSnakeView.getTileX(rawX);
        double y = this.mSnakeView.getTileY(rawY);

        switch(this.mSnake.getDirection())
        {
            case DIRECTION_UP:
            case DIRECTION_DOWN:
                if(this.mSnake.getPart(0).getX() > x) {
                    this.mDirectionPlayer = DIRECTION_LEFT;
                } else {
                    this.mDirectionPlayer = DIRECTION_RIGHT;
                }
                break;

            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                if(this.mSnake.getPart(0).getY() > y) {
                    this.mDirectionPlayer = DIRECTION_UP;
                } else {
                    this.mDirectionPlayer = DIRECTION_DOWN;
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "Stopping...");

        this.mMenu.getItem(0).setIcon(R.drawable.ic_play);
        this.mIsStopped = true;

        super.onStop();
    }
}
