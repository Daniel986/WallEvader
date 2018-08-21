package roma.wallevader;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.content.Context.SENSOR_SERVICE;

public class GameView extends SurfaceView implements Runnable {

    static MediaPlayer gameOnSound;
    private MediaPlayer gameOverSound;

    volatile boolean playing;
    private Thread gameThread = null;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private Player player;
    private Enemy[][] enemies;

    private int spaceCount;
    private int enemyCount = 3;
    private int rowsCount = 4;
    private int createdRowsCount = 0;

    private int maxY, maxX;
    private int x, dx, x2, dx2 = 0;
    private int maxSpaceStart, maxSpaceEnd;

    private boolean firstRun = true;
    private boolean changedXs = false;
    private boolean isGameOver = false;
    private boolean isAfterLastScreen = false;
    private boolean scored = false;

    private int score = 0;

    private Context context;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;

    private List<Star> stars = new CopyOnWriteArrayList<>();

    //defining a boom object to display blast
    private Boom boom;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.context = context;

        maxY = screenY;
        maxX = screenX;


        player = new Player(context, screenX, screenY);

        maxSpaceStart = maxX / 5;
        while(maxSpaceStart <= player.getBitmap().getWidth())
            maxSpaceStart += player.getBitmap().getWidth() / 3;

        maxSpaceEnd = maxX / 3;

        surfaceHolder = getHolder();
        paint = new Paint();

        gameOnSound = MediaPlayer.create(context, R.raw.gameon);
        gameOverSound = MediaPlayer.create(context, R.raw.gameover);


        enemies = new Enemy[rowsCount][enemyCount];

