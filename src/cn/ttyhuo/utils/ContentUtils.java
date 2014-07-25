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

        try{
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
            String[] projection = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.Contacts.LOOKUP_KEY };
            Cursor cursorContacts = context.getContentResolver().query(contactsUri,
                    projection, null, null, null);

            if (cursorContacts.getCount() > 0) {
                while (cursorContacts.moveToNext()) {
                    if (cursorContacts.getInt(1) > 0) {
                        String name = cursorContacts.getString(0);
                        String lookUp = cursorContacts.getString(2);

                        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        String[] proj2 = { ContactsContract.CommonDataKinds.Phone.NUMBER };
                        String selection = ContactsContract.Data.LOOKUP_KEY + "=?";
                        String[] selectionArgs = { lookUp };
                        Cursor cursorPhone = context.getContentResolver().query(
                                phoneUri, proj2, selection, selectionArgs, null);
                        while (cursorPhone.moveToNext()) {
                            ContactModel model = new ContactModel();
                            model.setName(name);
                            model.setPhone(cursorPhone.getString(0));
                            list.add(model);
                        }
                    }
                }
            }
        }
        catch (Throwable e){
            e.printStackTrace();
        }

		return list;
	}
}
