package Designs.Tomato;

import java.util.*;
import Designs.Tomato.models.*;
import Designs.Tomato.managers.*;
import Designs.Tomato.factories.*;
import Designs.Tomato.services.*;
import Designs.Tomato.strategies.*;

public class TomatoApp {
    public TomatoApp{
        public TomatoApp(){
            initializeRestaurants();
        }

        public void initializeRestaurants(){
            Restaurant restaurant1 = new Restaurant("Bikaner", "Delhi");
            restaurant1.addMenuItem(new MenuItem("P1", "Chhole Bhature", 120));
            restaurant1.addMenuItem(new MenuItem("P2", "Samosa", 15));

            Restaurant restaurant2 = new Restaurant("Haldiram", "Kolkata");
            restaurant2.addMenuItem(new MenuItem("P1", "RajKachori", 80));
            restaurant2.addMenuItem(new MenuItem("P2", "PavBhaji", 100));
            restaurant2.addMenuItem(new MenuItem("P3", "Dhokla", 50));
            
            Restaurant restaurant3 = new Restaurant("Sarvana Bhavan", "Chennai");
            restaurant3.addMenuItem(new MenuItem("P1", "Masala Dosa", 90));
            restaurant3.addMenuItem(new MenuItem("P2", "Idli Vada", 60));
            restaurant3.addMenuItem(new MenuItem("P3", "Filter Coffee", 30));

            RestaurantManager restaurantManagaer = RestaurantManager.getInstance();
            restaurantManager.addRestaurant(restaurant1);
            restaurantManager.addRestaurant(restaurant2);
            restaurantManager.addRestaurant(restaurant3);

            public void selectRestaurant(User user, Restaurant restaurant){
                Cart cart = user.getCart();
                cart.setRestaurant(restaurant);
            }

            public void addToCart(User user, String itemCode){
                Restaurant restaurant = user.getCart().getRestaurant();
                if(restaurant == null){
                    System.out.println("Please select a restaurant first.");
                    return;
                }

                for(MenuItem item : restaurant.getMenu()){
                    if(item.getCode().equals(itemCode)){
                        user.getCart().addItem(item);
                        break;
                    }
                }
            }

            

        }
    }
}
