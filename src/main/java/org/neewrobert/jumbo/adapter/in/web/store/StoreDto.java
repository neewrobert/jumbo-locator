package org.neewrobert.jumbo.adapter.in.web.store;

import jakarta.validation.constraints.*;

public record StoreDto(
        String uuid,
        @NotBlank @Size(max = 50) String city,
        @NotBlank @Pattern(regexp = "\\d{5}", message = "Postal code should be a 5 digit number") String postalCode,
        @NotBlank @Size(max = 100) String street,
        @Size(max = 100) String street2,
        @Size(max = 100) String street3,
        @NotBlank @Size(max = 100) String addressName,
        @NotNull @Min(-180) @Max(180) double longitude,
        @NotNull @Min(-90) @Max(90) double latitude,
        @NotBlank String complexNumber,
        boolean showWarningMessage,
        @Pattern(regexp = "\\d{2}:\\d{2}", message = "Time should be in HH:mm format") String todayOpen,
        @NotBlank @Size(max = 50) String locationType,
        boolean collectionPoint,
        @NotBlank String sapStoreID,
        @Pattern(regexp = "\\d{2}:\\d{2}", message = "Time should be in HH:mm format") String todayClose

) {
}