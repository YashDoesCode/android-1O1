package com.keeper.components.backend;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

public class JobSchedulerFallback extends JobService {

    private static final int JOB_ID = 8801;

    @Override
    public boolean onStartJob(JobParameters params) {
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static void scheduleLowCostJob(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            ComponentName component = new ComponentName(context, JobSchedulerFallback.class);
            JobInfo info = new JobInfo.Builder(JOB_ID, component)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(true)
                    .build();
            scheduler.schedule(info);
        }
    }
}
