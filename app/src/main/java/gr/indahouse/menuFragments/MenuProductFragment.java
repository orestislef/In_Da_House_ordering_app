package gr.indahouse.menuFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.indahouse.R;
import gr.indahouse.utils.ProductViewHolder;
import gr.indahouse.utils.Products;

public class MenuProductFragment extends Fragment {

    private static final String TAG = "MenuProductFragment";
    FirebaseRecyclerAdapter<Products, ProductViewHolder> productAdapter;
    FirebaseRecyclerOptions<Products> productOptions;

    DatabaseReference mProductRef;
    RecyclerView prodRecyclerView;

    String prodName, prodPrice;

    View view;

    public MenuProductFragment() {
        // Required empty public constructor
    }

    public static MenuProductFragment newInstance() {
        MenuProductFragment fragment = new MenuProductFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_product, container, false);

        //init Views
        prodRecyclerView = view.findViewById(R.id.menu_product_recyclerView);

        //init references
        mProductRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_products));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        prodRecyclerView.setLayoutManager(linearLayoutManager);

        loadProducts();

        return view;
    }

    private void loadProducts() {
        productOptions = new FirebaseRecyclerOptions.Builder<Products>().setQuery(mProductRef, Products.class).build();

        productAdapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(productOptions) {

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_product, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                Log.d(TAG, "onBindViewHolderProducts: " + position + " " + model.getProductName() + "\t" + model.getProductPrice());
                holder.prodName.setText(model.getProductName());
                holder.prodPrice.setText(model.getProductPrice());
            }
        };
        productAdapter.startListening();
        prodRecyclerView.setAdapter(productAdapter);
        Log.d(TAG, "onDataChange: ProductAdapterStartsListening");
    }
}