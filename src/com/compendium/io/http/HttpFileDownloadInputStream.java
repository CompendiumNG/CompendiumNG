/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2010 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.io.http;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.memeticvre.josekiclient.Base64;

/**
 * Downloads a file using an HTTP form
 * @author Andrew G D Rowley (University of Manchester) / Michelle Bachler
 * @version 1.0
 */
public class HttpFileDownloadInputStream extends InputStream {

    // The connection to the server
    private HttpURLConnection connection = null;

    // The input stream to read from
    private InputStream input = null;

    /**
     * Creates a new HttpFileDownloadInputStream
     * @param url The url to download from
     * @throws IOException
     */
    public HttpFileDownloadInputStream(URL url) throws IOException {

        if (!url.getProtocol().equals("http")) { 
            throw new MalformedURLException("URL is not a http URL"); 
        }

        // Get a Connection
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setUseCaches(false);
        connection.setRequestMethod("GET"); 
        connection.connect();
        input = connection.getInputStream();
    }

    /**
     * Creates a new HttpFileDownloadInputStream
     * @param url The url to download from
     * @throws IOException
     */
    public HttpFileDownloadInputStream(URL url, String username, String password) throws IOException {

        if (!url.getProtocol().equals("http")) { 
            throw new MalformedURLException("URL is not a http URL"); 
        }

        // Get a Connection
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setUseCaches(false);
        connection.setRequestMethod("GET"); 
        if ((username != null) && (password != null)) {
            String userpass = Base64.encodeBytes(
                        new String(username + ":" + password).getBytes( 
                                "UTF-8")); 
            connection.setRequestProperty("Authorization", 
                    "Basic " + userpass); 
        }
        connection.connect();
        input = connection.getInputStream();
    }

    /**
     * Read on the input stream.
     * @throws IOException
     */
    public int read() throws IOException {
        return input.read();
    }

    /**
     * Download the file to a String
     * @throws IOException
     */
    public String downloadToString() throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(input));
        String fileText = "";
        String next = "";
        while ((next = is.readLine()) != null) {
        	fileText+=next+"\n";
        }        
        return fileText;
    }

    
    /**
     * Downloads the file to the given location
     * @param sOutputName The filename/path to store the file to.
     * @throws IOException
     */
    public void downloadToFile(String sOutputName) throws IOException {

        FileOutputStream output = new FileOutputStream(sOutputName);
        
        byte[] buffer = new byte[8096];
        int bytesRead = 0;
        while ((bytesRead = read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        
        output.close();
    }
}
