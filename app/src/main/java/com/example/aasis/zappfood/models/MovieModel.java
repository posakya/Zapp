package com.example.aasis.zappfood.models;

/**
 * Created by Posakya on 7/29/2016.
 */
public class MovieModel {
    private String Id;
    private String Image;
    private String Categorie;
    int type;
    //private Float Rating;
    private String Description;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
