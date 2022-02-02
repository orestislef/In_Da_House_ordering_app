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
import gr.indahouse.utils.Extras;
import gr.indahouse.utils.ExtrasViewHolder;

public class ExtrasFragment extends Fragment {

    private static final String TAG = "ExtrasFragment";

    FirebaseRecyclerAdapter<Extras, ExtrasViewHolder> extrasAdapter;
    FirebaseRecyclerOptions<Extras> extrasOptions;

    DatabaseReference mExtraRef;

    RecyclerView extraRecyclerView;

    Button addExtraBtn;
    TextInputLayout newExtraNameTL, newExtraPriceTL, editExtraNameTL, editExtraPriceTL;
    String extraNameTL, extraPriceTL;
    ImageButton deleteExtraBtn;

    View view;

    public ExtrasFragment() {
        // Required empty public constructor
    }

    public static ExtrasFragment newInstance() {
        ExtrasFragment fragment = new ExtrasFragment();
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
        view = inflater.inflate(R.layout.fragment_extras, container, false);

        //init Views
        extraRecyclerView = view.findViewById(R.id.admin_extra_recycler_view);
        addExtraBtn = view.findViewById(R.id.add_extra_btn);
        deleteExtraBtn = view.findViewById(R.id.deleteExtraBtn);

        //init references
        mExtraRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_extra));

        //init LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        extraRecyclerView.setLayoutManager(linearLayoutManager);

        loadExtras();

        addExtraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExtraDialog();
            }
        });
        return view;
    }

    private void showAddExtraDialog() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View addNewExtraDialogView = factory.inflate(R.layout.add_extra, null);
        final AlertDialog addExtraDialog = new AlertDialog.Builder(getContext()).create();
        addExtraDialog.setView(addNewExtraDialogView);

        addNewExtraDialogView.findViewById(R.id.add_extra_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewExtra(addNewExtraDialogView, addExtraDialog);
            }
        });
        addNewExtraDialogView.findViewById(R.id.add_extra_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExtraDialog.dismiss();
            }
        });

        addExtraDialog.show();
    }

    private void addNewExtra(@NonNull View addNewExtraDialogView, AlertDialog addExtraDialog) {
        newExtraNameTL = addNewExtraDialogView.findViewById(R.id.new_extra_name_layout);
        newExtraPriceTL = addNewExtraDialogView.findViewById(R.id.new_extra_price_layout);

        //Check if valid values
        if (Objects.requireNonNull(newExtraNameTL.getEditText()).getText().toString().isEmpty()) {
            showError(newExtraNameTL, getString(R.string.name_is_not_valid));
        } else if (Objects.requireNonNull(newExtraPriceTL.getEditText()).getText().toString().isEmpty()) {
            showError(newExtraPriceTL, getString(R.string.price_is_not_valid));
        } else {
            //Get the values
            extraNameTL = newExtraNameTL.getEditText().getText().toString();
            extraPriceTL = newExtraPriceTL.getEditText().getText().toString();

            //Get Unique key ID
            String key = mExtraRef.push().getKey();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(getString(R.string.ref_extra_id), key);
            hashMap.put(getString(R.string.ref_extra_name), extraNameTL);
            hashMap.put(getString(R.string.ref_extra_price), extraPriceTL);

            mExtraRef.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), getString(R.string.successful_add_of_extra), Toast.LENGTH_SHORT).show();
                    addExtraDialog.dismiss();

                }
            });
        }
    }

    private void loadExtras() {
        extrasOptions = new FirebaseRecyclerOptions.Builder<Extras>().setQuery(mExtraRef, Extras.class).build();

        extrasAdapter = new FirebaseRecyclerAdapter<Extras, ExtrasViewHolder>(extrasOptions) {

            @NonNull
            @Override
            public ExtrasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_extra, parent, false);
                return new ExtrasViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ExtrasViewHolder holder, int position, @NonNull Extras model) {
                Log.d(TAG, "onBindViewHolderExtras: " + position + " " + model.getExtraName());
                Log.d(TAG, "onBindViewHolderExtras: " + position + " " + model.getExtraPrice());

                holder.extraName.setText(model.getExtraName());
                holder.extraPrice.setText(model.getExtraPrice());

                //Do deleteBtn visible
                holder.deleteExtraBtn.setVisibility(View.VISIBLE);

                holder.deleteExtraBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle(getString(R.string.are_you_sure_to_delete_extra_title_label))
                                .setIcon(R.drawable.ic_baseline_delete_24)
                                .setMessage(getString(R.string.are_you_sure_to_delete_extra_message_label)+": "+model.getExtraName()+";")
                                .setNegativeButton(getString(R.string.cancel_admin_btn_label), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        dialoginterface.cancel();
                                    }
                                })
                                .setPositiveButton(getString(R.string.delete_label), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        mExtraRef.child(model.getExtraId()).removeValue();
                                    }
                                }).show();
                    }
                });

                holder.singleViewExtraConstraint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onBindViewHolder: extraId: " + model.getExtraId());
                        showEditExtraDialog(model.getExtraId(), model.getExtraName(), model.getExtraPrice());
                    }
                });
            }
        };
        extrasAdapter.startListening();
        extraRecyclerView.setAdapter(extrasAdapter);
        Log.d(TAG, "onDataChange: ExtraAdapterStartsListening: ");
    }

    private void showEditExtraDialog(String extraId, String extraName, String extraPrice) {
        //replace euro sing
        extraPrice = extraPrice.replaceAll("€", "");

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View editExtraDialogView = factory.inflate(R.layout.edit_extra, null);
        final AlertDialog editExtraDialog = new AlertDialog.Builder(getContext()).create();
        editExtraDialog.setView(editExtraDialogView);

        //init Views
        editExtraNameTL = editExtraDialogView.findViewById(R.id.edit_extra_name_layout);
        editExtraPriceTL = editExtraDialogView.findViewById(R.id.edit_extra_price_layout);

        //Put values into editTexts
        Objects.requireNonNull(editExtraNameTL.getEditText()).setText(extraName);
        Objects.requireNonNull(editExtraPriceTL.getEditText()).setText(extraPrice);

        editExtraDialogView.findViewById(R.id.edit_extra_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditExtra(extraId, editExtraDialog);
            }
        });
        editExtraDialogView.findViewById(R.id.edit_extra_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editExtraDialog.dismiss();
            }
        });

        editExtraDialog.show();
    }

    private void EditExtra(String extraId, AlertDialog editExtraDialog) {
        if (Objects.requireNonNull(editExtraNameTL.getEditText()).getText().toString().isEmpty()) {
            showError(editExtraNameTL, getString(R.string.name_is_not_valid));
        } else if (Objects.requireNonNull(editExtraPriceTL.getEditText()).getText().toString().isEmpty()) {
            showError(editExtraPriceTL, getString(R.string.price_is_not_valid));
        } else{
            //Get the values
            extraNameTL = editExtraNameTL.getEditText().getText().toString();
            extraPriceTL = editExtraPriceTL.getEditText().getText().toString();

            //Get key ID
            String key = mExtraRef.child(extraId).getKey();
            Log.d(TAG, "EditExtra: key: " + key);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(getString(R.string.ref_extra_id), key);
            hashMap.put(getString(R.string.ref_extra_name), extraNameTL);
            hashMap.put(getString(R.string.ref_extra_price), extraPriceTL);

            mExtraRef.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), getString(R.string.successful_edit_of_extra), Toast.LENGTH_SHORT).show();
                    editExtraDialog.dismiss();
                }
            });
        }
    }

    private void showError(@NonNull TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }
}