package mg.studio.android.survey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReportActivity extends AppCompatActivity {
    private String[] answers;
    private int mUserId=0;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();
        answers = intent.getStringArrayExtra("answer");
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
            findViewById(R.id.btn_save).setEnabled(true);
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    public void onClickSave(View view){
        String content = ReportActivity.makeJsonString(answers);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            saveToSd(false, content);
            saveToSd(true, content);
            view.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_WRITE_EXTERNAL_STORAGE:{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    String content = ReportActivity.makeJsonString(answers);
                    saveToSd(false,content);
                    saveToSd(true,content);
                    findViewById(R.id.btn_save).setEnabled(false);
                }
                break;
            }
            default:
                break;
        }
    }

    public void saveToSd(boolean toSd, String content){
        try{
            String root = "";
            if(toSd){
                // true means Share Memory
                root = getApplicationContext()
                        .getExternalFilesDir(null).getAbsolutePath();
            }else{
                // false means interior of the app
                root = this.getCacheDir().getPath();
            }
            File directory = new File(root);
            // create dir
            if(!directory.exists()){
                directory.mkdirs();
            }
            SharedPreferences sp = getSharedPreferences(
                    "user_id",MODE_PRIVATE);
            mUserId = sp.getInt("id",0)+1;
            String strFileName = "answer"
                    +mUserId+".json";
            File answer = new File(root,strFileName);
            //create file
            if(!answer.exists()){
                answer.createNewFile();
            }
            if(!answer.canWrite()){
                answer.setWritable(true);
            }
            // write the json string into the file
            FileOutputStream out = new FileOutputStream(answer);
            out.write(content.getBytes());
            out.flush();
            out.close();
            // update user id file
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("id",mUserId);
            editor.commit();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static String makeJsonString(String[] content){
        try{
            // make json string
            JSONObject object = new JSONObject("{}");
            object.put("answer1",content[0]);
            object.put("answer2",content[1]);
            object.put("answer3",content[2]);
            object.put("answer4",content[3]);
            object.put("answer5",content[4]);
            object.put("answer6",content[5]);
            object.put("answer7",content[6]);
            object.put("answer8",content[7]);
            object.put("answer9",content[8]);
            object.put("answer10",content[9]);
            object.put("answer11",content[10]);
            object.put("answer12",content[11]);
            return object.toString();
        }catch (JSONException je){
            je.printStackTrace();
        }
        return "{}";
    }

    public void onClickExit(View view){
       finish();
       MainActivity.mainActivity.finish();
    }
}
