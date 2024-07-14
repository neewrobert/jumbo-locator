package org.neewrobert.jumbo.adapter.in.web.store;

public record StoreDto(
        String uuid,
        String city,
        String postalCode,
        String street,
        String street2,
        String street3,
        String addressName,
        double longitude,
        double latitude,
        String complexNumber,
        boolean showWarningMessage,
        String todayOpen,
        String locationType,
        boolean collectionPoint,
        String sapStoreID,
        String todayClose
) {
}