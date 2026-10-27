package com.redxela.api.meta;

import com.redxela.api.meta.MetaView.LevelMeta;
import com.redxela.api.meta.MetaView.PolicyMeta;
import com.redxela.api.meta.MetaView.ProductMeta;
import com.redxela.api.meta.MetaView.Requirement;
import com.redxela.api.meta.MetaView.ResourceMeta;
import com.redxela.api.meta.MetaView.TypeMeta;
import com.redxela.domain.catalog.MerchandiseType;
import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.domain.client.ServiceLevel;
import com.redxela.domain.resource.ResourceType;
import com.redxela.scheduling.policy.PolicyType;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/* Static description of the system: service levels, resources, policies, merchandise types and products. */
@RestController
@RequiredArgsConstructor
public class MetaController {

    private final ProductCatalog catalog;

    @GetMapping("/api/meta")
    public MetaView meta() {
        return new MetaView(levels(), resources(), policies(), types(), products());
    }

    private List<LevelMeta> levels() {
        return Arrays.stream(ServiceLevel.values())
                .map(level -> new LevelMeta(level.getLevel(), level.getLabel(), level.getTargetWaitMinutes()))
                .toList();
    }

    private List<ResourceMeta> resources() {
        return Arrays.stream(ResourceType.values())
                .map(type -> new ResourceMeta(type.name(), type.getLabel(), type.getCapacity(), type.getOrderingRank()))
                .toList();
    }

    private List<PolicyMeta> policies() {
        return Arrays.stream(PolicyType.values())
                .map(policy -> new PolicyMeta(policy.name(), policy.getLabel(), policy.getCriterion()))
                .toList();
    }

    private List<TypeMeta> types() {
        return Arrays.stream(MerchandiseType.values())
                .map(type -> new TypeMeta(type.name(), type.getLabel(), type.getDescription(), requirements(type)))
                .toList();
    }

    private List<Requirement> requirements(MerchandiseType type) {
        return type.getRequirements().stream()
                .map(requirement -> new Requirement(requirement.type().name(), requirement.units()))
                .toList();
    }

    private List<ProductMeta> products() {
        return catalog.all().stream()
                .map(product -> new ProductMeta(product.sku(), product.name(), product.type().name()))
                .toList();
    }
}