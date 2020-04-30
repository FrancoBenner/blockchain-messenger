package com.github.francobenner.messagingfinalproject.view;

import android.content.Context;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.francobenner.messagingfinalproject.R;

public class ContactEntry extends ConstraintLayout {

	private TextView contactNameView;
	private TextView contactIpView;

	public ContactEntry(Context context) {
		super(context);

		inflate(context, R.layout.contact_entry, this);
		contactNameView = findViewById(R.id.messageContentView);
		contactIpView = findViewById(R.id.contactEntryIp);
	}

	public void setContactName(String name) {
		contactNameView.setText(name);
		invalidate();
	}

	public void setContactIp(String ip) {
		contactIpView.setText(ip);
		invalidate();
	}

	public String getContactName() {
		return contactNameView.getText().toString();
	}

	public String getContactIp() {
		return contactIpView.getText().toString();
	}
}
