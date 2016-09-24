package com.myapp.handbook;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by sashutosh on 9/5/2016.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = HttpConnectionUtil.URL_ENPOINT+ "/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL);
                    //.addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}