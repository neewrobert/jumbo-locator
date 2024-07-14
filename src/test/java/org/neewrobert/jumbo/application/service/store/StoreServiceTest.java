package org.neewrobert.jumbo.application.service.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.neewrobert.jumbo.adapter.out.persistence.StoreEntity;
import org.neewrobert.jumbo.adapter.out.persistence.StoreRepository;
import org.neewrobert.jumbo.application.mapper.StoreMapper;
import org.neewrobert.jumbo.domain.model.Store;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class StoreServiceTest {
    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreService storeService;

    private Store store;
    private StoreEntity storeEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        store = new Store(null, "Test City", "12345", "Test Street", "1", "", "Test Store", UUID.randomUUID().toString(),
                4.615551, 51.778461, "1234", false, "08:00", "SupermarktPuP", true, "1001", "20:00");

        storeEntity = new StoreEntity("id1", "Test City", "12345", "Test Street", "1", "", "Test Store", UUID.randomUUID().toString(),
                new GeoJsonPoint(4.615551, 51.778461), "1234", false, "08:00", "SupermarktPuP", true, "1001", "20:00");
    }

    @Test
    public void testFindClosestStores() {
        when(storeRepository.findByLocationNear(any(Double.class), any(Double.class), any(PageRequest.class)))
                .thenReturn(List.of(storeEntity));
        when(storeMapper.toDomain(storeEntity)).thenReturn(store);

        List<Store> result = storeService.findClosestStores(51.778461, 4.615551, 5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(store, result.getFirst());
    }

    @Test
    public void testFindClosestStoresOpenNow() {
        when(storeRepository.findByLocationNearAndOpenNow(any(Double.class), any(Double.class), anyString(), any(PageRequest.class)))
                .thenReturn(List.of(storeEntity));
        when(storeMapper.toDomain(storeEntity)).thenReturn(store);

        List<Store> result = storeService.findClosestStoresOpenNow(51.778461, 4.615551, 5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(store, result.getFirst());
    }

    @Test
    public void testAddStore() {
        Store storeWithUuid = store.withUuid(UUID.randomUUID().toString());
        when(storeMapper.toEntity(any(Store.class))).thenReturn(storeEntity);
        when(storeRepository.save(any(StoreEntity.class))).thenReturn(storeEntity);
        when(storeMapper.toDomain(storeEntity)).thenReturn(storeWithUuid);

        Store result = storeService.addStore(store);

        assertNotNull(result);
        assertNotNull(result.uuid());
        assertEquals(storeWithUuid, result);
    }

    @Test
    public void testUpdateStore() {
        when(storeRepository.findByUuid(anyString())).thenReturn(Optional.of(storeEntity));
        when(storeMapper.toEntity(any(Store.class))).thenReturn(storeEntity);
        when(storeRepository.save(any(StoreEntity.class))).thenReturn(storeEntity);
        when(storeMapper.toDomain(storeEntity)).thenReturn(store);

        Optional<Store> result = storeService.updateStore(storeEntity.uuid(), store);

        assertTrue(result.isPresent());
        assertEquals(store, result.get());
    }

    @Test
    public void testUpdateStore_NotFound() {
        when(storeRepository.findByUuid(anyString())).thenReturn(Optional.empty());

        Optional<Store> result = storeService.updateStore(storeEntity.uuid(), store);

        assertFalse(result.isPresent());
    }
}
