package org.neewrobert.jumbo.adapter.out.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neewrobert.jumbo.config.RepositoryTestContainerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@Import(RepositoryTestContainerConfig.class)
@Testcontainers
public class StoreRepositoryIntegrationTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setUp() {
        mongoTemplate.dropCollection(StoreEntity.class);
        insertTestData();
    }

    private void insertTestData() {


        StoreEntity store1 = new StoreEntity("1", "'s Gravendeel", "3295 BD", "Kerkstraat", "37", "", "Jumbo 's Gravendeel Gravendeel Centrum",
                "uuid1", new GeoJsonPoint(4.615551, 51.778461), "33249", true, "08:00", "SupermarktPuP", true, "3605", "20:00");

        StoreEntity store2 = new StoreEntity("2", "'s-Heerenberg", "7041 JE", "Stadsplein", "71", "", "Jumbo 's-Heerenberg Stadsplein",
                "uuid2", new GeoJsonPoint(6.245829, 51.874272), "30170", true, "08:00", "Supermarkt", true, "4670", "21:00");

        mongoTemplate.save(store1);
        mongoTemplate.save(store2);
        mongoTemplate.indexOps("stores").ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
    }

    @Test
    public void testFindByLocationNear() {
        double latitude = 51.778461;
        double longitude = 4.615551;
        PageRequest pageRequest = PageRequest.of(0, 5);

        List<StoreEntity> result = storeRepository.findByLocationNear(longitude, latitude, pageRequest);

        assertEquals("uuid1", result.getFirst().uuid());
    }

    @Test
    public void testFindByLocationNearAndOpenNow() {
        double latitude = 51.778461;
        double longitude = 4.615551;
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        PageRequest pageRequest = PageRequest.of(0, 5);

        List<StoreEntity> result = storeRepository.findByLocationNearAndOpenNow(longitude, latitude, currentTime, pageRequest);

        assertEquals("uuid1", result.getFirst().uuid());
    }

    @Test
    public void testFindByUuid() {
        Optional<StoreEntity> result = storeRepository.findByUuid("uuid1");

        assertTrue(result.isPresent());
        assertEquals("uuid1", result.get().uuid());
    }
}
