package gr.indahouse.adminFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminFragmentAdapter extends FragmentStateAdapter {
    public AdminFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new AdminProductFragment();
            case 2:
                return new AdminExtrasFragment();
            default:
                return new AdminCategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
