package org.neewrobert.jumbo.adapter.in.web.store;

import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.neewrobert.jumbo.application.mapper.StoreMapper;
import org.neewrobert.jumbo.application.service.store.StoreService;
import org.neewrobert.jumbo.domain.model.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@Tag(name = "Store Management", description = "APIs for managing and finding stores")
public class StoreController {

    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);
    private final StoreService storeService;
    private final MeterRegistry meterRegistry;
    private final StoreMapper storeMapper;

    public StoreController(StoreService storeService, MeterRegistry meterRegistry, StoreMapper storeMapper) {
        this.storeService = storeService;
        this.meterRegistry = meterRegistry;
        this.storeMapper = storeMapper;
    }

    @Operation(summary = "Find the closest stores", description = "Find the closest Jumbo stores based on the given coordinates")
    @GetMapping("/stores/closest")
    public ResponseEntity<List<StoreDto>> getClosestStores(
            @Parameter(description = "Latitude of the location", required = true)
            @RequestParam @NotNull @Min(-90) @Max(90) Double latitude,
            @Parameter(description = "Longitude of the location", required = true)
            @RequestParam @NotNull @Min(-180) @Max(180) Double longitude,
            @Parameter(description = "Number of closest stores to return")
            @RequestParam(required = false, defaultValue = "5") @Min(1) @Max(100) int limit) {

        logger.info("Received request to find closest stores with latitude: {}, longitude: {}, limit: {}", latitude, longitude, limit);

        List<Store> stores = storeService.findClosestStores(latitude, longitude, limit);

        meterRegistry.counter("stores.closest.requests", "latitude", String.valueOf(latitude), "longitude", String.valueOf(longitude), "limit", String.valueOf(limit)).increment();

        return ResponseEntity.ok(stores.stream().map(storeMapper::toDto).toList());
    }

    @Operation(summary = "Find the closest stores that are open now", description = "Find the closest Jumbo stores that are currently open based on the given coordinates")
    @GetMapping("/stores/closest/open")
    public ResponseEntity<List<StoreDto>> getClosestStoresOpenNow(
            @Parameter(description = "Latitude of the location", required = true)
            @RequestParam @NotNull @Min(-90) @Max(90) Double latitude,
            @Parameter(description = "Longitude of the location", required = true)
            @RequestParam @NotNull @Min(-180) @Max(180) Double longitude,
            @Parameter(description = "Number of closest stores to return")
            @RequestParam(required = false, defaultValue = "5") @Min(1) @Max(100) int limit) {

        logger.info("Received request to find closest open stores with latitude: {}, longitude: {}, limit: {}", latitude, longitude, limit);

        List<Store> stores = storeService.findClosestStoresOpenNow(latitude, longitude, limit);

        meterRegistry.counter("stores.closest.open.requests", "latitude", String.valueOf(latitude), "longitude", String.valueOf(longitude), "limit", String.valueOf(limit)).increment();

        return ResponseEntity.ok(stores.stream().map(storeMapper::toDto).toList());
    }

    @Operation(summary = "Add a new store", description = "Create a new store")
    @PostMapping("/stores")
    public ResponseEntity<StoreDto> addStore(
            @Parameter(description = "Store details", required = true)
            @RequestBody @Valid StoreDto storeDto) {

        logger.info("Received request to add a new store: {}", storeDto.addressName());

        Store createdStore = storeService.addStore(storeMapper.toDomain(storeDto));
        meterRegistry.counter("stores.add.requests", "addressName", storeDto.addressName()).increment();

        var uri = "/stores/" + createdStore.uuid(); //this is new from the original code
        return ResponseEntity.created(URI.create(uri)).body(storeMapper.toDto(createdStore));
    }

    @Operation(summary = "Update an existing store", description = "Update an existing store by UUID")
    @PutMapping("/stores/{uuid}")
    public ResponseEntity<StoreDto> updateStore(
            @Parameter(description = "UUID of the store to update", required = true)
            @PathVariable String uuid,
            @Parameter(description = "Updated store details", required = true)
            @RequestBody @Valid StoreDto storeDto) {

        logger.info("Received request to update store with UUID: {}", uuid);
        meterRegistry.counter("stores.update.requests", "uuid", uuid).increment();
        Optional<Store> store = storeService.updateStore(uuid, storeMapper.toDomain(storeDto));

        return store.map(value -> ResponseEntity.ok(storeMapper.toDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    //get an existing store by UUID
    //IT'S NEW FROM THE ORIGINAL CODE
    @Operation(summary = "Get an existing store", description = "Get an existing store by UUID")
    @GetMapping("/stores/{uuid}")
    public ResponseEntity<StoreDto> getStore(
            @Parameter(description = "UUID of the store to get", required = true)
            @PathVariable String uuid) {

        logger.info("Received request to get store with UUID: {}", uuid);
        meterRegistry.counter("stores.get.requests", "uuid", uuid).increment();
        Optional<Store> store = storeService.getStore(uuid);

        return store.map(value -> ResponseEntity.ok(storeMapper.toDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
