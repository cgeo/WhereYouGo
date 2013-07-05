package menion.android.whereyougo.gui.dialogs;

import menion.android.whereyougo.MainAfterStart;
import menion.android.whereyougo.R;
import menion.android.whereyougo.settings.Loc;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.A;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

public class DialogMain extends DialogFragmentEx {

	@Override
	public Dialog createDialog(Bundle savedInstanceState) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<div align=\"center\"><h2><b>WhereYouGo</b></h2></div>");
		buffer.append("<div>");
		buffer.append("<b>Wherigo player for Android device</b><br /><br />");
		try {
			PackageManager pm = getActivity().getPackageManager();
			buffer.append(Loc.get(R.string.version) + "<br />&nbsp;&nbsp;<b>" + 
					pm.getPackageInfo(getActivity().getPackageName(), 0).versionName + "</b><br /><br />");
		} catch (Exception e) {}
		buffer.append(getString(R.string.author) + "<br />&nbsp;&nbsp;<b>Menion Asamm</b><br /><br />");
		buffer.append(getString(R.string.web_page) + "<br />&nbsp;&nbsp;<b><a href=\"http://forum.asamm.cz\">http://forum.asamm.cz</a></b><br /><br />");
		buffer.append(getString(R.string.libraries));
		buffer.append("<br />&nbsp;&nbsp;<b>OpenWig</b>");
		buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;Matejicek");
		buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small>http://code.google.com/p/openwig</small>");
		buffer.append("<br />&nbsp;&nbsp;<b>Kahlua</b>");
		buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;Kristofer Karlsson");
		buffer.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;<small>http://code.google.com/p/kahlua/</small>");
		buffer.append("</div>");
		
		// add news
		buffer.append(MainAfterStart.getNews(1, 
				Settings.getApplicationVersionActual()));
		
    	WebView webView = new WebView(A.getMain());
		webView.loadData(buffer.toString(), "text/html", "utf-8");
		webView.setLayoutParams(new  ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		webView.setBackgroundColor(Color.WHITE);

		return new AlertDialog.Builder(getActivity()).
				setTitle(R.string.about_application).
				setIcon(R.drawable.ic_title_logo).
				setView(webView).
				setNeutralButton(R.string.close, null).
				create();
	}
}
