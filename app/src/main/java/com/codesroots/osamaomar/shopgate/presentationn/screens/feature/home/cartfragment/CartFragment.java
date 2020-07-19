package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.cartfragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.entities.CartItems;
import com.codesroots.osamaomar.shopgate.entities.OrderModel;
import com.codesroots.osamaomar.shopgate.helper.AddorRemoveCallbacks;
import com.codesroots.osamaomar.shopgate.helper.EditCallbacks;
import com.codesroots.osamaomar.shopgate.helper.PreferenceHelper;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.getuserlocation.GetUserLocationActivity;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.cartfragment.adapter.CartAdapter;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainactivity.MainActivity;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.payment.PaymentFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.codesroots.osamaomar.shopgate.entities.names.FULL_ADDRESS;
import static com.codesroots.osamaomar.shopgate.entities.names.ORDER;
import static com.codesroots.osamaomar.shopgate.entities.names.USER_LANG;
import static com.codesroots.osamaomar.shopgate.entities.names.USER_LAT;

public class CartFragment extends Fragment implements EditCallbacks {

    private static final int REQUEST_CODE_LOCATION = 117;
    RecyclerView cartsRecycle;
    private TextView sale, totalvalue, charge, alltotalvalue;
    private CartAdapter cartAdapter;
    private FrameLayout progress;
    private ArrayList product_ids = new ArrayList<>();
    OrderModel orderModel = new OrderModel();
    List<CartItems.DataBean> products = new ArrayList<>();
    double tot_price = 0, chargrvalue = 0;
    public List<OrderModel.productSize> saledProducts = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        initialize(view);
        product_ids = PreferenceHelper.retriveCartItemsValue();
        CartViewModel mViewModel = ViewModelProviders.of(this, getViewModelFactory()).get(CartViewModel.class);

     //   if (tot_price < PreferenceHelper.getMIM_CHIPPING()) {
            if (PreferenceHelper.getCOUNTRY_ID() == 1) {
                charge.setText(PreferenceHelper.getIN_OMAN() + " " + getString(R.string.coin));
                chargrvalue = PreferenceHelper.getIN_OMAN();
            } else {
                charge.setText(PreferenceHelper.getOUT_OMAN() + " " + getString(R.string.coin));
                chargrvalue = PreferenceHelper.getOUT_OMAN();
            }
       // }

