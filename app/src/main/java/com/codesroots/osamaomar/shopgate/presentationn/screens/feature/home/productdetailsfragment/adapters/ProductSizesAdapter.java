package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.entities.ProductDetails;
import com.codesroots.osamaomar.shopgate.entities.StoreSetting;
import com.codesroots.osamaomar.shopgate.helper.PreferenceHelper;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.ProductDetailsFragment;

import java.util.List;

public class ProductSizesAdapter extends RecyclerView.Adapter<ProductSizesAdapter.ViewHolder> {

    private Context context;
    ProductDetailsFragment fragment;
    List<ProductDetails.ProductdetailsBean.ProductsizesBean> productsizes;
    public int mSelectedItem = 0;
    public float priceafteroffer = 0;


    private int offer;
    private StoreSetting setting;

    public ProductSizesAdapter(Context mcontext, List<ProductDetails.ProductdetailsBean.ProductsizesBean> sizes,
                               ProductDetailsFragment detailsFragment, int offer1) {
        context = mcontext;
        productsizes = sizes;
        fragment = detailsFragment;
        offer = offer1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.size_item_adapter, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.text.setText(productsizes.get(position).getSize());
        fragment.amount.setText(context.getText(R.string.remendier) + " " + String.valueOf(productsizes.get(mSelectedItem).getAmount()) + " " + context.getText(R.string.num));

        if (position == mSelectedItem)
            holder.text.setBackgroundResource(R.drawable.linear_background_for_selected_size);
        else
            holder.text.setBackgroundResource(R.drawable.linear_background_for_size);
    }

    @Override
    public int getItemCount() {
        return productsizes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        private TextView text;

        ViewHolder(View view) {
            super(view);
            mView = view;
            text = mView.findViewById(R.id.size);

            @SuppressLint("SetTextI18n") View.OnClickListener clickListener = v -> {
                mSelectedItem = getAdapterPosition();
                notifyDataSetChanged();
                if (offer > 0) {
                    if (PreferenceHelper.getCurrencyValue() > 0) {
                        fragment.oldprice.setText(productsizes.get(mSelectedItem).getStart_price()+ " " + PreferenceHelper.getCurrency());
                        fragment.price.setText(productsizes.get(mSelectedItem).getNew_price() + " " + PreferenceHelper.getCurrency());
                    }
                    else {
                        fragment.oldprice.setText(productsizes.get(mSelectedItem).getStart_price() + " " + context.getText(R.string.realcoin));
                        fragment.price.setText(productsizes.get(mSelectedItem).getNew_price() + " " + context.getText(R.string.realcoin));
                    }
                } else {

                    if (PreferenceHelper.getCurrencyValue() > 0)
                        fragment.price.setText(productsizes.get(mSelectedItem).getStart_price()+" "+PreferenceHelper.getCurrency());
                    else
                        fragment.price.setText(productsizes.get(mSelectedItem).getStart_price() + context.getText(R.string.realcoin));

//                    if (Float.valueOf(productsizes.get(mSelectedItem).getStart_price()) < fragment.setting.getData().get(0).getShippingPrice()) {
//                        fragment.charege.setText(R.string.charge_rules);
//                        fragment.freecharg = false;
//                    } else
//                        fragment.charege.setText(R.string.free_charge);
                }

                fragment.amount.setText(context.getText(R.string.remendier) + " " + String.valueOf(productsizes.get(mSelectedItem).getAmount()) +
                        " " + context.getText(R.string.num));
            };
            itemView.setOnClickListener(clickListener);
        }
    }
}