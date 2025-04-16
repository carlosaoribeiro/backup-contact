package com.carlosribeiro.backupcontato;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;

public class SplashActivity extends AppCompatActivity {

    // Launcher para permissão de leitura de contatos
    private final ActivityResultLauncher<String> launcherReadContacts =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    requestWriteContactsPermission();
                } else {
                    finishAffinity();
                }
            });

    // Launcher para permissão de escrita de contatos
    private final ActivityResultLauncher<String> launcherWriteContacts =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    requestLocationPermission();
                } else {
                    finishAffinity();
                }
            });

    // Launcher para permissão de localização (GPS)
    private final ActivityResultLauncher<String> launcherLocation =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    goToMain();
                } else {
                    finishAffinity();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inicia a sequência de permissões
        requestReadContactsPermission();
    }

    private void requestReadContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            launcherReadContacts.launch(Manifest.permission.READ_CONTACTS);
        } else {
            requestWriteContactsPermission();
        }
    }

    private void requestWriteContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            launcherWriteContacts.launch(Manifest.permission.WRITE_CONTACTS);
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            launcherLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            goToMain();
        }
    }

    private void goToMain() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1000);
    }

    // Função para salvar backup usando MediaStore
    public void salvarBackupNoDocuments(String nomeArquivo, byte[] conteudo) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, nomeArquivo);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/BackupContatos");

        ContentResolver resolver = getContentResolver();
        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

        if (uri != null) {
            try (OutputStream out = resolver.openOutputStream(uri)) {
                out.write(conteudo);
                out.flush();
                Log.d("SALVO", "Backup salvo com sucesso: " + uri.toString());
            } catch (IOException e) {
                Log.e("ERRO", "Erro ao salvar arquivo", e);
            }
        }
    }
}
