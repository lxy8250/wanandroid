package com.lxy.basemodel.network.model;

import java.util.List;

/**
 * date: 2019/1/29
 * @author lxy
 */

public class LoginModel {


    /**
     * data : {"chapterTops":[],"collectIds":[7919],"email":"","icon":"","id":16821,"password":"","token":"","type":0,"username":"lxy8250"}
     * errorCode : 0
     * errorMsg :
     */

    /**
     * chapterTops : []
     * collectIds : [7919]
     * email :
     * icon :
     * id : 16821
     * password :
     * token :
     * type : 0
     * username : lxy8250
     */

    private String email;
    private String icon;
    private int id;
    private String password;
    private String token;
    private int type;
    private String username;
    private List<?> chapterTops;
    private List<Integer> collectIds;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<?> getChapterTops() {
        return chapterTops;
    }

    public void setChapterTops(List<?> chapterTops) {
        this.chapterTops = chapterTops;
    }

    public List<Integer> getCollectIds() {
        return collectIds;
    }

    public void setCollectIds(List<Integer> collectIds) {
        this.collectIds = collectIds;
    }
}
