package org.neewrobert.jumbo.domain.model;

public record Store(
        String id,
        String city,
        String postalCode,
        String street,
        String street2,
        String street3,
        String addressName,
        String uuid,
        double longitude,
        double latitude,
        String complexNumber,
        boolean showWarningMessage,
        String todayOpen,
        String locationType,
        Boolean collectionPoint,
        String sapStoreID,
        String todayClose
) {
    public Store withUuid(String uuid) {
        return new Store(
                id,
                city,
                postalCode,
                street,
                street2,
                street3,
                addressName,
                uuid,
                longitude,
                latitude,
                complexNumber,
                showWarningMessage,
                todayOpen,
                locationType,
                collectionPoint,
                sapStoreID,
                todayClose
        );
    }
}
