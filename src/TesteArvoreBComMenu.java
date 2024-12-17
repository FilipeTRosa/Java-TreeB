import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class TesteArvoreBComMenu {
    public static void main(String[] args) {
        try {
            String arquivoRaiz = "raiz.txt";
            ArvoreB arvore = new ArvoreB(arquivoRaiz, 6); // Grau
            Scanner scanner = new Scanner(System.in);
            String chave;
            String nome;
            String arquivoEntrada = "dados_50.csv";

            while (true) {
                System.out.println("\nMenu de Opções:");
                System.out.println("1. Inserir um novo registro");
                System.out.println("2. Pesquisar um registro");
                System.out.println("3. Remover um registro"); // Nova opção
                System.out.println("4. Carregar Banco de arquivo TXT"); // Nova opção
                System.out.println("5. Imprimir");
                System.out.println("6. Forçar remoção");
                System.out.println("7. Sair");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Consumir a quebra de linha

                switch (opcao) {
                    case 1:
                        System.out.print("Digite a chave: ");
                        chave = scanner.nextLine();
                        System.out.print("Digite o nome: ");
                        nome = scanner.nextLine();
                        System.out.print("Digite os outros campos: ");
                        String outrosCampos = scanner.nextLine();

                        Registro novoRegistro = new Registro(chave, nome, outrosCampos, "null", "null");
                        arvore.inserir(novoRegistro);
                        System.out.println("Registro inserido com sucesso!");
                        break;

                    case 2:
                        // Realizar uma busca
                        System.out.println("Escolha o tipo de busca:");
                        System.out.println("1. Buscar por chave");
                        System.out.println("2. Buscar por nome");
                        System.out.print("Digite sua escolha: ");
                        int escolha = scanner.nextInt();
                        scanner.nextLine(); // Consumir a quebra de linha

                        if (escolha == 1) {
                            // Buscar por chave
                            System.out.print("Digite a chave para buscar: ");
                            String chaveBusca = scanner.nextLine();
                            Registro resultado = arvore.buscar(chaveBusca);
                            if (resultado != null) {
                                System.out.println("Registro encontrado: " + resultado);
                            } else {
                                System.out.println("Registro não encontrado.");
                            }
                        } else if (escolha == 2) {
                            // Buscar por nome
                            System.out.print("Digite o nome para buscar: ");
                            String nomeBusca = scanner.nextLine();
                            Registro resultado = buscarPorNome(arvore, nomeBusca);
                            if (resultado != null) {
                                System.out.println("Registro encontrado: " + resultado);
                            } else {
                                System.out.println("Registro não encontrado.");
                            }
                        } else {
                            System.out.println("Opção inválida. Retornando ao menu.");
                        }
                        break;

                    case 3:
                        System.out.print("Digite a chave para remover: ");
                        String chaveRemover = scanner.nextLine();
                        if (arvore.remover(chaveRemover)) {
                            System.out.println("Registro removido com sucesso.");
                        } else {
                            System.out.println("Erro: Registro não encontrado.");
                        }
                        break;
                    case 4:
                        System.out.println("Carregar arvore de arquivo ");


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
                                    chave = partes[0];
                                    nome = partes[1];
                                    String outrosDados = partes[2];
                                    Registro registro = new Registro(chave, nome, outrosDados, "null", "null");
                                    arvore.inserir(registro);
                                }
                            }
                        }

                        System.out.println("Todos os registros foram inseridos na Árvore B com sucesso.");
                        break;
                    case 5:
                        System.out.println("Impressao ArvoreB ");
                        arvore.imprimirArvorePersonalizado();

                        break;
                    case 6:
                        System.out.print("Digite a chave que deseja deletar: ");
                        String chaveParaDeletar = scanner.nextLine();
                        deletarLinhaDoCSV(arquivoEntrada, chaveParaDeletar);
                        excluirArquivosTxt();

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
                                    chave = partes[0];
                                    nome = partes[1];
                                    String outrosDados = partes[2];
                                    Registro registro = new Registro(chave, nome, outrosDados, "null", "null");
                                    arvore.inserir(registro);
                                }
                            }
                        }

                        System.out.println("A exclusão e reconstrução foram concluídas.");
                        break;
                    case 7:
                        System.out.println("Encerrando o programa.");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Registro buscarPorNome(ArvoreB arvore, String nomeBusca) throws IOException {
        Queue<Pagina> fila = new LinkedList<>();
        fila.add(arvore.carregarRaiz());

        while (!fila.isEmpty()) {
            Pagina atual = fila.poll();
            for (Registro reg : atual.getRegistros()) {
                if (reg.nome.equalsIgnoreCase(nomeBusca)) {
                    return reg; // Registro encontrado
                }
            }

            // Adiciona as páginas filhas na fila
            for (Registro reg : atual.getRegistros()) {
                if (!reg.chaveEsq.equals("null")) {
                    fila.add(Pagina.carregar(reg.chaveEsq));
                }
                if (!reg.chaveDir.equals("null")) {
                    fila.add(Pagina.carregar(reg.chaveDir));
                }
            }
        }

        return null; // Registro não encontrado
    }

    // Função para deletar a linha com a chave especificada do CSV
    public static void deletarLinhaDoCSV(String arquivoEntrada, String chave) {
        File arquivoOriginal = new File(arquivoEntrada);
        File arquivoTemp = new File("temp.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;
            boolean isFirstLine = true;

            while ((linha = reader.readLine()) != null) {
                if (isFirstLine) {
                    writer.write(linha); // Escreve o cabeçalho no novo arquivo
                    writer.newLine();
                    isFirstLine = false;
                    continue;
                }

                String[] partes = linha.split(";");
                if (!partes[0].equals(chave)) {
                    writer.write(linha);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao deletar linha do CSV: " + e.getMessage());
        }

        // Substitui o arquivo original pelo arquivo temporário
        if (arquivoOriginal.delete()) {
            arquivoTemp.renameTo(arquivoOriginal);
        } else {
            System.out.println("Erro ao substituir o arquivo original.");
        }
    }

    // Função para excluir todos os arquivos .txt na pasta atual
    public static void excluirArquivosTxt() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."), "*.txt")) {
            for (Path file : stream) {
                Files.delete(file);
                System.out.println("Arquivo deletado: " + file.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Erro ao excluir arquivos .txt: " + e.getMessage());
        }
    }


}
