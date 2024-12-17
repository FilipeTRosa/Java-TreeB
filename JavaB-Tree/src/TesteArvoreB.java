public class TesteArvoreB {
    public static void main(String[] args) {
        String arquivo = "arvoreB.dat";
        ArvoreB arvore;

        // Tentar carregar a árvore do arquivo
        arvore = ArvoreB.carregarDeArquivo(arquivo);
        if (arvore == null) {
            System.out.println("Nenhum arquivo encontrado. Criando uma nova árvore.");
            arvore = new ArvoreB(6);  // Ordem 6
        }

        // Inserir alguns dados de exemplo
        arvore.inserir(1234, "Lucas Martins");
        arvore.inserir(2345, "Camila Pereira");
        arvore.inserir(3456, "Daniel Souza");
        arvore.inserir(4567, "Beatriz Rocha");
        arvore.inserir(5678, "Ricardo Almeida");
        arvore.inserir(6789, "Fernanda Costa");
        arvore.inserir(7890, "Gustavo Lima");
        arvore.inserir(8901, "Mariana Silva");
        arvore.inserir(9012, "Thiago Fernandes");
        arvore.inserir(1010, "Patrícia Santos");
        arvore.inserir(1122, "Gabriela Oliveira");
        arvore.inserir(2233, "José Santos");
        arvore.inserir(3344, "Carlos Almeida");
        arvore.inserir(4455, "Ana Costa");
        arvore.inserir(5566, "Paulo Lima");
        arvore.inserir(6677, "Renata Martins");
        arvore.inserir(7788, "Fábio Pereira");
        arvore.inserir(8899, "Eliane Rocha");
        arvore.inserir(9900, "Eduardo Souza");
        arvore.inserir(1001, "Aline Silva");
        arvore.inserir(1111, "Flávio Fernandes");
        arvore.inserir(2222, "Vanessa Costa");
        arvore.inserir(3333, "Marcio Oliveira");
        arvore.inserir(4444, "Isabel Almeida");
        arvore.inserir(5555, "Thiago Lima");
        arvore.inserir(6666, "Juliana Martins");
        arvore.inserir(7777, "Mário Rocha");
        arvore.inserir(8888, "Carla Pereira");
        arvore.inserir(9999, "Roberta Silva");
        arvore.inserir(1011, "Cláudio Souza");
        arvore.inserir(2022, "Fabiana Costa");
        arvore.inserir(3033, "Simone Almeida");
        arvore.inserir(4044, "Henrique Oliveira");
        arvore.inserir(5055, "Lúcia Rocha");
        arvore.inserir(6066, "Marcos Lima");
        arvore.inserir(7077, "Vânia Fernandes");
        arvore.inserir(8088, "Nelson Santos");
        arvore.inserir(9099, "José Lima");
        arvore.inserir(10000, "Sandra Pereira");
        arvore.inserir(11100, "Clara Rocha");
        arvore.inserir(12121, "Gustavo Costa");
        arvore.inserir(13131, "Sônia Almeida");
        arvore.inserir(14141, "Rogério Oliveira");
        arvore.inserir(15151, "Marcela Martins");
        arvore.inserir(16161, "Ricardo Souza");
        arvore.inserir(17171, "Patrícia Lima");
        arvore.inserir(18181, "Alessandra Costa");
        arvore.inserir(19191, "Raul Rocha");
        arvore.inserir(20202, "Jorge Fernandes");
        arvore.inserir(21212, "Elaine Silva");


        // Imprimir a estrutura da árvore
        System.out.println("\nEstrutura da Árvore B:");
        arvore.imprimirArvore();

        // Salvar a árvore em um arquivo
        arvore.salvarEmArquivo(arquivo);
        arvore.salvarEmArquivoTexto("arvoreB.txt");
    }
}
