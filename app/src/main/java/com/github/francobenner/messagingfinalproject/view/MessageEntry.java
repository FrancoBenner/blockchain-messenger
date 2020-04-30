package com.github.francobenner.messagingfinalproject.view;

import android.content.Context;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.francobenner.messagingfinalproject.R;

public class MessageEntry extends ConstraintLayout {

	private TextView messageContentView;

	public MessageEntry(Context context) {
		super(context);

		inflate(context, R.layout.message_entry, this);
		messageContentView = findViewById(R.id.messageContentView);
	}

	public void setMessage(String message) {
		messageContentView.setText(message);
	}

	public String getMessage() {
		return messageContentView.getText().toString();
	}

	public void setSuccess(boolean success) {
		messageContentView.setTextColor(success ? getResources().getColor(R.color.message_success, null) : getResources().getColor(R.color.message_success, null));
	}

}
