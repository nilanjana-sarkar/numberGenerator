package com.test.numberGenerator.service;

import com.test.numberGenerator.domain.NumberGeneratorInput;
import com.test.numberGenerator.domain.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class NumberGeneratorServiceSpec {

    NumberGeneratorService generatorService = Mockito.mock(NumberGeneratorService.class);
    NumberGeneratorInput numberGeneratorInput;
    ConcurrentHashMap<String, Status> taskStatus = new ConcurrentHashMap<>();
    List<String> list;
    UUID uuid;

    @Before
    public void setUp() {
        generatorService = new NumberGeneratorService();
        numberGeneratorInput = new NumberGeneratorInput();
        numberGeneratorInput.setGoal("10");
        numberGeneratorInput.setStep("2");
        taskStatus = new ConcurrentHashMap<String, Status>();
        list = new ArrayList<>();
        list.add("4");
        list.add("2");
        list.add("0");
        uuid = UUID.randomUUID();
    }

    @AfterEach
    public void tearDown() {
        File file = new File("C:\\tmp\\");
        recursiveDelete(file);
    }

    public static void recursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }
        file.delete();
    }

    @Test
    public void generateNumberAndWriteFileTest() {
        File newFile = new File("C:\\tmp\\");
        newFile.mkdir();
        UUID uuid = UUID.randomUUID();
        taskStatus.put(uuid.toString(), Status.IN_PROGRESS);
        generatorService.generateNumberAndWriteFile(numberGeneratorInput, uuid, taskStatus);
        File file = new File("D:\\tmp\\"+uuid.toString()+"_output.txt");
        assertEquals(Status.SUCCESS, taskStatus.get(uuid.toString()));
    }

    @Test
    public void uploadFileTest() throws IOException, ExecutionException, InterruptedException {
        File newFile = new File("D:\\tmp\\");
        newFile.mkdir();
        UUID uuid = UUID.randomUUID();
        generatorService.uploadFile(list, uuid.toString());
        assertEquals("4,2,0", generatorService.readFileContents(uuid.toString()));
    }

    @Test(expected = NoSuchFileException.class)
    public void readFileContentsTest() throws IOException, ExecutionException, InterruptedException {
        File newFile = new File("C:\\tmp\\");
        newFile.mkdir();
        generatorService.readFileContents(uuid.toString());
    }

    @Test
    public void generateNumbersTest() {
        list = generatorService.generateNumbers(numberGeneratorInput);
        assertEquals(false, list.isEmpty());
        assertEquals(6, list.size());
    }
}
