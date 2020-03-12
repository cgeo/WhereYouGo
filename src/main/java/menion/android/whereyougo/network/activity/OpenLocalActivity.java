/*
 * Copyright 2020 c:geo team <whereyougo@cgeo.org>
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

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.network.DownloadCartridgeTask;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;

/**
 * @author JakeDot <mail@jakobmayer.at>
 */
public class OpenLocalActivity extends CustomActivity {
    private static final String TAG = "OpenLocalActivity";
    private ImportTask importTask;
    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            uri = getIntent().getData();

            if (uri == null) {
                finish();
                return;
            }

            setContentView(R.layout.layout_details);

            TextView tvName = (TextView) findViewById(R.id.layoutDetailsTextViewName);
            tvName.setText(R.string.import_cartridge);

            TextView tvDescription = (TextView) findViewById(R.id.layoutDetailsTextViewDescription);
            TextView tvState = (TextView) findViewById(R.id.layoutDetailsTextViewState);

            ContentResolver contentResolver = getContentResolver();

            Cursor c = contentResolver.query(uri, null, null, null, null);

            int nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIdx = c.getColumnIndex(OpenableColumns.SIZE);

            c.moveToFirst();

            final String name = c.getString(nameIdx);
            final long size = c.getLong(sizeIdx);

            File cartridgeFile = FileSystem.findFile(name);
            tvDescription.setText(
                String.format("File:\n%s",
                        Optional.ofNullable(name).orElse(uri.toString())
                ));

            ImageView ivImage = (ImageView) findViewById(R.id.mediaImageView);
            ivImage.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
            try {
                Bitmap icon = Images.getImageB(R.drawable.icon_gc_wherigo);
                ivImage.setImageBitmap(icon);
            } catch (Exception e) {
            }

            CustomDialog.setBottom(this, getString(R.string.import_button), new CustomDialog.OnClickListener() {
                @Override
                public boolean onClick(CustomDialog dialog, View v, int btn) {
                    if (importTask != null && importTask.getStatus() != Status.FINISHED) {
                        importTask.cancel(true);
                        importTask = null;
                    } else {
                        importTask = new ImportTask(OpenLocalActivity.this, name, size);
                        importTask.execute(uri);
                    }
                    return true;

                }
            }, null, null, getString(R.string.start), new CustomDialog.OnClickListener() {
                @Override
                public boolean onClick(CustomDialog dialog, View v, int btn) {
                    Intent intent = new Intent(OpenLocalActivity.this, MainActivity.class);
                    intent.putExtra("cguid", name);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    OpenLocalActivity.this.finish();
                    return true;
                }
            });
            Button buttonDownload = (Button) findViewById(R.id.button_positive);
            Button buttonStart = (Button) findViewById(R.id.button_negative);
            buttonStart.setEnabled(cartridgeFile != null);

        } catch (Exception e) {
            finish();
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (importTask != null && importTask.getStatus() != Status.FINISHED) {
            importTask.cancel(true);
            importTask = null;
        }
    }

    class ImportTask extends AsyncTask<Uri, ImportTask.Progress, Boolean> {
        final ProgressDialog progressDialog;
        final String name;
        final Long size;

        private String errorMessage;

        public ImportTask(final Context context, String name, long size) {
            super();

            this.name = name;
            this.size = size;

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
                    if (importTask != null && importTask.getStatus() != Status.FINISHED) {
                        importTask.cancel(false);
                        importTask = null;
                        Log.i("down", "cancel");
                        ManagerNotify.toastShortMessage(context, getString(R.string.cancelled));
                    }
                }
            });
        }

        @Override
        protected Boolean doInBackground(Uri... uris) {

            Uri uri = uris != null && uris.length > 0 ? uris[0] : null;
            try {
                InputStream input = getContentResolver().openInputStream(uri);

                return download(name, input, size);
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(new Progress(DownloadCartridgeTask.State.FAIL));

                return false;
            }

        }



        private boolean download(String filename, InputStream input, long total) {
            File file = new File(FileSystem.ROOT + filename);
            long completed = 0;
            int length;
            byte[] buffer = new byte[1024];
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(input);
                bos = new BufferedOutputStream(new FileOutputStream(file));
                publishProgress(new Progress(completed, total));
                while ((length = bis.read(buffer)) > 0 && !isCancelled()) {
                    bos.write(buffer, 0, length);
                    completed += length;
                    publishProgress(new Progress(completed, total));
                }
            } catch (IOException e) {
                Logger.e(TAG, "download(" + filename + ")", e);
                errorMessage = e.getMessage();
            } finally {
                Utils.closeStream(bis);
                Utils.closeStream(bos);
                if (completed != total) {
                    file.delete();
                } else {
                    publishProgress(new Progress(DownloadCartridgeTask.State.SUCCESS));
                }
            }
            return completed == total;
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
                OpenLocalActivity.this.finish();
                OpenLocalActivity.this.startActivity(OpenLocalActivity.this.getIntent());
            } else {
                progressDialog.setIndeterminate(false);
            }
            importTask = null;
        }

        @Override
        protected void onProgressUpdate(Progress... values) {
            super.onProgressUpdate(values);
            Progress progress = values[0];
            String suffix = "";
            if (progress.getState() == DownloadCartridgeTask.State.SUCCESS) {
                suffix = String.format(": %s", getString(R.string.ok));
            } else if (progress.getState() == DownloadCartridgeTask.State.FAIL) {
                if (progress.getMessage() == null){
                    suffix = String.format(": %s", getString(R.string.error));
                } else {
                    suffix = String.format(": %s(%s)", getString(R.string.error), progress.getMessage());
                }
            }

            progressDialog.setIndeterminate(false);
            progressDialog.setMax((int) progress.getTotal());
            progressDialog.setProgress((int) progress.getCompleted());
            progressDialog.setMessage(Html.fromHtml(getString(R.string.download_state_download) + suffix));

        }

        public class Progress {
            final DownloadCartridgeTask.State state;
            long total;
            long completed;
            String message;

            public Progress(DownloadCartridgeTask.State state) {
                this.state = state;
            }

            public Progress(DownloadCartridgeTask.State state, String message) {
                this.state = state;
                this.message = message;
            }

            public Progress(long completed, long total) {
                this.state = DownloadCartridgeTask.State.WORKING;
                this.total = total;
                this.completed = completed;
            }

            public DownloadCartridgeTask.State getState() {
                return state;
            }

            public String getMessage() {
                return message;
            }

            public long getTotal() {
                return total;
            }

            public long getCompleted() {
                return completed;
            }
        }
    }
}

