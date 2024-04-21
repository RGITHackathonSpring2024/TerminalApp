package ru.nosqd.rgit.terminalapp.ndef

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.util.Log
import ru.nosqd.rgit.terminalapp.R
import java.nio.charset.StandardCharsets

object NDefHelper {
    public fun getNdefMessagesFromIntent(intent: Intent): Array<NdefMessage?>? {
        var msgs: Array<NdefMessage?>? = null
        val action = intent.action
        if (action == NfcAdapter.ACTION_TAG_DISCOVERED || action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMsgs != null) {
                msgs = arrayOfNulls(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }
            } else {
                // Unknown tag type
                val empty = byteArrayOf()
                val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty)
                val msg = NdefMessage(arrayOf(record))
                msgs = arrayOf(msg)
            }
        } else {
            Log.e(R.layout.activity_reader.javaClass.simpleName, "Unknown intent.")
            return null
        }
        return msgs
    }

    fun getStringPayloadFromMessages(messages: Array<NdefMessage?>): String {
        val record = messages!![0]!!.records[0]
        val payload = record.payload
        return String(payload)
    }

    fun writeString(ndf: Ndef, data: String) {
        val record = NdefRecord.createMime("rgit/rndpass", data.toByteArray(StandardCharsets.UTF_8))
        val newMsg = NdefMessage(record)
        ndf.writeNdefMessage(newMsg)

    }

}