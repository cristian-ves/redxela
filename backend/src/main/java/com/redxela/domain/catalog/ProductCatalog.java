package com.redxela.domain.catalog;

import com.redxela.domain.exception.EntityNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Predefined product catalog. It is built once and never modified, so it can
 * be read from any thread. Receptions and orders can only refer to these
 * products; the interface never creates new ones.
 */
public class ProductCatalog {

    private final Map<String, Product> productsBySku = new LinkedHashMap<>();

    public ProductCatalog() {
        add("GEN-001", "Cotton T-shirt", MerchandiseType.GENERAL);
        add("GEN-002", "Notebook, 80 sheets", MerchandiseType.GENERAL);
        add("GEN-003", "Phone charger", MerchandiseType.GENERAL);
        add("FRG-001", "Ceramic mug set", MerchandiseType.FRAGILE);
        add("FRG-002", "Glass vase", MerchandiseType.FRAGILE);
        add("FRG-003", "LED monitor", MerchandiseType.FRAGILE);
        add("HVY-001", "Cast-iron cookware set", MerchandiseType.HEAVY);
        add("HVY-002", "Barbell weight set", MerchandiseType.HEAVY);
        add("HVY-003", "Steel tool cabinet", MerchandiseType.HEAVY);
        add("VAL-001", "Smartphone", MerchandiseType.HIGH_VALUE);
        add("VAL-002", "Laptop", MerchandiseType.HIGH_VALUE);
        add("VAL-003", "Wristwatch", MerchandiseType.HIGH_VALUE);
        add("REF-001", "Fresh dairy crate", MerchandiseType.REFRIGERATED);
        add("REF-002", "Frozen seafood box", MerchandiseType.REFRIGERATED);
        add("REF-003", "Vaccine cooler pack", MerchandiseType.REFRIGERATED);
    }

    public List<Product> all() {
        return List.copyOf(productsBySku.values());
    }

    public List<Product> ofType(MerchandiseType type) {
        return productsBySku.values().stream().filter(product -> product.type() == type).toList();
    }

    public Product find(String sku) {
        Product product = productsBySku.get(sku);
        if (product == null) {
            throw new EntityNotFoundException("Unknown product: " + sku);
        }
        return product;
    }

    private void add(String sku, String name, MerchandiseType type) {
        productsBySku.put(sku, new Product(sku, name, type));
    }
}
