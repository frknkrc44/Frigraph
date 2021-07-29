package com.kabouzeid.gramophone.helper;

import android.os.Handler;
import android.os.Looper;

import com.kabouzeid.gramophone.App;

public abstract class AsyncProcess<X, Y> extends Thread {
    protected X[] params;

    public AsyncProcess() {}

    @SafeVarargs
    public final AsyncProcess<X, Y> execute(X... params) {
        this.params = params;
        start();
        return this;
    }

    protected void runOnUiThread(Runnable runnable) {
        App.getMainHandler().post(runnable);
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