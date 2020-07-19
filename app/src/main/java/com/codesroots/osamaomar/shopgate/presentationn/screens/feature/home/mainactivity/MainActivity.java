package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainactivity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.helper.AddorRemoveCallbacks;
import com.codesroots.osamaomar.shopgate.helper.Converter;
import com.codesroots.osamaomar.shopgate.helper.PreferenceHelper;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.cartfragment.CartFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.favorite.FavoritsFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainactivity.adapter.AllDepartsAdapter;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainfragment.MainFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.morefragment.MenuFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.myorders.MyOrdersFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.offerfragment.OffersFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.ProductDetailsFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productfragment.ProductsFragment;

import java.util.ArrayList;

import static com.codesroots.osamaomar.shopgate.entities.names.PRODUCT_ID;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener, AddorRemoveCallbacks {

    RecyclerView alldepartsinNavigation;
    BottomNavigationView bottomNavigationView;
    MainActivityViewModel mainActivityViewModel;
    NavigationView navigationView;
    public DrawerLayout drawer;
    public ImageView logo, search;
    public TextView head_title;
    private EditText searchName;
    private ArrayList<String> arrayList = new ArrayList<>();
    private PreferenceHelper preferenceHelper;
    private static int cart_count = 0;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceHelper = new PreferenceHelper(this);
        initialize();
        setUpToggle();
        search.setOnClickListener(v -> {
            performSearch();
        });
        searchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new MainFragment()).commit();

        try {

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            Uri uri = intent.getData();
            if (uri != null) {
                if (uri.getPath().contains("productID")) {
                    Fragment fragment = new ProductDetailsFragment();
                    Bundle bundle1 = new Bundle();
                    String product = uri.getPath().split("=")[1];
                    bundle.putInt(PRODUCT_ID, Integer.valueOf(product));
                    fragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.mainfram, fragment)
                            .addToBackStack(null).commit();
                }
            }
            Log.d("Bundle", bundle.toString());

        } catch (Exception e) {
        }
    }

    private void initialize() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        search = findViewById(R.id.search);
        searchName = findViewById(R.id.searchName);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        alldepartsinNavigation = findViewById(R.id.all_departs);
        logo = findViewById(R.id.logo);
        head_title = findViewById(R.id.head_title);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        mainActivityViewModel = ViewModelProviders.of(this, getViewModelFactory()).get(MainActivityViewModel.class);
        mainActivityViewModel.sidemenuMutableLiveData.observe(this, sidemenu ->
        {
            assert sidemenu != null;
            alldepartsinNavigation.setAdapter(new AllDepartsAdapter(this, sidemenu.getCategory()));
        });
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS
                        , Manifest.permission.WRITE_CONTACTS}, 112);

    }

    private void performSearch() {

        if (!searchName.getText().toString().matches("")) {
            Fragment fragment = new ProductsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", searchName.getText().toString());
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, fragment).addToBackStack(null).commit();
        } else
            searchName.setError(getText(R.string.nosearchname));
    }

    private MainActivityModelFactory getViewModelFactory() {
        return new MainActivityModelFactory(getApplication());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        cart_count = PreferenceHelper.retriveCartItemsSize();
        menuItem.setIcon(Converter.convertLayoutToImage(MainActivity.this, cart_count, R.drawable.cartt));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new CartFragment()).addToBackStack(null).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.more:
                drawer.closeDrawers();
                getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new MenuFragment()).addToBackStack(null).commit();
                break;

            case R.id.notifications:
                if (PreferenceHelper.getUserId() > 0) {
                    drawer.closeDrawers();
                    popupFragments();
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new FavoritsFragment()).addToBackStack(null).commit();
                } else
                    Toast.makeText(MainActivity.this, getText(R.string.loginfirst), Toast.LENGTH_SHORT).show();
                break;
            case R.id.main:
                drawer.closeDrawers();
                popupFragments();
                getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new MainFragment()).addToBackStack(null).commit();
                break;

            case R.id.myorders:
                if (PreferenceHelper.getUserId() > 0) {
                    drawer.closeDrawers();
                    popupFragments();
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new MyOrdersFragment()).addToBackStack(null).commit();
                } else
                    Toast.makeText(MainActivity.this, getText(R.string.loginfirst), Toast.LENGTH_SHORT).show();
                break;

            case R.id.offers:
                drawer.closeDrawers();
                popupFragments();
                getSupportFragmentManager().beginTransaction().replace(R.id.mainfram, new OffersFragment()).addToBackStack(null).commit();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void popupFragments() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @Override
    public void onAddProduct() {
        cart_count++;
        invalidateOptionsMenu();
    }

    @Override
    public void onRemoveProduct() {
        cart_count--;
        invalidateOptionsMenu();
    }

    @Override
    public void onClearCart() {
        PreferenceHelper.clearCart();
        cart_count = 0;
        invalidateOptionsMenu();
    }

    public void setUpToggle() {
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        toggle.setDrawerIndicatorEnabled(false);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu, this.getTheme());

        toggle.setHomeAsUpIndicator(drawable);

        drawer.addDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener(v -> {
            if (drawer.isDrawerVisible(GravityCompat.START)) {

                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.syncState();
    }
}
