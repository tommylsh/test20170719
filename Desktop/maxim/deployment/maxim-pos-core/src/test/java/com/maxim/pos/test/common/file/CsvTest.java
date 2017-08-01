package com.maxim.pos.test.common.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxim.util.CsvReader;
import com.maxim.util.CsvWriter;

public class CsvTest {

    private static Logger logger = LoggerFactory.getLogger(FtpTest.class);

    @Test
    public void writeCsvTest() {

        FileOutputStream output = null;
        try {
            output = new FileOutputStream("C:/test.csv");
            output.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            CsvWriter csvWriter = new CsvWriter(output, ',', Charset.forName("UTF-8"));

            String[][] data = { { "a1,a1\r\n", "a2,,,a2'a2'" }, { "b1", "b2" } };
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    csvWriter.write(data[i][j].replaceAll("[\r\n]", ""));
                }

                csvWriter.endRecord();
            }

            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }

    }

    @Test
    public void readCsvTest() throws Exception {
        writeCsvTest();
        InputStreamReader input = null;
        
        try {
            FileInputStream stream = new FileInputStream("C:/test.csv");
            stream.read(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            input = new InputStreamReader(stream, "UTF-8");
            CsvReader reader = new CsvReader(input, ',');

            int rowIndex = 0;
            while (reader.readRecord()) {
                for (int colIndex = 0; colIndex < reader.getColumnCount(); colIndex++) {
                    String text = reader.get(colIndex).trim();

                    logger.info("row: {}, col {}: {}", rowIndex, colIndex, text);

                }
                rowIndex++;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(input);
        }

    }

}
