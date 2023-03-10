import com.google.gson.Gson;
import infra.Api;
import io.restassured.response.Response;
import models.dtos.EmployeeResponse;
import models.dtos.LoginResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

public class ApiBolttechTest {
    String adminEmail = "admin@email.com";
    String adminPassword = "potato";
    String currentAccessToken;

    @BeforeClass
    void create_baseData() {
        Api.register(adminEmail, adminPassword, "admin");
    }

    @BeforeClass
    void getAccessToken() {
        Response response1 = Api.login(adminEmail, adminPassword);
        var body1 = response1.getBody();
        Gson gson = new Gson();
        LoginResponse loginResponse = gson.fromJson(body1.asString(), LoginResponse.class);

        currentAccessToken = loginResponse.token;
    }

    @Test
    void test_CreateUser() {
        String email = "test" + new Random().nextInt() + "@email.com";
        String password = "potato";

        Response response = Api.register(email, password, "admin");
        var body = response.getBody();

        Assert.assertEquals(response.getStatusLine(), "HTTP/1.0 201 CREATED");
        Assert.assertEquals(body.asString(), "{\"success\":true, \"message\":\"created with role admin\"}");

        Response response1 = Api.login(email, password);
        var body1 = response1.getBody();
        Assert.assertEquals(response1.getStatusLine(), "HTTP/1.0 201 CREATED");
        Gson gson = new Gson();
        LoginResponse loginReponse = gson.fromJson(body1.asString(), LoginResponse.class);

        Assert.assertNotEquals(loginReponse.token, "");

    }

    @Test
    void test_LoginUser() {
        Response response1 = Api.login(adminEmail, adminPassword);
        var body1 = response1.getBody();

        Assert.assertEquals(response1.getStatusLine(), "HTTP/1.0 201 CREATED");
        Gson gson = new Gson();
        LoginResponse loginReponse = gson.fromJson(body1.asString(), LoginResponse.class);

        Assert.assertNotEquals(loginReponse.token, "");
    }

    @Test
    void test_EntryWithFakeUser() {
        String email = "fake@email.com";
        String password = "fakepass";

        Response response1 = Api.login(email, password);
        var body1 = response1.getBody();

        Assert.assertEquals(response1.getStatusLine(), "HTTP/1.0 201 CREATED");
        Gson gson = new Gson();
        LoginResponse loginReponse = gson.fromJson(body1.asString(), LoginResponse.class);

        Assert.assertEquals(loginReponse.token, "");
    }

    @Test
    void test_CreateNewEmployees() {
        String firstname = "jorge", lastname = "Feitosa", email = "jorge@feitosa.com";
        Response newEmployee = Api.createEmployee(firstname, lastname, email, currentAccessToken);

        Gson gson = new Gson();
        EmployeeResponse newEmployeeObject = gson.fromJson(newEmployee.asString(), EmployeeResponse.class);
        String newEmployeeID = newEmployeeObject.message.replaceAll("[^0-9]", "");
        newEmployeeObject.id = Integer.parseInt(newEmployeeID);

        Response checkNewEmployee = Api.getEmployee(newEmployeeObject.id, currentAccessToken);
        EmployeeResponse checkNewEmployeeObject = gson.fromJson(checkNewEmployee.asString(), EmployeeResponse.class);

        Assert.assertEquals(checkNewEmployeeObject.id, newEmployeeObject.id);
        Assert.assertEquals(checkNewEmployeeObject.email, email);
        Assert.assertEquals(checkNewEmployeeObject.first_name, firstname);
        Assert.assertEquals(checkNewEmployeeObject.last_name, lastname);

    }

    @Test
    void test_UpdateEmployees() {
        String firstname = "Gabro", lastname = "Wilds", email = "Gabro@wilds.com";
        Response newEmployee = Api.createEmployee(firstname, lastname, email, currentAccessToken);

        Gson gson = new Gson();
        EmployeeResponse newEmployeeObject = gson.fromJson(newEmployee.asString(), EmployeeResponse.class);
        String newEmployeeID = newEmployeeObject.message.replaceAll("[^0-9]", "");
        newEmployeeObject.id = Integer.parseInt(newEmployeeID);

        Response checkNewEmployee = Api.getEmployee(newEmployeeObject.id, currentAccessToken);
        EmployeeResponse checkNewEmployeeObject = gson.fromJson(checkNewEmployee.asString(), EmployeeResponse.class);

        Assert.assertEquals(checkNewEmployeeObject.id, newEmployeeObject.id);
        Assert.assertEquals(checkNewEmployeeObject.email, email);
        Assert.assertEquals(checkNewEmployeeObject.first_name, firstname);
        Assert.assertEquals(checkNewEmployeeObject.last_name, lastname);

        Response update = Api.editEmployee(checkNewEmployeeObject.id, "Feldspato", "outer", "feldspato@outer.com", currentAccessToken);
        EmployeeResponse updatedEmployee = gson.fromJson(update.asString(), EmployeeResponse.class);

        Assert.assertEquals(updatedEmployee.message, "updated");

        Response updatedEmployeeObjectResponse = Api.getEmployee(newEmployeeObject.id, currentAccessToken);
        EmployeeResponse updatedEmployeeObject = gson.fromJson(updatedEmployeeObjectResponse.asString(), EmployeeResponse.class);

        Assert.assertEquals(updatedEmployeeObject.id, checkNewEmployeeObject.id);
        Assert.assertEquals(updatedEmployeeObject.email, "feldspato@outer.com");
        Assert.assertEquals(updatedEmployeeObject.first_name, "Feldspato");
        Assert.assertEquals(updatedEmployeeObject.last_name, "outer");
    }

