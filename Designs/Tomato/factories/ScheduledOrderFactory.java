package Designs.Tomato.factories;

import java.util.*;
import Designs.Tomato.models.*;
import Designs.Tomato.strategies.*;


public class ScheduledOrderFactory implements OrderFactory{
    private String scheduleTime;

    public ScheduledOrderFactory(String scheduleTime){
        this.scheduleTime = scheduleTime;
    }

    @Override 
    public Order createOrder(user user, Cart cart, Restaurant restaurant, List<MenuItem> menuItems, PaymentStrategy paymentStartegy, double totalCost, String orderType){
        Order order = null;

        if(orderType.equals("Delivery")){
            DeliveryOrder deliveryOrder = new DeliveryOrder();
            deliveryOrder.setUserAddress(user.getAddress());
            order = deliveryOrder;
        }
        else{
            PickupOrder pickupOrder = new PickupOrder();
            pickupOrder.setRestaurantAddress(restaurant.getLocation());
            order = pickupOrder;
        }

        order.setuser(user);
        order.setRestaurant(restaurant);
        order.setItems(menuItems);
        order.setPaymentStrategy(paymentStrategy);
        order.setScheduled(scheduleTime);
        order.setTotal(totalCost);
        return order;
    }
}
