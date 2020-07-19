package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainactivity.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.entities.Sidemenu;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainactivity.MainActivity;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productfragment.ProductsFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.subcategryfragment.SubcategryFragment;

import java.util.List;

import static com.codesroots.osamaomar.shopgate.entities.names.CAT_ID;
import static com.codesroots.osamaomar.shopgate.entities.names.CAT_NAME;
import static com.codesroots.osamaomar.shopgate.entities.names.CAT_TYPE;
import static com.codesroots.osamaomar.shopgate.entities.names.SUBCATES_NAME;
import static com.codesroots.osamaomar.shopgate.entities.names.SUB_CAT_ID;

public class AllDepartsAdapter extends RecyclerView.Adapter<AllDepartsAdapter.ViewHolder> {

    private Context context;
    private List<Sidemenu.CategoryBean> categories;

    public AllDepartsAdapter(Context mcontext, List<Sidemenu.CategoryBean> category) {
        context = mcontext;
        categories = category;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.depart_item_in_navigation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.subDeparts.setAdapter(new AllSubDepartsAdapter(context, categories.get(position).getSubcats()));

        if (categories.get(position).getSubcats().size() == 0)
            holder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

        holder.text.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(CAT_ID, categories.get(position).getId());
            bundle.putString(CAT_NAME, categories.get(position).getName());
            bundle.putInt(CAT_TYPE, 0);
            Fragment fragment = new SubcategryFragment();
            fragment.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, fragment).addToBackStack(null).commit();
            ((MainActivity) context).drawer.closeDrawer(GravityCompat.START);
        });

        if (categories.get(position).getSubcats().size() > 0)
            holder.depart_icon.setImageResource(R.drawable.plus);
        else
            holder.depart_icon.setImageResource(R.drawable.minus);


        holder.depart_icon.setOnClickListener(v -> {
            if (categories.get(position).getSubcats().size() > 0) {
                if (holder.subDeparts.getVisibility() == View.VISIBLE) {
                    holder.subDeparts.setVisibility(View.GONE);
                    holder.depart_icon.setImageResource(R.drawable.plus);
                    //   holder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.plus, 0);
                } else {
                    holder.subDeparts.setVisibility(View.VISIBLE);
                    holder.depart_icon.setImageResource(R.drawable.minus);

                    // holder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.minus, 0);
                }
            }
        });


        holder.text.setText(categories.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        private TextView text;
        private RecyclerView subDeparts;
        private ImageView depart_icon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            subDeparts = mView.findViewById(R.id.subdeparts);
            text = mView.findViewById(R.id.name);
            depart_icon = mView.findViewById(R.id.depart_icon);
        }
    }
}