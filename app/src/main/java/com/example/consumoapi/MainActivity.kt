package com.example.consumoapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.consumoapi.model.Endereco
import com.example.consumoapi.service.RetrofitFactory
import com.example.consumoapi.ui.theme.ConsumoAPITheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConsumoAPITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CepScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CepScreen(modifier: Modifier = Modifier) {
    var cepState by remember { mutableStateOf("") }
    var ufState by remember { mutableStateOf("") }
    var cidadeState by remember { mutableStateOf("") }
    var ruaState by remember { mutableStateOf("") }

    var listaEnderecoState by remember {
        mutableStateOf(listOf<Endereco>())
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(color = Color.Blue)
                .padding(top = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CONSULTA CEP",
                fontSize = 24.sp,
                color = Color.White
            )
        }

        Card(
            modifier = Modifier.padding(horizontal = 16.dp)
                .fillMaxWidth()
                .offset(y = (-30).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Encontre o seu endereço",
                    fontSize = 20.sp
                )

                OutlinedTextField(
                    value = cepState,
                    onValueChange = { cepState = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Qual CEP está buscando?") },
                    trailingIcon = {
                        IconButton( onClick = { /* TODO */ } ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = ""
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row() {
                    OutlinedTextField(
                        value = ufState,
                        onValueChange = { ufState = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        label = { Text( text = "UF?" ) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Characters
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cidadeState,
                        onValueChange = { cidadeState = it },
                        modifier = Modifier.weight(2f),
                        label = { Text( text = "Qual a cidade?" ) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words
                        )
                    )
                }

                Row( verticalAlignment = Alignment.CenterVertically ) {
                    OutlinedTextField(
                        value = ruaState,
                        onValueChange = { ruaState = it },
                        modifier = Modifier.weight(2f),
                        label = { Text(text = "Qual o nome da rua?") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words
                        )
                    )

                    IconButton( onClick = {
                        var call = RetrofitFactory().getEnderecoService()
                            .getEnderecosByUfCidadeRua(
                                uf = ufState,
                                cidade = cidadeState,
                                rua = ruaState
                            )
                        call.enqueue(  object: Callback<List<Endereco>> {

                            override fun onResponse(
                                call: Call<List<Endereco>?>?,
                                response: Response<List<Endereco>?>?
                            ) {
                                TODO("Not yet implemented")
                            }

                            override fun onFailure(
                                call: Call<List<Endereco>?>?,
                                t: Throwable?
                            ) {
                                TODO("Not yet implemented")
                            }
                        })
                    } ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = ""
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(120) {
                CardEndereco()
            }
        }
    }
}

private fun Call<List<Endereco>>.enqueue(callback: Any) {}

@Composable
fun CardEndereco() {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "CEP:")
            Text(text = "Rua:")
            Text(text = "Cidade:")
            Text(text = "Bairro:")
            Text(text = "UF:")
        }
    }
}