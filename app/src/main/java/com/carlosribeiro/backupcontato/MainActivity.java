package com.carlosribeiro.backupcontato;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //TODO: Declareted attributes
    //TODO: Create layout
    //TODO: Create tela spleash Deve validar as permissoes do manisfet, o android deve receber o ok do usuario para as permissoes caso nao, encerre o app
    //TODO: Criar uma nova branch validar permissoes

    //quais sao as permissoes perigosas e as nao perigosas

    private TextView txtPermissoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // verificar namespace e applicationID quando for necessario renomear o pacote

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtPermissoes = findViewById(R.id.txtPermissoes);
        exibirPermissoesConcedidas();
    }

    private void exibirPermissoesConcedidas() {
        StringBuilder permissoes = new StringBuilder("Permissões concedidas:\n");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            permissoes.append("✔ Acesso aos Contatos\n");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            permissoes.append("✔ Escrever Contatos\n");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            permissoes.append("✔ Localização (GPS)\n");
        }

        txtPermissoes.setText(permissoes.toString());
    }
}
