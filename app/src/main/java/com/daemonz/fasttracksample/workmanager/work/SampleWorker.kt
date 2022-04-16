package com.daemonz.fasttracksample.workmanager.work

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters

@RequiresApi(Build.VERSION_CODES.M)
class SampleWorker(private val context:Context, workerParams: WorkerParameters):Worker(context,workerParams) {
    companion object{
        private const val TAG = "SampleWorker"
        private const val KEY_LIST_CONTACT = "list_contact"
    }
    override fun doWork(): Result {
        Log.d(TAG, "doWork: Hello Worker")
        sendSMS()
        return Result.success()
    }


    private fun sendSMS(){
        try {
            val contactList = inputData.getStringArray(KEY_LIST_CONTACT)
            val smsManager = context.getSystemService(SmsManager::class.java)
            contactList?.forEach {
                smsManager.sendTextMessage(it,null,"Hello there I'm ThangDN6",null,null)
                Log.d(TAG, "sendSMS: sms is sent to $it")
            }

        }catch (e: Exception){
            Log.d(TAG, "sendSMS: Fail")
        }

    }
}