package gr.indahouse.adminFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import gr.indahouse.R;
import gr.indahouse.utils.ProductViewHolder;
import gr.indahouse.utils.Products;

public class ProductFragment extends Fragment {

    private static final String TAG = "ProductFragment";
    FirebaseRecyclerAdapter<Products, ProductViewHolder> productsAdapter;
    FirebaseRecyclerOptions<Products> productsOptions;

    DatabaseReference mProductsRef, mCategoryRef;

    RecyclerView prodRecyclerView;

    Button addProductBtn;
    TextInputLayout newProdNameTL, newProdPriceTL, newProdCategoryIdTL, editProdNameTL, editProdPriceTL, editProdCategoryIdTL;
    String prodNameTL, prodPriceTL, prodCategoryIdTL;
    ImageButton deleteProdBtn;

    AutoCompleteTextView autoCompleteAddCategoryIdTextView, autoCompleteEditCategoryIdTextView;
    ArrayAdapter<String> adapterItemsId;
    ArrayAdapter<String> adapterItemsName;
    String[] itemsId;
    String[] itemsName;
    String editProdCategoryId;

    Map<String, String> map = new HashMap<String, String>();

    View view;

    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance() {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product, container, false);

        //init Views
        prodRecyclerView = view.findViewById(R.id.admin_product_recyclerView);
        addProductBtn = view.findViewById(R.id.add_product_btn);
        deleteProdBtn = view.findViewById(R.id.deleteProductBtn);

