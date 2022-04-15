package com.tranvanphay.amqp_service.amqp_service.amqp;

public class ConnectionInfo {
   private String host;
   private int port;
   private String userName;
   private String password;
   private String exchange;
   private String key;

    public ConnectionInfo(String host, int port, String userName, String password, String exchange, String key) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.exchange = exchange;
        this.key = key;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
