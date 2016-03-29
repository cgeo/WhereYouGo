/*
 * Copyright 2014 biylda <biylda@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package menion.android.whereyougo.network.activity;

import java.io.File;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.activity.CustomMainActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.network.DownloadCartridgeTask;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Images;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

  public class DownloadCartridgeActivity extends CustomMainActivity {
    private static final String TAG = "DownloadCartridgeActivity";
    private TextView tvDescription;
    private TextView tvState;
    private Button buttonDownload;
    private Button buttonStart;
    private DownloadCartridgeTask downloadTask;
    private String cguid;
    private File cartridgeFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Uri uri = getIntent().getData();
      try {
        cguid = uri.getQueryParameter("CGUID");
      } catch (Exception e) {
      }
      if (cguid == null) {
        finish();
        return;
      }
      cartridgeFile = FileSystem.findFile(cguid);

      setContentView(R.layout.layout_details);

      TextView tvName = (TextView) findViewById(R.id.layoutDetailsTextViewName);
      tvName.setText(Html.fromHtml(getString(R.string.download_cartridge)));

      tvDescription = (TextView) findViewById(R.id.layoutDetailsTextViewDescription);
      tvState = (TextView) findViewById(R.id.layoutDetailsTextViewState);

      if (cartridgeFile != null) {
        tvDescription.setText(Html.fromHtml("CGUID " + cguid + "\n" + cartridgeFile.getName().replace(cguid + "_", "")));
        tvState.setText(Html.fromHtml(getString(R.string.download_successful)));
      }else{
        tvDescription.setText(Html.fromHtml("CGUID " + cguid));
      }

      ImageView ivImage = (ImageView) findViewById(R.id.layoutDetailsImageViewImage);
      ivImage.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
      try {
        Bitmap icon = Images.getImageB(R.drawable.icon_gc_wherigo);
        ivImage.setImageBitmap(icon);
      } catch (Exception e) {
      }

      CustomDialog.setBottom(this, getString(R.string.download), new CustomDialog.OnClickListener() {
        @Override
        public boolean onClick(CustomDialog dialog, View v, int btn) {
          if (downloadTask != null && downloadTask.getStatus() != Status.FINISHED) {
            downloadTask.cancel(true);
            downloadTask = null;
          } else {
            String username = Preferences.GC_USERNAME;
            String password = Preferences.GC_PASSWORD;
            downloadTask = new DownloadTask(DownloadCartridgeActivity.this, username, password);
            downloadTask.execute(cguid);
          }
          return true;

        }
      }, null, null, getString(R.string.start), new CustomDialog.OnClickListener() {
        @Override
        public boolean onClick(CustomDialog dialog, View v, int btn) {
          PreferenceValues.setCurrentActivity(null);
          Intent intent = new Intent(DownloadCartridgeActivity.this, MainActivity.class);
          intent.putExtra("cguid", cguid);
          startActivity(intent);
          DownloadCartridgeActivity.this.finish();
          return true;
        }
      });
      buttonDownload = (Button) findViewById(R.id.button_positive);
      buttonStart = (Button) findViewById(R.id.button_negative);
      buttonStart.setEnabled(cartridgeFile != null);
    }

    class DownloadTask extends DownloadCartridgeTask {
      ProgressDialog progressDialog;

      public DownloadTask(final Context context, String username, String password) {
        super(username, password);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(1);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {

          @Override
          public void onCancel(DialogInterface arg0) {
            if (downloadTask != null && downloadTask.getStatus() != Status.FINISHED) {
              downloadTask.cancel(false);
              downloadTask = null;
              Log.i("down", "cancel");
              Toast.makeText(context, "cancelled", Toast.LENGTH_LONG).show();
            }
          }
        });
      }

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
      }

      @Override
      protected void onPostExecute(final Boolean result) {
        super.onPostExecute(result);
        if (result) {
          progressDialog.dismiss();
          MainActivity.refreshCartridges();
          cartridgeFile = FileSystem.findFile(cguid);
          if (cartridgeFile != null) {
            tvDescription.setText(Html.fromHtml("CGUID " + cguid + "\n" + cartridgeFile.getName().replace(cguid + "_", "")));
            tvState.setText(Html.fromHtml(getString(R.string.download_successful)));
          }else{
            tvDescription.setText(Html.fromHtml("CGUID " + cguid));
            tvState.setText(Html.fromHtml(""));
          }
          buttonStart.setEnabled(cartridgeFile != null);
        } else {
          progressDialog.setIndeterminate(false);
        }
        downloadTask = null;
      }

      @Override
      protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        Progress progress = values[0];
        switch (progress.getTask()) {
          case PING:
            progressDialog.setIndeterminate(true);
            if (progress.getState() == State.WORKING) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_connect)));
            } else if (progress.getState() == State.SUCCESS) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_connect)+": " + getString(R.string.ok)));
            } else if (progress.getState() == State.FAIL) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_connect)+": " + getString(R.string.error)));
            }
            break;
          case LOGIN:
            progressDialog.setIndeterminate(true);
            if (progress.getState() == State.WORKING) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_login)));
            } else if (progress.getState() == State.SUCCESS) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_login)+": " + getString(R.string.ok)));
            } else if (progress.getState() == State.FAIL) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_login)+": " + getString(R.string.error)));
            }
            break;
          case LOGOUT:
            progressDialog.setIndeterminate(true);
            if (progress.getState() == State.WORKING) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_logout)));
            } else if (progress.getState() == State.SUCCESS) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_logout)+": " + getString(R.string.ok)));
            } else if (progress.getState() == State.FAIL) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_logout)+": " + getString(R.string.error)));
            }
            break;
          case DOWNLOAD:
            progressDialog.setIndeterminate(true);
            if (progress.getState() == State.WORKING) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_download)));
            } else if (progress.getState() == State.SUCCESS) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_download)+": " + getString(R.string.ok)));
            } else if (progress.getState() == State.FAIL) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_download)+": " + getString(R.string.error)));
            }
            break;
          case DOWNLOAD_SINGLE:
            progressDialog.setIndeterminate(false);
            progressDialog.setMax((int) progress.getTotal());
            progressDialog.setProgress((int) progress.getCompleted());
            if (progress.getState() == State.WORKING) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_download)));
            } else if (progress.getState() == State.SUCCESS) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_download)+": " + getString(R.string.ok)));
            } else if (progress.getState() == State.FAIL) {
              progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_download)+": " + getString(R.string.error)));
            }
            break;
        }
      }

    }

    @Override
    public void onDestroy() {
      super.onDestroy();

      if (downloadTask != null && downloadTask.getStatus() != Status.FINISHED) {
        downloadTask.cancel(true);
        downloadTask = null;
      }
    }

    protected void startCartridge(String cguid) {
      PreferenceValues.setCurrentActivity(null);
      Intent intent = new Intent(this, MainActivity.class);
      intent.putExtra("cguid", cguid);
      startActivity(intent);
    }

    @Override
    protected void eventCreateLayout() {
      // TODO Auto-generated method stub
      
    }

    @Override
    protected void eventDestroyApp() {
      // TODO Auto-generated method stub
      
    }

    @Override
    protected void eventFirstInit() {
      // TODO Auto-generated method stub
      
    }

    @Override
    protected void eventRegisterOnly() {
      // TODO Auto-generated method stub
      
    }

    @Override
    protected void eventSecondInit() {
      // TODO Auto-generated method stub
      
    }

    @Override
    protected String getCloseAdditionalText() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    protected int getCloseValue() {
      // TODO Auto-generated method stub
      return 0;
    }

    
  }

