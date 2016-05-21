package com.maiiz.kinaraidee.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.maiiz.kinaraidee.fragment.HistoryFragment;
import com.maiiz.kinaraidee.fragment.SettingsFragment;

import java.lang.ref.WeakReference;

/**
 * Created by MaiiZ on 5/19/2016 AD.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
  private final SparseArray<WeakReference<Fragment>> instantiatedFragments = new SparseArray<>();
  private final int PAGE_COUNT = 2;
  private final String[] pageTitle = new String[]{"Settings", "History"};

  public ViewPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return SettingsFragment.newInstance();
      case 1:
        return HistoryFragment.newInstance();
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return PAGE_COUNT;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return pageTitle[position];
  }

  @Override
  public Object instantiateItem(final ViewGroup container, final int position) {
    final Fragment fragment = (Fragment) super.instantiateItem(container, position);
    instantiatedFragments.put(position, new WeakReference<>(fragment));
    return fragment;
  }

  @Override
  public void destroyItem(final ViewGroup container, final int position, final Object object) {
    instantiatedFragments.remove(position);
    super.destroyItem(container, position, object);
  }

  @Nullable
  public Fragment getFragment(final int position) {
    final WeakReference<Fragment> wr = instantiatedFragments.get(position);
    if (wr != null) {
      return wr.get();
    } else {
      return null;
    }
  }
}
