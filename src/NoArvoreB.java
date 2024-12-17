import java.io.Serializable;

// Classe para representar um nó da Árvore B
class NoArvoreB implements Serializable {
    private static final long serialVersionUID = 1L;  // Identificador para serialização

    int[] matriculas;
    String[] nomes;
    NoArvoreB[] filhos;
    int numChaves;
    boolean folha;

    public NoArvoreB(int ordem) {
        matriculas = new int[ordem - 1];
        nomes = new String[ordem - 1];
        filhos = new NoArvoreB[ordem];
        folha = true;
        numChaves = 0;
    }
}
