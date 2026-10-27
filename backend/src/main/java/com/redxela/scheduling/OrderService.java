package com.redxela.scheduling;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.order.Order;
import com.redxela.domain.order.OrderFactory;
import com.redxela.domain.order.OrderRequest;
import lombok.RequiredArgsConstructor;

/*
 * Entry point for new orders, whether they come from a client in the
 * interface or from the generator. A valid order is registered, placed in the
 * waiting list and the dispatcher is woken to look at it right away.
 */
@RequiredArgsConstructor
public class OrderService {

    private final OrderFactory factory;
    private final SimulationClock clock;
    private final OrderBook orderBook;
    private final WaitingList waitingList;
    private final DispatchSignal signal;

    public Order submit(OrderRequest request) {
        Order order = factory.create(request, clock.nowMinutes());
        orderBook.register(order);
        waitingList.add(order);
        signal.request();
        return order;
    }
}