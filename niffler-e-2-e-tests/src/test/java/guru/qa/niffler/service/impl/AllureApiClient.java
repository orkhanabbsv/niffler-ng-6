package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AllureApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.CreateProjectRequest;
import guru.qa.niffler.model.rest.SendResultRequest;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllureApiClient extends RestClient {

    private final AllureApi allureApi;

    public AllureApiClient(String url) {
        super(url);
        this.allureApi = create(AllureApi.class);
    }

    public void createProject(String projectId) {
        final Response<Void> response;
        try {
            CreateProjectRequest request = new CreateProjectRequest(projectId);
            response = allureApi.createProject(request).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
    }

    public void sendResults(SendResultRequest request, String projectId, boolean forceProjectCreation) {
        final Response<Void> response;
        try {
            response = allureApi.sendResults(projectId, forceProjectCreation, request).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    public void generateReport(String projectId,
                               String executionName,
                               String executionFrom,
                               String executionType
    ) {
        final Response<Void> response;
        try {
            response = allureApi.generateReport(projectId, executionName, executionFrom, executionType).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }
}
