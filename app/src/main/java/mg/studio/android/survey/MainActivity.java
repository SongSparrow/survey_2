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
        mCbAccept = (CheckBox)findViewById(R.id.cb_accept);
    }

    public void onClickNext(View view){
        int next;
        if(view != null) next  = view.getId();
        else return;
        switch (next){
            case R.id.btn_go:
                if(mCbAccept.isChecked()){
                    setContentView(R.layout.question_one);
                }else{
                    Toast.makeText(
                            this, R.string.accept_requests,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_next1:
                setContentView(R.layout.question_two);
                break;
            case R.id.btn_next2:
                setContentView(R.layout.question_three);
                break;
            case R.id.btn_next3:
                setContentView(R.layout.question_four);
                break;
            case R.id.btn_next4:
                setContentView(R.layout.question_five);
                break;
            case R.id.btn_next5:
                setContentView(R.layout.question_six);
                break;
            case R.id.btn_next6:
                setContentView(R.layout.question_seven);
                break;
            case R.id.btn_next7:
                setContentView(R.layout.question_eight);
                break;
            case R.id.btn_next8:
                setContentView(R.layout.question_nine);
                break;
            case R.id.btn_next9:
                setContentView(R.layout.question_ten);
                break;
            case R.id.btn_next10:
                setContentView(R.layout.question_eleven);
                break;
            case R.id.btn_next11:
                setContentView(R.layout.question_twelve);
                break;
            case R.id.btn_next12:
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
