package gr.indahouse.utils;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import gr.indahouse.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder{

    private final String TAG = "CategoryViewHolder";

    public CircleImageView catImage;
    public TextView catName, catDescription;
    public CardView catCardView;
    public ConstraintLayout singleViewCategoryConstraint;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        singleViewCategoryConstraint = itemView.findViewById(R.id.single_view_category_constraint);
        catImage = itemView.findViewById(R.id.categoryImage);
        catName = itemView.findViewById(R.id.categoryName);
        catDescription = itemView.findViewById(R.id.categoryDescription);
        catCardView = itemView.findViewById(R.id.singleCategoryCardView);
    }
}
