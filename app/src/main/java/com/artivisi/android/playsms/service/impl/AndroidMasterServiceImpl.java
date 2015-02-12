package com.artivisi.android.playsms.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.artivisi.android.playsms.domain.Credit;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.LoginHelper;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.ui.LoginActivity;
import com.google.gson.Gson;

import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by opaw on 2/5/15.
 */
public class AndroidMasterServiceImpl implements AndroidMasterService {


    private User user;
    RestTemplate restTemplate = new RestTemplate(true);
    private String PLAYSMS_URL;

    public AndroidMasterServiceImpl(){
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ( (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory() ).setReadTimeout( 60 * 1000 );
    }

    public AndroidMasterServiceImpl(User user) {
        this.user = user;
        PLAYSMS_URL = user.getServerUrl();
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ( (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory() ).setReadTimeout( 60 * 1000 );
    }


    private static final String BASE_URI = "/index.php?app=ws";

    @Override
    public LoginHelper getToken(String urlServer, String username, String password) throws Exception{
        String url = urlServer + BASE_URI + "&u=" + username + "&p=" + password + "&op=get_token&format=json";
        try {
            ResponseEntity<LoginHelper> responseEntity = restTemplate.getForEntity(url, LoginHelper.class);
            return responseEntity.getBody();
        } catch (RuntimeException e){
            throw e;
        }
    }

    @Override
    public MessageHelper getSentMessage() {
        String url = PLAYSMS_URL + BASE_URI + "&u=" + user.getUsername() + "&h=" + user.getToken() + "&op=ds&format=json";
        ResponseEntity<MessageHelper> responseEntity = restTemplate.getForEntity(url, MessageHelper.class);
        return responseEntity.getBody();
    }

    @Override
    public MessageHelper getInbox() {
        String url = PLAYSMS_URL + BASE_URI + "&u=" + user.getUsername() + "&h=" + user.getToken() + "&op=ix&format=json";
        ResponseEntity<MessageHelper> responseEntity = restTemplate.getForEntity(url, MessageHelper.class);
        return responseEntity.getBody();
    }

    @Override
    public MessageHelper sendMessage(String to, String msg) {
        String url = PLAYSMS_URL + BASE_URI +
                "&u=" + user.getUsername() + "&h=" + user.getToken() + "&op=pv&to=" + to + "&msg=" + msg + "&format=json";
        ResponseEntity<MessageHelper> responseEntity = restTemplate.getForEntity(url, MessageHelper.class);
        return responseEntity.getBody();
    }

    @Override
    public Credit getCredit() {
        String url = PLAYSMS_URL + BASE_URI + "&u=" + user.getUsername() + "&h=" + user.getToken() + "&op=cr&format=json";
        ResponseEntity<Credit> responseEntity = restTemplate.getForEntity(url, Credit.class);
        return responseEntity.getBody();
    }
}
