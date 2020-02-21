package mg.studio.android.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private CheckBox mCbAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        mCbAccept = (CheckBox)findViewById(R.id.cbAccept);
    }

    public void onClickNext(View view){
        int next;
        if(view != null) next  = view.getId();
        else return;
        switch (next){
            case R.id.btnGo:
                if(mCbAccept.isChecked()){
                    setContentView(R.layout.question_one);
                }else{
                    Toast.makeText(
                            this, R.string.accept_requests,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnNext1:
                setContentView(R.layout.question_two);
                break;
            case R.id.btnNext2:
                setContentView(R.layout.question_three);
                break;
            case R.id.btnNext3:
                setContentView(R.layout.question_four);
                break;
            case R.id.btnNext4:
                setContentView(R.layout.question_five);
                break;
            case R.id.btnNext5:
                setContentView(R.layout.question_six);
                break;
            case R.id.btnNext6:
                setContentView(R.layout.question_seven);
                break;
            case R.id.btnNext7:
                setContentView(R.layout.question_eight);
                break;
            case R.id.btnNext8:
                setContentView(R.layout.question_nine);
                break;
            case R.id.btnNext9:
                setContentView(R.layout.question_ten);
                break;
            case R.id.btnNext10:
                setContentView(R.layout.question_eleven);
                break;
            case R.id.btnNext11:
                setContentView(R.layout.question_twelve);
                break;
            case R.id.btnNext12:
                setContentView(R.layout.finish_survey);
                break;
            default:
                    break;
        }
    }

    public void Finish(View view) {
        super.finish();
    }
}
