package com.example.isreferee.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.isreferee.R;
import com.example.isreferee.model.Record;

import java.util.ArrayList;
import java.util.List;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {

    private final LayoutInflater mInflater;
    private List<Record> Records;
    private List<String> RecordsForHeat = new ArrayList<>();

    RecordListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new RecordViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        //if ((Integer) position ==null ) position = 1;
        Log.d("TESTMVVM", "POSITION Record  " + position);
        if (Records != null) {

            Record current = Records.get(position);
            int heat = current.getHeatNumber();
            holder.HeatNumber.setText(Integer.toString(current.getHeatNumber()));
            holder.LaneNumber.setText(current.getLaneNumber());
            holder.Record.setText(current.getRecord());
//            switch (current.getLaneNumber()){
//                case "1":
//                    RecordsForHeat.add(current.getHeatNumber()*4 - 3, current.getRecord());
//                    break;
//                case "2":
//                    RecordsForHeat.add(current.getHeatNumber()*4 - 2, current.getRecord());
//                    break;
//                case "3":
//                    RecordsForHeat.add(current.getHeatNumber()*4 - 1, current.getRecord());
//                    break;
//                case "4":
//                    RecordsForHeat.add(current.getHeatNumber()*4, current.getRecord());
//                    break;
//            }
//            if (heat > 1){
//                holder.heatN.setText(Integer.toString(current.getHeatNumber()-1));
//                holder.pastLane1.setText(RecordsForHeat.get(current.getHeatNumber()-1 *4-3));
//                holder.pastLane2.setText(RecordsForHeat.get(current.getHeatNumber()-1 *4-2));
//                holder.pastLane3.setText(RecordsForHeat.get(current.getHeatNumber()-1 *4-1));
//                holder.pastLane4.setText(RecordsForHeat.get(current.getHeatNumber()-1 *4));
//            }

        } else {
            // Covers the case of data not being ready yet.
            holder.Record.setText("00");
        }
    }

    void setRecords(List<Record> records){
//        Log.d("TESTMVVM", "Adapter Record  " +records.get(0).HeatNum);
        Records = records;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (Records != null)
            return Records.size();
        else return 0;
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView HeatNumber;
        private final TextView LaneNumber;
        private final TextView Record;

        private final TextView pastLane1;
        private final TextView pastLane2;
        private final TextView pastLane3;
        private final TextView pastLane4;
        private final TextView heatN;


        private RecordViewHolder(View itemView) {
            super(itemView);
            HeatNumber = itemView.findViewById(R.id.HeatNumberLbl);
            LaneNumber = itemView.findViewById(R.id.LaneNumberLbl);
            Record = itemView.findViewById(R.id.LaneRecord);

            pastLane1 = itemView.findViewById(R.id.lane1recordsshow);
            pastLane2 = itemView.findViewById(R.id.lane2recordsshow);
            pastLane3 = itemView.findViewById(R.id.lane3recordsshow);
            pastLane4 = itemView.findViewById(R.id.lane4recordsshow);
            heatN = itemView.findViewById(R.id.heatN);


        }
    }
}