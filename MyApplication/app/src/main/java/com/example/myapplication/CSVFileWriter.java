package com.example.myapplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVFileWriter {

    private PrintWriter csvWriter;
    private File file;

    public CSVFileWriter(File file) {
        this.file = file;

    }

    public void writeHeader(String data) {

        try {
            if (data != null) {

                csvWriter = new PrintWriter(new FileWriter(file, true));
                csvWriter.print(",");
                csvWriter.print(data);
                csvWriter.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

