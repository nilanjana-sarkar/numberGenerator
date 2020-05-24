package com.test.numberGenerator.web;

import com.test.numberGenerator.domain.NumberGeneratorInput;
import com.test.numberGenerator.domain.NumberGeneratorOutput;
import com.test.numberGenerator.domain.Result;
import com.test.numberGenerator.domain.Status;
import com.test.numberGenerator.exception.ActionException;
import com.test.numberGenerator.exception.FileNotFoundException;
import com.test.numberGenerator.service.NumberGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class NumberGeneratorController {

    @Autowired
    NumberGeneratorService numberGeneratorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberGeneratorController.class);

    static ConcurrentHashMap<String, Status> taskStatus = new ConcurrentHashMap<>();

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public ResponseEntity<NumberGeneratorOutput> generateTask(@RequestBody NumberGeneratorInput numberGeneratorInput) {
        Optional<NumberGeneratorInput> numberGeneratorInputOptional = Optional.ofNullable(numberGeneratorInput);
        NumberGeneratorOutput numberGeneratorOutput = new NumberGeneratorOutput();
        if(numberGeneratorInputOptional.isPresent()){
            NumberGeneratorInput numberGeneratorInput1 = numberGeneratorInputOptional.get();
            UUID uuid = UUID.randomUUID();
            taskStatus.put(uuid.toString(), Status.IN_PROGRESS);
            numberGeneratorOutput.setTask(uuid.toString());
            numberGeneratorService.generateNumberAndWriteFile(numberGeneratorInput1,uuid,taskStatus);
            return new ResponseEntity<NumberGeneratorOutput>(numberGeneratorOutput, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<NumberGeneratorOutput>(numberGeneratorOutput, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/tasks/{uuid}/status", method = RequestMethod.GET)
    public ResponseEntity<Result> getStatus(@PathVariable("uuid") String uuid) {
        LOGGER.info("UUID received as input:" +uuid);
        Result result = new Result();
        result.setResult(taskStatus.get(uuid).name());
        return new ResponseEntity<Result>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<Result> getResult(@PathVariable("uuid") String uuid, @RequestParam("action") String action)
            throws FileNotFoundException, ActionException {
        Result result = new Result();
        HttpStatus status;
        if(action.equalsIgnoreCase("get_numlist")) {
            String fileContent = null;
            try {
                fileContent = numberGeneratorService.readFileContents(uuid);
            } catch (Exception e) {
                throw new FileNotFoundException();
            }
            result.setResult(fileContent);
            status = HttpStatus.OK;
        }
        else {
            throw new ActionException();
        }
        return new ResponseEntity<Result>(result, status);
    }

}
