import java.util.*;
import java.io.*;

public class TesteArvoreB {
    public static void main(String[] args) {
        try {
            String arquivoRaiz = "raiz.txt";
            ArvoreB arvore = new ArvoreB(arquivoRaiz, 4); // Grau

            // Inserção de registros de teste
            arvore.inserir(new Registro("10", "Alice", "Dados1", "null", "null"));
            arvore.inserir(new Registro("20", "Bob", "Dados2", "null", "null"));
            arvore.inserir(new Registro("5", "Charlie", "Dados3", "null", "null"));
            arvore.inserir(new Registro("15", "Diana", "Dados4", "null", "null"));
            arvore.inserir(new Registro("25", "Eve", "Dados5", "null", "null"));
            arvore.inserir(new Registro("30", "Maria", "Dados6", "null", "null"));
            arvore.inserir(new Registro("6", "Paulo", "Dados7", "null", "null"));
            arvore.inserir(new Registro("9", "Jose", "Dados8", "null", "null"));
            arvore.inserir(new Registro("22", "Ana", "Dados9", "null", "null"));
            arvore.inserir(new Registro("35", "Joao", "Dados10", "null", "null"));
            arvore.inserir(new Registro("13", "Bibi", "Dados11", "null", "null"));


            // Pesquisa de um registro existente
            Registro encontrado = arvore.pesquisar("15");
            if (encontrado != null) {
                System.out.println("Registro encontrado: " + encontrado);
            } else {
                System.out.println("Registro não encontrado.");
            }

            // Pesquisa de um registro inexistente
            encontrado = arvore.pesquisar("30");
            if (encontrado != null) {
                System.out.println("Registro encontrado: " + encontrado);
            } else {
                System.out.println("Registro não encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}