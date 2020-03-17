package mg.studio.android.survey;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class FloatingService extends Service {
    private WindowManager wManager;
    private WindowManager.LayoutParams lParams;
    private View view;

    public FloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLockView();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startLockView(){
        if (ReportActivity.bStart) return;
        wManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        lParams = new WindowManager.LayoutParams();
        lParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        lParams.format = PixelFormat.RGBA_8888;
        lParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        lParams.x = 0;
        lParams.y = 0;
        lParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        view = LayoutInflater.from(this).inflate(R.layout.exit_lock, null);
        wManager.addView(view, lParams);
        ReportActivity.bStart = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ReportActivity.bStart = false;
        wManager.removeView(view);
    }
}
