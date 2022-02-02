package gr.indahouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.indahouse.menuFragments.MenuFragmentAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabaseRef, mUserRef;
    String profileImageUrlV, usernameV, streetV, floorV;
    CircleImageView profileImageViewHeader;
    TextView usernameHeader;
    ProgressDialog mLoadingBar;
    TabLayout menuTabLayout;
    ViewPager2 menuViewPager;
    MenuFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init AppToolbar
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        //init FirebaseDatabase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_users));

        //init Views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        menuTabLayout = findViewById(R.id.menuTabLayout);
        menuViewPager = findViewById(R.id.menuViewPager);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new MenuFragmentAdapter(fm, getLifecycle());
        menuViewPager.setAdapter(adapter);

        menuTabLayout.addTab(menuTabLayout.newTab().setText(getString(R.string.category_menu_label)));
        menuTabLayout.addTab(menuTabLayout.newTab().setText(getString(R.string.product_admin_label)));

        menuTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                menuViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        menuViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                menuTabLayout.selectTab(menuTabLayout.getTabAt(position));
            }
        });

        //init header in NavigationView
        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileImageViewHeader = view.findViewById(R.id.profileImageHeader);
        usernameHeader = view.findViewById(R.id.username_header);
        navigationView.setNavigationItemSelectedListener(this);

        //init loadingBar
        mLoadingBar = new ProgressDialog(this);
    }

    //Navigation draw
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                //just closes the Navigation Drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.profile:
                //starting ProfileActivity to edit users profile
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.admin:
                //starting adminActivity to edit categories etc
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                break;
            case R.id.logout:
                //log-out
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            //Send user to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            //fetching local variables profileImageUrlV, usernameV, streetV, floorV
            mUserRef.child(Objects.requireNonNull(mAuth.getUid())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        profileImageUrlV = Objects.requireNonNull(snapshot.child(getString(R.string.ref_users_profileImage)).getValue()).toString();
                        usernameV = Objects.requireNonNull(snapshot.child(getString(R.string.ref_users_username)).getValue()).toString();
                        streetV = Objects.requireNonNull(snapshot.child(getString(R.string.ref_users_street)).getValue()).toString();
                        floorV = Objects.requireNonNull(snapshot.child(getString(R.string.ref_users_floor)).getValue()).toString();

                        //header init Name and Picture
                        usernameHeader.setText(usernameV);
                        Picasso.get().load(profileImageUrlV).placeholder(R.drawable.ic_baseline_person_24).resize(250, 250).centerInside().into(profileImageViewHeader);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error);
                }
            });
        }
    }
}