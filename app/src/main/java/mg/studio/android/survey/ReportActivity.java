package mg.studio.android.survey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReportActivity extends AppCompatActivity {
    private int length;
    private String text;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();
        text = intent.getStringExtra("data");
        length = intent.getIntExtra("length", 0);
        showAnswer();
    }

    private void showAnswer(){
        try {
            JSONArray jQuestions = new JSONArray(text);
            LinearLayout lLayout = findViewById(R.id.show_answer);
            LinearLayout.LayoutParams lpq = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lpq.setMargins(0,0,0,5);

            LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lpa.setMargins(40,0, 40 ,15);
            for (int i = 0; i < length; i++) {
                JSONObject jObject = (JSONObject)jQuestions.get(i);
                TextView qText = new TextView(this);
                String qStr = jObject.getString("question");
                qText.setText(qStr);
                qText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.font_size_middle));
                qText.setTextColor(getColor(R.color.fontColor));
                lLayout.addView(qText, lpq);
                TextView aText = new TextView(this);
                String type = jObject.getString("type");
                if(type.equals("single") || type.equals("fill")){
                    String tmp = jObject.getJSONObject("answer").getString("1");
                    aText.setText(tmp);
                }else if(type.equals("multiple")){
                    String tmp = jObject.getJSONArray("answer").toString();
                    aText.setText(tmp);
                }
                aText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.font_size_average));
                aText.setTextColor(getColor(R.color.fontColor));
                lLayout.addView(aText,lpa);
            }
        } catch (JSONException je) {
            return;
        }
    }

    public void onClickSave(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {

            saveToSd(text);
            view.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            saveToSd(text);
            findViewById(R.id.btn_save).setEnabled(false);
        }
    }

    public void saveToSd(String content) {
        try {
            SharedPreferences sp = getSharedPreferences(
                    "user_id", MODE_PRIVATE);
            int iUserId = sp.getInt("id", 0) + 1;
            JSONObject jResult;
            JSONObject jObject =   new JSONObject();
            jObject.put("id",String.valueOf(iUserId));
            jObject.put("len", String.valueOf(length));
            jObject.put("questions", new JSONArray(content));

            String root = this.getCacheDir().getPath();
            File directory = new File(root);
            File result = new File(root, "result.json");
            //create file
            if (!result.exists()) {
                // write the json string into the file
                result.createNewFile();
                result.setWritable(true);
                jResult = new JSONObject();
                JSONArray jSurveys = new JSONArray();
                jSurveys.put(jObject);
                jResult.put("survey",jSurveys);
            }else{
                FileReader reader = new FileReader(result);
                BufferedReader buffReader = new BufferedReader(reader);
                String line;
                StringBuilder text = new StringBuilder();
                while ((line = buffReader.readLine()) != null) {
                    text.append(line.trim());
                }
                reader.close();
                jResult = new JSONObject(text.toString());
                JSONArray jSurveys = jResult.getJSONArray("survey");
                jSurveys.put(jObject);
                jResult.put("survey",jSurveys);
            }
            FileOutputStream out = new FileOutputStream(result);
            out.write(jResult.toString().getBytes());
            out.close();
            // update user id file
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("id", iUserId);
            editor.commit();
        } catch (IOException ioe) {
           return;
        }catch (JSONException je){
            return;
        }
    }

    public void onClickExit(View view) {
        finish();
        MainActivity.mainActivity.finish();
    }
}
