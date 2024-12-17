import java.util.*;
import java.io.*;

class Pagina {
    private int m; // Grau da Árvore B
    private List<Registro> registros;
    private String chave; // Nome do arquivo associado à página
    private String caminhoArquivo;

    public Pagina(int grau, String chave) {
        this.m = grau;
        this.chave = chave;
        this.registros = new ArrayList<>();
    }

    public static Pagina carregar(String chave) throws IOException {
        File arquivo = new File(chave + ".txt");
        Pagina pagina = new Pagina(6, chave); // Exemplo: grau = 2
        if (!arquivo.exists()) return pagina;

        BufferedReader reader = new BufferedReader(new FileReader(arquivo));
        String linha;
        while ((linha = reader.readLine()) != null) {
            String[] partes = linha.split(";");
            String chaveReg = partes[0];
            String nome = partes[1];
            String outros = partes[2];
            String chaveEsq = partes[3];
            String chaveDir = partes[4];
            pagina.registros.add(new Registro(chaveReg, nome, outros, chaveEsq, chaveDir));
        }
        reader.close();
        return pagina;
    }

    public void salvar() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.chave + ".txt"));
        for (Registro reg : registros) {
            writer.write(reg.toString());
            writer.newLine();
        }
        writer.close();
    }

    public boolean estaCheia() {
        return registros.size() >= (m-1);
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public String getChave() {
        return chave;
    }

    public void adicionarRegistro(Registro registro) {
        registros.add(registro);
        registros.sort(Comparator.comparing(r -> Integer.parseInt(r.chave))); // Mantém registros ordenados por chave
    }

    public Registro buscarRegistro(String chaveBusca) {
        for (Registro reg : registros) {
            if (reg.chave.equals(chaveBusca)) return reg;
        }
        return null;
    }

    public boolean ehFolha() {
        for (Registro reg : registros) {
            if (reg.chaveEsq != null && !reg.chaveEsq.equals("null")) {
                return false;
            }
            if (reg.chaveDir != null && !reg.chaveDir.equals("null")) {
                return false;
            }
        }
        return true;
    }

    public String getCaminho() {
        return this.caminhoArquivo;
    }
}