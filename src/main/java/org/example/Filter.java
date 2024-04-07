package org.example;

import java.io.Serializable;

public class Filter implements Serializable {

    private String area="";
    private String time="";
    private int numb=0;
    private double price=0.0;
    private double stars=0.0;

    public Filter(String area,String time,int numb,double price,double stars){
        this.area=area;
        this.time=time;
        this.numb=numb;
        this.price=price;
        this.stars=stars;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumb() {
        return numb;
    }

    public void setNumb(int numb) {
        this.numb = numb;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getStars() {
        return stars;
    }
    public void setStars(double stars) {
        this.stars = stars;
    }
    public boolean isCompatalbe(Room room){
        if(!this.getArea().equals(room.getArea())){
            return false;
        }
        return true;
    }
}