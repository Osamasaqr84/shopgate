package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.cartfragment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.entities.CartItems;
import com.codesroots.osamaomar.shopgate.entities.OrderModel;
import com.codesroots.osamaomar.shopgate.helper.PreferenceHelper;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.cartfragment.CartFragment;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    List<CartItems.DataBean> dataBeans;
    private CartFragment cartFragment;
    List<OrderModel.productSize> products = new ArrayList<>();

    public CartAdapter(Context mcontext, List<CartItems.DataBean> dataBeans1, CartFragment cartFragment1) {
        context = mcontext;
        dataBeans = dataBeans1;
        cartFragment = cartFragment1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_adapter, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {

            holder.name.setText(dataBeans.get(position).getProduct().getName());
            holder.amount.setText(context.getText(R.string.remendier) + " " + dataBeans.get(position).getAmount() + " " + context.getText(R.string.num));
            holder.products_count.setText(dataBeans.get(position).getUserAmount() + "");
            products.add(new OrderModel.productSize(dataBeans.get(position).getId()));


            if (dataBeans.get(position).getNew_price() > 0) {
                if (PreferenceHelper.getCurrencyValue() > 0)
                    holder.price.setText(dataBeans.get(position).getNew_price() + PreferenceHelper.getCurrency());
                else
                    holder.price.setText(dataBeans.get(position).getNew_price() + " " + context.getText(R.string.coin));

            } else {
                if (PreferenceHelper.getCurrencyValue() > 0)
                    holder.price.setText(dataBeans.get(position).getStart_price() + PreferenceHelper.getCurrency());
                else
                    holder.price.setText(dataBeans.get(position).getStart_price() + " " + context.getText(R.string.coin));
            }


            if (dataBeans.get(position).getProduct().getTotal_rating() != null)
                if (dataBeans.get(position).getProduct().getTotal_rating().size() > 0) {
                    holder.ratingBar.setRating(dataBeans.get(position).getProduct().getTotal_rating().get(0).getStars() /
                            dataBeans.get(position).getProduct().getTotal_rating().get(0).getCount());
                    holder.rateCount.setText("(" + dataBeans.get(position).getProduct().getTotal_rating().get(0).getCount() + ")");
                }
            if (dataBeans.get(position).getProduct().getProductphotos() != null) {
                if (dataBeans.get(position).getProduct().getProductphotos().size() > 0)
                    Glide.with(context.getApplicationContext())
                            .load(dataBeans.get(position).getProduct().getProductphotos().get(0).getPhoto()).placeholder(R.drawable.product).dontAnimate()
                            .into(holder.Image);
            }
        } catch (Exception e) {
            Log.d("exception", e.getMessage());
        }

        holder.delete_item.setOnClickListener(v -> {
            PreferenceHelper.removeItemFromCart(dataBeans.get(position).getId());
            //dataBeans.remove(position);
            cartFragment.onRemoveProduct(position, Integer.parseInt(holder.products_count.getText().toString()), 1f);
            //  notifyItemRemoved(position);
        });

        holder.quintityPlus.setOnClickListener(v ->
        {
            cartFragment.onPlusProduct(position);
        });

        holder.quintityMinus.setOnClickListener(v -> {
            cartFragment.onMinusProduct(position);
        });
    }

    @Override
    public int getItemCount() {
        return dataBeans.size();
    }

    private float getPriceAfterDiscount(String Percentage, Float oldPrice) {
        float offer = oldPrice * Integer.valueOf(Percentage) / 100;
        return oldPrice - offer;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        private ImageView Image, favorite, delete_item, quintityMinus, quintityPlus;
        private TextView name, rateCount, amount, price;
        private RatingBar ratingBar;
        private EditText products_count;

        ViewHolder(View view) {
            super(view);
            mView = view;
            Image = mView.findViewById(R.id.item_img);
            name = mView.findViewById(R.id.item_name);
            price = mView.findViewById(R.id.item_price);
            amount = mView.findViewById(R.id.quentity);
            rateCount = mView.findViewById(R.id.rate_count);
            ratingBar = mView.findViewById(R.id.rates);
            favorite = mView.findViewById(R.id.favorite);
            delete_item = mView.findViewById(R.id.delete_item);
            quintityMinus = mView.findViewById(R.id.quintityMinus);
            quintityPlus = mView.findViewById(R.id.quintityPlus);
            products_count = mView.findViewById(R.id.quintity_value);
        }
    }
}