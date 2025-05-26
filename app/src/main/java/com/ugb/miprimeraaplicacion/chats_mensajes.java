package com.ugb.miprimeraaplicacion;

public class chats_mensajes {
    public String para, de, para_de, msg;

    public chats_mensajes(){}
    public chats_mensajes(String de, String msg, String para, String para_de) {
        this.de = de;
        this.msg = msg;
        this.para = para;
        this.para_de = para_de;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getPara_de() {
        return para_de;
    }

    public void setPara_de(String para_de) {
        this.para_de = para_de;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
