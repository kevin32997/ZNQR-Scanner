package gov.zambonortedev.znqrscanner.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import gov.zambonortedev.znqrscanner.R;
import gov.zambonortedev.znqrscanner.models.RationHistory;

public class ListViewRCAdapter extends ArrayAdapter<RationHistory> {
    public ListViewRCAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    private List<RationHistory> rationHistories;
    private Context context;
    private Handler mainHandler;


    public ListViewRCAdapter(@NonNull Context context, List<RationHistory> rationHistories) {
        super(context, R.layout.listview_row_layout2, R.id.item_date_receive, rationHistories);
        this.rationHistories = rationHistories;
        this.context = context;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setList(List<RationHistory> list) {
        rationHistories.clear();
        rationHistories.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        rationHistories.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final RationHistory rationHistory = rationHistories.get(position);
        // Get image online
        return inflater.inflate(R.layout.listview_row_layout, parent, false);
    }
}
