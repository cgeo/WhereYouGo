package menion.android.whereyougo.gui.dialog;

import menion.android.whereyougo.R;
import menion.android.whereyougo.VersionInfo;
import menion.android.whereyougo.gui.extension.activity.CustomMainActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialogFragment;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.utils.A;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

public class AboutDialog extends CustomDialogFragment {

  @Override
  public Dialog createDialog(Bundle savedInstanceState) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<div align=\"center\"><h2><b>WhereYouGo</b></h2></div>");
    buffer.append("<div>");
    buffer.append("<b>Wherigo player for Android device</b><br /><br />");
    try {
      PackageManager pm = getActivity().getPackageManager();
      buffer.append(Locale.get(R.string.version) + "<br />&nbsp;&nbsp;<b>"
          + pm.getPackageInfo(getActivity().getPackageName(), 0).versionName + "</b><br /><br />");
    } catch (Exception e) {
    }
    buffer.append(getString(R.string.web_page));
    buffer
        .append("<br />&nbsp;&nbsp;<a href=\"https://groups.google.com/d/forum/whereyougo\">https://groups.google.com/d/forum/whereyougo</a>");
    buffer
        .append("<br />&nbsp;&nbsp;<a href=\"mailto:whereyougo@googlegroups.com/\">whereyougo@googlegroups.com</a>");
    buffer.append("<br /><br />");
    buffer.append(getString(R.string.author));
    buffer.append("<br />&nbsp;&nbsp;<b>Menion Asamm</b>");
    buffer
        .append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small><a href=\"https://code.google.com/p/android-whereyougo/\">https://code.google.com/r/android-whereyougo</a></small>");
    buffer
        .append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small><a href=\"http://forum.locusmap.eu/\">http://forum.locusmap.eu</a></small>");
    buffer.append("<br /><br />");
    buffer.append(getString(R.string.coauthor));
    buffer.append("<br />&nbsp;&nbsp;<b>biylda</b>");
    buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small><a href=\"https://code.google.com/r/biylda-whereyougo/\">https://code.google.com/r/biylda-whereyougo</a></small>");
    buffer.append("<br />&nbsp;&nbsp;<b>CachingFoX</b>");
    buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small><a href=\"https://code.google.com/r/cachingfox-whereyougo/\">https://code.google.com/r/cachingfox-whereyougo</a></small>");
    buffer.append("<br /><br />");
    buffer.append(getString(R.string.translation));
    buffer.append("<br />&nbsp;&nbsp;<b>Deutsch</b> - Stefan Amstart, Kilian Högy, Carsten Pietzsch");
    buffer.append("<br />&nbsp;&nbsp;<b>Français</b> - Nam");
    buffer.append("<br /><br />");
    buffer.append(getString(R.string.libraries));
    buffer.append("<br />&nbsp;&nbsp;<b>OpenWIG</b>");
    buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;matejcik");
    buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small><a href=\"https://code.google.com/p/openwig/\">https://code.google.com/p/openwig</a></small>");
    buffer.append("<br />&nbsp;&nbsp;<b>Kahlua</b>");
    buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;Kristofer Karlsson");
    buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small><a href=\"https://code.google.com/p/kahlua/\">https://code.google.com/p/kahlua</a></small>");
    buffer.append("<br />&nbsp;&nbsp;<b>MapsForge</b>");
    buffer
        .append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small><a href=\"https://code.google.com/p/mapsforge/\">https://code.google.com/p/mapsforge</a></small>");
    buffer.append("</div>");

    // add info
    buffer.append("<br />");
    buffer.append(CustomMainActivity.loadAssetString(PreferenceValues.getLanguageCode()
        + "_first.html"));
    // add news
    buffer.append("<br />");
    buffer.append(VersionInfo.getNews(1, PreferenceValues.getApplicationVersionActual()));

    WebView webView = new WebView(A.getMain());
    webView.loadData(buffer.toString(), "text/html; charset=utf-8", "utf-8");
    webView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT));
    webView.setBackgroundColor(Color.WHITE);

    return new AlertDialog.Builder(getActivity()).setTitle(R.string.about_application)
        .setIcon(R.drawable.ic_title_logo).setView(webView).setNeutralButton(R.string.close, null)
        .create();
  }
}
