package com.maiiz.kinaraidee.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.maiiz.kinaraidee.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by MaiiZ on 5/23/2016 AD.
 */
public class FullImageFragment extends Fragment {

  @BindView(R.id.fullFoodImage) ImageView fullFoodImage;

  private String foodImage;

  public static FullImageFragment newInstance() {
    FullImageFragment fragment = new FullImageFragment();
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_full_image, container, false);
    ButterKnife.bind(this, rootView);
    return rootView;
  }

  public void setImage(String foodImage) {
    this.foodImage = foodImage;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Glide.with(getContext())
      .load(foodImage)
      .diskCacheStrategy(DiskCacheStrategy.ALL)
      .into(fullFoodImage);
  }

  @OnClick(R.id.fullFoodImage)
  public void navigateToMain() {
    getActivity().getSupportFragmentManager().popBackStack();
  }
}
