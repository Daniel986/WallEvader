package roma.wallevader;

import android.graphics.Rect;


public class Enemy {
    private int x, dx;
    private int y;
    private int speed;

    private int maxX;
    private int minX;

    private int maxY;
    private int minY;

    //creating a rect object
    private Rect detectCollision;

    public Enemy(int screenX,
                 int screenY,
                 int spacex,
                 int spacedx,
                 int spaces,
                 int idx) {

        maxX = screenX;
        maxY = screenY;
        minX = 0;
        speed = 15;
        y = 0;

        if (spacex < 0 )
            spacex = 0;

        if(spaces == 1) {
            if(idx == 0) {
                this.x = minX;
                this.dx = spacex;
            }
            else {
                this.x = spacedx;
                this.dx = screenX;
            }
        }
        else {
            if(idx == 0) {
                this.x = minX;
                this.dx = spacex;
            }
            else if ( idx == 1){
                this.x = spacex;
                this.dx = spacedx;
            }
            else {
                this.x = spacedx;
                this.dx = screenX;
            }
        }
        detectCollision = new Rect(x, y, dx, 100);
    }

    public void update() {
        y += speed;

        detectCollision.top = y;
        detectCollision.bottom = y + 100;
        detectCollision.left = x;
        detectCollision.right = dx;

    }

    public void resetRect(int spacex, int spacedx, int spaces, int idx){

        this.y = -50;

        if (spacex < 0 )
            spacex = 0;

        if(spaces == 1) {
            if(idx == 0) {
                this.x = minX;
                this.dx = spacex;
            }
            else {
                this.x = spacedx;
                this.dx = maxX;
            }
        }
        else {
            if(idx == 0) {
                this.x = minX;
                this.dx = spacex;
        }
            else if ( idx == 1){
                this.x = spacex;
                this.dx = spacedx;
            }
            else {
                this.x = spacedx;
                this.dx = maxX;
            }
        }
    }

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }


    public int getY() {
        return y;
    }

}