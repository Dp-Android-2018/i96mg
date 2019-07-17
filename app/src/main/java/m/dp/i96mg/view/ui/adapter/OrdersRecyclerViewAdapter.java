package m.dp.i96mg.view.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemPendingOrderBinding;
import m.dp.i96mg.service.model.global.OrderResponseModel;
import m.dp.i96mg.view.ui.viewholder.OrdersViewHolder;


public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersViewHolder> {
    private ArrayList<OrderResponseModel> orderResponseModels;
    private String memberType;

    public OrdersRecyclerViewAdapter(ArrayList<OrderResponseModel> orderResponseModels, String memberType) {
        this.orderResponseModels = orderResponseModels;
        this.memberType=memberType;
    }


    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPendingOrderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_pending_order, parent, false);
        return new OrdersViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        holder.bindClass(orderResponseModels.get(position),memberType);
    }

    @Override
    public int getItemCount() {
        if (orderResponseModels != null) {
            return orderResponseModels.size();
        } else {
            return 0;
        }
    }
}
