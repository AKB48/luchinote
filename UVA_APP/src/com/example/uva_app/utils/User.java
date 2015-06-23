package com.example.uva_app.utils;

import java.io.Serializable;

public class User implements Serializable{

    private String username = null;
    private String nickname = null;
    
    public User() {
        
    }
    
    public User(String username, String nickname)
    {
        this.username = username;
        this.nickname = nickname;
    }
    
    public String getUsername()
    {
        return this.username;
    }
    
    public String getNickname()
    {
        return this.nickname;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

}
