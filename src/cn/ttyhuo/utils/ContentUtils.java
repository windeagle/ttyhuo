package cn.ttyhuo.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import cn.ttyhuo.model.ContactModel;

import java.util.ArrayList;
import java.util.List;

public class ContentUtils {
    public static List<ContactModel> getContactMap(Context context) {
        List<ContactModel> list = new ArrayList<ContactModel>();

        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.LOOKUP_KEY};

        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] proj2 = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.Data.LOOKUP_KEY + "=?";

        List<TempContact> tempContactList = new ArrayList<TempContact>();
        Cursor cursorContacts = context.getContentResolver().query(contactsUri,
                projection, null, null, null);
        try {
            if (cursorContacts.getCount() > 0) {
                while (cursorContacts.moveToNext()) {
                    if (cursorContacts.getInt(1) > 0) {
                        String name = cursorContacts.getString(0);
                        String lookUp = cursorContacts.getString(2);
                        TempContact tempContact = new TempContact();
                        tempContact.name = name;
                        String[] selectionArgs = {lookUp};
                        tempContact.lookup = selectionArgs;
                        tempContactList.add(tempContact);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (cursorContacts != null)
                cursorContacts.close();
        }

        if (tempContactList.isEmpty()) {
            return list;
        }

        for (TempContact tempContact : tempContactList) {
            Cursor cursorPhone = null;
            try {
                cursorPhone = context.getContentResolver().query(
                        phoneUri, proj2, selection, tempContact.lookup, null);
                while (cursorPhone.moveToNext()) {
                    ContactModel model = new ContactModel();
                    model.setName(tempContact.name);
                    model.setPhone(cursorPhone.getString(0));
                    list.add(model);
                }
            } finally {
                cursorPhone.close();
            }
        }

        return list;
    }

    private static class TempContact {
        String name;
        String[] lookup;
    }
}
