import java.util.*;
import java.util.function.BiPredicate;

public class OrderBook {

    private PriorityQueue<PriorityQueue<Order>> buyOrders;
    private PriorityQueue<PriorityQueue<Order>> sellOrders;
    private Map<Float,PriorityQueue<Order>> samePrices;

    public OrderBook(){
        //priorityqueue with the largest price is at the front
        buyOrders = new PriorityQueue<>(new Comparator<PriorityQueue<Order>>() {
            public int compare(PriorityQueue<Order> q1, PriorityQueue<Order> q2) {
                int res = 0;
                if (q1.peek().getPrice() < q2.peek().getPrice()) {
                    res = 1;
                }
                else {
                    res = -1;
                }
                return res;
            }
        });
        sellOrders = new PriorityQueue<>(new Comparator<PriorityQueue<Order>>() {
            public int compare(PriorityQueue<Order> q1, PriorityQueue<Order> q2) {
                int res = 0;
                if (q1.peek().getPrice() > q2.peek().getPrice()) {
                    res = 1;
                }
                else {
                    res = -1;
                }
                return res;
            }
        });
        samePrices = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BUY ORDERS:" + "\n" + "\n");
        while (!buyOrders.isEmpty()) {
            PriorityQueue<Order> innerPQ = buyOrders.poll();
            
            while (!innerPQ.isEmpty()) {
                sb.append(innerPQ.poll() + "\n");
            }
        }
        sb.append("\n");
        sb.append("SELL ORDERS:" + "\n" + "\n");
        while (!sellOrders.isEmpty()) {
            PriorityQueue<Order> innerPQ = sellOrders.poll();
            
            while (!innerPQ.isEmpty()) {
                sb.append(innerPQ.poll() + "\n");
            }
        }

        return sb.toString();
    }

    public void makeOrder(Order order) {
        resolveOrder(order);
        if(order.getOrderType() == OrderType.fillOrChill && !order.isFilled()) {
            addOrder(order);
        }
    }

    public void resolveOrder(Order order) {
        Side side = order.getSide();
        PriorityQueue<PriorityQueue<Order>> opposingOrders; 
        BiPredicate<Float,Float> isPriceTooExpensive;
        //When buying you cannot buy orders with a higher price than you
        //When selling you cannot sell to orders with price lower than you
        if(side == Side.BUY) {
            opposingOrders = sellOrders;
            isPriceTooExpensive = (x,y) -> x < y;
        } else {
            opposingOrders = buyOrders;
            isPriceTooExpensive = (x,y) -> x > y;
        }

        //Outer loop runs as long as there are opposing offers with <= price of placed order and order is not filled
        while(true) {
            PriorityQueue<Order> cheapestOrders = opposingOrders.peek();
            if(order.isFilled() || cheapestOrders == null || cheapestOrders.peek() == null) {
                break;
            } 
            
            float levelPrice = cheapestOrders.peek().getPrice();

            if (isPriceTooExpensive.test(order.getPrice(),levelPrice)) {
                break;
            }

            // Runs aslong as order is not filled, since we know the prices at this level are viable for this order
            while(!order.isFilled()) {
                int remainingStocks = order.getRemainingQuantity();
                int stocksAvailable = cheapestOrders.peek().getRemainingQuantity();
                if(remainingStocks >= stocksAvailable) {
                    cheapestOrders.poll();
                    order.updateOrder(stocksAvailable);
                    break;
                }
                else {
                    cheapestOrders.peek().updateOrder(remainingStocks);
                    order.updateOrder(remainingStocks);
                }
            }
            //If we fulfilled all orders at this price, we need to pop the inner priority queue
            //should also update map, such that we signal that we have no orders at this price anymore
            if(cheapestOrders.peek() == null) {
                samePrices.remove(levelPrice);
                opposingOrders.poll();

            }
        }


    }

    public void addOrder(Order order) {
        Side side = order.getSide();
        PriorityQueue<Order> ordersWithSamePrice = samePrices.get(order.getPrice());
        PriorityQueue<PriorityQueue<Order>> ordersSameBid;

        if(side == Side.BUY) {
            ordersSameBid = buyOrders;
        } else{
            
            ordersSameBid = sellOrders;
        }

        if(ordersWithSamePrice == null) {
            //makes sure that the order with the lowest timestamp is first in the prio queue
            //with the way it is implemented could also just have used an int field that increases by 1 for each new order
            ordersWithSamePrice = new PriorityQueue<>(new Comparator<Order>() {
                public int compare(Order order1, Order order2) {
                    return order1.getTimeStamp().compareTo(order2.getTimeStamp());
                }
            });
            ordersWithSamePrice.add(order);

            ordersSameBid.add(ordersWithSamePrice);
            samePrices.put(order.getPrice(), ordersWithSamePrice);
        } else {
            ordersWithSamePrice.add(order);
        }
    }
    

    
    public PriorityQueue<PriorityQueue<Order>> getBuyOrders() {
        return buyOrders;
    }

    public PriorityQueue<PriorityQueue<Order>> getSellOrders() {
        return sellOrders;
    }



}

