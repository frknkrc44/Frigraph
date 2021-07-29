package com.kabouzeid.gramophone.helper;

import android.os.Handler;
import android.os.Looper;

public abstract class AsyncProcess<X, Y> extends Thread {
    protected X[] params;
    private final Handler uiThread;

    public AsyncProcess() {
        uiThread = new Handler(Looper.getMainLooper());
    }

    @SafeVarargs
    public final AsyncProcess<X, Y> execute(X... params) {
        this.params = params;
        start();
        return this;
    }

    private void runOnUiThread(Runnable runnable) {
        uiThread.post(runnable);
    }

    @Override
    public void run() {
        runOnUiThread(this::onPreExecute);
        runOnUiThread(() -> onPostExecute(doInBackground(params)));
    }

    protected void onPreExecute() {}

    protected abstract Y doInBackground(X[] params);

    protected void onPostExecute(Y l) {}

    public void joinSafe() {
        try {
            if (isAlive()) {
                onCancelled(null);
                join();
            }
        } catch (Throwable t) {
            // do nothing
        }
    }

    public void cancel(boolean s) {
        joinSafe();
    }

    protected void onCancelled(Y s) {
        onPostExecute(s);
    }
}