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

import org.jetbrains.annotations.NotNull;
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
    private JSONArray questions;
    private JSONObject[] answers;
    static AppCompatActivity mainActivity;
    private int qNum = 0; // number of questions
    private int qSeq = 0; // sequence

/*    this is the test string but to prove the flexibility of the app,
      I replace the test string with previous questions!!!!!!!!!!!!!!!!!!!!
     if you want to know the  result of test string, please
     replace  JSONObject json = new JSONObject(text.toString());
     with JSONObject json = new JSONObject(test);
     in GetQuestions function*/

    //    private String test = " {\"survey\":{\"id\":\"12344134\",\"len\":\"2\",\"questions\":[{\"type\":\"single\",\"question\":\"How well do the professors teach at this university?\",\"options\":[{\"1\":\"Extremely well\"},{\"2\":\"Very well\"}]},{\"type\":\"single\",\"question\":\"How effective is the teaching outside yur major at the univesrity?\",\"options\":[{\"1\":\"Extremetly effective\"},{\"2\":\"Very effective\"},{\"3\":\"Somewhat effective\"},{\"4\":\"Not so effective\"},{\"5\":\"Not at all effective\"}]}]}}";

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
            answers = null;
        } else {
            qNum = questions.length();
            answers = new JSONObject[qNum];
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

    // if the type is single, the app will load a layout
    // to display the single choice question
    public void setSingleLayout(JSONObject ques,
                              int title_id) throws JSONException {
        if (ques == null) return;
        setContentView(R.layout.question_single);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
        // get options
        JSONArray jArray = ques.getJSONArray("options");
        int size = jArray.length();
        String[] optionText = new String[size];
        RadioGroup rGroup = findViewById(R.id.options);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        // add the options into the ViewGroup
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

    // if the type is multiple, the app will load a layout
    // to display the multiple choice question
    public void setMultipleLayout(JSONObject ques,
                                int title_id) throws JSONException {
        if (ques == null) return;
        setContentView(R.layout.question_multiple);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
        // get the options
        JSONArray jArray = ques.getJSONArray("options");
        int size = jArray.length();
        String[] optionText = new String[size];
        LinearLayout lLayout = findViewById(R.id.options);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // add the options to the ViewGroup
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

    // if the type is fill, the app will load a layout
    // to display the fill-blank-question
    public void setFillLayout(JSONObject ques,
                              int title_id) throws JSONException {
        if(ques == null) return;
        setContentView(R.layout.question_fill);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
    }

    // the app will check whether the user agrees to
    // our requirements or not, then load the question layout
    public void onClickGo(View view) {
        if (mCbAccept.isChecked()) {
            goNextPage();
        } else {
            Toast.makeText(this, R.string.accept_requests,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // the app will save the user's answer after clicking
    // the button on the single choice question layout
    public void onClickSingleNext(View view) {
        RadioGroup rGroup = findViewById(R.id.options);
        // get the checked radiobutton
        int checkedId = rGroup.getCheckedRadioButtonId();
        if (checkedId > 0) {
            try{
                String answer = ((RadioButton) findViewById(checkedId)).getText().toString();
                Log.i("onClickSingleNext", answer);
                JSONObject jQuestion = new JSONObject();
                jQuestion.put("type","single");
                TextView question = findViewById(R.id.question);
                jQuestion.put("question", question.getText().toString());
                JSONObject jOption = new JSONObject();
                jOption.put("1",answer);
                jQuestion.put("answer", jOption);
                answers[qSeq-1] = jQuestion;
                goNextPage(); // load next question
            }catch (JSONException je){
                return;
            }
        }else{
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // the app will save the user's answer after clicking
    // the button on the multiple choice question layout
    public void onClickMultipleNext(View view) {
        LinearLayout lLayout = findViewById(R.id.options);
        int n = lLayout.getChildCount();
        int x = 0;
        try{
            JSONObject jQuestion = new JSONObject();
            jQuestion.put("type","multiple");
            TextView view1 = findViewById(R.id.question);
            jQuestion.put("question",view1.getText().toString());
            JSONArray jAnswer = new JSONArray();
            // check every checkbox
            for (int i = 0; i < n; i++) {
                CheckBox cb = (CheckBox) lLayout.getChildAt(i);
                if (cb.isChecked()) {
                    String answer = cb.getText().toString();
                    JSONObject jOption = new JSONObject();
                    jOption.put(String.valueOf(x+1),answer);
                    jAnswer.put(jOption);
//                    Log.i("onClickMultipleNext", answer);
                    x++;
                }
            }
            jQuestion.put("answer",jAnswer);
            answers[qSeq-1] = jQuestion;
        }catch (JSONException je){
            return;
        }
        if(x>0){
            goNextPage(); // load next question
        }else{
            // if no one option was selected!
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // the app will save the user's answer after clicking
    // the button on the fill-blank-question layout
    public void onClickFillNext(View view) {
        EditText editText = findViewById(R.id.answer_text);
        String answer = editText.getText().toString();
        if(answer.length() != 0){
            try{
                JSONObject jQuestion = new JSONObject();
                jQuestion.put("type","fill");
                TextView view1 = findViewById(R.id.question);
                jQuestion.put("question",view1.getText().toString());
                JSONObject jText = new JSONObject();
                jText.put("1",answer);
                jQuestion.put("answer",jText);
                answers[qSeq-1] = jQuestion;
                goNextPage();
            }catch (JSONException je){return;}
        }else{
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // check the type of next question and then load it
    private void goNextPage() {
        try {
            if (qSeq < qNum) {
                JSONObject question = ((JSONObject) questions.get(qSeq++));
                String type = question.getString("type");
                if (type.equals("single")) {
                    setSingleLayout(question, R.string.single);
                } else if (type.equals("multiple")) {
                    setMultipleLayout(question, R.string.multiple);
                } else if (type.equals("fill")) {
                    setFillLayout(question, R.string.fill);
                }
            } else {
                setContentView(R.layout.finish_survey);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    // start report activity
    public void onClickReport(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        JSONArray jsonArray = new JSONArray();
        for (int i=0;i<qNum;i++)
            jsonArray.put(answers[i]);
        intent.putExtra("data", jsonArray.toString());
        intent.putExtra("length",qNum);
        startActivity(intent);
    }
}
