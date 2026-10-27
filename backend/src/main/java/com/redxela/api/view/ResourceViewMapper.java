package com.redxela.api.view;

import com.redxela.api.view.ResourceView.Holding;
import com.redxela.resources.ResourceManager;
import com.redxela.resources.ResourcePool;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceViewMapper {

    private final ResourceManager resources;

    public List<ResourceView> toViews() {
        return resources.pools().stream().map(this::toView).toList();
    }

    private ResourceView toView(ResourcePool pool) {
        return new ResourceView(pool.type().name(), pool.type().getLabel(), pool.capacity(), pool.available(),
                holdings(pool.holders()), holdings(pool.waiters()));
    }

    private List<Holding> holdings(Map<String, Integer> unitsByOwner) {
        return unitsByOwner.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new Holding(entry.getKey(), entry.getValue()))
                .toList();
    }
}