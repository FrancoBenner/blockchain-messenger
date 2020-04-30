package com.github.francobenner.messagingfinalproject.ui.contacts;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.francobenner.messagingfinalproject.R;
import com.github.francobenner.messagingfinalproject.struct.Contact;
import com.github.francobenner.messagingfinalproject.ui.messages.MessagesFragment;
import com.github.francobenner.messagingfinalproject.utils.ContactsManager;
import com.github.francobenner.messagingfinalproject.view.ContactEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    private LinearLayout contactsLayout;
    private FloatingActionButton addContactFab;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadContacts() {
        ArrayList<Contact> contacts = ContactsManager.loadContacts(Objects.requireNonNull(getContext()));
        contactsLayout.removeAllViews();

        for (Contact contact : contacts) {
            ContactEntry entry = new ContactEntry(getContext());
            entry.setContactName(contact.getName());
            entry.setContactIp(contact.getIp());

            entry.setOnClickListener(view -> {
                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

                MessagesFragment fragment = (MessagesFragment) fragmentManager.findFragmentByTag(contact.getIp());
                if (fragment == null)
                    fragment = MessagesFragment.newInstance(contact);

                fragmentManager.beginTransaction()
                        .replace(R.id.mainContactLayout, fragment, contact.getIp())
                        .addToBackStack(null)
                        .commit();
            });

            entry.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                TextView alert = new TextView(getContext());
                alert.setText(getString(R.string.delete_contact_prompt, ((ContactEntry) v).getContactName()));

                builder.setView(alert);

                builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
                builder.setPositiveButton(R.string.delete_contact, (dialog, which) -> {
                    ContactsManager.deleteContactByName(((ContactEntry) v).getContactName());
                    contactsLayout.removeView(v);
                });

                builder.create().show();

                return true;
            });

            contactsLayout.addView(entry);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactsLayout = view.findViewById(R.id.contactsLayout);
        addContactFab = view.findViewById(R.id.addContactFab);

        addContactFab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText name = new EditText(getContext());
            name.setHint(R.string.new_contact_name);
            layout.addView(name);

            EditText ip = new EditText(getContext());
            ip.setHint(R.string.new_contact_ip);
            layout.addView(ip);

            builder.setView(layout);
            builder.setPositiveButton(R.string.new_contact_add_contact, (dialog, which) -> {
                Contact contact = new Contact(name.getText().toString(), ip.getText().toString());
                ContactsManager.addContact(contact);
                loadContacts();
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        loadContacts();

        return view;
    }
}