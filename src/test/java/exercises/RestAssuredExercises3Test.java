package exercises;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class RestAssuredExercises3Test {

    private static RequestSpecification requestSpec;

    @BeforeAll
    public static void setUp() {

        createRequestSpecification();
        createResponseSpecification();
        getNinthDriverId();
    }

    static void createRequestSpecification() {

        requestSpec = new RequestSpecBuilder().
                setBaseUri("http://localhost").
                setPort(9876).
                setBasePath("/api/f1").
                build();
    }


    /*******************************************************
     * Create a static ResponseSpecification that checks whether:
     * - the response has statusCode 200
     * - the response contentType is JSON
     * - the value of MRData.CircuitTable.Circuits.circuitName[0]
     *   is equal to 'Albert Park Grand Prix Circuit'
     ******************************************************/

    private static ResponseSpecification responseSpec;


    static void createResponseSpecification() {
        responseSpec = new ResponseSpecBuilder().
                expectStatusCode(200).expectContentType(ContentType.JSON).
                expectBody("MRData.CircuitTable.Circuits[0].circuitName", equalTo("Albert Park Grand Prix Circuit")).build();

    }

    /*******************************************************
     * Retrieve the list of 2016 Formula 1 drivers and store
     * the driverId for the ninth mentioned driver in a
     * static String variable
     * Use /2016/drivers.json
     ******************************************************/

    private static String ninthDriverId;

@BeforeAll
    static void getNinthDriverId() {
        ninthDriverId = given().
                spec(requestSpec).
                when().
                get("/2016/drivers.json").
                then().
                extract().
                path("MRData.DriverTable.Drivers[8].driverId");


    }

    /*******************************************************
     * Retrieve the circuit data for the first race in 2014
     * Use the previously created ResponseSpecification to
     * execute the specified checks
     * Use /2014/1/circuits.json
     * Additionally, check that the circuit is located in Melbourne
     ******************************************************/

    @Test
    public void useResponseSpecification() {

        given().
                spec(requestSpec).
                when().
                get("/2014/1/circuits.json").
                then().
                spec(responseSpec).
                body("MRData.CircuitTable.Circuits[0].Location.locality", is("Melbourne"));
    }

    /*******************************************************
     * Retrieve the driver data for the ninth mentioned driver
     * Use the previously extracted driverId to do this
     * Use it as a path parameter to /drivers/<driverId>.json
     * Check that the driver is German
     ******************************************************/

    @Test
    public void useExtractedDriverId() {

        given().
                spec(requestSpec).
                when().
                get("/drivers/"+ninthDriverId+".json").
                then().
                body("MRData.DriverTable.Drivers[0].nationality",equalTo("German"))

        ;
    }
}