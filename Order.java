import java.sql.Timestamp;

enum Side {
    BUY,
    SELL
}

enum OrderType {
    fillOrCancel,
    fillOrChill
}

// Fifo
public class Order {
    private Timestamp timeStamp;
    private int initialQuantity;
    private float price;
    private Side side;
    private int remainingQuantity;
    private OrderType orderType;
    private int orderId;
    
    public Order(int quantity,float price,Side side, OrderType orderType, OrderId orderId) {
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        this.orderType = orderType;
        this.price = price;
        this.initialQuantity = quantity;
        this.remainingQuantity = quantity;
        this.side = side;
        this.orderId = orderId.id;
    }

    @Override
    public String toString() {
        return String.format("%s %s AT %s dkk ORDERED AT %s" ,"" + side, "" + remainingQuantity,"" + price,"" + timeStamp);
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void updateOrder(int incomingQuantity) {
        this.remainingQuantity = remainingQuantity - incomingQuantity;
    }    

    public boolean isFilled() {
        return remainingQuantity == 0;
    }
    
    public float getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public int getOrderId() {
        return orderId;
    }
    
}
