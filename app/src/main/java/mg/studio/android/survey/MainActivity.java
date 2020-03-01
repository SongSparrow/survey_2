package mg.studio.android.survey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private CheckBox mCbAccept;
    private String[] answers=null;
    static AppCompatActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        mCbAccept = (CheckBox)findViewById(R.id.cb_accept);
        mainActivity = this;
    }

    public void onClickNext(View view){
        int next;
        if(view != null) next  = view.getId();
        else return;
        switch (next){
            case R.id.btn_go:{
                if(mCbAccept.isChecked()){
                    answers = new String[12];
                    initialUserId();
                    setContentView(R.layout.question_one);
                }else{
                    Toast.makeText(
                            this, R.string.accept_requests,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btn_next1:{
                answers[0] = getAnswer(R.id.rg_question1);
                if (answers[0].length() == 0) break;
                setContentView(R.layout.question_two);
                break;
            }
            case R.id.btn_next2:{
                answers[1] = getAnswer(R.id.rg_question2);
                if(answers[1].length() == 0) break;
                setContentView(R.layout.question_three);
                break;
            }
            case R.id.btn_next3:{
                answers[2] = getAnswer(R.id.rg_question3);
                if(answers[2].length() == 0) break;
                setContentView(R.layout.question_four);
                break;
            }
            case R.id.btn_next4:{
                answers[3]="Your answer is ";
                int [] checkedIds = new int[7];
                checkedIds[0] = R.id.cb_music;
                checkedIds[1] = R.id.cb_photo;
                checkedIds[2] = R.id.cb_game;
                checkedIds[3] = R.id.cb_business;
                checkedIds[4] = R.id.cb_gps;
                checkedIds[5] = R.id.cb_data;
                checkedIds[6] = R.id.cb_others;
                for (int i=0;i<7;i++){
                    CheckBox cb = (CheckBox) findViewById(checkedIds[i]);
                    if(cb.isChecked()){
                        answers[3] +=  cb.getText().toString();
                        answers[3] += ",";
                    }
                }
                if(answers[3].equals("Your answer is ")){
                    Toast.makeText(this,
                            R.string.empty_answer,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                setContentView(R.layout.question_five);
                break;
            }
            case R.id.btn_next5:{
                answers[4]="Your answer is ";
                int [] checkedIds = new int[7];
                checkedIds[0] = R.id.cb_music;
                checkedIds[1] = R.id.cb_photo;
                checkedIds[2] = R.id.cb_game;
                checkedIds[3] = R.id.cb_business;
                checkedIds[4] = R.id.cb_gps;
                checkedIds[5] = R.id.cb_data;
                checkedIds[6] = R.id.cb_others;
                for (int i=0;i<7;i++){
                    CheckBox cb = (CheckBox) findViewById(checkedIds[i]);
                    if(cb.isChecked()){
                        answers[4] +=  cb.getText().toString();
                        answers[4] += ",";
                    }
                }
                if(answers[4].equals("Your answer is ")){
                    Toast.makeText(this,
                            R.string.empty_answer,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                setContentView(R.layout.question_six);
                break;
            }
            case R.id.btn_next6:{
                EditText editText = (EditText) findViewById(R.id.edt_answer6);
                answers[5] = "Your answer is ";
                answers[5] += editText.getText().toString();
                if (answers[5].length() == 0) {
                    Toast.makeText(this,
                            R.string.empty_answer,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                setContentView(R.layout.question_seven);
                break;
            }
            case R.id.btn_next7:{
                answers[6] = getAnswer(R.id.rg_question7);
                if(answers[6].length() == 0) break;
                setContentView(R.layout.question_eight);
                break;
            }
            case R.id.btn_next8:{
                answers[7] = getAnswer(R.id.rg_question8);
                if(answers[7].length() == 0) break;
                setContentView(R.layout.question_nine);
                break;
            }
            case R.id.btn_next9:{
                answers[8] = getAnswer(R.id.rg_question9);
                if(answers[8].length() == 0) break;
                setContentView(R.layout.question_ten);
                break;
            }
            case R.id.btn_next10: {
                answers[9] = getAnswer(R.id.rg_question10);
                if (answers[9].length() == 0) break;
                setContentView(R.layout.question_eleven);
                break;
            }
            case R.id.btn_next11:{
                answers[10] = getAnswer(R.id.rg_question11);
                if(answers[10].length() == 0) break;
                setContentView(R.layout.question_twelve);
                break;
            }
            case R.id.btn_next12:{
                answers[11] = getAnswer(R.id.rg_question12);
                if(answers[11].length() == 0) break;
                setContentView(R.layout.finish_survey);
                break;
            }
            default:
                    break;
        }
    }

    public void Finish(View view) throws IOException {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("answer",answers);
        startActivity(intent);
    }

    private void initialUserId(){
        SharedPreferences sp = getSharedPreferences(
                "user_id",MODE_PRIVATE);
        if(sp.getInt("id",-1)<0){
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("id",0);
            editor.commit();
        }
    }

    private String getAnswer(int rgId){
        RadioGroup rg = (RadioGroup) findViewById(rgId);
        int checkedId = rg.getCheckedRadioButtonId();
        if(checkedId == -1){
            Toast.makeText(this,
                    R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
            return "";
        }
        RadioButton rb = (RadioButton) findViewById(checkedId);
        return "Your answer is "+rb.getText().toString();
    }
}
