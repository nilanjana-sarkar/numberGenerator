package com.test.numberGenerator.web;

import com.test.numberGenerator.domain.NumberGeneratorInput;
import com.test.numberGenerator.domain.NumberGeneratorOutput;
import com.test.numberGenerator.domain.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URISyntaxException;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NumberGeneratorControllerSpec{

    @LocalServerPort
    int randomServerPort;

    @Test
    public void generateTask() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        final String baseUrl = "http://localhost:" + randomServerPort + "/api/generate";
        URI uri = new URI(baseUrl);
        NumberGeneratorInput numberGeneratorInput = new NumberGeneratorInput();
        numberGeneratorInput.setGoal("10");
        numberGeneratorInput.setStep("2");
        ResponseEntity<NumberGeneratorOutput> result = restTemplate.postForEntity(uri, numberGeneratorInput, NumberGeneratorOutput.class);

        Assert.assertEquals(202, result.getStatusCodeValue());
    }

    @Test
    public void getStatus() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        String uuid = "5c1545e9-9522-40da-92cf-99922891686d";
        final String baseUrl = "http://localhost:" + randomServerPort + "/api/task/"+ uuid + "/status";
        URI uri = new URI(baseUrl);
        ResponseEntity<Result> result;
        try
        {
            restTemplate.getForEntity(uri, Result.class);
            Assert.fail();
        }
        catch(HttpClientErrorException ex)
        {
            Assert.assertEquals(404, ex.getRawStatusCode());
            Assert.assertTrue(ex.getResponseBodyAsString().contains("Not Found"));
        }
    }

    @Test
    public void getResult() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        String uuid = "5c1545e9-9522-40da-92cf-99922891686d";
        final String baseUrl = "http://localhost:" + randomServerPort + "/api/tasks/"+ uuid + "?action=get_numlist";
        URI uri = new URI(baseUrl);
        ResponseEntity<Result> result;
        try
        {
            restTemplate.getForEntity(uri, Result.class);
        }
        catch(HttpClientErrorException ex)
        {
            Assert.assertEquals(404, ex.getRawStatusCode());
            Assert.assertTrue(ex.getResponseBodyAsString().contains("Not Found"));
        }
    }
}
