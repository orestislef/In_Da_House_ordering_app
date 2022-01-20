package gr.indahouse.utils;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import gr.indahouse.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    private final String TAG = "ProductViewHolder";
    public TextView prodName, prodPrice;
    public CardView prodCardView;
    public ConstraintLayout singleViewProductConstraint;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        singleViewProductConstraint = itemView.findViewById(R.id.single_view_product_constraint);
        prodName = itemView.findViewById(R.id.productName);
        prodCardView = itemView.findViewById(R.id.singleProductCardView);
        prodPrice = itemView.findViewById(R.id.productPrice);

    }
}
