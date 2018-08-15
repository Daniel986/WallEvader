package roma.wallevader;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private Button play;
    private Button highScore;
    private int newScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extrasBundle = this.getIntent().getExtras();
        if(this.getIntent().hasExtra("score"))
         newScore = extrasBundle.getInt("score");


        play = findViewById(R.id.btnPlay);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });

        highScore= findViewById(R.id.btnHighScore);
        highScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, topScore.class);
                //if(newScore != 0)
                intent.putExtra("score", newScore);
                startActivity(intent);
            }
        });
    }
}
