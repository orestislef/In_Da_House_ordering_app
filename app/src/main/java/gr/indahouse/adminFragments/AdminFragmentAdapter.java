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
            case 0:
                return new CategoryFragment();
            case 1:
                return new ProductFragment();
            case 2 :
                return new ExtrasFragment();
        }
        return new CategoryFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
