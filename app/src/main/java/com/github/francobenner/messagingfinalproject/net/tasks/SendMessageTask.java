package com.github.francobenner.messagingfinalproject.net.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.github.francobenner.messagingfinalproject.net.listeners.MessageSendingCompletionListener;

import java.io.BufferedWriter;
import java.util.ArrayList;

public class SendMessageTask extends AsyncTask<String, Void, Boolean> {

	private static final String TAG = "SendMessageTask";

	private final BufferedWriter out;
	private ArrayList<MessageSendingCompletionListener> listeners = new ArrayList<>();

	public SendMessageTask(BufferedWriter out) {
		this.out = out;
	}

	public void addListener(MessageSendingCompletionListener listener) {
		listeners.add(listener);
	}

	@Override
	protected Boolean doInBackground(String... message) {
		if (message.length < 1)
			return false;

		try {
			out.write("message");
			out.newLine();
			out.write(message[0]);
			out.newLine();
			out.flush();

			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error occurred while sending message: ", e);
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean sendSuccess) {
		super.onPostExecute(sendSuccess);

		for (MessageSendingCompletionListener listener : listeners) {
			listener.onMessageSent(sendSuccess);
		}

		//listeners are single use because we only send message once
		listeners.clear();
	}
}
