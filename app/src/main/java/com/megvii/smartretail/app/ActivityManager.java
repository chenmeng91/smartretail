package com.megvii.smartretail.app;

import android.app.Activity;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Build;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ActivityManager {

    private static ArrayList<WeakReference<Activity>> activityTask = new ArrayList<WeakReference<Activity>>();// 维护当前程序的一个堆栈
    private static ActivityManager INSTANCE = new ActivityManager();

    public static ActivityManager getInstance() {
        return INSTANCE;
    }

    /**
     * 关闭自己维护的activityTask中的全部activity
     */
    public void finishAllActivity() {
        final int size = activityTask.size();
        for (int i = (size - 1); i > -1; i--) {
            WeakReference<Activity> activityWeakRef = activityTask.get(i);
            Activity activity = activityWeakRef.get();
            if (activity != null) {
                activity.finish();
            }
        }
        activityTask.clear();
    }

    /**
     * 
     * @param activity
     * @description 将activity从自己维护的activityTask中出栈
     */
    public void popActivity(Activity activity) {

        if (activity == null) {
            return;
        }

        /**
         * 如果页面不是处于关闭过程中，则不从堆栈中弹出
         */
        if (!activity.isFinishing()) {
            return;
        }

        final int size = activityTask.size();
        for (int i = (size - 1); i > -1; i--) {
            WeakReference<Activity> activityWeakRef = activityTask.get(i);
            Activity refActivity = activityWeakRef.get();
            if (refActivity != null && refActivity == activity) {
                activityTask.remove(i);
                break;
            }
        }
    }

    /**
     * 
     * @param activity
     * @description 将activity入栈到自己维护的activityTask中
     */
    public void pushActivity(Activity activity) {
        /**
         * 清除堆栈中存在的空引用，防止因为系统回收导致的空引用泛滥 从栈底开始，一直清除到第一个不为空的弱引用
         */
        for (int i = 0; i < activityTask.size(); i++) {
            WeakReference<Activity> activityWeakRef = activityTask.get(i);
            Activity refActivity = activityWeakRef.get();
            if (refActivity != null) {
                break;
            }
            activityTask.remove(i);
            i--;
        }

        if (activity != null) {
            activityTask.add(new WeakReference<Activity>(activity));
        }
    }

    /**
     * 判断是否在后台运行
     * 
     * @return
     */
    public boolean isUIShown() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            android.app.ActivityManager activityManager =
                    (android.app.ActivityManager) (SmartretailApplication.getInstance()
                            .getSystemService(Context.ACTIVITY_SERVICE));
            String packageName = SmartretailApplication.getInstance().getPackageName();
            if (activityManager == null) {
                return false;
            }

            List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
            if (tasksInfo == null || tasksInfo.size() < 1) {
                return false;
            }

            // 应用程序位于堆栈的顶层
            if (!packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return false;
            }

            return true;
        }

        return getTopActivity() != null;
    }

    /**
     * 
     * @return
     * @description 获取栈顶Activity
     */
    public Activity getTopActivity() {
        Activity result = null;

        final int size = activityTask.size();
        for (int i = (size - 1); i > -1; i--) {
            WeakReference<Activity> activityWeakRef = activityTask.get(i);
            Activity refActivity = activityWeakRef.get();
            if (refActivity != null) {
                result = refActivity;
                break;
            }
        }
        return result;
    }
}
