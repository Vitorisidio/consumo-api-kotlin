
package com.example.consumoapi

//----------------- IMPORTAÇÕES -----------------------------//

import android.os.Bundle
import android.util.Log

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
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.consumoapi.model.Endereco
import com.example.consumoapi.service.RetrofitFactory
import com.example.consumoapi.ui.theme.ConsumoAPITheme

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//----------------- ACTIVITY PRINCIPAL ----------------------//

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ConsumoAPITheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    CepScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


//----------------- TELA DE CONSULTA DE CEP -----------------//

@Composable
fun CepScreen(modifier: Modifier = Modifier) {

    // Estados dos campos
    var cepState by remember { mutableStateOf("") }
    var ufState by remember { mutableStateOf("") }
    var cidadeState by remember { mutableStateOf("") }
    var ruaState by remember { mutableStateOf("") }

    // Lista de endereços retornados pela API
    var listaEnderecoState by remember {
        mutableStateOf<List<Endereco>>(emptyList())
    }

    // Mensagem para apresentar erros ou resultados
    var mensagemState by remember { mutableStateOf("") }

    // Indica se a consulta está sendo realizada
    var carregandoState by remember { mutableStateOf(false) }


    Column(
        modifier = modifier.fillMaxSize()
    ) {

        // Cabeçalho azul
        Column(
            modifier = Modifier
                .fillMaxWidth()
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


        // Cartão com os campos de pesquisa
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
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


                // Pesquisa pelo CEP
                OutlinedTextField(
                    value = cepState,

                    onValueChange = {
                        // Permite somente números e até 8 dígitos
                        cepState = it.filter { caractere ->
                            caractere.isDigit()
                        }.take(8)
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text(text = "Qual CEP está buscando?")
                    },

                    trailingIcon = {

                        IconButton(
                            onClick = {
                                // Verifica se o CEP possui 8 números
                                val cep = cepState.filter { it.isDigit() }

                                if (cep.length != 8) {
                                    mensagemState = "CEP inválido."
                                } else {
                                    carregandoState = true
                                    mensagemState = ""

                                    val call = RetrofitFactory()
                                        .getEnderecoService()
                                        .getEnderecoByCep(cep)

                                    call.enqueue(object : Callback<Endereco> {

                                        override fun onResponse(
                                            call: Call<Endereco>,
                                            response: Response<Endereco>
                                        ) {
                                            carregandoState = false

                                            if (response.isSuccessful) {
                                                val endereco = response.body()

                                                if (endereco != null && endereco.rua.isNotBlank()) {
                                                    ufState = endereco.uf
                                                    cidadeState = endereco.cidade
                                                    ruaState = endereco.rua

                                                    listaEnderecoState = listOf(endereco)
                                                    mensagemState = "1 endereço encontrado."
                                                } else {
                                                    listaEnderecoState = emptyList()
                                                    mensagemState = "Nenhum endereço encontrado."
                                                }
                                            } else {
                                                mensagemState = "Erro na consulta: ${response.code()}"
                                                Log.e("CEP", "Erro na consulta: ${response.code()}")
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<Endereco>,
                                            t: Throwable
                                        ) {
                                            carregandoState = false
                                            mensagemState = "Não foi possível consultar os endereços."

                                            Log.e("CEP", "Falha na consulta", t)
                                        }
                                    })
                                }
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Pesquisar CEP"
                            )
                        }
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),

                    singleLine = true
                )


                // Campos de UF e cidade
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = ufState,

                        onValueChange = {
                            ufState = it
                                .filter { caractere ->
                                    caractere.isLetter()
                                }
                                .uppercase()
                                .take(2)
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),

                        label = {
                            Text(text = "UF?")
                        },

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Characters
                        ),

                        singleLine = true
                    )


                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )


                    OutlinedTextField(
                        value = cidadeState,

                        onValueChange = {
                            cidadeState = it
                        },

                        modifier = Modifier.weight(2f),

                        label = {
                            Text(text = "Qual a cidade?")
                        },

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words
                        ),

                        singleLine = true
                    )
                }


                // Campo da rua e botão de pesquisa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = ruaState,

                        onValueChange = {
                            ruaState = it
                        },

                        modifier = Modifier.weight(1f),

                        label = {
                            Text(text = "Qual o nome da rua?")
                        },

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words
                        ),

                        singleLine = true
                    )


                    IconButton(
                        onClick = {

                            if (
                                ufState.isBlank() ||
                                cidadeState.isBlank() ||
                                ruaState.isBlank()
                            ) {

                                mensagemState =
                                    "Preencha a UF, a cidade e a rua."

                            } else {

                                carregandoState = true
                                mensagemState = ""

                                val call = RetrofitFactory()
                                    .getEnderecoService()
                                    .getEnderecosByUfCidadeRua(
                                        uf = ufState,
                                        cidade = cidadeState,
                                        rua = ruaState
                                    )

                                call.enqueue(
                                    object : Callback<List<Endereco>> {

                                        override fun onResponse(
                                            call: Call<List<Endereco>>,
                                            response: Response<List<Endereco>>
                                        ) {

                                            carregandoState = false

                                            if (response.isSuccessful) {

                                                listaEnderecoState =
                                                    response.body() ?: emptyList()

                                                mensagemState =
                                                    if (listaEnderecoState.isEmpty()) {
                                                        "Nenhum endereço encontrado."
                                                    } else {
                                                        "${listaEnderecoState.size} endereço(s) encontrado(s)."
                                                    }

                                            } else {

                                                mensagemState =
                                                    "Erro na consulta: ${response.code()}"

                                                Log.e(
                                                    "CEP",
                                                    "Erro HTTP: ${response.code()}"
                                                )
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<List<Endereco>>,
                                            t: Throwable
                                        ) {

                                            carregandoState = false

                                            mensagemState =
                                                "Não foi possível consultar os endereços."

                                            Log.e(
                                                "CEP",
                                                "Falha na requisição",
                                                t
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar endereço"
                        )
                    }
                }


                // Mensagem de carregamento, erro ou resultado
                if (carregandoState) {

                    Text(
                        text = "Buscando endereços...",
                        color = Color.Gray
                    )

                } else if (mensagemState.isNotBlank()) {

                    Text(
                        text = mensagemState,
                        color = Color.DarkGray
                    )
                }
            }
        }


        // Lista de endereços
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {

            items(listaEnderecoState) { endereco ->

                CardEndereco(
                    endereco = endereco
                )
            }
        }
    }
}


//----------------- CARTÃO DE ENDEREÇO ----------------------//

@Composable
fun CardEndereco(endereco: Endereco) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Text(
                text = "CEP: ${endereco.cep}"
            )

            Text(
                text = "Rua: ${endereco.rua}"
            )

            Text(
                text = "Cidade: ${endereco.cidade}"
            )

            Text(
                text = "Bairro: ${endereco.bairro}"
            )

            Text(
                text = "UF: ${endereco.uf}"
            )
        }
    }
}
