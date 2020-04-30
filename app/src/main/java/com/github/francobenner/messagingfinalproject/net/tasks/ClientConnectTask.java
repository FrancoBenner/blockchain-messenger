package com.github.francobenner.messagingfinalproject.net.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.github.francobenner.messagingfinalproject.net.listeners.ClientConnectionListener;
import com.github.francobenner.messagingfinalproject.net.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnectTask extends AsyncTask<Socket, Void, Boolean> {

	private static final String TAG = "ClientConnectTask";

	private String ip;
	private ArrayList<ClientConnectionListener> listeners = new ArrayList<>();

	public ClientConnectTask(String ip) {
		this.ip = ip;
	}

	public void addClientConnectionListener(ClientConnectionListener listener) {
		listeners.add(listener);
	}

	@Override
	protected Boolean doInBackground(Socket... sockets) {
		Log.d(TAG, "Connecting " + sockets.length + " sockets to IP " + ip);

		if (sockets.length < 1)
			return false;

		try {
			sockets[0].connect(new InetSocketAddress(ip, Server.PORT), 2000);
			return true;
		} catch (IOException e) {
			Log.e(TAG, "Failed to connect to other end: ", e);
		}

		Log.d(TAG, "doInBackground returned false.");
		return false;
	}

	@Override
	protected void onPostExecute(Boolean connectionSuccess) {
		super.onPostExecute(connectionSuccess);

		Log.d(TAG, "Notifying listeners.");
		for (ClientConnectionListener listener : listeners) {
			listener.onClientConnection(ip, connectionSuccess);
		}

		//listeners are single use
		listeners.clear();
	}
}
