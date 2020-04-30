package com.github.francobenner.messagingfinalproject.net;

import android.util.Log;

import com.github.francobenner.messagingfinalproject.net.listeners.MessageListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler extends Thread {

	private static final String TAG = "ConnectionHandler";

	private Socket connection = null;
	private ArrayList<MessageListener> listeners = new ArrayList<>();
	private BufferedReader in;
	private BufferedWriter out;

	private boolean started = false;

	ConnectionHandler() {
	}

	public void setConnection(Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		if (connection == null)
			return;

		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

			while (!connection.isClosed()) {
				String firstLine = in.readLine();

				switch (firstLine.toLowerCase()) {
					case "message": {
						Log.d(TAG, "Received new message.");
						StringBuilder builder = new StringBuilder();

						while (in.ready()) {
							builder.append(in.readLine());
							builder.append("\n");
						}

						String msg = builder.toString();
						for (MessageListener listener : listeners) {
							listener.onMessage(msg);
						}
						break;
					}
					case "disconnect": {
						Log.d(TAG, "Client disconnected.");
						in.close();
						out.close();
						connection.close();
						break;
					}
					default: {
						Log.e(TAG, "Invalid message header - " + firstLine);
						break;
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Error occurred in client connection: ", e);
		}
	}

	public void startIfNotRunning() {
		if (started || connection == null)
			return;

		started = true;
		start();
	}

	public void addMessageListener(MessageListener listener) {
		listeners.add(listener);
	}

	public BufferedReader getInStream() {
		return in;
	}

	public BufferedWriter getOutStream() {
		return out;
	}

	boolean isClosed() {
		return connection.isClosed();
	}
}
