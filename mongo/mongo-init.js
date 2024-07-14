const fs = require('fs');
const path = require('path');


const storesFilePath = path.resolve('/docker-entrypoint-initdb.d/stores.json');

// Read the stores.json file and parse it as JSON
let storesData;
try {
    const data = fs.readFileSync(storesFilePath, 'utf8');
    const jsonData = JSON.parse(data);
    storesData = jsonData.stores;  // Access the stores array within the jsonData object
} catch (err) {
    print('Error reading or parsing stores.json file:', err);
}

if (Array.isArray(storesData)) {
    const stores = storesData.map(store => ({
        city: store.city,
        postalCode: store.postalCode,
        street: store.street,
        street2: store.street2,
        street3: store.street3,
        addressName: store.addressName,
        uuid: store.uuid,
        location: {
            type: 'Point',
            coordinates: [parseFloat(store.longitude), parseFloat(store.latitude)]
        },
        complexNumber: store.complexNumber,
        showWarningMessage: store.showWarningMessage,
        todayOpen: store.todayOpen,
        locationType: store.locationType,
        collectionPoint: store.collectionPoint,
        sapStoreID: store.sapStoreID,
        todayClose: store.todayClose
    }));

    // Insert the stores into the stores collection
    stores.forEach(store => db.stores.insert(store));

    // Create a geospatial index on the location field
    db.stores.createIndex({ location: '2dsphere' });

    // Create a unique index on the uuid field
    db.stores.createIndex({ uuid: 1 }, { unique: true });
} else {
    print('storesData is not an array:', storesData);
}
