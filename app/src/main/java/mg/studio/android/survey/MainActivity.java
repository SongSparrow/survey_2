package mg.studio.android.survey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CheckBox mCbAccept;
    private String[] answers = null;
    private JSONArray questions;
    private Map<String, String> survey;
    static AppCompatActivity mainActivity;
    private int qNum = 0;
    private int qSeq = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        mCbAccept = (CheckBox) findViewById(R.id.cb_accept);
        mainActivity = this;
        // initial question list
        questions = GetQuestions();
        if (questions == null) {
            qNum = 0;
        } else {
            qNum = questions.length();
        }
        qSeq = 0;
    }

    // initial question list
    @Nullable
    private JSONArray GetQuestions() {
        try {
            // read sample.json
            InputStreamReader inputReader = new InputStreamReader(
                    getAssets().open("sample.json"));
            BufferedReader buffReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder text = new StringBuilder();
            while ((line = buffReader.readLine()) != null) {
                text.append(line.trim());
            }
            inputReader.close();
            // analyse text
            JSONObject json = new JSONObject(text.toString());
            JSONObject survey = json.getJSONObject("survey");
            // return question json object list
            return survey.getJSONArray("questions");
        } catch (IOException ioe) {
            return null;
        } catch (JSONException je) {
            return null;
        }
    }

    public void setSingleView(JSONObject ques,
                              int title_id) throws JSONException {
        if (ques == null) return;
        setContentView(R.layout.question_single);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
        JSONArray jArray = ques.getJSONArray("options");
        int size = jArray.length();
        String[] optionText = new String[size];
        RadioGroup rGroup = findViewById(R.id.options);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < size; i++) {
            optionText[i] = ((JSONObject) jArray.get(i)).getString(String.valueOf(i + 1));
            RadioButton option = new RadioButton(this);
            option.setText(optionText[i]);
            option.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.font_size_average));
            option.setPadding(0,5,0,5);
            rGroup.addView(option, lp);
        }
    }

    public void setMultipleView(JSONObject ques,
                                int title_id) throws JSONException {
        if (ques == null) return;
        setContentView(R.layout.question_multiple);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
        JSONArray jArray = ques.getJSONArray("options");
        int size = jArray.length();
        String[] optionText = new String[size];
        LinearLayout lLayout = findViewById(R.id.options);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < size; i++) {
            optionText[i] = ((JSONObject) jArray.get(i)).getString(String.valueOf(i + 1));
            CheckBox cb = new CheckBox(this);
            cb.setText(optionText[i]);
            cb.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.font_size_average));
            cb.setPadding(0,5,0,5);
            lLayout.addView(cb, lp);
        }
    }

    public void setFillView(JSONObject ques,
                            int title_id) throws JSONException {
        setContentView(R.layout.question_fill);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
    }

    public void onClickGo(View view) {
        if (mCbAccept.isChecked()) {
            goNextPage();
        } else {
            Toast.makeText(this, R.string.accept_requests,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSingleNext(View view) {
        RadioGroup rGroup = findViewById(R.id.options);
        int checkedId = rGroup.getCheckedRadioButtonId();
        if (checkedId > 0) {
            String answer = ((RadioButton) findViewById(checkedId)).getText().toString();
            Log.i("onClickSingleNext", answer);
            goNextPage();
        }else{
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickMultipleNext(View view) {
        LinearLayout lLayout = findViewById(R.id.options);
        int n = lLayout.getChildCount();
        int x = 0;
        for (int i = 0; i < n; i++) {
            CheckBox cb = (CheckBox) lLayout.getChildAt(i);
            if (cb.isChecked()) {
                String answer = cb.getText().toString();
                Log.i("onClickMultipleNext", answer);
                x++;
            }
        }
        if(x>0){
            goNextPage();
        }else{
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickFillNext(View view) {
        EditText editText = findViewById(R.id.answer_text);
        String answer = editText.getText().toString();
        if(answer.length() != 0){

            goNextPage();
        }else{
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goNextPage() {
        try {
            if (qSeq < qNum) {
                JSONObject question = ((JSONObject) questions.get(qSeq++));
                String type = question.getString("type");
                if (type.equals("single")) {
                    setSingleView(question, R.string.single);
                } else if (type.equals("multiple")) {
                    setMultipleView(question, R.string.multiple);
                } else if (type.equals("fill")) {
                    setFillView(question, R.string.fill);
                }
            } else {
                setContentView(R.layout.finish_survey);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

}
