package roma.wallevader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Player {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speedX = 0;
    private int maxX;
    private int minY;


    private final int MIN_SPEED = -20;
    private final int MAX_SPEED = 20;

    private Rect detectCollision;

    public Player(Context context, int screenX, int screenY) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        x = (screenX - bitmap.getWidth()) / 2;
        y = screenY * 2 / 3 ;
        maxX = screenX;
        minY = 0;

        //initializing rect object
        detectCollision =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update() {

        x += speedX;

        if (speedX >= MAX_SPEED) {
            speedX = MAX_SPEED;
        }

        if (speedX <= MIN_SPEED) {
            speedX = MIN_SPEED;
        }

        if (x <= bitmap.getWidth()) {
            x = bitmap.getWidth();
        }
        if (x >= maxX - bitmap.getWidth()) {
            x = maxX - bitmap.getWidth();
        }


        //adding top, left, bottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();

    }

    //getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setSpeedX(float acceleration) {
        if(acceleration > 0.01) {
            if (acceleration <= 0.02)
                this.speedX += 1;
            else if(acceleration > 0.02)
                this.speedX += 2;
        }
        else if (acceleration < -0.01){
            if (acceleration >= -0.02)
                this.speedX -= 1;
            else if(acceleration < -0.02)
                this.speedX -= 2;
        }
        else
            this.speedX = 0;
    }
}