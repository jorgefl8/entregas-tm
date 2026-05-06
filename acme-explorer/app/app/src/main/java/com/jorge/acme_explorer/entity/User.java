package com.jorge.acme_explorer.entity;

public class User {

    private String uid;
    private String name;
    private String surname;
    private String email;
    private String photoUrl;
    private long registeredAt;

    public User() {
    }

    public User(String uid, String name, String surname, String email, String photoUrl, long registeredAt) {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.photoUrl = photoUrl;
        this.registeredAt = registeredAt;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public long getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(long registeredAt) { this.registeredAt = registeredAt; }
}
