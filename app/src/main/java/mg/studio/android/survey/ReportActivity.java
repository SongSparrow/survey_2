package mg.studio.android.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ReportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();
        String[] answers = intent.getStringArrayExtra("answer");
        try {
            ((TextView) findViewById(R.id.txt_answer1)).setText(answers[0]);
            ((TextView) findViewById(R.id.txt_answer2)).setText(answers[1]);
            ((TextView) findViewById(R.id.txt_answer3)).setText(answers[2]);
            ((TextView) findViewById(R.id.txt_answer4)).setText(answers[3]);
            ((TextView) findViewById(R.id.txt_answer5)).setText(answers[4]);
            ((TextView) findViewById(R.id.txt_answer6)).setText(answers[5]);
            ((TextView) findViewById(R.id.txt_answer7)).setText(answers[6]);
            ((TextView) findViewById(R.id.txt_answer8)).setText(answers[7]);
            ((TextView) findViewById(R.id.txt_answer9)).setText(answers[8]);
            ((TextView) findViewById(R.id.txt_answer10)).setText(answers[9]);
            ((TextView) findViewById(R.id.txt_answer11)).setText(answers[10]);
            ((TextView) findViewById(R.id.txt_answer12)).setText(answers[11]);
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    public void onClickExit(View view){
        super.finish();
    }
}
