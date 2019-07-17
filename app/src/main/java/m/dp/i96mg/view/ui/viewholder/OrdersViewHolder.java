package m.dp.i96mg.view.ui.viewholder;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import kotlin.Lazy;
import m.dp.i96mg.databinding.ItemPendingOrderBinding;
import m.dp.i96mg.service.model.global.OrderResponseModel;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.view.ui.activity.PayCardActivity;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class OrdersViewHolder extends RecyclerView.ViewHolder {
    ItemPendingOrderBinding binding;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private OrderResponseModel orderResponseModel;
    private String memberType;

    public OrdersViewHolder(ItemPendingOrderBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;

    }

    public void bindClass(OrderResponseModel orderResponseModel, String memberType) {
        this.orderResponseModel = orderResponseModel;
        this.memberType=memberType;
        if (memberType.equals(ConfigurationFile.Constants.PENDING_ORDERS_ACTIVITY)){
            binding.tvOrderNumber.setText(orderResponseModel.getOrderNo());
            binding.tvTimeLeft.setText(orderResponseModel.getTimeLeft());
            binding.tvTotalPrice.setText(String.valueOf(orderResponseModel.getTotalPrice()));
        }else {
            binding.tvOrderNumber.setText(orderResponseModel.getOrderNo());
            binding.tvTotalPrice.setText(String.valueOf(orderResponseModel.getTotalPrice()));
            binding.textViewTime.setText(orderResponseModel.getOrderStatus());
            binding.textView4.setVisibility(View.INVISIBLE);
        }
        makeActionOnClickOnItem();
    }

    private void makeActionOnClickOnItem() {
        binding.getRoot().setOnClickListener(view -> {
            if (memberType.equals(ConfigurationFile.Constants.PENDING_ORDERS_ACTIVITY)){
                Intent intent = new Intent(view.getContext(), PayCardActivity.class);
                intent.putExtra(ConfigurationFile.Constants.ORDER_ID, orderResponseModel.getId());
                view.getContext().startActivity(intent);
            }
        });
    }


}
