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
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import net.memeticvre.josekiclient.Base64;

/**
 * Uploads a file using HTTP
 * @author Andrew G D Rowley (University of Manchester) / Michelle Bachler
 * @version 1.0
 */
public class HttpFileUploadOutputStream extends OutputStream {

    // The connection to the http server
    private HttpURLConnection connection = null;

    // The output stream of the connection
    private DataOutputStream output = null;

    // The query variables
    private HashMap queryVars = new HashMap();

    // The query variable names in order
    private Vector queryNames = new Vector();

    // The boundary
    private String boundary = ""; 

    // The response
    private String response = null;

    /**
     * Creates a new UploadFile
     * @param url The url to upload to
     * @param filename The name to give the file
     * @throws IOException
     */
    public HttpFileUploadOutputStream(URL url, String filename, String username, String password) throws IOException {

        if (!url.getProtocol().equals("http")) { 
            throw new MalformedURLException("URL is not a http URL"); 
        }
        String query = url.getQuery();
        if (query != null) {
            String[] items = query.split("&"); 
            for (int i = 0; i < items.length; i++) {
                String[] item = items[i].split("=", 2); 
                if (item.length == 2) {
                    queryVars.put(item[0], item[1]);
                } else {
                    queryVars.put(item[0], ""); 
                }
                queryNames.add(item[0]);
            }
        }

        // Generate a boundary
        byte b[] = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) (((Math.random() * 1000) % ('z' - 'a')) + 'a');
        }
        boundary = new String(b);

        // Connect to the server
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST"); 
        connection.setRequestProperty("Connection", "Keep-Alive");  
        connection.setRequestProperty("Content-Type", 
                "multipart/form-data;boundary=" + boundary); 
        if ((username != null) && (password != null)) {
            String userpass = Base64.encodeBytes(
                        new String(username + ":" + password).getBytes( 
                                "UTF-8")); 
            connection.setRequestProperty("Authorization", 
                    "Basic " + userpass); 
        }
        connection.connect();
        output = new DataOutputStream(connection.getOutputStream());

        // Output the query variables
        for (int i = 0; i < queryNames.size(); i++) {
            String name = (String) queryNames.get(i);
            String value = (String) queryVars.get(name);
            output.writeBytes("--" + boundary + "\r\n");  
            output.writeBytes("Content-Disposition: form-data;" + 
                    " name=\"" + name + "\"\r\n");  
            output.writeBytes("\r\n"); 
            output.writeBytes(value + "\r\n"); 
        }

        // Start the file
        output.writeBytes("--" + boundary + "\r\n");  
        output.writeBytes("Content-Disposition: form-data;" 
                + " name=\"import\";" 
                + " filename=\"" + filename + "\"\r\n");  
        output.writeBytes("\r\n"); 
    }

    /**
    * Return a random number between the given integers.
    * @param int low
    * @param int high
    * @return int the random number
    */
    public int rand(int l, int h)   {
        int n = h-l+1;
        int i = (int)(Math.random()*1000) % n;
        if (i < 0) {
            i=-i;
        }
        return(l+i);
    }

    /**
     * Write the given int to the output Stream.
     * @param b, the int to write.
     * @throws IOException
     */
    public void write(int b) throws IOException {
        output.write(b);
    }

    /**
     * Close the output stream.
     * @throws IOException
     */
    public void close() throws IOException {
        output.writeBytes("\r\n"); 
        output.writeBytes("--" + boundary + "--\r\n");  
        output.close();
    }

    /**
     * Returns the first line of the response
     * @throws IOException
     */
    public String getResponse() throws IOException {
        if (response == null) {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String line = ""; 

            while (((line = input.readLine()) != null) && line.equals("")) { 
                // Do Nothing
            }

            if (line == null) {
                line = ""; 
            }
            response = line;
            input.close();
        }
        return response;
    }

    /**
     * Uploads to the server from a file
     * @param filename The name of the file to upload
     * @return The string returned from the upload to display to users
     * @throws IOException
     */
    public String uploadFromFile(String filename) throws IOException {

        FileInputStream input = new FileInputStream(filename);
        byte[] buffer = new byte[8096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            write(buffer, 0, bytesRead);
        }
        close();

        return getResponse();
    }
}
