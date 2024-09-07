package com.example.apichecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apichecker.model.Address
import com.example.apichecker.model.RetrofitClient
import com.example.apichecker.ui.theme.APICHECKERTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            APICHECKERTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SearchCEPScreen( modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }


    @Composable
    fun SearchCEPScreen(
        modifier: Modifier = Modifier,
    ) {
        var cep by remember { mutableStateOf("") }
        var addressResponse by remember { mutableStateOf<Address?>(null) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        val modifierTextField = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = modifierTextField
                .background(MaterialTheme.colorScheme.background)
                .fillMaxHeight()
        ) {
            Text(
                text = "Escreva o CEP",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            TextField(
                value = cep,
                onValueChange = { cep = it },
                placeholder = { Text("xxxxx-yyy") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Green,
                    unfocusedIndicatorColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 10.dp, 30.dp, 10.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (cep.isEmpty() || cep.length != 8) {
                        errorMessage = "Erro no preenchimento do CEP"
                    } else {
                        scope.launch {
                            val address = getAdress(cep)  // Chama a função e obtém o endereço
                            if (address != null) {
                                addressResponse = address  // Atualiza o estado com o endereço
                                errorMessage = null         // Limpa a mensagem de erro
                            } else {
                                errorMessage = "CEP não encontrado"
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp, 10.dp, 50.dp, 10.dp)
                    .height(50.dp)
            ) {
                Text("Procurar CEP")
            }
            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            addressResponse?.let {
                AddressCard(it)  // Passa o endereço para o card
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }


    suspend fun getAdress(cep: String): Address {
        return withContext(Dispatchers.IO){
            RetrofitClient.enderecoService.findByCep(cep)
        }

    }

    @Composable
    fun AddressCard(
        adress:Address
    ){
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AddressField("CEP", adress.cep)
                AddressField("Logradouro", adress.logradouro)
                AddressField("Complemento", adress.complemento)
                AddressField("Unidade", adress.unidade)
                AddressField("Bairro", adress.bairro)
                AddressField("Localidade", adress.localidade)
                AddressField("UF", adress.uf)
                AddressField("Estado", adress.estado)
                AddressField("Região", adress.regiao)
                AddressField("IBGE", adress.ibge)
                AddressField("GIA", adress.gia)
                AddressField("DDD", adress.ddd)
                AddressField("SIAFI", adress.siafi)
            }
        }
    }
    @Composable
    fun AddressField(label: String, value: String) {
        if (value.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "$label: $value")

            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultScreen() {
        val modifierScreen: Modifier = Modifier
        SearchCEPScreen(modifier = modifierScreen)
    }

    @Preview(showBackground = true)
    @Composable
    fun CardCEP() {
        val modifierScreen: Modifier = Modifier
        AddressCard(Address("58070","Nego"))
    }
}
