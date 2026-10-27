package com.redxela.domain.order;

/*
 * What the scheduler needs from the inventory. Stock is reserved when an order
 * is admitted, consumed when it completes and released if the order goes back
 * to the waiting list.
 */
public interface StockReservation {

    boolean isAvailable(Order order);

    boolean tryReserve(Order order);

    void consume(Order order);

    void release(Order order);
}
