# 📇 ContatoBackup

Aplicativo Android para exportação de contatos da agenda para um arquivo `.csv`, com suporte a ordenação alfabética, barra de progresso e visualização de backups anteriores.

---

## ✅ Funcionalidades

- 📤 Exporta contatos da agenda (nome, telefone, e-mail) para um arquivo `.csv`
- 📂 Salva o arquivo em `/Documents/MeusContatos` no armazenamento externo
- ⏱️ Barra de progresso durante a exportação
- 🔠 Ordena os contatos por nome
- 🧹 Ignora contatos sem telefone
- 📄 Permite visualizar arquivos `.csv` anteriores com busca por nome, telefone ou e-mail
- 🔎 Interface com campo de busca para filtrar os contatos visualizados
- 🕵️ Lista de backups existentes com ordenação do mais recente para o mais antigo
- 🧰 Compatível com Android 7.0 (API 24) até Android 13+

---

## 🧪 Pré-requisitos

- Android Studio (recomendado: versão Hedgehog ou mais recente)
- Android SDK mínimo: 24
- Permissões necessárias:
  - `READ_CONTACTS`
  - `WRITE_CONTACTS`
  - `WRITE_EXTERNAL_STORAGE` (ou `MediaStore` para Android 10+)

---

## 🚀 Como usar

1. Instale o aplicativo no dispositivo Android
2. Permita as permissões solicitadas na primeira execução
3. Clique em **Exportar Contatos** para gerar o arquivo CSV
4. Após a exportação, o app exibirá os arquivos disponíveis para visualização
5. Toque e segure em um arquivo para visualizar os dados com suporte a busca

---

## 📁 Estrutura de Arquivos

- Os arquivos são salvos com o nome: `contato_dd-MM-yyyy_HH-mm.csv`
- Diretório de destino: `/Documents/MeusContatos/`

---

## 📱 Telas do Aplicativo

- `MainActivity`: tela principal com botões de exportação e restauração
- `ListaArquivosActivity`: exibe os backups salvos para visualização
- `VisualizarCSVActivity`: exibe os contatos do arquivo selecionado com campo de busca

---

## 🛠️ Tecnologias Utilizadas

- Java
- Android SDK
- AsyncTask (com suporte a atualização de progresso)
- FileWriter / BufferedReader
- ListView + SearchView
- File I/O + ordenação por data
- MediaStore para atualização do sistema de arquivos

---

## 📌 Observações

- A partir do Android 10 (API 29), recomenda-se utilizar `MediaStore` e `Storage Access Framework (SAF)` para acesso externo.
- O aplicativo ignora contatos que não possuem número de telefone.

---

## 🔐 Segurança e Privacidade

O app **não envia dados de contatos para a internet**. Todos os dados exportados permanecem no armazenamento local do usuário.

---

## 📄 Licença

Este projeto é de uso educacional e pode ser adaptado livremente para fins não comerciais. Para uso comercial, favor entrar em contato com o autor.

---
