package gr.indahouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import gr.indahouse.adminFragments.AdminFragmentAdapter;

public class AdminActivity extends AppCompatActivity {

    TabLayout adminTabLayout;
    ViewPager2 adminViewPager;
    AdminFragmentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //init Views
        adminTabLayout = findViewById(R.id.adminTabLayout);
        adminViewPager = findViewById(R.id.adminViewPager);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new AdminFragmentAdapter(fm,getLifecycle());
        adminViewPager.setAdapter(adapter);

        adminTabLayout.addTab(adminTabLayout.newTab().setText(getString(R.string.category_admin_label)));
        adminTabLayout.addTab(adminTabLayout.newTab().setText(getString(R.string.product_admin_label)));

        adminTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adminViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        adminViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                adminTabLayout.selectTab(adminTabLayout.getTabAt(position));
            }
        });

    }
}