        //init references
        mProductsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_products));
        mCategoryRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_category));

        //init LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        prodRecyclerView.setLayoutManager(linearLayoutManager);

        loadProducts();

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();
            }
        });

        return view;
    }

    private void loadProducts() {
        productsOptions = new FirebaseRecyclerOptions.Builder<Products>().setQuery(Objects.requireNonNull(mProductsRef), Products.class).build();

        productsAdapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(productsOptions) {
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_product, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                Log.d(TAG, "onBindViewHolderProducts: " + position + " " + model.getProductName());
                holder.prodName.setText(model.getProductName());
                holder.prodPrice.setText(model.getProductPrice());

                //Do deleteBtn visible
                holder.deleteProdBtn.setVisibility(View.VISIBLE);

                holder.deleteProdBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle(getString(R.string.are_you_sure_to_delete_product_title_label))
                                .setIcon(R.drawable.ic_baseline_delete_24)
                                .setMessage(getString(R.string.are_you_sure_to_delete_product_message_label)+": "+model.getProductName()+";")
                                .setNegativeButton(getString(R.string.cancel_admin_btn_label), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        dialoginterface.cancel();
                                    }
                                })
                                .setPositiveButton(getString(R.string.delete_label), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        mProductsRef.child(model.getProductId()).removeValue();
                                    }
                                }).show();
                    }
                });

                holder.singleViewProductConstraint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onBindViewHolder: productId: " + model.getProductId());
                        showEditProductDialog(model.getProductId(), model.getProductName(), model.getProductPrice(), model.getProductCategoryId());
                    }
                });
            }
        };
        productsAdapter.startListening();
        prodRecyclerView.setAdapter(productsAdapter);
        Log.d(TAG, "onDataChange: ProductAdapterStartsListening: ");
    }

    private void addNewProduct(@NonNull View addNewProductDialogView, AlertDialog addProductDialog, String newProdCategoryId) {
        //init Views
        newProdNameTL = addNewProductDialogView.findViewById(R.id.new_product_name_layout);
        newProdPriceTL = addNewProductDialogView.findViewById(R.id.new_product_price_layout);
        newProdCategoryIdTL = addNewProductDialogView.findViewById(R.id.new_product_category_id_layout);

        //Check if valid values
        if (Objects.requireNonNull(newProdNameTL.getEditText()).getText().toString().isEmpty()) {
            showError(newProdNameTL, getString(R.string.name_is_not_valid));
        } else if (Objects.requireNonNull(newProdPriceTL.getEditText()).getText().toString().isEmpty()) {
            showError(newProdPriceTL, getString(R.string.price_is_not_valid));
        } else if (Objects.requireNonNull(newProdCategoryIdTL.getEditText()).getText().toString().isEmpty()) {
            showError(newProdCategoryIdTL, getString(R.string.product_category_id_is_not_valid));
        } else {
            //Get the Values
            prodNameTL = Objects.requireNonNull(newProdNameTL.getEditText()).getText().toString();
            prodPriceTL = Objects.requireNonNull(newProdPriceTL.getEditText()).getText().toString();
            prodCategoryIdTL = newProdCategoryId;

            //Get unique key ID
            String key = mProductsRef.push().getKey();
//            productsCount++;

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(getString(R.string.ref_product_id), key);
            hashMap.put(getString(R.string.ref_product_name), prodNameTL);
            hashMap.put(getString(R.string.ref_product_price), prodPriceTL);
            hashMap.put(getString(R.string.ref_product_category_id), prodCategoryIdTL);


            mProductsRef.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getContext(), getString(R.string.successful_add_of_product), Toast.LENGTH_SHORT).show();
                    addProductDialog.dismiss();
                }
            });
        }

    }

    private void showAddProductDialog() {
        final String[] addNewProdCategoryId = {null};

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View addNewProductDialogView = factory.inflate(R.layout.add_product, null);
        final AlertDialog addProductDialog = new AlertDialog.Builder(getContext()).create();
        addProductDialog.setView(addNewProductDialogView);

        autoCompleteAddCategoryIdTextView = addNewProductDialogView.findViewById(R.id.new_product_category_id);
        adapterItemsId = new ArrayAdapter<>(getContext(), R.layout.category_list_item, itemsName);
        autoCompleteAddCategoryIdTextView.setAdapter(adapterItemsId);

        addNewProductDialogView.findViewById(R.id.add_product_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewProduct(addNewProductDialogView, addProductDialog, addNewProdCategoryId[0]);
            }
        });
        addNewProductDialogView.findViewById(R.id.add_product_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductDialog.dismiss();
            }
        });

        //Change Name of category id To id of category
        autoCompleteAddCategoryIdTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addNewProdCategoryId[0] = itemsId[position];
            }
        });

        addProductDialog.show();
    }

    private void showEditProductDialog(String productId, String productName, String productPrice, String productCategoryId) {
        //replace euro sing
        productPrice = productPrice.replaceAll("€", "");

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View editProductDialogView = factory.inflate(R.layout.edit_product, null);
        final AlertDialog editProductDialog = new AlertDialog.Builder(getContext()).create();
        editProductDialog.setView(editProductDialogView);

        autoCompleteEditCategoryIdTextView = editProductDialogView.findViewById(R.id.edit_product_category_id);
        adapterItemsId = new ArrayAdapter<>(getContext(), R.layout.category_list_item, itemsName);
        autoCompleteEditCategoryIdTextView.setText(map.get(productCategoryId));
        autoCompleteEditCategoryIdTextView.setAdapter(adapterItemsId);


        //init Views
        editProdNameTL = editProductDialogView.findViewById(R.id.edit_product_name_layout);
        editProdPriceTL = editProductDialogView.findViewById(R.id.edit_product_price_layout);
        editProdCategoryIdTL = editProductDialogView.findViewById(R.id.edit_product_category_id_layout);

        //Put values into editTexts
        Objects.requireNonNull(editProdNameTL.getEditText()).setText(productName);
        Objects.requireNonNull(editProdPriceTL.getEditText()).setText(productPrice);

        //Change Name of category id To id of category
        autoCompleteEditCategoryIdTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editProdCategoryId = itemsId[position];
            }
        });

        editProductDialogView.findViewById(R.id.edit_product_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProduct(productId, editProductDialog, editProdCategoryId);
            }
        });
        editProductDialogView.findViewById(R.id.edit_product_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProductDialog.dismiss();
            }
        });
        editProductDialog.show();

    }

    private void EditProduct(String productId, AlertDialog editProductDialog, String prodCategoryId) {
        //Check if valid values
        if (Objects.requireNonNull(editProdNameTL.getEditText()).getText().toString().isEmpty()) {
            showError(editProdNameTL, getString(R.string.name_is_not_valid));
        } else if (Objects.requireNonNull(editProdPriceTL.getEditText()).getText().toString().isEmpty()) {
            showError(editProdPriceTL, getString(R.string.price_is_not_valid));
        } else if (Objects.requireNonNull(editProdCategoryIdTL.getEditText()).getText().toString().isEmpty()) {
            showError(editProdCategoryIdTL, getString(R.string.product_category_id_is_not_valid));
        } else {
            //Get the values
            prodNameTL = Objects.requireNonNull(editProdNameTL.getEditText()).getText().toString();
            prodPriceTL = Objects.requireNonNull(editProdPriceTL.getEditText()).getText().toString();
            prodCategoryId = editProdCategoryId;

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(getString(R.string.ref_product_id), productId);
            hashMap.put(getString(R.string.ref_product_name), prodNameTL);
            hashMap.put(getString(R.string.ref_product_price), prodPriceTL);
            hashMap.put(getString(R.string.ref_product_category_id), prodCategoryId);

            mProductsRef.child(productId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), getString(R.string.successful_edit_of_product), Toast.LENGTH_SHORT).show();
                    editProductDialog.dismiss();
                }
            });
        }

    }

    private void showError(@NonNull TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }

    @Override
    public void onStart() {
        super.onStart();
        mCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                map.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: Name of Category:" + categorySnapshot.child(getString(R.string.ref_category_name)).getValue());
                    Log.d(TAG, "onDataChange: key of Category:" + categorySnapshot.child(getString(R.string.ref_category_id)).getValue());

                    map.put(Objects.requireNonNull(categorySnapshot.child(getString(R.string.ref_category_id)).getValue()).toString()
                            , Objects.requireNonNull(categorySnapshot.child(getString(R.string.ref_category_name)).getValue()).toString());
                }
                Log.d(TAG, "onDataChange: testing123" + map);
                itemsId = map.keySet().toArray(new String[0]);
                itemsName = map.values().toArray(new String[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}