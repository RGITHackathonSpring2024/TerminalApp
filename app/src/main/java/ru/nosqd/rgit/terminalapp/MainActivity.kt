package ru.nosqd.rgit.terminalapp

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.nosqd.rgit.terminalapp.api.API_SECRET
import ru.nosqd.rgit.terminalapp.api.API_URL
import ru.nosqd.rgit.terminalapp.api.GetCardReponse
import ru.nosqd.rgit.terminalapp.api.ProcessCardRequest
import ru.nosqd.rgit.terminalapp.api.ProcessCardResponse
import ru.nosqd.rgit.terminalapp.api.client
import ru.nosqd.rgit.terminalapp.ui.theme.TerminalAppTheme
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private var listenModeMut = mutableStateOf(ListenMode.INFO)
    private var withdrawAmountMut = mutableIntStateOf(100)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TerminalAppTheme {
                var listenMode by remember { listenModeMut }
                var withdrawAmount by remember { withdrawAmountMut }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.padding(16.dp, 16.dp)) {
                        Row {
                            OutlinedButton(onClick = { listenMode = ListenMode.INFO }, Modifier.fillMaxWidth(0.5f)) {
                                Text("Режим информации")
                            }
                            Spacer(Modifier.size(8.dp))
                            OutlinedButton(onClick = { listenMode = ListenMode.WITHDRAW }, Modifier.fillMaxWidth()) {
                                Text("Режим списания")
                            }
                        }
                        Spacer(Modifier.size(32.dp))
                        Row {
                            Text("Режим работы: ${listenMode.name}")
                            if (listenMode == ListenMode.WITHDRAW)
                                Text("Сумма вывода: $withdrawAmount")
                        }
                    }

                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()
        if (intent.action == "ru.nosqd.rgit.terminalapp.WithNFCDataIntent") {
            Log.i("activity", "launched with nfc data.")

//            val msgs = intent.getParcelableArrayExtra("nfcMessages", Array<NdefMessage?>::class.java)!!
            val tag = intent.getParcelableExtra("nfcTag", Tag::class.java)!!
            val data = intent.getStringExtra("nfcData")!!
            val ndf = Ndef.get(tag)

            if (data.startsWith("pass:")) {
                val id = data.substring(5)

                GlobalScope.launch {
                    Log.i("a",API_URL + "/internalapis/update-card")
                    Log.i("a",API_SECRET)
                    val response = client.post(API_URL + "/internalapis/update-card") {
                        contentType(ContentType.Application.Json)
                        setBody(ProcessCardRequest(id))
                        header(HttpHeaders.Authorization, API_SECRET)
                    }
                    if (response.status != HttpStatusCode.Forbidden) {
                        val data: ProcessCardResponse = response.body()
                        ndf.connect()
                        val record = NdefRecord.createMime("rgit/rndpass", "pass:${data.newToken}".toByteArray(StandardCharsets.UTF_8))
                        val message = NdefMessage(record)
                        ndf.writeNdefMessage(message)
                        ndf.close()

                        val response2 = client.get(API_URL + "/internalapis/cards/${data.cardId}") {
                            contentType(ContentType.Application.Json)
                            header(HttpHeaders.Authorization, API_SECRET)

                        }
                        //Log.e("kehelo", response.status.value)
                        Log.e("kehelo", response.bodyAsText())
                        val cardData: GetCardReponse = response2.body()
                        Log.i("khelo", cardData.card.id)
                        Log.i("khelo", cardData.card.permanentAccountNumber)
                        Log.i("khelo", cardData.card.lastToken)
                    }
                    else {
                       // Toast.makeText(this@MainActivity, "ошибка чек logcat", Toast.LENGTH_LONG).show()
                        Log.e("error", response.bodyAsText())
                    }
                }
            }
            else {
                Toast.makeText(this, "Данная метка не поддерживается", Toast.LENGTH_LONG).show()
                System.exit(0)
            }

        }
    }
}