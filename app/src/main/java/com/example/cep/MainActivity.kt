package com.example.cep

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.launch
import com.example.cep.R

class MainActivity : AppCompatActivity() {

    // API Interface
    interface ViaCepApi {
        @GET("{cep}/json/")
        suspend fun buscarCep(@Path("cep") cep: String): CepResponse
    }

    // Data class para mapear a resposta da API
    data class CepResponse(
        val cep: String?,
        val logradouro: String?,
        val bairro: String?,
        val localidade: String?,
        val uf: String?
    )

    // Retrofit
    private val api: ViaCepApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referências aos campos do layout
        val editCep = findViewById<TextInputEditText>(R.id.editCep)
        val btBuscarCep = findViewById<Button>(R.id.btBuscarCep)
        val editLogradouro = findViewById<TextInputEditText>(R.id.editLogradouro)
        val editBairro = findViewById<TextInputEditText>(R.id.editBairro)
        val editCidade = findViewById<TextInputEditText>(R.id.editCidade)
        val editEstado = findViewById<TextInputEditText>(R.id.editEstado)

        // Configuração do botão de buscar CEP
        btBuscarCep.setOnClickListener {
            val cep = editCep.text.toString()

            // Validação do CEP
            if (cep.length == 8) {
                buscarCep(cep, editLogradouro, editBairro, editCidade, editEstado)
            } else {
                editCep.error = getString(R.string.erro_cep)
            }
        }
    }

    private fun buscarCep(
        cep: String,
        logradouroField: EditText,
        bairroField: EditText,
        cidadeField: EditText,
        estadoField: EditText
    ) {
        lifecycleScope.launch {
            try {
                val response = api.buscarCep(cep)

                // Atualiza os campos com os dados do CEP
                logradouroField.setText(response.logradouro ?: getString(R.string.nao_encontrado))
                bairroField.setText(response.bairro ?: getString(R.string.nao_encontrado))
                cidadeField.setText(response.localidade ?: getString(R.string.nao_encontrado))
                estadoField.setText(response.uf ?: getString(R.string.nao_encontrado))

            } catch (e: Exception) {
                logradouroField.setText(getString(R.string.erro_busca))
                bairroField.setText(getString(R.string.erro_busca))
                cidadeField.setText(getString(R.string.erro_busca))
                estadoField.setText(getString(R.string.erro_busca))
            }
        }
    }
}