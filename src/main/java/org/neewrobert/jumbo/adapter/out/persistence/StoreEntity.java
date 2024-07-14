package org.neewrobert.jumbo.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stores")
public record StoreEntity(
        @Id String id,
        String city,
        String postalCode,
        String street,
        String street2,
        String street3,
        String addressName,
        String uuid,
        @GeoSpatialIndexed
        GeoJsonPoint location,
        String complexNumber,
        boolean showWarningMessage,
        String todayOpen,
        String locationType,
        Boolean collectionPoint,
        String sapStoreID,
        String todayClose
) {
    public StoreEntity withId(String id) {
        return new StoreEntity(
                id,
                city,
                postalCode,
                street,
                street2,
                street3,
                addressName,
                uuid,
                location,
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