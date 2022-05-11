package menion.android.whereyougo.gui.dialog;

import menion.android.whereyougo.R;
import menion.android.whereyougo.VersionInfo;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialogFragment;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.utils.A;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

public class AboutDialog extends CustomDialogFragment {

    @Override
    public Dialog createDialog(Bundle savedInstanceState) {
        String buffer = "<div align=\"center\"><h2><b>" + A.getAppName() + "</b></h2></div>" +
                "<div align=\"center\"><h3><b>" + A.getAppVersion() + "</b></h3></div>" +
                MainActivity.loadAssetString(PreferenceValues.getLanguageCode() + "_first.html") +
                "<br /><br />" +
                MainActivity.loadAssetString(PreferenceValues.getLanguageCode() + "_about.html") +
                "<br /><br />" +
                VersionInfo.getNews(1, PreferenceValues.getApplicationVersionActual());
        // add info
        // add about info
        // add news

        WebView webView = new WebView(A.getMain());
        webView.loadData(buffer, "text/html; charset=utf-8", "utf-8");
        webView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        webView.setBackgroundColor(Color.WHITE);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.about_application)
                .setIcon(R.drawable.ic_title_logo).setView(webView).setNeutralButton(R.string.close, null)
                .create();
    }
}
