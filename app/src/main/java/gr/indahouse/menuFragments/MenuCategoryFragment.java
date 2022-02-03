package gr.indahouse.menuFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.squareup.picasso.Picasso;

import java.util.Objects;

import gr.indahouse.R;
import gr.indahouse.utils.Categories;
import gr.indahouse.utils.CategoryViewHolder;

public class MenuCategoryFragment extends Fragment {

    private static final String TAG = "MenuCategoryFragment";
    FirebaseRecyclerAdapter<Categories, CategoryViewHolder> categoryAdapter;
    FirebaseRecyclerOptions<Categories> categoryOptions;

    DatabaseReference mCategoryRef;
    RecyclerView catRecyclerView;

    String catName, catDesc, catUrl;

    View view;


    public MenuCategoryFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static MenuCategoryFragment newInstance() {
        MenuCategoryFragment fragment = new MenuCategoryFragment();
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
        view = inflater.inflate(R.layout.fragment_menu_category, container, false);

        //init Views
        catRecyclerView = view.findViewById(R.id.menu_category_recyclerView);

        //init references
        mCategoryRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_category));

        //init LineaLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        catRecyclerView.setLayoutManager(linearLayoutManager);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(getString(R.string.category_menu_label));


        loadCategories();

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

                holder.catCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //inflate MenuCategoryFragment//

                        Bundle bundle = new Bundle();
                        bundle.putString(getString(R.string.ref_product_category_id),model.getCategoryId());
                        bundle.putString(getString(R.string.ref_category_name),model.getCategoryName());
                        MenuProductFragment menuProductFragment = new MenuProductFragment();
                        menuProductFragment.setArguments(bundle);

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragmentContainerView,menuProductFragment);
                        ft.addToBackStack("MenuCategoryFragment");
                        ft.commit();
                    }
                });
            }
        };
        categoryAdapter.startListening();
        catRecyclerView.setAdapter(categoryAdapter);
        Log.d(TAG, "onDataChange: CategoryAdapterStartsListening");
    }
}
