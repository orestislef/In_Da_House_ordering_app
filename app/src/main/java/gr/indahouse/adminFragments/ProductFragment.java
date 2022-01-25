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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import gr.indahouse.R;
import gr.indahouse.utils.ProductViewHolder;
import gr.indahouse.utils.Products;

public class ProductFragment extends Fragment {

    private static final String TAG = "productFragment";
    FirebaseRecyclerAdapter<Products, ProductViewHolder> productsAdapter;
    FirebaseRecyclerOptions<Products> productsOptions;

    DatabaseReference mProductsRef;
    RecyclerView prodRecyclerView;

    Button addProductBtn;
    TextInputLayout newProdName, newProdPrice, newProdCategoryId, editProdName, editProdPrice, editProdCategoryId;
    String prodName, prodPrice, prodCategoryId;
    ImageButton deleteProdBtn;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product, container, false);

        //init Views
        prodRecyclerView = view.findViewById(R.id.admin_product_recyclerView);
        addProductBtn = view.findViewById(R.id.add_product_btn);
        deleteProdBtn = view.findViewById(R.id.deleteProductBtn);

        //init references
        mProductsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_products));

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
                holder.deleteBtn.setVisibility(View.VISIBLE);

                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle(getString(R.string.are_you_sure_to_delete_product_title_label))
                                .setIcon(R.drawable.ic_baseline_delete_24)
                                .setMessage(getString(R.string.are_you_sure_to_delete_product_message_label))
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

    private void addNewProduct(View addNewProductDialogView, AlertDialog addProductDialog) {
        //init Views
        newProdName = addNewProductDialogView.findViewById(R.id.new_product_name_layout);
        newProdPrice = addNewProductDialogView.findViewById(R.id.new_product_price_layout);
        newProdCategoryId = addNewProductDialogView.findViewById(R.id.new_product_category_id_layout);

        //Check if valid values
        if (Objects.requireNonNull(newProdName.getEditText()).getText().toString().isEmpty()) {
            showError(newProdName, getString(R.string.name_is_not_valid));
        } else if (Objects.requireNonNull(newProdPrice.getEditText()).getText().toString().isEmpty()) {
            showError(newProdPrice, getString(R.string.price_is_not_valid));
        } else if (Objects.requireNonNull(newProdCategoryId.getEditText()).getText().toString().isEmpty()) {
            //edw na valw na kanei fetch apo tis katigories se dropDownMenu
        } else {
            //Get the Values
            prodName = Objects.requireNonNull(newProdName.getEditText()).getText().toString();
            prodPrice = Objects.requireNonNull(newProdPrice.getEditText()).getText().toString();
            prodCategoryId = Objects.requireNonNull(newProdCategoryId.getEditText()).getText().toString();

            //Get unique key ID
            String key = mProductsRef.push().getKey();
//            productsCount++;

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(getString(R.string.ref_product_id), key);
//            hashMap.put(getString(R.string.ref_category_position), categoriesCount.toString());
            hashMap.put(getString(R.string.ref_product_name), prodName);
            hashMap.put(getString(R.string.ref_product_price), prodPrice);
            hashMap.put(getString(R.string.ref_product_category_id), prodCategoryId);


            mProductsRef.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getContext(), getString(R.string.successful_add_of_category), Toast.LENGTH_SHORT).show();
                    addProductDialog.dismiss();
                }
            });
        }

    }

    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }

    private void showAddProductDialog() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View addNewProductDialogView = factory.inflate(R.layout.add_product, null);
        final AlertDialog addProductDialog = new AlertDialog.Builder(getContext()).create();
        addProductDialog.setView(addNewProductDialogView);
        addNewProductDialogView.findViewById(R.id.add_product_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewProduct(addNewProductDialogView, addProductDialog);
            }
        });
        addNewProductDialogView.findViewById(R.id.add_product_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductDialog.dismiss();
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

        //init Views
        editProdName = editProductDialogView.findViewById(R.id.edit_product_name_layout);
        editProdPrice = editProductDialogView.findViewById(R.id.edit_product_price_layout);
        editProdCategoryId = editProductDialogView.findViewById(R.id.edit_product_category_id_layout);


        //Put values into editTexts
        Objects.requireNonNull(editProdName.getEditText()).setText(productName);
        Objects.requireNonNull(editProdPrice.getEditText()).setText(productPrice);
        Objects.requireNonNull(editProdCategoryId.getEditText()).setText(productCategoryId);

        editProductDialogView.findViewById(R.id.edit_product_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProduct(productId, editProductDialog);
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

    private void EditProduct(String productId, AlertDialog editProductDialog) {
        //Check if valid values
        if (Objects.requireNonNull(editProdName.getEditText()).getText().toString().isEmpty()) {
            showError(editProdName, getString(R.string.name_is_not_valid));
        } else if (Objects.requireNonNull(editProdPrice.getEditText()).getText().toString().isEmpty()) {
            showError(editProdPrice, getString(R.string.price_is_not_valid));
        } else if (Objects.requireNonNull(editProdCategoryId.getEditText()).getText().toString().isEmpty()) {
            showError(editProdCategoryId, getString(R.string.product_category_id_is_not_valid));
        } else {
            //Get the values
            prodName = Objects.requireNonNull(editProdName.getEditText()).getText().toString();
            prodPrice = Objects.requireNonNull(editProdPrice.getEditText()).getText().toString();
            prodCategoryId = Objects.requireNonNull(editProdCategoryId.getEditText()).getText().toString();

            //Get unique key ID
            String key = mProductsRef.child(productId).getKey();
            Log.d(TAG, "EditProduct: key: " + key);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(getString(R.string.ref_product_id), key);
//        hashMap.put(getString(R.string.ref_category_position), categoryPosition);
            hashMap.put(getString(R.string.ref_product_name), prodName);
            hashMap.put(getString(R.string.ref_product_price), prodPrice);
            hashMap.put(getString(R.string.ref_product_category_id), prodCategoryId);

            mProductsRef.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), getString(R.string.successful_edit_of_product), Toast.LENGTH_SHORT).show();
                    editProductDialog.dismiss();
                }
            });
        }

    }
}