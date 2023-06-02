package com.iamverycute.simplicity.browser;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class BinaryRequest extends Request<byte[]> {
    private final BinaryResponse responseListener;
    private final int index;

    public BinaryRequest(int method, String url, BinaryResponse responseListener, int index) {
        super(method, url, error -> {
            if (responseListener != null) {
                responseListener.onError(error, index);
            }
        });
        this.responseListener = responseListener;
        this.index = index;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        if (responseListener != null) responseListener.onResponse(response.data, index);
        return null;
    }

    @Override
    protected void deliverResponse(byte[] response) {

    }

    interface BinaryResponse {
        void onResponse(byte[] data, int index);

        void onError(VolleyError error, int index);
    }
}
