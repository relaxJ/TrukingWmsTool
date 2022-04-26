package com.truking.wms.tool.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.truking.wms.tool.R;
import com.truking.wms.tool.activities.BocibiaoqianDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ShipmentdetailAdapter extends RecyclerView.Adapter<ShipmentdetailAdapter.ViewHolder> {
    private Context context;
    private JSONArray array;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemCode;
        TextView itemName;
        TextView attribute1;
        TextView fromQty;
        View clickArea;
        public ViewHolder(View view) {
            super(view);
            itemCode = view.findViewById(R.id.itemCode);
            itemName = view.findViewById(R.id.itemName);
            attribute1 = view.findViewById(R.id.attribute1);
            fromQty = view.findViewById(R.id.fromQty);
            clickArea = view.findViewById(R.id.shipmentdetail_item);
        }
    }

    public ShipmentdetailAdapter(Context context,
                                 JSONArray array) {
        this.array = array;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_shipmentdetail_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final JSONObject obj;
        try {
            obj = (JSONObject) array.get(position);
            holder.itemCode.setText(obj.optString("itemCode"));
            holder.itemName.setText(obj.optString("itemName"));
            holder.attribute1.setText(obj.optString("attribute1"));
            holder.fromQty.setText(obj.optString("fromQty"));
            holder.clickArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(context, BocibiaoqianDetailActivity.class);
                    intent.putExtra("obj",  obj.toString());
                    context.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return array.length();
    }

}
