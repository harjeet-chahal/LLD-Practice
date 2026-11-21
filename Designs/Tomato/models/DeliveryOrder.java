package Designs.Tomato.models;

public class DeliveryOrder extends Order{
    private String userAddress;

    public DeliveryOrder(){
        userAddress = "";
    }

    @Override 
    public String getType(){
        return "Delivery";
    }

    public void setUserAddress(String a){
        userAddress = a;
    }

    public String getUserAddress(){
        return userAddress;
    }
}