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

package menion.android.whereyougo.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import menion.android.whereyougo.utils.FileSystem;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class DownloadCartridgeTask extends
    AsyncTask<String, DownloadCartridgeTask.Progress, Boolean> {
  public enum Task {
    PING, LOGIN, DOWNLOAD, DOWNLOAD_SINGLE, LOGOUT
  };
  public enum State {
    WORKING, SUCCESS, FAIL
  };

  public class Progress {
    public Progress(Task task, State state) {
      this.task = task;
      this.state = state;
    }

    public Progress(Task task, long completed, long total) {
      this.state = State.WORKING;
      this.task = task;
      this.total = total;
      this.completed = completed;
    }

    Task task;
    State state;
    long total;
    long completed;
    
    public Task getTask() {
      return task;
    }

    public State getState() {
      return state;
    }

    public long getTotal() {
      return total;
    }

    public long getCompleted() {
      return completed;
    }
  }
  
  static final String LOGIN = "https://www.wherigo.com/login/default.aspx";
  static final String DOWNLOAD = "http://www.wherigo.com/cartridge/download.aspx";
  String username;
  String password;

  public DownloadCartridgeTask(String username, String password) {
    super();
    this.username = username;
    this.password = password;
  }

  @Override
  protected Boolean doInBackground(String... arg0) {

    DefaultHttpClient httpClient = new DefaultHttpClient();
    BasicCookieStore cookieStore = new BasicCookieStore();
    HttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

    // login
    try {
      HttpGet httpGet = new HttpGet(LOGIN);
      publishProgress(new Progress(Task.PING, State.WORKING));
      if (status(httpClient.execute(httpGet, localContext)) != HttpStatus.SC_OK
          || cookieStore.getCookies().size() == 0) {
        publishProgress(new Progress(Task.PING, State.FAIL));
        return false;
      } else {
        publishProgress(new Progress(Task.PING, State.SUCCESS));
      }
      if (isCancelled())
        return false;

      HttpPost httpPost = new HttpPost(LOGIN);
      ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
      postParameters.add(new BasicNameValuePair("__EVENTTARGET", ""));
      postParameters.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
      postParameters.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$Login1$Login1$UserName",
          username));
      postParameters.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$Login1$Login1$Password",
          password));
      postParameters.add(new BasicNameValuePair(
          "ctl00$ContentPlaceHolder1$Login1$Login1$LoginButton", "Sign In"));
      httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
      publishProgress(new Progress(Task.LOGIN, State.WORKING));
      if (status(httpClient.execute(httpPost, localContext)) != HttpStatus.SC_OK
          || cookieStore.getCookies().size() <= 1) {
        publishProgress(new Progress(Task.LOGIN, State.FAIL));
        return false;
      } else {
        publishProgress(new Progress(Task.LOGIN, State.SUCCESS));
      }
      if (isCancelled())
        return false;
    } catch (Exception e) {
      publishProgress(new Progress(Task.PING, State.FAIL));
      return false;
    }

    // download
    for (int i = 0; i < arg0.length; i++) {
      String cguid = arg0[i];
      try {
        HttpPost httpPost = new HttpPost(DOWNLOAD + "?CGUID=" + cguid);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("__EVENTTARGET", ""));
        postParameters.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        postParameters.add(new BasicNameValuePair(
            "ctl00$ContentPlaceHolder1$EULAControl1$uxEulaAgree", "on"));
        postParameters.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$uxDeviceList", "4"));
        postParameters.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$btnDownload",
            "Download Now"));
        httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
        publishProgress(new Progress(Task.DOWNLOAD, i, arg0.length));
        if (download(cguid, httpClient.execute(httpPost, localContext)) != HttpStatus.SC_OK) {
          publishProgress(new Progress(Task.DOWNLOAD, State.FAIL));
        } else {
          publishProgress(new Progress(Task.DOWNLOAD, State.SUCCESS));
        }
        if (isCancelled())
          return false;
      } catch (Exception e) {
        publishProgress(new Progress(Task.DOWNLOAD, State.FAIL));
        return false;
      }
    }

    // logout
    try {
      HttpPost httpPost = new HttpPost(LOGIN);
      ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
      postParameters.add(new BasicNameValuePair("__EVENTTARGET",
          "ctl00$ProfileWidget$LoginStatus1$ctl00"));
      postParameters.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
      httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
      publishProgress(new Progress(Task.LOGOUT, State.WORKING));
      if (status(httpClient.execute(httpPost, localContext)) != HttpStatus.SC_OK
          || cookieStore.getCookies().size() > 1) {
        publishProgress(new Progress(Task.LOGOUT, State.FAIL));
        return false;
      }
      publishProgress(new Progress(Task.LOGOUT, State.SUCCESS));
    } catch (Exception e) {
      publishProgress(new Progress(Task.LOGOUT, State.FAIL));
      return false;
    }
    return true;
  }

  private int status(HttpResponse response) throws IllegalStateException, IOException {
    StatusLine statusLine = response.getStatusLine();
    int statusCode = statusLine.getStatusCode();
    if (statusCode == HttpStatus.SC_OK) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        entity.consumeContent();
        return statusCode;
      }
    }
    return -1;
  }

  private int download(String filename, HttpResponse response) throws IllegalStateException,
      IOException {
    StatusLine statusLine = response.getStatusLine();
    int statusCode = statusLine.getStatusCode();
    if (statusCode == HttpStatus.SC_OK) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        if (!"application/octet-stream".equals(entity.getContentType().getValue())) {
          entity.consumeContent();
          return -1;
        }
        long total = entity.getContentLength();
        String suggestedFilename =
            suggestedFilename(response.getFirstHeader("Content-Disposition"));
        String filePath =
            FileSystem.ROOT
                + (suggestedFilename == null ? filename + ".gwc" : filename + "_"
                    + suggestedFilename);
        File file = new File(filePath);
        if (file.exists() && file.length() == total) {
          entity.consumeContent();
          publishProgress(new Progress(Task.DOWNLOAD_SINGLE, total, total));
          return HttpStatus.SC_OK;
        }
        long completed = 0;
        int length;
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(entity.getContent());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        publishProgress(new Progress(Task.DOWNLOAD_SINGLE, completed, total));
        while ((length = bis.read(buffer)) > 0 && !isCancelled()) {
          bos.write(buffer, 0, length);
          completed += length;
          publishProgress(new Progress(Task.DOWNLOAD_SINGLE, completed, total));
        }
        bis.close();
        bos.close();
        entity.consumeContent();
        if (isCancelled()) {
          file.delete();
          return -1;
        }
        return statusCode;
      }
    }
    return -1;
  }

  private String suggestedFilename(Header header) {
    if (header != null) {
      HeaderElement[] helelms = header.getElements();
      if (helelms.length > 0) {
        HeaderElement helem = helelms[0];
        if (helem.getName().equalsIgnoreCase("attachment")) {
          NameValuePair nmv = helem.getParameterByName("filename");
          if (nmv != null) {
            return nmv.getValue();
          }
        }
      }
    }
    return null;
  }

}
