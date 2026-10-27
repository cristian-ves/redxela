package com.redxela.api.reception;

import com.redxela.api.reception.ReceptionView.TruckView;
import com.redxela.reception.ReceptionArea;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/* Manual entry of merchandise arriving at the reception area. */
@RestController
@RequestMapping("/api/receptions")
@RequiredArgsConstructor
public class ReceptionController {

    private final ReceptionArea reception;
    private final ReceptionViewMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TruckView register(@RequestBody NewTruck request) {
        return mapper.toTruck(reception.register(request.sku(), request.units()));
    }

    public record NewTruck(String sku, int units) {
    }
}