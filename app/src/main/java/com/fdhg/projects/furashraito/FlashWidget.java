package com.fdhg.projects.furashraito;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

public class FlashWidget extends AppWidgetProvider {

    private static boolean isFlashOn = false;
    private static Camera camera;

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction() == null) {
            context.startService(new Intent(context, FlashService.class));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, FlashService.class));
    }

    public static class FlashService extends IntentService {
        public FlashService() {
            super(FlashService.class.getName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            ComponentName me = new ComponentName(this, FlashWidget.class);
            AppWidgetManager mgr = AppWidgetManager.getInstance(this);
            mgr.updateAppWidget(me, buildUpdate(this));
        }

        private RemoteViews buildUpdate(Context context) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget);

            if (isFlashOn)
                view.setImageViewResource(R.id.ivWidget, R.drawable.ic_launcher_off);
            else
                view.setImageViewResource(R.id.ivWidget, R.drawable.ic_launcher);

            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            mgr.updateAppWidget(new ComponentName(context, FlashWidget.class), view);

            if (isFlashOn) {
                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    isFlashOn = false;
                }
            } else {
                camera = Camera.open();
                if (camera != null) {
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview();
                    isFlashOn = true;
                }
            }

            Intent i = new Intent(this, FlashWidget.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
            view.setOnClickPendingIntent(R.id.ivWidget, pi);

            return view;
        }
    }
}