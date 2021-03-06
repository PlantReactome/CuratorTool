/*
 * Created on Jun 18, 2009
 *
 */
package org.gk.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

/**
 * A group of utiltiy methods related to File I/O.
 * @author wgm
 *
 */
public class FileUtilities {
    // For input
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    // For output
    private FileWriter fileWriter;
    private PrintWriter printWriter;
    
    public FileUtilities() {
    }
    
	public void setInput(String fileName) throws IOException {
		setInput(fileName, false);
	}
    
    public void setInput(String fileName, boolean isURL) throws IOException {
        inputStreamReader = isURL ? new InputStreamReader(new URL(fileName).openStream()) : new FileReader(fileName);
        bufferedReader = new BufferedReader(inputStreamReader);
    }
    
    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }
    
    public void close() throws IOException {
        if (bufferedReader != null) {
            bufferedReader.close();
            inputStreamReader.close();
            bufferedReader = null;
            inputStreamReader = null;
        }
        if (printWriter != null) {
            printWriter.close();
            printWriter = null;
            fileWriter.close();
            fileWriter = null;
        }
    }
    
    public void setOutput(String fileName) throws IOException {
        fileWriter = new FileWriter(fileName);
        printWriter = new PrintWriter(fileWriter);
    }
    
    public void printLine(String line) throws IOException {
        printWriter.println(line);
    }
}
