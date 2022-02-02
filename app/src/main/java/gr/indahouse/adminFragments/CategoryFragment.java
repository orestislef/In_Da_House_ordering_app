package gr.indahouse.adminFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import gr.indahouse.R;
import gr.indahouse.utils.Categories;
import gr.indahouse.utils.CategoryViewHolder;

public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";
    FirebaseRecyclerAdapter<Categories, CategoryViewHolder> categoryAdapter;
    FirebaseRecyclerOptions<Categories> categoryOptions;

    DatabaseReference mCategoryRef, mProductRef;
    RecyclerView catRecyclerView;

    Button addCategoryBtn;
    TextInputLayout newCatName, newCatDesc, newCatImgUrl, editCatName, editCatDesc, editCatImgUrl;
    String catName, catDesc, catImgUrl;
    Long categoriesCount;
    ImageButton deleteCatBtn;

    View view;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
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
        view = inflater.inflate(R.layout.fragment_category, container, false);

        //init Views
        catRecyclerView = view.findViewById(R.id.admin_category_recyclerView);
        addCategoryBtn = view.findViewById(R.id.add_category_btn);
        deleteCatBtn = view.findViewById(R.id.deleteCategoryButton);

        //init references
        mCategoryRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_category));
        mProductRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_products));

        //init LineaLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        catRecyclerView.setLayoutManager(linearLayoutManager);

        loadCategories();

        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

        return view;
    }

    private void loadCategories() {
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


                //Do delete button visible
                holder.deleteCatBtn.setVisibility(View.VISIBLE);

                holder.deleteCatBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialogDeleteProductFromCategory(model.getCategoryId(), model.getCategoryName());
                        ShowAlertDialogDeleteCategory(model.getCategoryId(), model.getCategoryName());
                    }
                });

                holder.singleViewCategoryConstraint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: categoryId: " + model.getCategoryId());
                        showEditCategoryDialog(model.getCategoryId(), model.getCategoryName(), model.getCategoryDesc(), model.getCategoryImageUrl(), model.getCategoryPosition());
                    }
                });
            }
        };
        categoryAdapter.startListening();
        catRecyclerView.setAdapter(categoryAdapter);
        Log.d(TAG, "onDataChange: CategoryAdapterStartsListening");
    }

    private void showAddCategoryDialog() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View addNewCategoryDialogView = factory.inflate(R.layout.add_category, null);
        final AlertDialog addCategoryDialog = new AlertDialog.Builder(getContext()).create();
        addCategoryDialog.setView(addNewCategoryDialogView);
        addNewCategoryDialogView.findViewById(R.id.add_category_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCategory(addNewCategoryDialogView, addCategoryDialog);
            }
        });
        addNewCategoryDialogView.findViewById(R.id.add_category_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategoryDialog.dismiss();
            }
        });

        addCategoryDialog.show();
    }

    private void showEditCategoryDialog(String id, String categoryName, String categoryDesc, String categoryImageUrl, String categoryPosition) {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View editCategoryDialogView = factory.inflate(R.layout.edit_category, null);
        final AlertDialog editCategoryDialog = new AlertDialog.Builder(getContext()).create();
        editCategoryDialog.setView(editCategoryDialogView);

        //init Views
        editCatName = editCategoryDialogView.findViewById(R.id.edit_category_name_layout);
        editCatDesc = editCategoryDialogView.findViewById(R.id.edit_category_desc_layout);
        editCatImgUrl = editCategoryDialogView.findViewById(R.id.edit_category_img_url_layout);

        //Put values into editTexts
        Objects.requireNonNull(editCatName.getEditText()).setText(categoryName);
        Objects.requireNonNull(editCatDesc.getEditText()).setText(categoryDesc);
        Objects.requireNonNull(editCatImgUrl.getEditText()).setText(categoryImageUrl);

        editCategoryDialogView.findViewById(R.id.edit_category_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCategory(id, categoryPosition, editCategoryDialog);
            }
        });
        editCategoryDialogView.findViewById(R.id.edit_category_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCategoryDialog.dismiss();
            }
        });

        editCategoryDialog.show();
    }

    private void addNewCategory(View addNewCategoryDialogView, AlertDialog addCategoryDialog) {
        //init Views
        newCatName = addNewCategoryDialogView.findViewById(R.id.new_category_name_layout);
        newCatDesc = addNewCategoryDialogView.findViewById(R.id.new_category_desc_layout);
        newCatImgUrl = addNewCategoryDialogView.findViewById(R.id.new_category_img_url_layout);

        //Check if valid values
        if (Objects.requireNonNull(newCatName.getEditText()).getText().toString().isEmpty()) {
            showError(newCatName, getString(R.string.name_is_not_valid));
        } else if (Objects.requireNonNull(newCatDesc.getEditText()).getText().toString().isEmpty()) {
            showError(newCatDesc, getString(R.string.desc_is_not_valid));
        } else if (Objects.requireNonNull(newCatImgUrl.getEditText()).getText().toString().isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(getString(R.string.alertdialog_title_image_not_valid_label));
            alertDialog.setMessage(getString(R.string.alertdialog_message_image_not_valid_label));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            //Get the Values
            catName = Objects.requireNonNull(newCatName.getEditText()).getText().toString();
            catDesc = Objects.requireNonNull(newCatDesc.getEditText()).getText().toString();
            catImgUrl = Objects.requireNonNull(newCatImgUrl.getEditText()).getText().toString();

            //Get unique key ID
            String key = mCategoryRef.push().getKey();
            categoriesCount++;

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(getString(R.string.ref_category_id), key);
            hashMap.put(getString(R.string.ref_category_position), categoriesCount.toString());
            hashMap.put(getString(R.string.ref_category_name), catName);
            hashMap.put(getString(R.string.ref_category_desc), catDesc);
            hashMap.put(getString(R.string.ref_category_image_url), catImgUrl);


            mCategoryRef.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getContext(), getString(R.string.successful_add_of_category), Toast.LENGTH_SHORT).show();
                    addCategoryDialog.dismiss();
                }
            });
        }

    }

    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }

    private void EditCategory(String categoryId, String categoryPosition, AlertDialog editCategoryDialog) {
        //Check if valid values
        if (Objects.requireNonNull(editCatName.getEditText()).getText().toString().isEmpty()) {
            showError(editCatName, getString(R.string.name_is_not_valid));
        } else if (Objects.requireNonNull(editCatDesc.getEditText()).getText().toString().isEmpty()) {
            showError(editCatDesc, getString(R.string.desc_is_not_valid));
        } else if (Objects.requireNonNull(editCatImgUrl.getEditText()).getText().toString().isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(getString(R.string.alertdialog_title_image_not_valid_label));
            alertDialog.setMessage(getString(R.string.alertdialog_message_image_not_valid_label));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            //Get the Values
            catName = Objects.requireNonNull(editCatName.getEditText()).getText().toString();
            catDesc = Objects.requireNonNull(editCatDesc.getEditText()).getText().toString();
            catImgUrl = Objects.requireNonNull(editCatImgUrl.getEditText()).getText().toString();

            //Get unique key ID
            String key = mCategoryRef.child(categoryId).getKey();
            Log.d(TAG, "EditCategory: key: " + key);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(getString(R.string.ref_category_id), key);
            hashMap.put(getString(R.string.ref_category_position), categoryPosition);
            hashMap.put(getString(R.string.ref_category_name), catName);
            hashMap.put(getString(R.string.ref_category_desc), catDesc);
            hashMap.put(getString(R.string.ref_category_image_url), catImgUrl);

            mCategoryRef.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getContext(), getString(R.string.successful_edit_of_category), Toast.LENGTH_SHORT).show();
                    editCategoryDialog.dismiss();
                }
            });
        }

    }

    public void showAlertDialogDeleteProductFromCategory(String categoryId, String categoryName) {
        mProductRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: test321 " + Objects.equals(categorySnapshot.child(getString(R.string.ref_product_category_id)).getValue(), categoryId));
                    if (Objects.equals(categorySnapshot.child(getString(R.string.ref_product_category_id)).getValue(), categoryId)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle(getString(R.string.are_you_sure_to_delete_product_from_category_message_label))
                                .setIcon(R.drawable.ic_baseline_delete_24)
                                .setMessage(getString(R.string.are_you_sure_to_delete_product_items_message_label)
                                        + " "
                                        + categorySnapshot.child(getString(R.string.ref_product_name)).getValue()
                                        + " "
                                        + getString(R.string.owns_at_category_name_label)
                                        + ";"
                                )
                                .setNegativeButton(getString(R.string.cancel_admin_btn_label), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        dialoginterface.cancel();
                                    }
                                })
                                .setPositiveButton(getString(R.string.delete_label), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        categorySnapshot.getRef().removeValue();
                                    }
                                }).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void ShowAlertDialogDeleteCategory(String categoryId, String categoryName) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getString(R.string.are_you_sure_to_delete_category_title_label))
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setMessage(getString(R.string.are_you_sure_to_delete_category_message_label) + " " + categoryName + ";")
                .setNegativeButton(getString(R.string.cancel_admin_btn_label), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.delete_label), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        mCategoryRef.child(categoryId).removeValue();

                    }
                }).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Get all categories count
        mCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesCount = snapshot.getChildrenCount();
                Log.d(TAG, "onDataChange: count of Categories: " + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}