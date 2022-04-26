package com.truking.wms.tool.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.truking.wms.tool.R;
import com.truking.wms.tool.activities.BocibiaoqianActivity;
import com.truking.wms.tool.activities.BocibiaoqianDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WarehousedetailAdapter extends RecyclerView.Adapter<WarehousedetailAdapter.ViewHolder> {
    private Context context;
    private JSONArray array;
    private String taskCode;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView warehouseCode;
        TextView ck;
        View clickArea;
        public ViewHolder(View view) {
            super(view);
            warehouseCode = view.findViewById(R.id.tv_warehouseCode);
            ck = view.findViewById(R.id.tv_ck);
            clickArea = view.findViewById(R.id.warehouse_item);
        }
    }

    public WarehousedetailAdapter(Context context,
                                  JSONArray array,String taskCode) {
        this.array = array;
        this.taskCode = taskCode;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cks_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final JSONObject obj;
        try {
            obj = (JSONObject) array.get(position);
            holder.warehouseCode.setText(obj.optString("warehouseCode"));
            holder.ck.setText(obj.optString("ck"));
            holder.clickArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(context, BocibiaoqianActivity.class);
                    intent.putExtra("taskCode",  taskCode);
                    intent.putExtra("warehouseCode",  obj.optString("warehouseCode"));
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
