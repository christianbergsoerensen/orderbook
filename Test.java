public class Test {
    public static void main(String[] args) {
        //buy order
        OrderBook orderBook = new OrderBook();

        Order order1 = new Order(10, 10.0f, Side.BUY, OrderType.fillOrChill, new OrderId());
        Order order2 = new Order(10, 10.1f, Side.BUY, OrderType.fillOrChill, new OrderId());
        
        Order order3 = new Order(10, 10.2f, Side.SELL, OrderType.fillOrChill, new OrderId());
        Order order4 = new Order(10, 10.4f, Side.SELL, OrderType.fillOrChill, new OrderId());

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);
        orderBook.addOrder(order4);

        Order order5 = new Order(56, 10f, Side.SELL, OrderType.fillOrChill, new OrderId());
        orderBook.makeOrder(order5);

        Order order6 = new Order(56, 10f, Side.BUY, OrderType.fillOrChill, new OrderId());
        orderBook.makeOrder(order6);

        System.out.println(orderBook);
        
    }


}