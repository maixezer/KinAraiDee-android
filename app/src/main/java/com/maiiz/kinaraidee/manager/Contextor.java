package com.maiiz.kinaraidee.manager;

import android.content.Context;

/**
 * Created by MaiiZ on 5/10/2016 AD.
 */
public class Contextor {

  private static Contextor instance;

  public static Contextor getInstance() {
    if (instance == null) instance = new Contextor();
    return instance;
  }

  private Context mContext;

  private Contextor() {
  }

  public void init(Context context) {
    mContext = context;
  }

  public Context getContext() {
    return mContext;
  }
}
