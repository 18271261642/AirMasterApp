package com.app.airmaster.http;

import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

/**
 * Created by Admin
 * Date 2022/3/21
 * @author Admin
 */
public class RequestServer implements IRequestServer {

    private BodyType bodyType = BodyType.FORM;

    @Override
    public String getHost() {
        return "http://47.113.195.211:8091/";
    }

    @Override
    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType b){
        this.bodyType = b;
    }
}
