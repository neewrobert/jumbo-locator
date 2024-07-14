package org.neewrobert.jumbo.application.service.store;

import org.neewrobert.jumbo.adapter.out.persistence.StoreEntity;
import org.neewrobert.jumbo.adapter.out.persistence.StoreRepository;
import org.neewrobert.jumbo.application.mapper.StoreMapper;
import org.neewrobert.jumbo.domain.model.Store;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    public StoreService(StoreRepository storeRepository, StoreMapper storeMapper) {
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
    }

    public List<Store> findClosestStores(double latitude, double longitude, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return storeRepository.findByLocationNear(latitude, longitude, pageRequest).stream()
                .map(storeMapper::toDomain)
                .toList();
    }

    public List<Store> findClosestStoresOpenNow(double latitude, double longitude, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        var currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        return storeRepository.findByLocationNearAndOpenNow(latitude, longitude, currentTime, pageRequest).stream()
                .map(storeMapper::toDomain)
                .toList();
    }

    public Store addStore(Store store) {
        if (store.uuid() == null){
            store = store.withUuid(UUID.randomUUID().toString());
        }
        StoreEntity savedEntity = storeRepository.save(storeMapper.toEntity(store));
        return storeMapper.toDomain(savedEntity);
    }

    public Optional<Store> updateStore(String uuid, Store storeToUpdate) {
        return storeRepository.findByUuid(uuid)
                .map(existingStore -> {
                    var toUpdate = storeMapper.toEntity(storeToUpdate).withId(existingStore.id());
                    return storeMapper.toDomain(storeRepository.save(toUpdate));
                });
    }

}
