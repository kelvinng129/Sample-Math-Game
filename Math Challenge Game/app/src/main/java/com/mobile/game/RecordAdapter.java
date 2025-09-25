package com.mobile.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordAdapter extends ArrayAdapter<RecordBean> {

    public RecordAdapter(Context context, List<RecordBean> persons) {
        super(context, 0, persons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RecordBean person = getItem(position);

        // Checks if an existing view is being reused; if not, it inflates a new view from the item_record layout resource.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
        }

        // Lookup view for data population
        //Set the text for each TextView using the data from the RecordBean object.
        TextView pos = convertView.findViewById(R.id.date);
        TextView name = convertView.findViewById(R.id.correct);
        TextView correct = convertView.findViewById(R.id.time);

        pos.setText(person.getDate()+" ,");
        name.setText(person.getCorrect() + " corrects,");
        correct.setText(person.getTime() + " sec");

        // Return the completed view to render on screen
        return convertView;
    }
}
