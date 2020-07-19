package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.entities.ProductDetails;
import com.codesroots.osamaomar.shopgate.entities.StoreSetting;
import com.codesroots.osamaomar.shopgate.helper.AddorRemoveCallbacks;
import com.codesroots.osamaomar.shopgate.helper.PreferenceHelper;
import com.codesroots.osamaomar.shopgate.helper.ResourceUtil;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainactivity.MainActivity;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.adapters.ProductImagesAdapter;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.adapters.ProductSizesAdapter;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.rate.RateActivity;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.showimage.ImageActivity;

import java.util.ArrayList;

import static com.codesroots.osamaomar.shopgate.entities.names.PRODUCT_ID;


public class ProductDetailsFragment extends Fragment {

    private ProductDetailsViewModel mViewModel;
    RecyclerView images_rec, sizes_rec;
    private int productid;
    FrameLayout loading;
    public TextView product_name, description, price, ratecount, amount, addtocart, charege, oldprice;
    RatingBar ratingBar;
    public ImageView item_img;
    int userid = PreferenceHelper.getUserId(), favid = 0;
    ProductSizesAdapter productSizesAdapter;
    ProductImagesAdapter productImagesAdapter;
    ArrayList<String> images = new ArrayList<>();
    ImageView addToFav, share;
    boolean productfav;
    ProductDetails.ProductdetailsBean productdetails;
    public StoreSetting setting;
    public boolean freecharg = false;
    private String imagurl = "";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.product_details_fragment, container, false);
        ((MainActivity) getActivity()).head_title.setText(getText(R.string.product_details));
        ((MainActivity) getActivity()).logo.setVisibility(View.VISIBLE);
        productid = getArguments().getInt(PRODUCT_ID, 0);
        findViewsFromXml(view);

        mViewModel = ViewModelProviders.of(this, getViewModelFactory()).get(ProductDetailsViewModel.class);

        if (ResourceUtil.getCurrentLanguage(getActivity()).matches("en"))
            description.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next, 0);

        mViewModel.throwableMutableLiveData.observe(this, throwable ->
                {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
                }
        );


        mViewModel.productDetailsMutableLiveData.observe(this, productDetails ->
        {
            loading.setVisibility(View.GONE);
            if (productDetails.getProductdetails().size() > 0) {
                productdetails = productDetails.getProductdetails().get(0);
                if (productDetails.getProductdetails() != null)
                    setDataToViews(productDetails.getProductdetails().get(0));
            } else
                Toast.makeText(getActivity(), getActivity().getText(R.string.error_in_data), Toast.LENGTH_SHORT).show();
        });


        addtocart.setOnClickListener(v -> {
            if (userid > 0) {
                if (PreferenceHelper.retriveCartItemsValue() != null) {
                    if (!PreferenceHelper.retriveCartItemsValue().contains(String.valueOf(productdetails.getProductsizes().get(productSizesAdapter.mSelectedItem).getId()))) {
                        PreferenceHelper.addItemtoCart(productdetails.getProductsizes().get(productSizesAdapter.mSelectedItem).getId());
                        ((AddorRemoveCallbacks) getActivity()).onAddProduct();
                        Toast.makeText(getActivity(), getActivity().getText(R.string.addtocartsuccess), Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getActivity(), getActivity().getText(R.string.aleady_exists), Toast.LENGTH_SHORT).show();
                } else {
                    PreferenceHelper.addItemtoCart(productdetails.getProductsizes().get(productSizesAdapter.mSelectedItem).getId());
                    ((AddorRemoveCallbacks) getActivity()).onAddProduct();
                    Toast.makeText(getActivity(), getActivity().getText(R.string.addtocartsuccess), Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(getActivity(), getActivity().getText(R.string.loginfirst), Toast.LENGTH_SHORT).show();

        });

        addToFav.setOnClickListener(v ->
        {
            addToFav.setEnabled(false);
            if (!productfav) {
                mViewModel.AddToFav();
                productfav = true;
            } else {
                mViewModel.DeleteFav(userid, productid);
                productfav = false;
            }
        });

        share.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "http://www.shopgate.om/productID=" + productid;
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "I suggest to see this product");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.addCategory("productDetails");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });

        mViewModel.addToFavMutableLiveData.observe(this, aBoolean ->
        {
            addToFav.setEnabled(true);
            addToFav.setImageResource(R.drawable.favoried);
        });

        mViewModel.deleteToFavMutableLiveData.observe(this, aBoolean ->
        {
            addToFav.setEnabled(true);
            addToFav.setImageResource(R.drawable.like);
        });

        mViewModel.throwablefav.observe(this, throwable ->
        {
            addToFav.setEnabled(false);
            Toast.makeText(getActivity(), getText(R.string.error_tryagani), Toast.LENGTH_SHORT).show();
        });

        ratingBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Intent intent = new Intent(getActivity(), RateActivity.class);
                intent.putExtra(PRODUCT_ID, productid);
                getActivity().startActivity(intent);
            }
            return true;
        });

        item_img.setOnClickListener(v ->
                {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.putExtra("url", imagurl);
                    getContext().startActivity(intent);
                }
        );
        return view;
    }

    private void findViewsFromXml(View view) {
        images_rec = view.findViewById(R.id.images_rec);
        sizes_rec = view.findViewById(R.id.sizes);
        loading = view.findViewById(R.id.progress);
        product_name = view.findViewById(R.id.product_name);
        description = view.findViewById(R.id.description);
        price = view.findViewById(R.id.price);
        ratecount = view.findViewById(R.id.rate_count);
        ratingBar = view.findViewById(R.id.rates);
        item_img = view.findViewById(R.id.item_img);
        addToFav = view.findViewById(R.id.fav);
        amount = view.findViewById(R.id.amount);
        addtocart = view.findViewById(R.id.addtocart);
        charege = view.findViewById(R.id.charge);
        share = view.findViewById(R.id.share);
        oldprice = view.findViewById(R.id.oldprice);
        oldprice.setPaintFlags(oldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

    }

    @SuppressLint("SetTextI18n")
    private void setDataToViews(ProductDetails.ProductdetailsBean productdetailsBean) {

        try {
            for (int i = 0; i < productdetailsBean.getProductphotos().size(); i++)
                images.add(productdetailsBean.getProductphotos().get(i).getPhoto());
            Glide.with(getActivity()).load(productdetailsBean.getProductphotos().get(0).getPhoto())
                    .useAnimationPool(true).placeholder(R.drawable.product).into(item_img);
            imagurl = productdetailsBean.getProductphotos().get(0).getPhoto();
            if (productdetailsBean.getOffers().size() > 0)
                productSizesAdapter = new ProductSizesAdapter(getActivity(), productdetailsBean.getProductsizes(),
                        this, productdetailsBean.getOffers().get(0).getPercentage());
            else
                productSizesAdapter = new ProductSizesAdapter(getActivity(), productdetailsBean.getProductsizes(), this, 0);

            productImagesAdapter = new ProductImagesAdapter(getActivity(), productdetailsBean.getProductphotos(), this);
            images_rec.setAdapter(productImagesAdapter);
            sizes_rec.setAdapter(productSizesAdapter);
            product_name.setText(productdetailsBean.getName());

            if (productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getNew_price() > 0) {
                if (PreferenceHelper.getCurrencyValue() > 0) {
                    price.setText(productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getNew_price() + PreferenceHelper.getCurrency());
                    oldprice.setText(productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getStart_price() + PreferenceHelper.getCurrency());
                } else {
                    price.setText(productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getNew_price() + " " + getText(R.string.coin));
                    oldprice.setText(productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getStart_price() + " " + getText(R.string.coin));
                }
            } else if (PreferenceHelper.getCurrencyValue() > 0)
                price.setText(productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getStart_price() + PreferenceHelper.getCurrency());
            else
                price.setText(productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getStart_price() + " " + getText(R.string.coin));

            description.setText(Html.fromHtml(productdetailsBean.getDescription()));
            description.setOnClickListener(v -> {
                Fragment fragment = new DescriptionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("description", productdetailsBean.getDescription());
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainfram, fragment)
                        .addToBackStack(null).commit();

            });


            if (productdetailsBean.getTotal_rating() != null) {
                if (productdetailsBean.getTotal_rating().size() > 0) {
                    ratecount.setText("(" + productdetailsBean.getTotal_rating().get(0).getCount() + ")");
                    ratingBar.setRating(productdetailsBean.getTotal_rating().get(0).getStars() /
                            productdetailsBean.getTotal_rating().get(0).getCount());
                }
            }

            if (productdetailsBean.getFavourites().size() > 0) {
                addToFav.setImageResource(R.drawable.favoried);
                productfav = true;
                favid = productdetailsBean.getFavourites().get(0).getId();
            } else {
                addToFav.setImageResource(R.drawable.like);
                productfav = false;
            }
        } catch (Exception e) {
        }
    }

    public void setImageToImageView(String url) {
        Glide.with(getActivity()).load(url)
                .useAnimationPool(true).placeholder(R.drawable.product).into(item_img);
        imagurl = url;
    }


    private ProductDetailsModelFactory getViewModelFactory() {
        return new ProductDetailsModelFactory(this.getActivity().getApplication(), productid, userid);
    }

}
