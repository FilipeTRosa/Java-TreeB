import java.io.*;
import java.util.*;

// Classe para representar a Árvore B
class ArvoreB implements Serializable {
    private static final long serialVersionUID = 1L;  // serialVersionUID para ArvoreB

    private NoArvoreB raiz;
    private int ordem;

    // Construtor para inicializar a árvore
    public ArvoreB(int ordem) {
        this.ordem = ordem;
        raiz = new NoArvoreB(ordem);
    }

    public NoArvoreB getRaiz() {
        return raiz;
    }

    // Função de busca
    public String buscar(int matricula) {
        return buscarNo(raiz, matricula);
    }

    private String buscarNo(NoArvoreB no, int matricula) {
        int i = 0;
        while (i < no.numChaves && matricula > no.matriculas[i]) {
            i++;
        }

        if (i < no.numChaves && matricula == no.matriculas[i]) {
            return no.nomes[i];
        }

        if (no.folha) {
            return null;
        }

        return buscarNo(no.filhos[i], matricula);
    }

    // Função de inserção
    public void inserir(int matricula, String nome) {
        NoArvoreB raizAntiga = raiz;

        // Se a raiz estiver cheia, cria uma nova raiz
        if (raiz.numChaves == ordem - 1) {
            NoArvoreB novaRaiz = new NoArvoreB(ordem);
            novaRaiz.folha = false;
            novaRaiz.filhos[0] = raiz;
            dividir(novaRaiz, 0);
            raiz = novaRaiz;
        }

        inserirNo(raiz, matricula, nome);
    }
    private void dividir(NoArvoreB no, int i) {
        // Calculando o índice da chave do meio
        int t = (ordem - 1) / 2;  // t é a chave do meio

        // Filho que será dividido
        NoArvoreB y = no.filhos[i];

        // Novo nó para armazenar as chaves e filhos divididos
        NoArvoreB z = new NoArvoreB(ordem);
        z.folha = y.folha;
        z.numChaves = t;  // Novo nó tem t chaves

        // Passando as t últimas chaves de y para z
        for (int j = 0; j < t; j++) {
            z.matriculas[j] = y.matriculas[j + t + 1];  // Transfere as chaves
            z.nomes[j] = y.nomes[j + t + 1];
        }

        // Se y não for folha, passa os t+1 filhos de y para z
        if (!y.folha) {
            for (int j = 0; j < t + 1; j++) {
                z.filhos[j] = y.filhos[j + t + 1];  // Transfere os filhos
            }
        }

        // Atualiza o número de chaves de y
        y.numChaves = t;

        // Desloca os filhos de no para dar espaço ao novo filho z
        for (int j = no.numChaves; j > i; j--) {
            no.filhos[j + 1] = no.filhos[j];
        }

        // Insere z como filho de no
        no.filhos[i + 1] = z;

        // Desloca as chaves de no para dar espaço à chave do meio
        for (int j = no.numChaves; j > i; j--) {
            no.matriculas[j] = no.matriculas[j - 1];
            no.nomes[j] = no.nomes[j - 1];
        }

        // "Sobe" a chave do meio para o nó pai
        no.matriculas[i] = y.matriculas[t];
        no.nomes[i] = y.nomes[t];

        // Limpa a chave promovida de y
        y.matriculas[t] = 0;
        y.nomes[t] = null;

        // Incrementa o número de chaves de no
        no.numChaves++;
    }


    private void inserirNo(NoArvoreB no, int matricula, String nome) {
        int i = no.numChaves - 1;

        if (no.folha) {
            // Inserir a chave no nó folha
            while (i >= 0 && matricula < no.matriculas[i]) {
                no.matriculas[i + 1] = no.matriculas[i];
                no.nomes[i + 1] = no.nomes[i];
                i--;
            }

            no.matriculas[i + 1] = matricula;
            no.nomes[i + 1] = nome;
            no.numChaves++;
        } else {
            // Encontrar o filho correto para continuar a inserção
            while (i >= 0 && matricula < no.matriculas[i]) {
                i--;
            }
            i++;

            // Inicializa o filho se ele for null
            if (no.filhos[i] == null) {
                no.filhos[i] = new NoArvoreB(ordem);
            }

            // Se o filho estiver cheio, divide o nó
            if (no.filhos[i].numChaves == ordem - 1) {
                dividir(no, i);  // Dividir o nó cheio
                if (matricula > no.matriculas[i]) {
                    i++;
                }
            }

            inserirNo(no.filhos[i], matricula, nome);
        }
    }

    // Função para salvar a árvore em um arquivo
    public void salvarEmArquivo(String nomeArquivo) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            out.writeObject(this);
            System.out.println("Árvore B salva em " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Função para imprimir a árvore B
    public void imprimirArvore() {
        imprimirNo(raiz, 0);
    }

    // Função auxiliar para imprimir um nó da árvore B
    private void imprimirNo(NoArvoreB no, int nivel) {
        if (no != null) {
            System.out.print("Nível " + nivel + ": ");
            for (int i = 0; i < no.numChaves; i++) {
                System.out.print(no.matriculas[i] + " (" + no.nomes[i] + ") ");
            }
            System.out.println();

            // Chama recursivamente para os filhos, se o nó não for uma folha
            if (!no.folha) {
                for (int i = 0; i <= no.numChaves; i++) {
                    imprimirNo(no.filhos[i], nivel + 1);
                }
            }
        }
    }
    // Função para carregar a árvore de um arquivo
    public static ArvoreB carregarDeArquivo(String nomeArquivo) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            return (ArvoreB) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //VERIFICAR CORRETUDE

    // Método para gerar a representação textual da árvore
    public String gerarRepresentacao() {
        StringBuilder sb = new StringBuilder();
        gerarRepresentacao(this.raiz, 0, sb);
        return sb.toString();
    }

    private void gerarRepresentacao(NoArvoreB no, int nivel, StringBuilder sb) {
        if (no != null) {
            sb.append("Nível ").append(nivel).append(": ");
            for (int i = 0; i < no.numChaves; i++) {
                sb.append(no.matriculas[i]).append(" ");
            }
            sb.append("\n");

            if (!no.folha) {
                for (int i = 0; i <= no.numChaves; i++) {
                    gerarRepresentacao(no.filhos[i], nivel + 1, sb);
                }
            }
        }
    }

    // Método para salvar a representação da árvore em um arquivo de texto
    public void salvarEmArquivoTexto(String nomeArquivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nomeArquivo))) {
            pw.print(gerarRepresentacao());
            System.out.println("Árvore B salva em " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar a árvore em arquivo: " + e.getMessage());
        }
    }
}
