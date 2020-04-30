package com.github.francobenner.messagingfinalproject.net;

import android.util.Log;

import com.github.francobenner.messagingfinalproject.net.listeners.ClientConnectionListener;
import com.github.francobenner.messagingfinalproject.net.listeners.MessageSendingCompletionListener;
import com.github.francobenner.messagingfinalproject.net.tasks.ClientConnectTask;
import com.github.francobenner.messagingfinalproject.net.tasks.SendMessageTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Client {

	private static final String TAG = "Client";
	private static final ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();

	private String ip;
	//private BufferedReader in = null;
	private BufferedWriter out = null;
	private boolean initialConnectionComplete = false;
	private ArrayList<ClientConnectionListener> listeners = new ArrayList<>();

	private Client(String ip) {
		this.ip = ip;
	}

	public static Client getClientFor(String ip) {
		if (clients.containsKey(ip))
			return clients.get(ip);

		Client client = new Client(ip);
		clients.put(ip, client);
		return client;
	}

	public void connect() {
		try {
			ConnectionHandler handler = Server.getHandlerByIP(ip);
			if (handler != null && handler.getOutStream() != null) {
				Log.d(TAG, "Handler is open, using existing connection.");
				out = handler.getOutStream();
				initialConnectionComplete = true;

				for (ClientConnectionListener listener : listeners) {
					listener.onClientConnection(ip, true);
				}
				Log.d(TAG, "Notified listeners.");

				listeners.clear();
				return;
			}

			Socket socket = new Socket();

			ClientConnectTask connectTask = new ClientConnectTask(ip);
			connectTask.addClientConnectionListener((ip, success) -> {
				Log.d(TAG, "Received connection event for " + ip);

				if (!this.ip.equals(ip))
					return;

				try {
					//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				} catch (IOException e) { // this should never happen
					Log.wtf(TAG, "Failed to get output stream: ", e);
				}
				initialConnectionComplete = true;
				Log.d(TAG, "Connection complete, success? " + success);

				for (ClientConnectionListener listener : listeners) {
					listener.onClientConnection(ip, success);
				}
				Log.d(TAG, "Notified listeners.");

				listeners.clear();
			});

			connectTask.execute(socket);
			Log.d(TAG, "Client connecting to " + ip);

		} catch (Exception e) {
			Log.e(TAG, "Failed to connect to other end: ", e);
		}
	}

	public void sendMessage(String message, MessageSendingCompletionListener listener) {
		if (!initialConnectionComplete)
			return;

		SendMessageTask sendMessageTask = new SendMessageTask(out);
		sendMessageTask.addListener(listener);
		sendMessageTask.execute(message);
	}

	public void disconnect() {
		if (!initialConnectionComplete)
			return;

		try {
			out.write("disconnect");
			out.newLine();
			out.close();
		} catch (Exception e) {
			Log.e(TAG, "Error occurred while disconnecting: ", e);
		}
	}

	public void addClientConnectionListener(ClientConnectionListener listener) {
		listeners.add(listener);
	}

}
