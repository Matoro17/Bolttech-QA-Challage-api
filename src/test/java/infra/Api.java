package infra;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Base64;

public class Api {
    static String baseUri = "http://localhost:5000";

    static public Response register(String email, String password, String role) {
        RestAssured.baseURI = Api.baseUri;
        RequestSpecification request = RestAssured.given();
        request.contentType("multipart/form-data")
                .multiPart("email", email)
                .multiPart("password", password)
                .multiPart("role", role);

        return request.post("/api/register");
    }

    public static Response login(String email, String password) {
        String authorization = email + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(authorization.getBytes());

        RestAssured.baseURI = Api.baseUri;
        RequestSpecification request = RestAssured.given();

        request.contentType("multipart/form-data")
                .header("Authorization", "Basic " + encoded);
        return request.post("/api/login");
    }

    public static Response getAllEmployees(String accessToken) {
        RestAssured.baseURI = Api.baseUri;
        RequestSpecification request = RestAssured.given();

        request.contentType("multipart/form-data")
                .header("accessToken", accessToken);
        return request.get("/api/employees/all");
    }

    public static Response getEmployee(Integer id, String accessToken) {
        RestAssured.baseURI = Api.baseUri;
        RequestSpecification request = RestAssured.given();

        request.contentType("multipart/form-data").header("accessToken", accessToken);
        return request.get("/api/employees/" + id);
    }

    public static Response createEmployee(String firstName, String lastName, String email, String accessToken) {
        RestAssured.baseURI = Api.baseUri;
        RequestSpecification request = RestAssured.given();
        request.contentType("multipart/form-data")
                .header("accessToken", accessToken)
                .multiPart("firstname", firstName)
                .multiPart("lastname", lastName)
                .multiPart("email", email);

        return request.post("/api/employees");
    }

    public static Response editEmployee(Integer id, String firstName, String lastName, String email, String accessToken) {
        RestAssured.baseURI = Api.baseUri;
        RequestSpecification request = RestAssured.given();
        request.contentType("multipart/form-data")
                .header("accessToken", accessToken)
                .multiPart("id", id)
                .multiPart("firstname", firstName)
                .multiPart("lastname", lastName)
                .multiPart("email", email);

        return request.put("/api/employees");
    }

    public static Response deleteEmployee(Integer id, String accessToken) {
        RestAssured.baseURI = Api.baseUri;
        RequestSpecification request = RestAssured.given();
        request.contentType("multipart/form-data")
                .header("accessToken", accessToken)
                .multiPart("id", id);

        return request.delete("/api/employees");

    }
}
