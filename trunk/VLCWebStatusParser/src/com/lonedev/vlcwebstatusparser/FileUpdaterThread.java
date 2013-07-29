package com.lonedev.vlcwebstatusparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles the checking of the VLC web URL and updating a file with
 * the details of the next song/track.
 * 
 * @author Richard Hawkes
 */
public class FileUpdaterThread extends Thread {
    private String webUrl;
    private int refreshDelay;
    private String outputFormat;
    private File outputFile;
    private boolean keepRunning = true;
    private boolean copyAlbumArt = false;
    private FileUpdaterErrorHandler errorHandler;
    

    public FileUpdaterThread(String webUrl, int refreshDelay, String outputFormat, File outputFile, FileUpdaterErrorHandler errorHandler, boolean copyAlbumArt) {
        setWebUrl(webUrl);
        setRefreshDelay(refreshDelay);
        setOutputFormat(outputFormat);
        setOutputFile(outputFile);
        setErrorHandler(errorHandler);
        setCopyAlbumArt(copyAlbumArt);
    }
    
    @Override
    public void run() {
        try {
            XMLMetadataHandler metaHandler = new XMLMetadataHandler();
            String currentTitle = "";
            String currentState = "";
            
            while (keepRunning) {
                metaHandler.parse(webUrl);
                Properties metaDataHandlerProperties = metaHandler.getMetaProperties();
                String title = metaDataHandlerProperties.getProperty("title");
                String state = metaDataHandlerProperties.getProperty("state");
                String albumArtUrl = metaDataHandlerProperties.getProperty("artwork_url");
                
                if (!state.equalsIgnoreCase("playing") && !state.equals(currentState)) {
                    // If we're here it's because the Web URL is telling us it's paused or stopped.
                    writeNewDetailsToFile(state.toUpperCase());
                    currentTitle = "";
                    currentState = state; // Update the current state to paused/stopped so the file only gets updated when the state changes.
                } else if (title != null && !title.equals(currentTitle) && state.equalsIgnoreCase("playing")) {
                    String outputText = getReformattedOutputText(metaDataHandlerProperties);
                    writeNewDetailsToFile(outputText);
                    maybeCopyArtwork(albumArtUrl);
                    currentTitle = title; // Update the current title with the new song/track so the file will only get updated again when the title changes.
                    currentState = state; // Not actually sure if this is needed now.
                }
                
                try { Thread.sleep(refreshDelay); } catch (Exception ex) { }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorHandler.handleError(ex.toString());
        }
    }
    
    /**
     * @return the webUrl
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * @param webUrl the webUrl to set
     */
    private void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    /**
     * @return the refreshDelay
     */
    public int getRefreshDelay() {
        return refreshDelay;
    }

    /**
     * @param refreshDelay the refreshDelay to set
     */
    private void setRefreshDelay(int refreshDelay) {
        this.refreshDelay = refreshDelay;
    }

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     */
    private void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the outputFile
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * @param outputFile the outputFile to set
     */
    private void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
    
    public void stopUpdaterThread() {
        this.keepRunning = false;
    }

    private String getReformattedOutputText(Properties metaDataHandlerProperties) {
        Pattern metaSearch = Pattern.compile("\\{.*?\\}");
        Matcher matcher = metaSearch.matcher(outputFormat);
        
        StringBuffer reformattedTextSB = new StringBuffer();
        
        while (matcher.find()) {
            String metadataName = outputFormat.substring(matcher.start()+1, matcher.end()-1);
            String metadataValue = metaDataHandlerProperties.getProperty(metadataName);
            matcher.appendReplacement(reformattedTextSB, metadataValue == null ? "null" : metadataValue);
        }
        
        matcher.appendTail(reformattedTextSB);
        
        return reformattedTextSB.toString();
    }

    /**
     * @return the errorHandler
     */
    public FileUpdaterErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * @param errorHandler the errorHandler to set
     */
    public void setErrorHandler(FileUpdaterErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    private void writeNewDetailsToFile(String outputText) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFile);
        out.println(outputText);
        out.close();
    }

    /**
     * @return the copyAlbumArt
     */
    public boolean isCopyAlbumArt() {
        return copyAlbumArt;
    }

    /**
     * @param copyAlbumArt the copyAlbumArt to set
     */
    public void setCopyAlbumArt(boolean copyAlbumArt) {
        this.copyAlbumArt = copyAlbumArt;
    }

    private void maybeCopyArtwork(String albumArtUrl) throws MalformedURLException, URISyntaxException {
        if (isCopyAlbumArt() && albumArtUrl != null) {
            File albumArt = new File(new URL(albumArtUrl).toURI());
            if (albumArt.exists()) {
                String copyToDir = getOutputFile().getParent();
                // TODO - The art should be copied to the same folder as the output file.
            }
        }
    }
}
