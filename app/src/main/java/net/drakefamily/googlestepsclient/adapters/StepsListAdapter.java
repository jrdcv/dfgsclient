package net.drakefamily.googlestepsclient.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.drakefamily.googlestepsclient.R;
import net.drakefamily.googlestepsclient.model.StepsData;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jrdrake.engineer on 6/14/20
 */
public class StepsListAdapter  extends RecyclerView.Adapter<StepsListItemHolder> {

    private static final DateFormat df = DateFormat.getDateInstance();

    List<StepsData.DailyStepsData> data = null;

    public StepsListAdapter(List<StepsData.DailyStepsData> list) {
        data = list;
    }

    @NonNull
    @Override
    public StepsListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StepsListItemHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsListItemHolder holder, int position) {
        StepsData.DailyStepsData dsd = data.get(position);
        holder.textViewDate.setText(df.format(new Date(dsd.getEndTimeStamp())));
        holder.textViewCount.setText(String.valueOf(dsd.getStepCount()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

class StepsListItemHolder extends RecyclerView.ViewHolder {

    TextView textViewCount;
    TextView textViewDate;

    public StepsListItemHolder(LayoutInflater inflater, ViewGroup container) {
        super(inflater.inflate(R.layout.layout_day_of_steps, container, false));
        textViewDate = itemView.findViewById(R.id.textview_date);
        textViewCount = itemView.findViewById(R.id.textview_count);
    }
}
