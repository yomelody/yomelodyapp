package com.yomelody;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yomelody.Models.PhoneBookContact;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class PhoneBookActivity extends AppCompatActivity {

    private Activity mActivity;
    private ImageView backIv;
    private RecyclerView rvContacts;
    private EditText search;

    private static final int REQUEST_READ_CONTACTS = 444;
    private ProgressDialog pDialog;
    private List<PhoneBookContact> phoneBookList = new ArrayList();
    AllContactsAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_book);
        mActivity=PhoneBookActivity.this;
        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Reading contacts...");
        pDialog.setCancelable(false);
//        pDialog.show();

        backIv=findViewById(R.id.backIv);
        rvContacts=findViewById(R.id.rvContacts);
        search=findViewById(R.id.search);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAllContacts();
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAllContacts();
            }
        }
    }

    private void getAllContacts() {

        if (!mayRequestContacts()) {
            return;
        }

        PhoneBookContact mPhoneBookContact;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    mPhoneBookContact = new PhoneBookContact();
                    mPhoneBookContact.setName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mPhoneBookContact.setNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    phoneBookList.add(mPhoneBookContact);
                }
            }

            contactAdapter = new AllContactsAdapter(phoneBookList);
            rvContacts.setLayoutManager(new LinearLayoutManager(this));
            rvContacts.setAdapter(contactAdapter);
            addTextListener();
        }
    }

    public void addTextListener(){

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final List<PhoneBookContact> filteredList = new ArrayList<>();

                for (int i = 0; i < phoneBookList.size(); i++) {

                    final String text = phoneBookList.get(i).getName().toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(phoneBookList.get(i));
                    }
                }

                rvContacts.setLayoutManager(new LinearLayoutManager(mActivity));
                contactAdapter = new AllContactsAdapter(filteredList);
                rvContacts.setAdapter(contactAdapter);
                contactAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }

    public class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ContactViewHolder>{

        List<PhoneBookContact> phoneList;

        public AllContactsAdapter(List<PhoneBookContact> phoneBookList) {
            phoneList=phoneBookList;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.row_phone_contacts, null);
            ContactViewHolder contactViewHolder = new ContactViewHolder(view);
            return contactViewHolder;
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, final int position) {
            holder.contactName.setText(phoneList.get(position).getName());
            holder.contactNumber.setText(phoneList.get(position).getNumber());

            holder.contactRv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(phoneList.get(position).getNumber())){
                        Uri uri = Uri.parse("smsto:" + phoneList.get(position).getNumber());
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        intent.putExtra("sms_body", "Hey "+
                                phoneList.get(position).getName()+", Join me in #YoMelody. "+
                                "Follow this link to download the app " +
                                "https://play.google.com/store/apps/details?id=com.yomelody");
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(mActivity, "Please add contact number.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return phoneList.size();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder{

            TextView contactName, contactNumber;
            RelativeLayout contactRv;

            public ContactViewHolder(View itemView) {
                super(itemView);
                contactName = itemView.findViewById(R.id.contactName);
                contactNumber =  itemView.findViewById(R.id.contactNumber);
                contactRv =  itemView.findViewById(R.id.contactRv);
            }
        }
    }
}
