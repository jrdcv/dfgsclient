package net.drakefamily.googlestepsclient.model;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;

import net.drakefamily.googlestepsclient.views.MainStepsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jrdrake.engineer on 6/14/2020
 */
public class StepsData {

    private static final String TAG = StepsData.class.getSimpleName();

    private static StepsData INSTANCE = null;

    private Activity activity;
    private GoogleSignInAccount googleSignInAccount;

    public static void init(Activity activity) {

        if (INSTANCE == null) {
            INSTANCE = new StepsData();
            INSTANCE.activity = activity;

            // Confirm that we are signed in so we can retrieve information
            GoogleSignInOptionsExtension fitnessOptions =
                    FitnessOptions.builder()
                            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                            .build();

            INSTANCE.googleSignInAccount = GoogleSignIn.getAccountForExtension(INSTANCE.activity,
                    fitnessOptions);

            if (!GoogleSignIn.hasPermissions(INSTANCE.googleSignInAccount, fitnessOptions)) {
                Log.d(TAG, "not granted, request");
                GoogleSignIn.requestPermissions(
                        INSTANCE.activity,
                        MainStepsActivity.GOOGLE_FIT_SIGN_IN_RESULT,
                        INSTANCE.googleSignInAccount,
                        fitnessOptions);
            }
        }
    }

    public static void getStepData(final StepDataListener listener) {

        // Ah, the wonders of the internet. For the moment, use the google sample code
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -2);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .enableServerQueries()
                .build();

        Fitness.getHistoryClient(INSTANCE.activity, INSTANCE.googleSignInAccount)
                .readData(readRequest)
                .addOnSuccessListener(response -> {
                    List<DailyStepsData> dailyData = new ArrayList<>();

                    for (Bucket b : response.getBuckets()) {
                        List<DataSet> dataSets = b.getDataSets();
                        for (DataSet ds : dataSets) {
                            for (DataPoint dp : ds.getDataPoints()) {
                                for (Field field : dp.getDataType().getFields()) {
                                    if (field.getName().equals(Field.FIELD_STEPS.getName())) {
                                        long dayEndTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                                        int stepValue = dp.getValue(field).asInt();
                                        dailyData.add(new DailyStepsData(dayEndTime, stepValue));
                                    }
                                }
                            }
                        }
                        if (listener != null) {
                            listener.onStepDataRetrieved(dailyData);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "failed to retrieve step count:  " + e.getMessage());
                });
    }

    public static class DailyStepsData implements Comparable<DailyStepsData> {
        long endTimeStamp;
        int stepCount;

        public DailyStepsData(long endTimeStamp, int stepCount) {
            this.endTimeStamp = endTimeStamp;
            this.stepCount = stepCount;
        }

        @Override
        public int compareTo(DailyStepsData o) {
            return endTimeStamp > o.endTimeStamp ? 1 : -1;
        }

        public long getEndTimeStamp() {
            return endTimeStamp;
        }

        public int getStepCount() {
            return stepCount;
        }
    }

    public interface StepDataListener {
        public void onStepDataRetrieved(List<DailyStepsData> list);
    }

}
