package com.redxela.api.order;

import com.redxela.domain.exception.EntityNotFoundException;
import com.redxela.domain.order.Order;
import com.redxela.domain.order.OrderRequest;
import com.redxela.domain.order.OrderRequest.LineRequest;
import com.redxela.scheduling.OrderBook;
import com.redxela.scheduling.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/* Manual order entry. The order goes to the waiting list; the scheduler decides when it starts. */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orders;
    private final OrderBook orderBook;
    private final OrderViewMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderView create(@RequestBody NewOrder request) {
        boolean immediate = Boolean.TRUE.equals(request.immediate());
        return mapper.toView(orders.submit(new OrderRequest(request.clientId(), request.lines(), immediate)));
    }

    @GetMapping("/{id}")
    public OrderView find(@PathVariable("id") String id) {
        Order order = orderBook.find(id);
        if (order == null) {
            throw new EntityNotFoundException("Unknown order: " + id);
        }
        return mapper.toView(order);
    }

    /* "immediate" is optional and defaults to false, so a normal order does not have to mention it. */
    public record NewOrder(String clientId, List<LineRequest> lines, Boolean immediate) {
    }
}
