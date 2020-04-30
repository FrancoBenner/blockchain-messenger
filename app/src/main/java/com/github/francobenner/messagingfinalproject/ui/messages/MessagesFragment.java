package com.github.francobenner.messagingfinalproject.ui.messages;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.francobenner.messagingfinalproject.R;
import com.github.francobenner.messagingfinalproject.net.Client;
import com.github.francobenner.messagingfinalproject.net.ConnectionHandler;
import com.github.francobenner.messagingfinalproject.net.Server;
import com.github.francobenner.messagingfinalproject.struct.Contact;
import com.github.francobenner.messagingfinalproject.ui.contacts.ContactsFragment;
import com.github.francobenner.messagingfinalproject.view.MessageEntry;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {

	private static final String TAG = "MessagesFragment";

	private Contact contact;
	private Client client;
	private ConnectionHandler handler;

	private LinearLayout messageListLayout;
	private ScrollView messageScrollView;

	public MessagesFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment MessagesFragment.
	 */
	public static MessagesFragment newInstance(Contact contact) {
		MessagesFragment fragment = new MessagesFragment();
		fragment.setContact(contact);
		return fragment;
	}

	private void setContact(Contact contact) {
		this.contact = contact;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container != null)
			container.removeAllViews();

		View view = inflater.inflate(R.layout.fragment_messages, container, false);

		TextView messageFragmentTitle = view.findViewById(R.id.messageFragmentTitle);
		Button sendMessageButton = view.findViewById(R.id.sendMessageButton);
		EditText messageTextBox = view.findViewById(R.id.messageTextBox);
		messageListLayout = view.findViewById(R.id.messageListLayout);
		messageScrollView = view.findViewById(R.id.messageScrollView);

		sendMessageButton.setOnClickListener(v -> {
			String message = messageTextBox.getText().toString();
			messageTextBox.setText("");

			MessageEntry messageEntry = new MessageEntry(getContext());
			messageEntry.setMessage(message);

			messageListLayout.addView(messageEntry);
			messageScrollView.smoothScrollTo(0, messageScrollView.getHeight());

			client.sendMessage(message, messageEntry::setSuccess);
		});

		handler = Server.getHandlerByIP(contact.getIp());

		initHandler();
		//messageFragmentTitle.setText(getString(R.string.message_title, contact.getIp()));

		client = Client.getClientFor(contact.getIp());

		client.addClientConnectionListener((ip, success) -> {
			if (!ip.equals(contact.getIp()))
				return;

			if (success) {
				sendMessageButton.setEnabled(true);
				messageFragmentTitle.setText(getString(R.string.message_title, contact.getIp()));
			} else {
				messageFragmentTitle.setText(R.string.error_client_connection_failed);
			}
		});

		messageFragmentTitle.setText(R.string.attempting_connection);
		client.connect();

		OnBackPressedCallback callback = new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
						.replace(R.id.mainContactLayout, new ContactsFragment(), null)
						.addToBackStack(null)
						.commit();
			}
		};

		Objects.requireNonNull(getActivity()).getOnBackPressedDispatcher().addCallback(this, callback);

		return view;
	}

	private void initHandler() {
		if (handler == null)
			return;

		handler.startIfNotRunning();

		handler.addMessageListener(message -> {
			MessageEntry messageEntry = new MessageEntry(getContext());
			messageEntry.setMessage(message);
			messageEntry.setSuccess(true);

			messageListLayout.addView(messageEntry);

				/*View lastChild = messageScrollView.getChildAt(messageScrollView.getChildCount() - 1);
				int bottom = lastChild.getBottom() + messageScrollView.getPaddingBottom();
				int scrollY = messageScrollView.getScrollY();
				int viewHeight = messageScrollView.getHeight();
				int delta = bottom - (scrollY + viewHeight);*/

			messageScrollView.smoothScrollTo(0, messageScrollView.getHeight());
		});
	}
}