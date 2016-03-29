package menion.android.whereyougo.gui.dialog;

import menion.android.whereyougo.R;
import menion.android.whereyougo.VersionInfo;
import menion.android.whereyougo.gui.extension.activity.CustomMainActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialogFragment;
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
    try {
      PackageManager pm = getActivity().getPackageManager();
      String versionName = pm.getPackageInfo(getActivity().getPackageName(), 0).versionName;
      buffer.append("<div align=\"center\"><h3><b>" + versionName + "</b></h3></div>");
    } catch (Exception e) {
    }
    // add info
    buffer.append(CustomMainActivity.loadAssetString(PreferenceValues.getLanguageCode()
        + "_first.html"));
    // add about info
    buffer.append("<br /><br />");
    buffer.append(CustomMainActivity.loadAssetString(PreferenceValues.getLanguageCode()
            + "_about.html"));
    // add news
    buffer.append("<br /><br />");
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
