package menion.android.whereyougo.network;

import menion.android.whereyougo.R;
import menion.android.whereyougo.utils.Logger;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginTask extends
        AsyncTask<Void, LoginTask.Progress, Boolean> {
    private static final String TAG = "LoginTask";
    private static final String LOGIN = "https://www.wherigo.com/login/default.aspx";

    private final String username;
    private final String password;
    private final AlertDialog loginCheckdialog;
    private OkHttpClient httpClient;
    private String errorMessage;

    public LoginTask(View view, String username, String password) {
        super();
        this.username = username;
        this.password = password;

        // Inflate Custom alert dialog view
        View customAlertDialogView = LayoutInflater.from(view.getContext())
                .inflate(R.layout.custom_progress_dialog, null, false);
        loginCheckdialog = new MaterialAlertDialogBuilder(view.getContext())
                .setView(customAlertDialogView)
                .setTitle(R.string.pref_gc_check_dialog_default_title)
                .setMessage(R.string.pref_gc_check_dialog_default_message)
                .setNegativeButton(R.string.pref_gc_check_dialog_default_negative_button, (dialog, which) -> {
                    this.cancel(true);
                })
                .create();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loginCheckdialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return init() && ping() && login() && logout();
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        switch (values[0].state) {
            case FAIL:
                loginCheckdialog.setMessage(loginCheckdialog.getContext().getString(R.string.pref_gc_check_dialog_message_unsuccessfull));
                loginCheckdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText(R.string.pref_gc_check_dialog_negative_button_close);
                loginCheckdialog
                        .findViewById(R.id.dialogProgressBarLayout)
                        .setVisibility(View.GONE);
                break;
            case SUCCESS:
                if (values[0].task == Task.LOGOUT) {
                    loginCheckdialog.setMessage(loginCheckdialog.getContext().getString(R.string.pref_gc_check_dialog_message_successfull));
                    loginCheckdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText(R.string.pref_gc_check_dialog_negative_button_close);
                    loginCheckdialog
                            .findViewById(R.id.dialogProgressBarLayout)
                            .setVisibility(View.GONE);
                }
                break;
            case WORKING:
                switch (values[0].task) {
                    case INIT:
                        loginCheckdialog.setMessage(loginCheckdialog.getContext().getString(R.string.pref_gc_check_dialog_message_init));
                        break;
                    case PING:
                        loginCheckdialog.setMessage(loginCheckdialog.getContext().getString(R.string.pref_gc_check_dialog_message_pinging));
                        break;
                    case LOGIN:
                        loginCheckdialog.setMessage(loginCheckdialog.getContext().getString(R.string.pref_gc_check_dialog_message_login));
                        break;
                    case LOGOUT:
                        loginCheckdialog.setMessage(loginCheckdialog.getContext().getString(R.string.pref_gc_check_dialog_message_logout));
                        break;
                    default:
                        loginCheckdialog.setMessage(loginCheckdialog.getContext().getString(R.string.pref_gc_check_dialog_message_error));
                        break;
                }
                break;
            default:
                loginCheckdialog.setMessage(loginCheckdialog.getContext().getString(R.string.pref_gc_check_dialog_message_error));
                break;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        loginCheckdialog.dismiss();
    }

    private boolean init() {
        try {
            System.setProperty("http.keepAlive", "false");
            httpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(new TLSSocketFactory())
                    .cookieJar(new NonPersistentCookieJar())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            Logger.e(TAG, "init()", e);
            errorMessage = e.getMessage();
        }
        if (httpClient == null) {
            publishProgress(new Progress(Task.INIT, State.FAIL, errorMessage));
        }
        return httpClient != null;
    }

    private boolean ping() {
        Request request = new Request.Builder()
                .url(LOGIN)
                .build();
        return handleRequest(request, Task.PING) != null;
    }

    private boolean login() {
        RequestBody formBody = new FormBody.Builder()
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("ctl00$ContentPlaceHolder1$Login1$Login1$UserName", username)
                .add("ctl00$ContentPlaceHolder1$Login1$Login1$Password", password)
                .add("ctl00$ContentPlaceHolder1$Login1$Login1$LoginButton", "Sign In")
                .build();
        Request request = new Request.Builder()
                .url(LOGIN)
                .post(formBody)
                .build();
        publishProgress(new Progress(Task.LOGIN, State.WORKING));
        Response response = handleRequest(request);
        if (response != null && !LOGIN.equals(response.request().url().toString())) {
            publishProgress(new Progress(Task.LOGIN, State.SUCCESS));
            return true;
        } else {
            publishProgress(new Progress(Task.LOGIN, State.FAIL, errorMessage));
            return false;
        }
    }

    private boolean logout() {
        RequestBody formBody = new FormBody.Builder()
                .add("__EVENTTARGET", "ctl00$ProfileWidget$LoginStatus1$ctl00")
                .add("__EVENTARGUMENT", "")
                .build();
        Request request = new Request.Builder()
                .url(LOGIN)
                .post(formBody)
                .build();
        boolean result = handleRequest(request, Task.LOGOUT) != null;
        publishProgress(new Progress(Task.LOGOUT, (result) ? State.SUCCESS : State.FAIL));
        return result;
    }

    private Response handleRequest(Request request, Task task) {
        publishProgress(new Progress(task, State.WORKING));
        Response response = handleRequest(request);
        if (response != null) {
            publishProgress(new Progress(task, State.WORKING));
        } else {
            publishProgress(new Progress(task, State.FAIL, errorMessage));
        }
        return response;
    }

    private Response handleRequest(Request request) {
        if (isCancelled()) {
            return null;
        }
        Response response;
        try {
            response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Request " + request.toString() + " failed: " + response);
            }
        } catch (Exception e) {
            Logger.e(TAG, "handleRequest(" + request.toString() + ")", e);
            errorMessage = e.getMessage();
            return null;
        }
        return response;
    }

    public enum Task {
        INIT, PING, LOGIN, LOGOUT
    }

    public enum State {
        WORKING, SUCCESS, FAIL
    }

    public static class Progress {
        protected final Task task;
        protected final State state;
        protected String message;

        public Progress(@NonNull Task task, @NonNull State state) {
            this.task = task;
            this.state = state;
        }

        public Progress(@NonNull Task task, @NonNull State state, @NonNull String message) {
            this.task = task;
            this.state = state;
            this.message = message;
        }

        public Task getTask() {
            return task;
        }

        public State getState() {
            return state;
        }

        public String getMessage() {
            return message;
        }
    }
}