    @Test
    void test_DeleteEmployee() {
        String firstname = "Gabro", lastname = "Wilds", email = "Gabro@wilds.com";
        Response newEmployee = Api.createEmployee(firstname, lastname, email, currentAccessToken);

        Gson gson = new Gson();
        EmployeeResponse newEmployeeObject = gson.fromJson(newEmployee.asString(), EmployeeResponse.class);
        String newEmployeeID = newEmployeeObject.message.replaceAll("[^0-9]", "");
        newEmployeeObject.id = Integer.parseInt(newEmployeeID);

        Response deleted = Api.deleteEmployee(newEmployeeObject.id, currentAccessToken);
        EmployeeResponse deletedResponse = gson.fromJson(deleted.asString(), EmployeeResponse.class);

        Assert.assertEquals(deletedResponse.message, "deleted");

        Response deletedEmployeResponse = Api.getEmployee(newEmployeeObject.id, currentAccessToken);
        EmployeeResponse deletedConfirmation = gson.fromJson(deletedEmployeResponse.asString(), EmployeeResponse.class);

        Assert.assertEquals(deletedConfirmation.message, "employee not found");

    }

    @Test
    void create_OnlyReadUser() {
        String email = "test" + new Random().nextInt() + "@email.com";
        String password = "potato";

        Response response = Api.register(email, password, "read");
        var body = response.getBody();

        Assert.assertEquals(response.getStatusLine(), "HTTP/1.0 201 CREATED");
        Assert.assertEquals(body.asString(), "{\"success\":true, \"message\":\"created with role read\"}");

        Response response1 = Api.login(email, password);
        var body1 = response1.getBody();

        Assert.assertEquals(response1.getStatusLine(), "HTTP/1.0 201 CREATED");
        Gson gson = new Gson();
        LoginResponse loginReponse = gson.fromJson(body1.asString(), LoginResponse.class);
        Assert.assertNotEquals(loginReponse.token, "");

        Response testFail = Api.createEmployee("will", "notu", "work@email.com", loginReponse.token);
        EmployeeResponse failedTry = gson.fromJson(testFail.asString(), EmployeeResponse.class);

        Assert.assertEquals(failedTry.message, "user does not have permissions to execute it");
        Assert.assertFalse(failedTry.success);

    }

    @Test
    void create_OnlyWriteUser() {
        String email = "test" + new Random().nextInt() + "@email.com";
        String password = "potato";

        Response response = Api.register(email, password, "write");
        var body = response.getBody();

        Assert.assertEquals(response.getStatusLine(), "HTTP/1.0 201 CREATED");
        Assert.assertEquals(body.asString(), "{\"success\":true, \"message\":\"created with role write\"}");

        Response response1 = Api.login(email, password);
        var body1 = response1.getBody();

        Assert.assertEquals(response1.getStatusLine(), "HTTP/1.0 201 CREATED");
        Gson gson = new Gson();
        LoginResponse loginReponse = gson.fromJson(body1.asString(), LoginResponse.class);
        Assert.assertNotEquals(loginReponse.token, "");

        Response testWork = Api.createEmployee("will", "definitely", "work@email.com", loginReponse.token);
        EmployeeResponse writeEmployee = gson.fromJson(testWork.asString(), EmployeeResponse.class);
        String newEmployeeID = writeEmployee.message.replaceAll("[^0-9]", "");
        writeEmployee.id = Integer.parseInt(newEmployeeID);

        Assert.assertTrue(writeEmployee.success);

        Response getEmployee = Api.getEmployee(writeEmployee.id, loginReponse.token);
        EmployeeResponse getEmployeeObject = gson.fromJson(getEmployee.asString(), EmployeeResponse.class);

        Assert.assertEquals(getEmployeeObject.last_name, "definitely");

    }

}
