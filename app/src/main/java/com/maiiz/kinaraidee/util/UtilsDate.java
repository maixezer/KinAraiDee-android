package com.maiiz.kinaraidee.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MaiiZ on 5/21/2016 AD.
 */
public class UtilsDate {

  public static String convertDate(String oldDate) {
    try {
      Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(
        oldDate.substring(0, 24)
      );

      return new SimpleDateFormat("dd/MM/yyyy").format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static String getCurrentDate() {
    return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
  }
}
