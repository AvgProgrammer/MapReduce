package org.example;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Room implements Serializable {
    private String roomName;
    private String area;
    private int noOfReviews;
    private int noOfPersons;
    private double stars;
    private String roomImage;
    private ArrayList<String> Booked;

    public Room(String Name,int numbPerson,String Area,double stars,int reviews,String img){
        this.roomName=Name;
        this.area=Area;
        this.noOfReviews=reviews;
        this.noOfPersons=numbPerson;
        this.stars=stars;
        this.roomImage=img;
    }
    public boolean isBooked(LocalDate startDate, LocalDate endDate){
        String[] parts;
        for(String key: Booked){
            parts = key.split("-");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate StartDate1 = LocalDate.parse(parts[0], formatter);
                LocalDate EndDate1 = LocalDate.parse(parts[1], formatter);
                if(!StartDate1.isAfter(endDate) && !EndDate1.isBefore(startDate)){
                    return  true;
                }

            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in the format DD-MM-YYYY.");
            }
        }
        return false;
    }
    public void AddDate(LocalDate startdate,LocalDate enddate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String startString = startdate.format(formatter);
        String endString = enddate.format(formatter);
        Booked.add(startString+"-"+endString);
    }

}
