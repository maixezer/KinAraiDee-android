package com.maiiz.kinaraidee;

import android.app.Application;

import com.maiiz.kinaraidee.manager.Contextor;

/**
 * Created by MaiiZ on 5/10/2016 AD.
 */
public class KinAraiDeeApplication extends Application{

  @Override
  public void onCreate() {
    super.onCreate();

    Contextor.getInstance().init(getApplicationContext());
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }
}
