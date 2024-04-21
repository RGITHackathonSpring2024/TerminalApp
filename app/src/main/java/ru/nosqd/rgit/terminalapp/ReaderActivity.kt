package ru.nosqd.rgit.terminalapp

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.nosqd.rgit.terminalapp.ndef.NDefHelper


class ReaderActivity : AppCompatActivity() {
    private lateinit var nfc: NfcAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reader)
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

            if (tag.techList.any { it.equals("android.nfc.tech.Ndef") }) {
                val msgs = NDefHelper.getNdefMessagesFromIntent(getIntent())!!
                val payload = NDefHelper.getStringPayloadFromMessages(msgs)

                val i = Intent("ru.nosqd.rgit.terminalapp.WithNFCDataIntent")
                i.putExtra("nfcData", payload)
                i.putExtra("nfcMessages", msgs)
                i.putExtra("nfcTag", tag)
                startActivity(i)
            }
        }
    }
}