        boom = new Boom(context);

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                player.setSpeedX(sensorEvent.values[0]); // TODO return this shyte
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);

        gameOnSound.start();
        initNewRow(0);

    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        player.update();

        //setting boom outside the screen
        //boom.setX(-550);
        //boom.setY(-550);

        if (!stars.isEmpty())
            //synchronized (stars) {
            for (Star s : stars) {
                s.update();
                if (Rect.intersects(player.getDetectCollision(), s.getDetectCollision())) {
                    stars.remove(s);
                    score += 100;
                } else if (s.getY() > maxY) {
                    stars.remove(s);
                }
                // TODO : check collision with player
            }
        // }

        if (firstRun) {



            if (enemies[createdRowsCount][0].getY() >= maxY / 4) {// TODO: if first run - add row every ~300px in Y direction (initialize new row)
                initNewRow(++createdRowsCount);
            }

            if (createdRowsCount == rowsCount - 1) {
                firstRun = false;
            }
        }

        for (int j = 0; j < createdRowsCount + 1; j++) {


            if (!scored && enemies[j][0].getY() >= maxY * 2 / 3 && enemies[j][0].getY() <= maxY * 2 / 3 + 10) { // regular scores
                score += 50;
                scored = true;
                //Log.d("SCORE", "score: " + score + ", row: " + j + ", enemy Y: " + enemies[j][0].getY());
            }

            for (int i = 0; i < enemyCount; i++) {
                enemies[j][i].update();
            }

            if (enemies[j][0].getY() >= maxY) {
                Random generator = new Random();
                spaceCount = generator.nextInt(2) + 1; // randomize num of spaces
                if (spaceCount == 1) {
                    dx = generator.nextInt(maxSpaceEnd) + maxSpaceStart; // width of space (200 - 399 px) // TODO : init star in the middle of x and dx - ((x+dx)/2)
                    x = generator.nextInt(maxX - dx); // placement of space
                    if (generator.nextInt(2) == 1) { // randomize the chance of a star to appear
                        stars.add(new Star(context, x + generator.nextInt(maxSpaceStart)));
                    }
                } else {
                    dx = generator.nextInt(maxSpaceEnd) + maxSpaceStart;
                    x = generator.nextInt(maxX / 2) - dx;
                    dx2 = generator.nextInt(maxSpaceEnd) + maxSpaceStart;
                    while (x2 < x + dx + 100) // assure sensible spacing
                        x2 = (generator.nextInt(maxX / 2) + maxX / 2) - dx2;

                    int starplacement = generator.nextInt(3); // randomize the chance of a star to appear
                    if (starplacement > 0) {
                        if (starplacement == 1)
                            stars.add(new Star(context, x + generator.nextInt(maxSpaceStart)));
                        else
                            stars.add(new Star(context, x2 + generator.nextInt(maxSpaceStart)));
                    }
                }
                changedXs = true;
            }

            for (int i = 0; i < enemyCount; i++) {

                if (changedXs && enemies[j][i].getY() > maxY) {
                    if (spaceCount == 1) {
                        enemies[j][i].resetRect(x, x + dx, spaceCount, i);
                    } else {
                        if (i == 0)
                            enemies[j][i].resetRect(x, x + dx, spaceCount, i);
                        else if (i == 1)
                            enemies[j][i].resetRect(x + dx, x2, spaceCount, i);
                        else {
                            enemies[j][i].resetRect(x2, x2 + dx2, spaceCount, i);
                        }
                    }
                }

                //if collision occurs with player
                if (Rect.intersects(player.getDetectCollision(), enemies[j][i].getDetectCollision())) {

                    //displaying boom at that location
                    boom.setX(player.getDetectCollision().left - 50);
                    boom.setY(player.getDetectCollision().top - 50);

                    // TODO : uncomment when finished
                      gameOnSound.stop();
//
//                    try {
                      gameOverSound.start();
//                    gameThread.sleep(100);
//                    gameOverSound.start();
//                    gameThread.sleep(100);
//                    gameOverSound.start();
//                    //gameThread.sleep(100);
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    isGameOver = true;

                    // TODO : change intent

                }
//                else if (player.getDetectCollision().bottom >  enemies[j][i].getDetectCollision().top) {
//
//                }


                // TODO : add bonus stars
            }
        }
        changedXs = false;
        scored = false;
    }

    private void initNewRow(int j) {

        Random generator = new Random();
        // make first space
        //for(int j = 0; j < rowsCount; ) {

        spaceCount = generator.nextInt(2 - 1) + 1;
        //spaceCount = 2;
        if (spaceCount == 1) {
            dx = generator.nextInt(maxSpaceEnd) + maxSpaceStart; // width of space (300 - 400 px)
            x = generator.nextInt(maxX - dx); // placement of space
            for (int i = 0; i < enemyCount; i++) {
                enemies[j][i] = new Enemy(maxX, maxY, x, x + dx, spaceCount, i);
            }
        } else {
            dx = generator.nextInt(maxSpaceEnd) + maxSpaceStart; // width of space (300 - 400 px)
            x = generator.nextInt(maxX / 2) - dx; // placement of space
            dx2 = generator.nextInt(maxSpaceEnd) + maxSpaceStart; // width of space (300 - 400 px)
            while (x2 < x + dx + 100) // assure sensible spacing
                x2 = (generator.nextInt(maxX / 2) + maxX / 2) - dx2; // placement of space
            for (int i = 0; i < enemyCount; i++) {
                if (i == 0)
                    enemies[j][i] = new Enemy(maxX, maxY, x, x + dx, spaceCount, i);
                else if (i == 1)
                    enemies[j][i] = new Enemy(maxX, maxY, x + dx, x2, spaceCount, i);
                else
                    enemies[j][i] = new Enemy(maxX, maxY, x2, x2 + dx2, spaceCount, i);
            }
        }
    }

    private void draw() {
        if(isAfterLastScreen && isGameOver) {
            try {
                gameThread.sleep(1000);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("score", score);
                context.startActivity(intent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(getResources().getColor(R.color.purple));


            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            for (Star s : stars) {
                canvas.drawBitmap(
                        s.getBitmap(),
                        s.getX(),
                        s.getY(),
                        paint);
            }


            paint.setColor(getResources().getColor(R.color.mustard));
            for (int j = 0; j < createdRowsCount + 1; j++)
                for (int i = 0; i < enemyCount; i++) {
                    canvas.drawRect(enemies[j][i].getDetectCollision(), paint);
                }


            //drawing boom image
            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );

            paint.setColor(getResources().getColor(R.color.reddish));
            paint.setTextSize(80);
            canvas.drawText(Integer.toString(score), maxX * 0.1f, maxY * 5 / 6, paint);


            //draw game Over when the game is over
            if (isGameOver) {
                //paint.setColor(getResources().getColor(R.color.mustard));
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("Game Over", canvas.getWidth() / 2, yPos, paint);
                isAfterLastScreen = true;
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    // make game 60fps
    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        gameOnSound.stop();
        sensorManager.unregisterListener(gyroscopeEventListener);
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
        gameOnSound.start();
        gameThread = new Thread(this);
        gameThread.start();
    }


}