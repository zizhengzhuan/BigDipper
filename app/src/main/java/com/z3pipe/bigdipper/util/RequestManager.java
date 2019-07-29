package com.z3pipe.bigdipper.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * @link https://www.z3pipe.com
 * @author zhengzhuanzi
 * @date 2019-04-10
 */
public class RequestManager {

    private static final int TIMEOUT = 15 * 1000;

    public interface RequestHandler {
        /**
         * 上传任务完成
         * @param success
         */
        void onComplete(boolean success);
    }

    private static class RequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        private RequestHandler handler;

        public RequestAsyncTask(RequestHandler handler) {
            this.handler = handler;
        }

        @Override
        protected Boolean doInBackground(String... request) {
            return sendRequest(request[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            handler.onComplete(result);
        }
    }

    public static boolean sendRequest(String request) {
        InputStream inputStream = null;
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestMethod("POST");
            connection.connect();
            inputStream = connection.getInputStream();
            while (inputStream.read() != -1){

            }
            return true;
        } catch (IOException error) {
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException secondError) {
                Log.w(RequestManager.class.getSimpleName(), secondError);
            }
        }
    }

    public static void sendRequestAsync(String request, RequestHandler handler) {
        RequestAsyncTask task = new RequestAsyncTask(handler);
        task.execute(request);
    }

}
