package mg.studio.android.survey;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    private int length;
    private String text;
    private String surveyId;
    private String provider;
    private final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };
    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();
        // get the answer string from MainActivity
        text = intent.getStringExtra("data");
        length = intent.getIntExtra("length", 0);
        surveyId = intent.getStringExtra("surveyId");
        dbHelper = null;
        // display them on the show answer TextView
        showAnswer();
    }
    private void getPermissions() {
        boolean flag = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                flag = false;
            }
        }
        final int REQUEST_PERMISSION = 100;
        if (!flag) {
            ActivityCompat.requestPermissions(
                    this, permissions, REQUEST_PERMISSION);
        }
    }
    @Nullable
    private Location getCurrentLocation() {
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> list;
        if (lManager != null) {
            list = lManager.getProviders(true);
        } else {
            Toast.makeText(this, R.string.no_lct_service,
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, R.string.no_lct_provider,
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        Location location = null;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            location = lManager.getLastKnownLocation(provider);
        }
        return location;
    }
    @Nullable
    private String getIMEI() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tManager == null) return null;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED) {
            return tManager.getImei();
        }
        return null;
    }
    // display the answer on the show answer TextView
    private void showAnswer() {
        try {
            // question ViewGroup
            JSONArray jQuestions = new JSONArray(text);
            LinearLayout lLayout = findViewById(R.id.show_answer);
            LinearLayout.LayoutParams lpq = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lpq.setMargins(0, 0, 0, 5);
            // answer ViewGroup
            LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lpa.setMargins(40, 0, 40, 15);
            // add questions and answers
            for (int i = 0; i < length; i++) {
                JSONObject jObject = (JSONObject) jQuestions.get(i);
                TextView qText = new TextView(this);
                String qStr = jObject.getString("question");
                qText.setText(qStr);
                qText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.font_size_middle));
                qText.setTextColor(getColor(R.color.fontColor));
                lLayout.addView(qText, lpq);
                // ------------------------------------------------------------
                TextView aText = new TextView(this);
                String type = jObject.getString("type");
                if (type.equals("radio") || type.equals("text")) {
                    String tmp = jObject.getJSONObject("answer").getString("1");
                    aText.setText(tmp);
                } else if (type.equals("checkbox")) {
                    String tmp = jObject.getJSONArray("answer").toString();
                    aText.setText(tmp);
                }
                aText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.font_size_average));
                aText.setTextColor(getColor(R.color.fontColor));
                lLayout.addView(aText, lpa);
            }
        } catch (JSONException je) {
            return;
        }
    }
    // check permission and save the answer to json
    public void onClickSave(@NotNull View view) {
        getPermissions();
        saveToDatabase();
        view.setEnabled(false);
    }

    public void saveToDatabase() {
        String time = Calendar.getInstance().getTime().toString();
        Location location = getCurrentLocation();
        String IMEI = getIMEI();
        double latitude = 0;
        double longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        SharedPreferences sp = getSharedPreferences(
                "user_id", MODE_PRIVATE);
        int userId = sp.getInt("id", 0) + 1;
        // AnswerManager Table
        // survey id  / answerTime / locationLat / locationLong / IMEI
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("surveyId", surveyId);
        values.put("answerTime", time);
        values.put("locationLat", latitude);
        values.put("locationLong", longitude);
        values.put("IMEI", IMEI);
        long ok = db.insert("AnswerManager", null, values);
        if (ok > 0) {
            // Answer table : userId, questionType, question, answer
            String answerTableName = "answer" + surveyId;
            String createTable = "create table " + answerTableName +
                    " (userId int, questionType nvarchar(20), " +
                    "question nvarchar(100), answer nvarchar(200));";
            db.execSQL(createTable);
            try {
                JSONArray jArray = new JSONArray(text);
                for (int i = 0; i < length; i++) {
                    values.clear();
                    values.put("userId", userId);
                    JSONObject jObject = jArray.getJSONObject(i);
                    String strType = jObject.getString("type");
                    values.put("questionType", strType);
                    String strQuestion = jObject.getString("question");
                    values.put("question", strQuestion);
                    String strAnswer;
                    if (strType.equals("checkbox")) {
                        strAnswer = jObject.getJSONArray("answer").toString();
                    } else {
                        strAnswer = jObject.getString("answer");
                    }
                    values.put("answer", strAnswer);
                    long rowId = db.insert(answerTableName, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("id", userId);
                editor.commit();
                Toast.makeText(this, R.string.save_ok,
                        Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                db.endTransaction();
                db.close();
                Toast.makeText(this, R.string.save_fail,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            db.endTransaction();
            db.close();
            Toast.makeText(this, R.string.save_fail,
                    Toast.LENGTH_SHORT).show();
        }
    }
    // exit the app
    public void onClickExit(View view) {
        finish();
        MainActivity.mainActivity.finish();
    }
}
