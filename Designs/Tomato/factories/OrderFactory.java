package Designs.Tomato.factories;

import java.util.*;
import Designs.Tomato.models.*;
import Designs.Tomato.strategies.*;

public interface OrderFactory {
    Order createOrder(User user, Cart cart, Restaurant restaurant, List<MenuItem> menuItems, PaymentStrategy paymentStrategy, double totalCost, String OrderType);
}
