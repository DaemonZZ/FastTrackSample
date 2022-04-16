package com.daemonz.fasttracksample.workmanager.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.daemonz.fasttracksample.R
import com.daemonz.fasttracksample.databinding.ActivityWorkManagerSampleBinding
import com.daemonz.fasttracksample.workmanager.work.SampleWorker

@RequiresApi(Build.VERSION_CODES.N)
class WorkManagerSampleActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ThangDN6"
        private const val KEY_LIST_CONTACT = "list_contact"

    }

    private lateinit var binding: ActivityWorkManagerSampleBinding
    private val contactList = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_manager_sample)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.SEND_SMS
                ),
                1
            )
        } else {
            getContactList()
            setUpWork()
        }


        val adapter = PhoneListAdapter(contactList.toList())
        binding.recycler.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@WorkManagerSampleActivity)
        }


    }

    @SuppressLint("Range")
    private fun getContactList() {
        val cr = contentResolver
        val cur: Cursor? = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name: String = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur?.moveToNext() == true) {
                        val phoneNo: String = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        contactList.add(phoneNo.filter { it.isDigit() })
                        Log.i(TAG, "Name: $name")
                        Log.i(TAG, "Phone Number: $phoneNo")
                    }
                    pCur?.close()
                }
            }
        }
        cur?.close()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContactList();
                setUpWork()
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Denied",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setUpWork(){
        val constraint = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val data = Data.Builder().apply {
            putStringArray(KEY_LIST_CONTACT,contactList.toTypedArray())
        }.build()
        val workRequest = OneTimeWorkRequest.Builder(SampleWorker::class.java)
            .setConstraints(constraint)
            .setInputData(data)
            .build()

        val workManager = WorkManager.getInstance(this)

        workManager.enqueue(workRequest)

        workManager.getWorkInfoByIdLiveData(workRequest.id).observe(this) {
            Log.d(TAG, "onCreate: livedata: $it")
            if (it != null && it.state == WorkInfo.State.SUCCEEDED) {
                Log.d(TAG, "onCreate: Work completed...${Calendar.getInstance().time}")
            }
        }
    }


}