package com.redxela.api.meta;

import java.util.List;

/*
 * Static information the interface asks for once: labels, capacities and the
 * rules of each merchandise type. Everything else comes in the snapshot, so
 * the snapshot only needs identifiers.
 */
public record MetaView(List<LevelMeta> levels, List<ResourceMeta> resources, List<PolicyMeta> policies,
                       List<TypeMeta> types, List<ProductMeta> products) {

    public record LevelMeta(int level, String label, int targetWaitMinutes) {
    }

    public record ResourceMeta(String type, String label, int capacity, int orderingRank) {
    }

    public record PolicyMeta(String id, String label, String criterion) {
    }

    public record Requirement(String resource, int units) {
    }

    public record TypeMeta(String id, String label, String description, List<Requirement> requirements) {
    }

    public record ProductMeta(String sku, String name, String type) {
    }
}