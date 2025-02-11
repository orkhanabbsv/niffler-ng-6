package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.model.rest.SendResultRequest;
import guru.qa.niffler.service.impl.AllureApiClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
public class AllureResultExtension implements SuiteExtension {

    private static final String PROJECT_ID = "niffler";

    private final String allureApi = System.getenv("ALLURE_DOCKER_API");
    private final String executionType = System.getenv("EXECUTION_TYPE");

    private final String allurePath = "/niffler/niffler-e-2-e-tests/build/allure-results";

    private final AllureApiClient allureApiClient = new AllureApiClient(allureApi);

    @Override
    public void afterSuite() throws IOException {
        allureApiClient.createProject(PROJECT_ID);
        log.info("Project {} created successfully", PROJECT_ID);


        SendResultRequest request = new SendResultRequest(getResultsForm());
        log.info("Sending results to Allure API: {}", request);
        allureApiClient.sendResults(request, PROJECT_ID, true);
        log.info("Results sent to Allure API");


        log.info("Generating Allure report");
        allureApiClient.generateReport(PROJECT_ID, null, null, executionType);
        log.info("Allure report generated");
    }

    private List<SendResultRequest.Result> getResultsForm() {
        List<SendResultRequest.Result> results = new ArrayList<>();
        File folder = new File(allurePath);

        if (!folder.exists() || !folder.isDirectory()) {
            log.error("Allure results directory not found: {}", allurePath);
            throw new AssertionError("Allure results directory not found: " + allurePath);
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                results.add(new SendResultRequest.Result(file.getName(), encodeFileToBase64(file)));
            }
        }
        return results;
    }

    private String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
