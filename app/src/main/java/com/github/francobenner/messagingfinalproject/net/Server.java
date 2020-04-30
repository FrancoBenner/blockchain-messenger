package com.github.francobenner.messagingfinalproject.net;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {

	public static final int PORT = 31337;

	private static final String TAG = "Server";
	private static final int MAX_CONNECTIONS = 16;

	private static ConcurrentHashMap<String, ConnectionHandler> handlers = new ConcurrentHashMap<>();
	private PriorityQueue<Socket> socketQueue = new PriorityQueue<>();
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private ServerSocket serverSocket;

	public Server() {
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(PORT);

			executor.scheduleAtFixedRate(() -> {
				for (Map.Entry<String, ConnectionHandler> handlerEntry : handlers.entrySet()) {
					if (handlerEntry.getValue().isClosed())
						handlers.remove(handlerEntry.getKey());
				}

				if (handlers.size() >= MAX_CONNECTIONS)
					return;

				if (socketQueue.isEmpty())
					return;

				Socket socket = socketQueue.poll();

				assert socket != null;

				String ip = socket.getInetAddress().getHostAddress();

				ConnectionHandler handler = getHandlerByIP(ip);
				handler.setConnection(socket);
				handler.startIfNotRunning();

				Log.d(TAG, "Received new connection from IP " + ip);

				handlers.put(ip, handler);
			}, 1000, 100, TimeUnit.MILLISECONDS);

			while (!serverSocket.isClosed()) {
				Socket connection = serverSocket.accept();
				socketQueue.add(connection);
			}

		} catch (Exception e) {
			Log.e(TAG, "Error occurred while running server.", e);
		}
	}

	public static ConnectionHandler getHandlerByIP(String ip) {
		if (!handlers.containsKey(ip))
			handlers.put(ip, new ConnectionHandler());

		return handlers.get(ip);
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {
			serverSocket.close();
		} catch (IOException e) {
			Log.e(TAG, "Error occurred while stopping server.", e);
		}
	}
}
