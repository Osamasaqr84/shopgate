package com.example.osamaomar.shopgate.presentationn.screens.feature.home.morefragment;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osamaomar.shopgate.R;
import com.example.osamaomar.shopgate.entities.Currency;
import com.example.osamaomar.shopgate.helper.PreferenceHelper;
import com.example.osamaomar.shopgate.helper.ResourceUtil;
import com.example.osamaomar.shopgate.presentationn.screens.feature.home.mainfragment.MainFragment;
import com.example.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.ProductDetailsModelFactory;
import com.example.osamaomar.shopgate.presentationn.screens.feature.login.LoginFragment;
import com.example.osamaomar.shopgate.presentationn.screens.feature.splash.SplashActivity;
import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private MenuViewModel mViewModel;
    private TextView currency, lang, login,logout;
    private List<Currency.DataBean> dataBeanList = new ArrayList<>();

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    private boolean curremtlang; ///true if arabic

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        mViewModel = ViewModelProviders.of(this, getViewModelFactory()).get(MenuViewModel.class);
        currency = view.findViewById(R.id.currency);
        login = view.findViewById(R.id.login);
        lang = view.findViewById(R.id.lang);
        logout = view.findViewById(R.id.logout);
        currency.setOnClickListener(v -> mViewModel.getCurrencyData());
        login.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.mainfram, new LoginFragment()).addToBackStack(null).commit());

        if (ResourceUtil.getCurrentLanguage(getActivity()).matches("en")) {
            login.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next, 0);
            lang.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next, 0);
            logout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next, 0);
            currency.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next, 0);
        }
        if (PreferenceHelper.getUserId()>0)
            login.setVisibility(View.GONE);
        else
            logout.setVisibility(View.GONE);

        logout.setOnClickListener(v -> {
            PreferenceHelper.setUserId(0);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new MainFragment()).addToBackStack(null).commit();
            Toast.makeText(getActivity(),getText(R.string.youlogout),Toast.LENGTH_SHORT).show();
        });
        mViewModel.currencyMutableLiveData.observe(this, currency1 ->
        {
            dataBeanList = currency1.getData();
            showDialog(dataBeanList);
        });

        if (ResourceUtil.getCurrentLanguage(getActivity()).matches("ar")) {
            lang.setText("الانجليزية");
            curremtlang = true;
        } else {
            lang.setText("Arabic");
            curremtlang = false;
        }

        lang.setOnClickListener(v -> {
            if (curremtlang)
                ResourceUtil.changeLang("en", getActivity());
            else
                ResourceUtil.changeLang("ar", getActivity());

            Intent i = new Intent(getActivity(), SplashActivity.class);
            startActivity(i);
            getActivity().finishAffinity();
        });
        return view;
    }

    private void showDialog(List<Currency.DataBean> dataBeanList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
        for (int i = 0; i < dataBeanList.size(); i++)
            arrayAdapter.add(dataBeanList.get(i).getName());
        builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());
        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            String strName = arrayAdapter.getItem(which);
            AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
            builderInner.setMessage(strName);
            PreferenceHelper.setCURRENCY(dataBeanList.get(which).getName());
            PreferenceHelper.setCURRENCYArabic(dataBeanList.get(which).getName_ar());
            PreferenceHelper.setCURRENCY_VALUE(dataBeanList.get(which).getValue());
            builderInner.setTitle("Your Selected Item is");
            builderInner.setPositiveButton("Ok", (dialog1, which1) -> dialog1.dismiss());
            builderInner.show();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new MainFragment()).addToBackStack(null).commit();

        });
        builderSingle.show();
    }

    private ProductDetailsModelFactory getViewModelFactory() {
        return new ProductDetailsModelFactory(this.getActivity().getApplication());
    }

}
