package gr.indahouse.menuFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class MenuFragmentAdapter extends FragmentStateAdapter {
    public MenuFragmentAdapter(FragmentManager fm, Lifecycle lifecycle) {
        super(fm, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new MenuProductFragment();
        }
        return new MenuCategoryFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
