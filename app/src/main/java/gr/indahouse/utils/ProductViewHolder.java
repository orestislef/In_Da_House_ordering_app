package gr.indahouse.utils;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.indahouse.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    private final String TAG = "ProductViewHolder";

    CircleImageView prodProfileImage;
    TextView prodName, prodDescription;
    CardView prodCardView;
    ConstraintLayout singleViewProductConstraint;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        singleViewProductConstraint = itemView.findViewById(R.id.single_view_product_constraint);
        prodProfileImage = itemView.findViewById(R.id.productImage);
        prodName = itemView.findViewById(R.id.productName);
        prodDescription = itemView.findViewById(R.id.productDescription);
        prodCardView = itemView.findViewById(R.id.singleProductCardView);

    }
}
