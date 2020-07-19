package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.cartfragment;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.codesroots.osamaomar.shopgate.domain.ServerGateway;
import com.codesroots.osamaomar.shopgate.entities.CartItems;
import com.codesroots.osamaomar.shopgate.entities.ProductDetails.ProductdetailsBean.OffersBean;
import com.codesroots.osamaomar.shopgate.entities.ProductDetails;
import com.codesroots.osamaomar.shopgate.helper.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class CartViewModel extends ViewModel {

    public MutableLiveData<List<CartItems.DataBean>> cartItemsMutableLiveData = new MutableLiveData<java.util.List<CartItems.DataBean>>();
    public MutableLiveData<Throwable> throwableMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> noItemsFound = new MutableLiveData<>();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private ServerGateway serverGateway;
    private ArrayList<Integer> product_ids;

    public CartViewModel(ServerGateway serverGateway1,ArrayList<Integer> ids) {
        serverGateway = serverGateway1;
        product_ids = ids;
        getProductsIfFound();
    }

    private void getProductsIfFound() {
        if (product_ids==null)
            noItemsFound.postValue(true);

        else
        {
            if (product_ids.size()>0)
             getCartProducts();
            else
            noItemsFound.postValue(true);
        }

    }

    private void getCartProducts(){
        mCompositeDisposable.add(
                serverGateway.getCartProducts(product_ids)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( this::postDataResponse,
                                this::postError));
    }


    private void postDataResponse(CartItems cartItems) {
        List<CartItems.DataBean> products = new ArrayList<>();
        products =  cartItems.getData();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProduct().getOffers().size() > 0)
                products.set(i,convertCurrencyWithOffer(products.get(i).getProduct().getOffers().get(0), products.get(i)));
            else
                products.set(i, convertCurrency(products.get(i)));
        }
        cartItems.setData(products);
        cartItemsMutableLiveData.postValue(cartItems.getData());
    }


    private void postError(Throwable throwable) {
        throwableMutableLiveData.postValue(throwable);
    }


    private CartItems.DataBean convertCurrencyWithOffer(CartItems.DataBean.ProductBean.OffersBean offersBean, CartItems.DataBean product) {

        float priceAfterOffer = product.getStart_price() - product.getStart_price() * offersBean.getPercentage()/100;

        product.setNew_price(Float.valueOf(new DecimalFormat("##.##").format(priceAfterOffer)));

        return convertCurrency(product);
    }


    private CartItems.DataBean convertCurrency(CartItems.DataBean product) {

        if (PreferenceHelper.getCurrencyValue() > 0) {
            product.setStart_price(product.getStart_price() * PreferenceHelper.getCurrencyValue() );
            product.setNew_price(product.getNew_price() * PreferenceHelper.getCurrencyValue());

            return product;
        }
        return product;
    }
}
