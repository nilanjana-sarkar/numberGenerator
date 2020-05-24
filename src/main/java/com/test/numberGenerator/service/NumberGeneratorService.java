package com.test.numberGenerator.service;

import com.test.numberGenerator.domain.NumberGeneratorInput;
import com.test.numberGenerator.domain.Status;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class NumberGeneratorService {


    @Async("asyncThreadExecutor")
    public void generateNumberAndWriteFile(NumberGeneratorInput numberGeneratorInput, UUID uuid, ConcurrentHashMap<String, Status> taskStatus){
        try {
            List<String> list = generateNumbers(numberGeneratorInput);
            uploadFile(list,uuid.toString());
            System.out.println("ConcurrentHashMap: " + taskStatus);
            //Thread.sleep(100000);
            taskStatus.replace(uuid.toString(), Status.SUCCESS);
        } catch (Exception | Error e) {
            taskStatus.replace(uuid.toString(), Status.ERROR);
        }
    }

    public List<String> generateNumbers(NumberGeneratorInput numberGeneratorInput) {
        List<String> list = new ArrayList<>();
        try {
            int goal = Integer.parseInt(numberGeneratorInput.getGoal());
            int step = Integer.parseInt(numberGeneratorInput.getStep());
            for(int i = goal; i >=0; i = i-step)
            {
                list.add(String.valueOf(i));
            }
        }
        catch (Exception | Error e){
            e.printStackTrace();
            throw e;
        }
        return list;
    }

    public void uploadFile(List<String> list, String uuid) throws IOException {
        try {
            String filePath = "C:\\tmp\\" + uuid + "_output.txt";
            Path path = Paths.get(filePath);
            String input = String.join(",", list);
            byte[] byteArray = input.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(byteArray);
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            CompletionHandler handler = new CompletionHandler() {
                @Override
                public void completed(Object result, Object attachment) {
                    System.out.println(attachment + " completed and " + result + " bytes are written.");
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println(attachment + " failed with exception:");
                    exc.printStackTrace();
                }
            };
            channel.write(buffer, 0, "Async Task", handler);
            channel.close();
        } catch (IOException | Error e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String readFileContents(String uuid) throws InterruptedException, ExecutionException, IOException {
        String fileContent = null;
        try {
            String filePath = "C:\\tmp\\"+uuid+"_output.txt";
            Path path = Paths.get(filePath);
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                    path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(10000);
            Future<Integer> operation = fileChannel.read(buffer, 0);
            operation.get();

            fileContent = new String(buffer.array()).trim();
            buffer.clear();
        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return fileContent;
    }

}
