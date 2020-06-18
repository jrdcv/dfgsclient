package net.drakefamily.googlestepsclient.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.fitness.data.DataPoint;

import net.drakefamily.googlestepsclient.R;
import net.drakefamily.googlestepsclient.adapters.StepsListAdapter;
import net.drakefamily.googlestepsclient.model.StepsData;

import java.util.Collections;
import java.util.List;

/**
 * Created by jrdrake.engineer on 6/14/20
 */
public class StepsListFragment extends Fragment implements StepsData.StepDataListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String TAG = StepsListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "steps_list_fragment_tag";

    private RecyclerView recyclerView;
    private TextView emptyWarning;
    private Switch orderSwitch;
    private List<StepsData.DailyStepsData> stepsDataList;

    // Follow the newInstance() convention although we have no params right now.
    public static StepsListFragment newInstance() {
        StepsListFragment slf = new StepsListFragment();
        return slf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_steps_list);
        emptyWarning = view.findViewById(R.id.textview_empty);
        orderSwitch = view.findViewById(R.id.switch_reverse_sort);
        orderSwitch.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        StepsData.init(getActivity());
        StepsData.getStepData(this);
    }

    @Override
    public void onStepDataRetrieved(List<StepsData.DailyStepsData> list) {
        if (list.size() > 0) {
            stepsDataList = list;
            StepsListAdapter adapter = new StepsListAdapter(stepsDataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            orderSwitch.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            emptyWarning.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // In theory we cannot get here wihtout data, but be sure
        if (recyclerView.getAdapter() != null) {
            if (isChecked) {
                Collections.reverse(stepsDataList);
            } else {
                Collections.sort(stepsDataList);
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
