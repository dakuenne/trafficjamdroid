/*
 * Copyright (c) 2011, Daniel Kuenne
 * 
 * This file is part of TrafficJamDroid.
 *
 * TrafficJamDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TrafficJamDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TrafficJamDroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.traffic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.traffic.logging.Log;
import org.traffic.server.data.Meta;
import org.traffic.server.data.Request;

/**
 * Helper-class to communicate over a {@link Socket}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 223 $
 */
public class SocketCommunicator {

	/**
	 * Reads gzip-compressed data from the given socket.
	 * 
	 * @param socket
	 *            The socket to read from
	 * @throws IOException
	 *             Connection could not be established
	 */
	public static Request read(Socket socket) throws Exception {
		GZIPInputStream in = new GZIPInputStream(socket.getInputStream());
		int c;
		StringBuffer res = new StringBuffer();
		while ((c = in.read()) >= 0)
			res.append((char) c);
		return parseInput(res.toString());
	}

	/**
	 * Writes the given text gzip-compressed to the given socket.
	 * 
	 * @param socket
	 *            The socket to write to
	 * @param text
	 *            The text to write to the socket
	 * @throws IOException
	 *             Connection could not be established
	 */
	private static void write(Socket socket, String text) throws IOException {
		GZIPOutputStream out = new GZIPOutputStream(socket.getOutputStream());
		out.write(text.getBytes(), 0, text.length());
		out.write('\n');
		out.finish();
		out.flush();
	}

	/**
	 * Parses the incoming plain text and creates a {@link Request}-object with
	 * {@link Meta}-information and a {@link JSONObject}.
	 * 
	 * @param input
	 *            The incoming JSON as plain text
	 * @return A {@link Request}-object
	 * @throws Exception
	 *             Error in the JSON-structure
	 */
	private static Request parseInput(String input) throws Exception {
		if (input == null || input.replace("\n", "").equals(""))
			throw new RuntimeException(
					"empty_input: The input may not be empty");

		JSONObject jsonObject = JSONObject.fromObject(input);
		Meta meta = null;
		JSONObject data = null;

		// parse the information stored in the meta-part
		try {
			meta = new Meta(jsonObject.getJSONObject("meta"));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("invalid_metadata: " + e.getMessage());
		}

		// parse the information stored in the data-part
		try {
			data = jsonObject.getJSONObject("data");
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("invalid_data: " + e.getMessage());
		}

		return new Request(meta, data);
	}

	/**
	 * Generates a JSON-String of the informations in the <i>response</i> and
	 * sends this to the client using the <code>SocketCommunicator</code>
	 * 
	 * @param response
	 *            Object, which is send to the client
	 */
	public static void writeOutput(Socket socket, Object response) {
		try {
			// sending a list as response
			if (response instanceof List) {
				JSONArray text = JSONArray.fromObject(response);
				SocketCommunicator.write(socket, text.toString());

				// sending a map
			} else if (response instanceof Map) {
				SocketCommunicator.write(socket, JSONSerializer
						.toJSON(response).toString());

				// sending an object
			} else {
				JSONObject text = JSONObject.fromObject(response);
				SocketCommunicator.write(socket, text.toString());
			}
		} catch (Exception e) {
			Log.e("Server", e.getClass() + "@writeOutput: " + e.getMessage());
		}
	}

	/**
	 * Queries the given the <code>serviceUrl</code>, reads the response and
	 * delivers it to the calling method.
	 * 
	 * @param serviceUrl
	 *            The {@link URL} to query
	 * @param serviceCharset
	 *            The charset used for the communication
	 * @return The incoming data
	 * @throws Exception
	 *             Connection problems
	 */
	public static String getContent(String serviceUrl, String serviceCharset)
			throws Exception {
		URL service = new URL(serviceUrl);
		URLConnection serviceConnection = service.openConnection();
		serviceConnection.setDoOutput(true);
		// write request to the connection
		OutputStreamWriter request = new OutputStreamWriter(
				serviceConnection.getOutputStream());
		request.flush();
		// read returned output
		BufferedReader in = new BufferedReader(new InputStreamReader(
				serviceConnection.getInputStream(), serviceCharset));
		String inputLine;
		String returnedContent = "";
		while ((inputLine = in.readLine()) != null) {
			returnedContent += inputLine;
		}
		in.close();
		request.close();
		return returnedContent;
	}
}
