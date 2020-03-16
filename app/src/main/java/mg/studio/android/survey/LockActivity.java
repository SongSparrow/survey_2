package mg.studio.android.survey;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LockActivity extends AppCompatActivity {

    private static String password;
    private boolean exitPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent =  getIntent();
        boolean isLock = intent.getBooleanExtra(getString(R.string.is_lock_extra),true);
        if(isLock){
            setContentView(R.layout.activity_lock);
            exitPermission = true;
        }else{
            setContentView(R.layout.activity_unlock);
            exitPermission = false;
        }

    }

    //阻止后退键退出应用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
            if (!exitPermission)
                return false;
        return super.onKeyDown(keyCode, event);
    }

    //使用应用前设置密码
    public void onClickSetPswd(View view) {
        EditText edtPwdFill = findViewById(R.id.fill_pswd);
        EditText edtPwdConfirm = findViewById(R.id.cf_pswd);
        if(edtPwdFill.length() == 0 || edtPwdConfirm.length() == 0){
            Toast.makeText(this, R.string.empty_pswd,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String p1 = edtPwdFill.getText().toString();
        String p2 = edtPwdConfirm.getText().toString();
        if(p1.equals(p2)){
            password = p1;
        }else {
            Toast.makeText(this, R.string.match_pswd,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        exitPermission = false;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //使用密码解锁可退出
    public void onClickUnlock(View view) {
        EditText edtPswdUnlock = findViewById(R.id.unlock_pswd);
        if (edtPswdUnlock.length() == 0) {
            Toast.makeText(this, R.string.empty_pswd,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (edtPswdUnlock.getText().toString().equals(password)) {
            exitPermission = true;
            finish();
        } else {
            Toast.makeText(this,
                    R.string.wrong_pswd,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
