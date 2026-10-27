package com.redxela.domain.order;

import java.util.List;

/*
 * What a client asks for. The merchandise type is not sent: it is derived from
 * the products. "immediate" starts the order without checking resource
 * availability, which is how conflicts are provoked on purpose.
 */
public record OrderRequest(String clientId, List<LineRequest> lines, boolean immediate) {

    public record LineRequest(String sku, int units) {
    }
}
