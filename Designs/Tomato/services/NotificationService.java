package Designs.Tomato.services;

import java.util.*;
import Designs.Tomato.models.*;

public class NotificationService {
    public static void notify(Order order){
        System.out.println("\nNotification: New " + order.getType() + " orderPlaced!");
        System.out.println("----------------------------");
        System.out.println("Order Id: " + order.getOrderId());
        System.out.println("Customer: " + order.getRestaurant().getName());
        System.out.println("Items Ordered:");

        List<MenuItem> items = order.getItems();
        for(MenuItem item : items){
            System.out.println(" -" + item.getName() + " ($" + item.getPrice() + ")");
        }

        System.out.println("Total: $" + order.getTotal());
        System.out.println("Scheduled For: " + order.getScheduled());
        System.out.println("Payment: Done");
        System.out.println("---------------------------");
    }
}
