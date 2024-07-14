package org.neewrobert.jumbo.adapter.in.web.store;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.neewrobert.jumbo.config.MongoDBTestContainerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MongoDBTestContainerConfig.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StoreControllerIntegrationTest {


    @Autowired
    MongoTemplate mongoTemplate;

    @LocalServerPort
    private int port = 0;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        mongoTemplate.dropCollection("stores");
        insertTestData();
        createGeospatialIndex();
    }

    @Test
    public void testGetClosestStores() {

        double baseLat = 51.778;
        double baseLon = 4.615;

        Response response = given()
                .param("latitude", baseLat)
                .param("longitude", baseLon)
                .param("limit", 5)
                .when()
                .get("/stores/closest")
                .then()
                .statusCode(200)
                .body("$", hasSize(5))
                .extract().response();

        List<StoreDto> apiStores = response.jsonPath().getList(".", StoreDto.class);
        assertEquals(5, apiStores.size());
    }

    @Test
    public void testAddStore() {
        StoreDto newStore = new StoreDto(
                null,
                "New City",
                "12345",
                "New Street",
                "1",
                "",
                "New Store",
                4.0,
                52.0,
                "00000",
                false,
                "08:00",
                "Supermarkt",
                false,
                "0000",
                "20:00"
        );

        given()
                .contentType("application/json")
                .body(newStore)
                .when()
                .post("/stores")
                .then()
                .statusCode(200)
                .body("city", equalTo("New City"))
                .body("postalCode", equalTo("12345"));
    }

    @Test
    public void testGetClosestStoresWithDifferentLimits() {
        double baseLat = 51.778;
        double baseLon = 4.615;
        int limit = 2;

        given()
                .param("latitude", baseLat)
                .param("longitude", baseLon)
                .param("limit", limit)
                .when()
                .get("/stores/closest")
                .then()
                .statusCode(200)
                .body("$", hasSize(limit));

    }


    @Test
    public void testGetClosestStoresWithInvalidCoordinates() {
        given()
                .param("latitude", "invalid")
                .param("longitude", "invalid")
                .param("limit", 5)
                .when()
                .get("/stores/closest")
                .then()
                .statusCode(400)
                .body("error", equalTo("Invalid Parameter"));
    }

    private void insertTestData() {
        List<Document> stores = List.of(
                new Document()
                        .append("city", "'s Gravendeel")
                        .append("postalCode", "3295 BD")
                        .append("street", "Kerkstraat")
                        .append("street2", "37")
                        .append("street3", "")
                        .append("addressName", "Jumbo 's Gravendeel Gravendeel Centrum")
                        .append("uuid", "EOgKYx4XFiQAAAFJa_YYZ4At")
                        .append("location", new Document("type", "Point")
                                .append("coordinates", List.of(4.615551, 51.778461)))
                        .append("complexNumber", "33249")
                        .append("showWarningMessage", true)
                        .append("todayOpen", "08:00")
                        .append("locationType", "SupermarktPuP")
                        .append("collectionPoint", true)
                        .append("sapStoreID", "3605")
                        .append("todayClose", "20:00"),
                new Document()
                        .append("city", "'s-Heerenberg")
                        .append("postalCode", "7041 JE")
                        .append("street", "Stadsplein")
                        .append("street2", "71")
                        .append("street3", "")
                        .append("addressName", "Jumbo 's-Heerenberg Stadsplein")
                        .append("uuid", "7ewKYx4Xqp0AAAFIHigYwKrH")
                        .append("location", new Document("type", "Point")
                                .append("coordinates", List.of(6.245829, 51.874272)))
                        .append("complexNumber", "30170")
                        .append("showWarningMessage", true)
                        .append("todayOpen", "08:00")
                        .append("locationType", "Supermarkt")
                        .append("sapStoreID", "4670")
                        .append("todayClose", "21:00"),
                new Document()
                        .append("city", "Aalsmeer")
                        .append("postalCode", "1431 HN")
                        .append("street", "Ophelialaan")
                        .append("street2", "124")
                        .append("street3", "")
                        .append("addressName", "Jumbo Aalsmeer Ophelialaan")
                        .append("uuid", "gssKYx4XJwoAAAFbn.BMqPTb")
                        .append("location", new Document("type", "Point")
                                .append("coordinates", List.of(4.762433, 52.264417)))
                        .append("complexNumber", "33010")
                        .append("showWarningMessage", true)
                        .append("todayOpen", "08:00")
                        .append("locationType", "SupermarktPuP")
                        .append("collectionPoint", true)
                        .append("sapStoreID", "3178")
                        .append("todayClose", "22:00"),
                new Document()
                        .append("city", "Aalst")
                        .append("postalCode", "5582 CL")
                        .append("street", "Hortensialaan")
                        .append("street2", "2")
                        .append("street3", "")
                        .append("addressName", "Jumbo Aalst Paul en Marjon Houben")
                        .append("uuid", "Tk0KYx4XZ3YAAAFc_DRE1DKo")
                        .append("location", new Document("type", "Point")
                                .append("coordinates", List.of(5.469597, 51.399843)))
                        .append("complexNumber", "33011")
                        .append("showWarningMessage", true)
                        .append("todayOpen", "08:00")
                        .append("locationType", "SupermarktPuP")
                        .append("collectionPoint", true)
                        .append("sapStoreID", "3754")
                        .append("todayClose", "20:00"),
                new Document()
                        .append("city", "Aalten")
                        .append("postalCode", "7122 WJ")
                        .append("street", "Admiraal de Ruyterstraat")
                        .append("street2", "10")
                        .append("street3", "")
                        .append("addressName", "Jumbo Aalten Leussink")
                        .append("uuid", "0XcKYx4XNRQAAAFI3LgYwKxK")
                        .append("location", new Document("type", "Point")
                                .append("coordinates", List.of(6.576066, 51.923993)))
                        .append("complexNumber", "30519")
                        .append("showWarningMessage", true)
                        .append("todayOpen", "08:00")
                        .append("locationType", "SupermarktPuP")
                        .append("collectionPoint", true)
                        .append("sapStoreID", "6487")
                        .append("todayClose", "21:00")
        );

        stores.forEach(store -> mongoTemplate.save(store, "stores"));
    }

    private void createGeospatialIndex() {
        mongoTemplate.indexOps("stores").ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
    }

}