        mViewModel.cartItemsMutableLiveData.observe(this, dataBeans -> {
            {
                try {
                    progress.setVisibility(View.GONE);
                    products = dataBeans;
                    cartAdapter = new CartAdapter(getActivity(), products, this);
                    cartsRecycle.setAdapter(cartAdapter);
                    tot_price = 0;
                    for (int i = 0; i < dataBeans.size(); i++) {
                        if (products.get(i).getNew_price() > 0)
                            tot_price += products.get(i).getNew_price();
                        else
                            tot_price += products.get(i).getStart_price();
                    }
                    if (PreferenceHelper.getCurrencyValue() > 0)
                        totalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price), PreferenceHelper.getCurrency()));
                    else
                        totalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price), getString(R.string.coin)));

                    checkDeliveryPrice();

                } catch (Exception c) {
                    Log.d("Ecc", c.toString());
                }
            }
        });

        mViewModel.noItemsFound.observe(this, aBoolean -> {
            sale.setEnabled(false);
            progress.setVisibility(View.GONE);
            Snackbar.make(view, getText(R.string.noitemsincart), Snackbar.LENGTH_SHORT).show();
        });

        mViewModel.throwableMutableLiveData.observe(this, throwable ->
        {
            progress.setVisibility(View.GONE);
            Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
        });

        sale.setOnClickListener(v -> {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getNew_price() > 0)
                    saledProducts.add(new OrderModel.productSize(products.get(i).getProduct_id(), products.get(i).getUserAmount(),
                            String.valueOf(products.get(i).getNew_price()), "خصم بسبب العرض رقم " + products.get(i).getProduct().getOffers().get(0).getId()
                    ));
                else
                    saledProducts.add(new OrderModel.productSize(products.get(i).getProduct_id(), products.get(i).getUserAmount(),
                            String.valueOf(products.get(i).getStart_price()), " "
                    ));

            }
            orderModel.setOrderdetails(saledProducts);
            Intent intent = new Intent(getActivity(), GetUserLocationActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOCATION);
        });
        return view;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == REQUEST_CODE_LOCATION) {
            if (resultCode == RESULT_OK) {
                Fragment fragment = new PaymentFragment();
                Bundle bundle = new Bundle();
                orderModel.setAddress(data.getExtras().getString(FULL_ADDRESS));
                orderModel.setUser_lat(data.getExtras().getString(USER_LAT));
                orderModel.setUser_long(data.getExtras().getString(USER_LANG));
                bundle.putSerializable(ORDER, orderModel);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, fragment).addToBackStack(null).commit();
            } else {
                Toast.makeText(getActivity(), "You haven't selected address", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initialize(View view) {
        cartsRecycle = view.findViewById(R.id.cart_Rec);
        sale = view.findViewById(R.id.sale);
        totalvalue = view.findViewById(R.id.totalvalue);
        alltotalvalue = view.findViewById(R.id.alltotalvalue);
        charge = view.findViewById(R.id.chargevalue);
        progress = view.findViewById(R.id.progress);
        ((MainActivity) Objects.requireNonNull(getActivity())).head_title.setText(getText(R.string.cart));
        ((MainActivity) getActivity()).logo.setVisibility(View.INVISIBLE);
    }

    private ViewModelProvider.Factory getViewModelFactory() {
        return new CartsViewModelFactory(getActivity().getApplication(), product_ids);
    }


    @Override
    public void onPlusProduct(int index) {
        if (products.get(index).getUserAmount() + 1 <= products.get(index).getAmount()) {
            int newAmount = products.get(index).getUserAmount() + 1;
            products.get(index).setUserAmount(products.get(index).getUserAmount() + 1);
            if (products.get(index).getNew_price() > 0)
                tot_price += products.get(index).getNew_price();
            else
                tot_price += products.get(index).getStart_price();

            if (PreferenceHelper.getCurrencyValue() > 0)
                totalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price), PreferenceHelper.getCurrency()));
            else
                totalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price), getString(R.string.coin)));
            cartAdapter.notifyItemChanged(index);
            checkDeliveryPrice();
        } else
            Toast.makeText(getContext(), getContext().getText(R.string.requestnotallow), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRemoveProduct(int index, int count, float itemprice) {

        for (int i = 0; i < products.size(); i++) {
            if (i == index) {
                if (products.get(i).getNew_price() > 0)
                    tot_price -= products.get(i).getNew_price() * count;
                else
                    tot_price -= products.get(i).getStart_price() * count;

            }
        }
        products.remove(index);
        cartAdapter.notifyDataSetChanged();
        if (PreferenceHelper.getCurrencyValue() > 0)
            totalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price), PreferenceHelper.getCurrency()));
        else
            totalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price), getString(R.string.coin)));
        cartAdapter.notifyItemChanged(index);
        checkDeliveryPrice();
        ((AddorRemoveCallbacks) getActivity()).onRemoveProduct();
    }

    @Override
    public void onMinusProduct(int index) {

        if ((products.get(index).getUserAmount() - 1) > 0) {
            products.get(index).setUserAmount(products.get(index).getUserAmount() - 1);
            if (products.get(index).getNew_price() > 0)
                tot_price -= products.get(index).getNew_price();
            else
                tot_price -= products.get(index).getStart_price();

            if (PreferenceHelper.getCurrencyValue() > 0)
                totalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price), PreferenceHelper.getCurrency()));
            else
                totalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price), getString(R.string.coin)));

            cartAdapter.notifyItemChanged(index);
            checkDeliveryPrice();
        }

    }

    private void checkDeliveryPrice() {
        if (PreferenceHelper.getCOUNTRY_ID() == 1) {
            if (PreferenceHelper.getCurrencyValue() > 0) {
                charge.setText(String.format("%s %s", PreferenceHelper.getIN_OMAN() * PreferenceHelper.getCurrencyValue(), PreferenceHelper.getCurrency()));
                alltotalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price + PreferenceHelper.getIN_OMAN() * PreferenceHelper.getCurrencyValue()), PreferenceHelper.getCurrency()));
            } else {
                charge.setText(String.format("%s %s", PreferenceHelper.getIN_OMAN(), getString(R.string.coin)));
                alltotalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price + PreferenceHelper.getIN_OMAN()), getString(R.string.coin)));
            }

        } else {
            if (PreferenceHelper.getCurrencyValue() > 0) {
                charge.setText(String.format("%s %s", PreferenceHelper.getOUT_OMAN() * PreferenceHelper.getCurrencyValue(), PreferenceHelper.getCurrency()));
                alltotalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price + PreferenceHelper.getOUT_OMAN() * PreferenceHelper.getCurrencyValue()), PreferenceHelper.getCurrency()));

            } else {
                alltotalvalue.setText(String.format("%s %s", new DecimalFormat("##.##").format(tot_price + PreferenceHelper.getOUT_OMAN()), getString(R.string.coin)));
                charge.setText(String.format("%s %s", PreferenceHelper.getOUT_OMAN(), getString(R.string.coin)));
            }
        }

    }
}
