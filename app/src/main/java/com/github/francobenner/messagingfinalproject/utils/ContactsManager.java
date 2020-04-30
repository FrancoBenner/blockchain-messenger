package com.github.francobenner.messagingfinalproject.utils;

import android.content.Context;
import android.util.Log;

import com.github.francobenner.messagingfinalproject.struct.Contact;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class ContactsManager {

    private static final String TAG = "ContactsManager";
    private static final ReentrantLock lock = new ReentrantLock();
    private static File contactsFile = null;

    /**
     * Load contacts from the contacts file
     * @param context Activity context
     * @return An ArrayList of loaded contacts
     */
    public static ArrayList<Contact> loadContacts(Context context) {
        contactsFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + "contacts.json");

        try {
            if (!contactsFile.exists()) {
                Log.d(TAG, "Contacts file does not exist. Creating one now.");
                boolean createSuccess = contactsFile.createNewFile();
                if (!createSuccess) {
                    Log.wtf(TAG, "Could not create contacts file.");
                }

                saveContacts(new ArrayList<>());

                //no contacts since we just created a new file
                return new ArrayList<>();
            }

            return readContacts();
        } catch (Exception e) {
            Log.e(TAG, "Failed to load contacts.", e);
        }

        return new ArrayList<>();
    }

    private static ArrayList<Contact> readContacts() {
        try {
            String rawContactString = new String(Files.readAllBytes(contactsFile.toPath()));
            if (rawContactString.isEmpty())
                return new ArrayList<>();

            JSONArray contactsJsonArr = new JSONArray(rawContactString);

            ArrayList<Contact> contacts = new ArrayList<>();

            for (int i = 0; i < contactsJsonArr.length(); i++) {
                JSONObject contactJson = contactsJsonArr.getJSONObject(i);

                String name = contactJson.getString("name");
                String ip = contactJson.getString("ip");

                contacts.add(new Contact(name, ip));
            }

            return contacts;
        } catch (Exception e) {
            Log.e(TAG, "Failed to read contacts from file.", e);
        }

        return new ArrayList<>();
    }

    private static void saveContacts(ArrayList<Contact> contacts) {
        try {
            JSONArray contactsJsonArr = new JSONArray();

            for (Contact contact : contacts) {
                JSONObject contactJson = new JSONObject();

                contactJson.put("name", contact.getName());
                contactJson.put("ip", contact.getIp());

                contactsJsonArr.put(contactJson);
            }

            FileWriter writer = new FileWriter(contactsFile);
            writer.write(contactsJsonArr.toString(4));
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to save contacts.", e);
        }
    }

    public static void deleteContactByName(String name) {
        if (contactsFile == null) {
            Log.wtf(TAG, "Contacts file is null!");
        }

        lock.lock();

        ArrayList<Contact> contacts = readContacts();
        ArrayList<Contact> newContacts = new ArrayList<>();
        contacts.forEach(contact -> {
            if (contact.getName().equals(name))
                return;

            newContacts.add(contact);
        });

        saveContacts(newContacts);

        lock.unlock();
    }

    public static void addContact(Contact contact) {
        if (contactsFile == null) {
            Log.wtf(TAG, "Contacts file is null!");
        }

        lock.lock();

        ArrayList<Contact> contacts = readContacts();
        contacts.add(contact);
        saveContacts(contacts);

        lock.unlock();
    }

}
