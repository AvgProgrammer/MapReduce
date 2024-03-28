package org.example;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Hey Please choose a role:\n1)Landlord\n2)Customer");
        int role=sc.nextInt();
        if(role==1) {
            handleLandLord();
        }else if(role==2){
            handleTenant();
        }else{
            System.out.println("Wrong Input");
        }

    }

    private static void handleLandLord(){
        int number;
        do {
            Scanner sc=new Scanner(System.in);
            System.out.println("Please choose one of the following\n1)Add file\n2)Exit");
            number = sc.nextInt();
            if (number == 1) {
                System.out.println("Enter file path:");
                String path = sc.nextLine();
                    /*Gson gson = new Gson();
                    try (Reader reader = new FileReader("data.json")) {
                        Room room = gson.fromJson(reader, Room.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
            }
        } while (number != 2);
    }

    private static void handleTenant(){
        System.out.println("Hello Customer");
        int answer=0;
        String area="";
        String time="";
        int numb=0;
        double price=0.0;
        double stars=0.0;
        Scanner sc=new Scanner(System.in);
        int answer2=0;
        do{
            do {
                System.out.println("Please Choose Filters:\n" +
                        "1)Area\n" +
                        "2)Time Period\n" +
                        "3)Number of tenants\n" +
                        "4)Price\n" +
                        "5)Stars");
                answer=sc.nextInt();
                sc.nextLine();
                switch (answer){
                    case 1:
                        System.out.println("Enter Area");
                        area=sc.nextLine();
                        break;
                    case 2:
                        System.out.println("Enter Time Period(DD-MM-YYYY-DD-MM-YYY)");
                        time=sc.nextLine();
                        break;
                    case 3:
                        do{
                            System.out.println("Enter Number of tenants");
                            numb=sc.nextInt();
                            sc.nextLine();
                        }while(numb<=0);
                        break;
                    case 4:
                        do{
                            System.out.println("Enter Price");
                            price=sc.nextInt();
                            sc.nextLine();
                        }while (price<0);
                        break;
                    case 5:
                        do{
                            System.out.println("Enter Stars");
                            stars=sc.nextDouble();
                            sc.nextLine();
                        }while (stars<0.0 || stars>5.0);
                        break;
                }

            }while (answer<1 || answer>5);
            System.out.println("Do you want to enter another filter:\n1)Yes\n2)No");
            answer2= sc.nextInt();
        }while (answer2==1);
        sc.close();

    }
}