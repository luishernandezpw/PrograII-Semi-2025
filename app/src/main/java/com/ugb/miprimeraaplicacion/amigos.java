package com.ugb.miprimeraaplicacion;

public class amigos {
    String idAmigo;
    String nombre;
    String direccion;
    String telefono;
    String email;
    String dui;
    String foto;
    String urlCompletaFotoFirestore;
    String token;

    public amigos() {}
    public amigos(String idAmigo, String nombre, String direccion, String telefono, String email, String dui, String foto, String urlCompletaFotoFirestore, String token) {
        this.idAmigo = idAmigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.dui = dui;
        this.foto = foto;
        this.token = token;
        this.urlCompletaFotoFirestore = urlCompletaFotoFirestore;
    }
    public String getUrlCompletaFotoFirestore() {
        return urlCompletaFotoFirestore;
    }
    public void setUrlCompletaFotoFirestore(String urlCompletaFotoFirestore) {
        this.urlCompletaFotoFirestore = urlCompletaFotoFirestore;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdAmigo() {
        return idAmigo;
    }

    public void setIdAmigo(String idAmigo) {
        this.idAmigo = idAmigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
