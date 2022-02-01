package gr.indahouse.utils;

import static java.security.AccessController.getContext;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import gr.indahouse.AdminActivity;
import gr.indahouse.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    private final String TAG = "ProductViewHolder";

    public TextView prodName, prodPrice, prodCategoryId;
    public CardView prodCardView;
    public ConstraintLayout singleViewProductConstraint;
    public ImageButton deleteBtn;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        singleViewProductConstraint = itemView.findViewById(R.id.single_view_product_constraint);
        prodName = itemView.findViewById(R.id.productName);
        prodPrice = itemView.findViewById(R.id.productPrice);
        prodCategoryId = itemView.findViewById(R.id.edit_product_category_id);
        prodCardView = itemView.findViewById(R.id.singleProductCardView);
        deleteBtn = itemView.findViewById(R.id.deleteProductBtn);
    }

}
