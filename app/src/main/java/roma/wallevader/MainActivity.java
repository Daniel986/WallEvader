package roma.wallevader;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button play;
    private Button highScore;
    static String[] scores1;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private int newScore = 0;
    private boolean flag = false;
    private boolean isUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extrasBundle = this.getIntent().getExtras();
        if(this.getIntent().hasExtra("score"))
         newScore = extrasBundle.getInt("score");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Top 5 places");



        readFromDatabase();

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
                intent.putExtra("score", scores1);
                startActivity(intent);
            }
        });
    }


    public void writeToDatabase(String[] scores) {
        // Write a message to the database
        String tmp = TextUtils.join(":", scores);
        myRef.setValue(tmp);
        //myRef.setValue("100:90:80:70:60");
    }

    public void resetValues() {
        // Write a message to the database
        //myRef.setValue(tmp);
        myRef.setValue("0:0:0:0:0");
    }

    public void readFromDatabase() {

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                //Log.d("logger", "Value is: " + value);
                String[] scores = value.split(":");
                scores1 = scores;
                flag = true;
                if(newScore != 0 && !isUpdated) {
                    checkNewScore(newScore, scores1);
                    isUpdated = true;
                }
                //updateScoreBoard();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("logger", "Failed to read value.", error.toException());
            }
        });

    }

    public boolean checkNewScore(int newScore,String[] scores) {

        if (newScore > Integer.valueOf(scores[4])) {
            if (newScore > Integer.valueOf(scores[3])) {
                if (newScore > Integer.valueOf(scores[2])) {
                    if (newScore > Integer.valueOf(scores[1])) {
                        if (newScore > Integer.valueOf(scores[0])) {
                            scores[4] = scores[3];
                            scores[3] = scores[2];
                            scores[2] = scores[1];
                            scores[1] = scores[0];
                            scores[0] = String.valueOf(newScore);
                        } else {
                            scores[4] = scores[3];
                            scores[3] = scores[2];
                            scores[2] = scores[1];
                            scores[1] = String.valueOf(newScore);
                        }
                    } else {
                        scores[4] = scores[3];
                        scores[3] = scores[2];
                        scores[2] = String.valueOf(newScore);
                    }
                } else {
                    scores[4] = scores[3];
                    scores[3] = String.valueOf(newScore);
                }
            } else {
                scores[4] = String.valueOf(newScore);
            }
            writeToDatabase(scores);
            return true;
        } else return false;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}
