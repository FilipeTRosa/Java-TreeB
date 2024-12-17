public class Registro {
    String chave;
    String nome;
    String outrosCampos;
    String chaveEsq;
    String chaveDir;

    public Registro(String chave, String nome, String outrosCampos, String chaveEsq, String chaveDir) {
        this.chave = chave;
        this.nome = nome;
        this.outrosCampos = outrosCampos;
        this.chaveEsq = chaveEsq;
        this.chaveDir = chaveDir;
    }

    @Override
    public String toString() {
        return chave + ";" + nome + ";" + outrosCampos + ";" + chaveEsq + ";" + chaveDir;
    }
}
