package menion.android.whereyougo.network;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import menion.android.whereyougo.utils.Logger;
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
    private OkHttpClient httpClient;
    private String errorMessage;

    public LoginTask(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return init() && ping() && login() && logout();
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
        return handleRequest(request, Task.LOGOUT) != null;
    }

    private Response handleRequest(Request request, Task task) {
        publishProgress(new Progress(task, State.WORKING));
        Response response = handleRequest(request);
        if (response != null) {
            publishProgress(new Progress(task, State.SUCCESS));
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
        INIT, PING, LOGIN, DOWNLOAD, DOWNLOAD_SINGLE, LOGOUT
    }

    public enum State {
        WORKING, SUCCESS, FAIL
    }

    public static class Progress {
        protected final Task task;
        protected final State state;
        protected long completed;
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

        public long getCompleted() {
            return completed;
        }
    }
}
