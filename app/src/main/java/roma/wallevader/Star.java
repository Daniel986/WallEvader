package roma.wallevader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


public class Star {
    private int x;
    private int y;
    private int speed = 15;
    private Bitmap bitmap;

    private Rect detectCollision;

    public Star(Context context, int x) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star30x30);

        this.x = x;
        this.y = 0;

        //initializing rect object
        detectCollision =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update() {
        y += speed;

        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();

    }

    //one more getter for getting the rect object
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
}