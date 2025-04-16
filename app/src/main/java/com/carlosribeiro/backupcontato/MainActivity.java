package com.carlosribeiro.backupcontato;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    //TODO: Declareted attributes
    //TODO: Create layout
    //TODO: Create tela spleash Deve validar as permissoes do manisfet, o android deve receber o ok do usuario para as permissoes caso nao, encerre o app
    //TODO: Criar uma nova branch validar permissoes

    //quais sao as permissoes perigosas e as nao perigosas

    private TextView txtPermissoes;
    private Button btnExportar;
    private ProgressBar progresso;

    private TextView statusExportacao;


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
        btnExportar = findViewById(R.id.btnExportar);
        progresso = findViewById(R.id.progresso);
        statusExportacao = findViewById(R.id.statusExportacao);

        exibirPermissoesConcedidas();

        btnExportar.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_progress, null);

            ProgressBar progressBar = dialogView.findViewById(R.id.barraCircular);
            TextView textoProgresso = dialogView.findViewById(R.id.textoProgresso);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Exportando contatos")
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            dialog.show();

            Executors.newSingleThreadExecutor().execute(() -> {
                List<String[]> contatos = obterContatosDoAparelho();

                if (contatos.isEmpty()) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        new AlertDialog.Builder(this)
                                .setTitle("Nenhum contato encontrado")
                                .setMessage("Não há contatos disponíveis para exportar.")
                                .setPositiveButton("Fechar", null)
                                .show();
                    });
                } else {
                    // Simulação de progresso visual (aqui é fake pra UX)
                    for (int i = 0; i <= 100; i += 10) {
                        int finalI = i;
                        runOnUiThread(() -> {
                            progressBar.setProgress(finalI);
                            textoProgresso.setText("Exportando " + finalI + "%");
                        });
                        try {
                            Thread.sleep(80); // simula tempo de exportação
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    String csv = gerarCsvDosContatos(contatos);
                    salvarCsvNoDocuments("contatos_exportados.csv", csv);

                    runOnUiThread(() -> {
                        dialog.dismiss();

                        String caminho = "Download/BackupContatos/contatos_exportados.csv";
                        statusExportacao.setText("✔ Exportação finalizada!\n📁 Arquivo salvo em: " + caminho);

                        Toast.makeText(this, "Exportação concluída!", Toast.LENGTH_SHORT).show();
                    });

                }
            });
        });

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

    private List<String[]> obterContatosDoAparelho() {
        List<String[]> lista = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String nome = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String numero = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    lista.add(new String[]{nome, numero});
                }
            }
        }

        return lista;
    }

    private String gerarCsvDosContatos(List<String[]> contatos) {
        StringBuilder csv = new StringBuilder();
        csv.append("Nome,Telefone\n");
        for (String[] contato : contatos) {
            csv.append("\"").append(contato[0]).append("\",")
                    .append("\"").append(contato[1]).append("\"\n");
        }
        return csv.toString();
    }

    private void salvarCsvNoDocuments(String nomeArquivo, String conteudoCsv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ (API 29+)
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, nomeArquivo);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/BackupContatos");

            ContentResolver resolver = getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (OutputStream out = resolver.openOutputStream(uri)) {
                    out.write(conteudoCsv.getBytes());
                    out.flush();
                    Log.d("EXPORT", "CSV salvo com sucesso: " + uri.toString());
                } catch (IOException e) {
                    Log.e("EXPORT", "Erro ao salvar CSV", e);
                }
            } else {
                Log.e("EXPORT", "Erro: URI nula");
            }

        } else {
            // Android 9 ou inferior → precisa de permissão WRITE_EXTERNAL_STORAGE
            File pasta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BackupContatos");
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            File arquivo = new File(pasta, nomeArquivo);

            try (FileOutputStream out = new FileOutputStream(arquivo)) {
                out.write(conteudoCsv.getBytes());
                out.flush();
                Log.d("EXPORT", "CSV salvo em: " + arquivo.getAbsolutePath());
            } catch (IOException e) {
                Log.e("EXPORT", "Erro ao salvar CSV (modo compatibilidade)", e);
            }
        }
    }


}
