package ru.nosqd.rgit.terminalapp

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.nio.charset.StandardCharsets
import java.util.Random


class MainActivity : AppCompatActivity() {
    private lateinit var nfc: NfcAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        nfc = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        handleIntent(intent!!)
    }

    private fun handleIntent(intent: Intent) {
        Log.i("intent", intent.action!!)
        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)!!

            if (tag.techList.any { it -> it.equals("android.nfc.tech.Ndef") }) {
                val msgs = NDefHelper.getNdefMessagesFromIntent(getIntent())!!
                val payload = NDefHelper.getStringPayloadFromMessages(msgs)

                val ndf = Ndef.get(tag)
                ndf.connect()

                val item = Random().nextInt(100)
                NDefHelper.writeString(ndf, item.toString())

                Log.i("NFC Tag/Read", payload)
                Log.i("NFC Tag/Wrote", item.toString())

                ndf.close()
            }



        }
    }



}