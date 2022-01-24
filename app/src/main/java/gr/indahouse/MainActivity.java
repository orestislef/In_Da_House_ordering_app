package gr.indahouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
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
import gr.indahouse.utils.Categories;
import gr.indahouse.utils.CategoryViewHolder;
import gr.indahouse.utils.Products;
import gr.indahouse.utils.ProductViewHolder;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabaseRef, mUserRef, mProductRef, mCategoryRef;
    String profileImageUrlV, usernameV, streetV, floorV;
    CircleImageView profileImageViewHeader;
    TextView usernameHeader;
    ProgressDialog mLoadingBar;
    FirebaseRecyclerAdapter<Products, ProductViewHolder> productAdapter;
    FirebaseRecyclerOptions<Products> productOptions;
    FirebaseRecyclerAdapter<Categories, CategoryViewHolder> categoryAdapter;
    FirebaseRecyclerOptions<Categories> categoryOptions;

    RecyclerView recyclerView;

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
        mProductRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_products));
        mCategoryRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_category));

        //init Views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        recyclerView = findViewById(R.id.recyclerView);

        //init header in NavigationView
        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileImageViewHeader = view.findViewById(R.id.profileImageHeader);
        usernameHeader = view.findViewById(R.id.username_header);
        navigationView.setNavigationItemSelectedListener(this);

        //init loadingBar
        mLoadingBar = new ProgressDialog(this);

        //init LineaLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Listener for new products to scroll to top recyclerView.smoothScrollToPosition((int) snapshot.getChildrenCount());
        mProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recyclerView.smoothScrollToPosition((int) snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Listener for new categories to scroll to top recyclerView.smoothScrollToPosition((int) snapshot.getChildrenCount());
        mCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recyclerView.smoothScrollToPosition((int) snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                //starting ProfileActivity to edit users profile
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

                        loadCategories();
                        //loadProducts();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.toString());
                }
            });
        }
    }

    public void loadCategories() {
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);

        categoryOptions = new FirebaseRecyclerOptions.Builder<Categories>().setQuery(Objects.requireNonNull(mCategoryRef), Categories.class).build();

        categoryAdapter = new FirebaseRecyclerAdapter<Categories, CategoryViewHolder>(categoryOptions) {
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_category, parent, false);
                return new CategoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Categories model) {
                Log.d(TAG, "onBindViewHolderCategories: " + position + " " + model.getCategoryName() + "\t" + model.getCategoryImageUrl());
                holder.catName.setText(model.getCategoryName());
                holder.catDescription.setText(model.getCategoryDesc());
                Picasso.get().load(model.getCategoryImageUrl()).placeholder(R.drawable.ic_baseline_local_cafe_24).resize(250, 250).centerInside().into(holder.catImage);

                holder.singleViewCategoryConstraint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: categoryId: " + model.getCategoryId());
                        loadProducts(model.getCategoryId(), model.getCategoryName());
                    }
                });
            }
        };
        categoryAdapter.startListening();
        recyclerView.setAdapter(categoryAdapter);
        Log.d(TAG, "onDataChange: CategoryAdapterStartsListening");
    }

    private void loadProducts(String categoryID, String categoryName) {
        productOptions = new FirebaseRecyclerOptions.Builder<Products>().setQuery(mProductRef.orderByChild(getString(R.string.ref_category_id)).equalTo(categoryID), Products.class).build();

        productAdapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(productOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                Log.d(TAG, "onBindViewHolderProduct: " + position + " " + model.getProductName() + "\t" + model.getProductPrice());
                holder.prodName.setText(model.getProductName());
                holder.prodPrice.setText(model.getProductPrice());

                holder.singleViewProductConstraint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadProductDetailsDialog(model.getProductId());
                        categoryAdapter.stopListening();
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_product, parent, false);
                return new ProductViewHolder(view);
            }
        };
        productAdapter.startListening();
        recyclerView.setAdapter(productAdapter);
        Log.d(TAG, "onDataChange: ProductAdapterStartsListening");

        toolbar.setTitle(categoryName);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productAdapter.stopListening();
                loadCategories();
                setSupportActionBar(toolbar);
            }
        });
    }

    private void loadProductDetailsDialog(String idOfProduct) {
        mProductRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: " + idOfProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}