package com.linktech.gft.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.linktech.gft.base.BaseApplication;
import com.linktech.gft.bean.JuHeIpAddress;
import com.linktech.gft.interfaces.IIpChinaInterface;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IpJudgeService extends Service {
    /**
     * 是否是国内地址
     */
    JuHeIpAddress result;

    /**
     * binder
     */
    private IBinder singleIBinder;
    private OkHttpClient client;
    private Request request;
    private Handler handler = new Handler();

    /**
     * 当前ip地址
     */
    private String currentIpAddress;
    private Runnable runnable = this::retry;

    public IpJudgeService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        singleIBinder = new IIpChinaInterface.Stub() {
            @Override
            public boolean isChinaIp() {
                return result != null && result.getData()!=null &&result.getData().isChinaInner();
            }
        };

        try {
            ((IIpChinaInterface.Stub) singleIBinder).asBinder().linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Logger.d("远程服务:已断开");
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        NetworkUtils.getIPAddress(true);
        client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(12000, TimeUnit.MILLISECONDS)
                .writeTimeout(12000, TimeUnit.MILLISECONDS)
                .build();

        // 监听ip地址变化
        retry();

        return singleIBinder;
    }

    /**
     * 刷新网络状态信息
     */
    private void retry() {
        if (currentIpAddress == null || !currentIpAddress.equals(NetworkUtils.getIPAddress(true))) {
            currentIpAddress = NetworkUtils.getIPAddress(true);
            request = new Request.Builder().url(BaseApplication.app.getBaseWeUrl() + "/api/broker/ip").build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.d("远程服务:无法解析ip地址数据");
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        String datas= response.body().string();
                        Logger.d("远程服务:已获取最新ip地址信息:" + currentIpAddress + datas);
                        result = new Gson().fromJson(datas, JuHeIpAddress.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        handler.postDelayed(runnable, 20 * 1000);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

        client = null;
        request = null;
        singleIBinder = null;
        handler.removeCallbacks(runnable);
        handler = null;
    }
}
