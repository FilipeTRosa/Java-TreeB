import java.io.*;
import java.util.*;

public class TesteArvoreBComArquivo {
    public static void main(String[] args) {
        try {
            String arquivoRaiz = "raiz.txt";
            ArvoreB arvore = new ArvoreB(arquivoRaiz, 6); // Grau da Árvore B
            String arquivoEntrada = "dados_20.csv";

            // Ler registros do arquivo e inserir na árvore
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivoEntrada))) {
                String linha;
                boolean isFirstLine = true;
                while ((linha = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false; // Ignorar a primeira linha (cabeçalho)
                        continue;
                    }
                    String[] partes = linha.split(";");
                    if (partes.length == 3) {
                        String chave = partes[0];
                        String nome = partes[1];
                        String outrosDados = partes[2];
                        Registro registro = new Registro(chave, nome, outrosDados, "null", "null");
                        arvore.inserir(registro);
                    }
                }
            }

            System.out.println("Todos os registros foram inseridos na Árvore B com sucesso.");
            arvore.imprimirArvorePersonalizado();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}