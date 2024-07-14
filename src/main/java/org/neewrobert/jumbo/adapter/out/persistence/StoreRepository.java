package org.neewrobert.jumbo.adapter.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends MongoRepository<StoreEntity, String> {

    @Query(value = "{ 'location': { $near: { $geometry: { type: 'Point', coordinates: [?0, ?1] } } } }")
    List<StoreEntity> findByLocationNear(double longitude, double latitude, Pageable pageable);

    Optional<StoreEntity> findByUuid(String uuid);

    @Query("{'location': { $near: { $geometry: { type: 'Point', coordinates: [?0, ?1] } } }, 'todayOpen': { $lte: ?2 }, 'todayClose': { $gte: ?2 }}")
    List<StoreEntity> findByLocationNearAndOpenNow(double longitude, double latitude, String currentTime, Pageable pageable);
}
