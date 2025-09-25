package com.mobile.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RankAdapter extends ArrayAdapter<RankBean> {

    public RankAdapter(Context context, List<RankBean> persons) {
        super(context, 0, persons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RankBean person = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_rank, parent, false);
        }

        // Lookup view for data population
        TextView pos = convertView.findViewById(R.id.pos);
        TextView name = convertView.findViewById(R.id.name);
        TextView correct = convertView.findViewById(R.id.correct);
        TextView time = convertView.findViewById(R.id.time);

        pos.setText("Rank" + (position + 1) + ",");////Converts the zero-based position to a one-based rank.
        name.setText(person.getName() + ",");
        correct.setText(person.getCorrect() + "corrects,");
        time.setText(person.getTime() + "sec");

        // Return the completed view to render on screen
        return convertView;
    }
}
