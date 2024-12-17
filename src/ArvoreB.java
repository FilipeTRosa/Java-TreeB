import java.io.*;
import java.util.*;


class ArvoreB {
    private String arquivoRaiz;
    private int m; // Grau da Árvore B

    public ArvoreB(String arquivoRaiz, int m) {
        this.arquivoRaiz = arquivoRaiz;
        this.m = m;
    }

    public Pagina carregarRaiz() throws IOException {
        File arquivo = new File(arquivoRaiz);
        if (!arquivo.exists()) {
            return null; // Retorna null se o arquivo raiz não existir
        }

        BufferedReader reader = new BufferedReader(new FileReader(arquivo));
        String chaveRaiz = reader.readLine();
        reader.close();
        return Pagina.carregar(chaveRaiz);
    }

    public void salvarRaiz(String chave) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoRaiz));
        writer.write(chave);
        writer.close();
    }

    public Registro pesquisar(String chaveBusca) throws IOException {
        Pagina atual = carregarRaiz();
        while (atual != null) {
            Registro encontrado = atual.buscarRegistro(chaveBusca);
            if (encontrado != null) return encontrado;

            // Determinar a próxima página
            String proxPaginaChave = null;
            for (Registro reg : atual.getRegistros()) {
                if (chaveBusca.compareTo(reg.chave) < 0) {
                    proxPaginaChave = reg.chaveEsq;
                    break;
                }
                proxPaginaChave = reg.chaveDir;
            }

            if (proxPaginaChave == null || proxPaginaChave.equals("null")) return null;
            atual = Pagina.carregar(proxPaginaChave);
        }
        return null;
    }

    private Pagina encontrarFolha(Pagina pagina, Registro novoRegistro) throws IOException {
        while (pagina.getRegistros().size() > 0 && !pagina.getRegistros().get(0).chaveEsq.equals("null")) {
            String proxPaginaChave = null;
            Registro registroAnterior = null; // Para rastrear o registro anterior

            for (Registro reg : pagina.getRegistros()) {
                if (Integer.parseInt(novoRegistro.chave) < Integer.parseInt(reg.chave)) {
                    // Se houver um registro anterior, use a chaveDir dele
                    if (registroAnterior != null) {
                        proxPaginaChave = registroAnterior.chaveDir;
                    } else {
                        // Caso contrário, siga pela chaveEsq do registro atual
                        proxPaginaChave = reg.chaveEsq;
                    }
                    break;
                }
                // Atualiza o registro anterior e tenta chaveDir do registro atual
                registroAnterior = reg;
                proxPaginaChave = reg.chaveDir;
            }

            // Caso nenhum próximo caminho seja encontrado, retorna a página atual
            if (proxPaginaChave == null || proxPaginaChave.equals("null")) {
                return pagina;
            }

            // Carregar a próxima página
            pagina = Pagina.carregar(proxPaginaChave);
        }
        return pagina;
    }


    public void inserir(Registro novoRegistro) throws IOException {
        Pagina raiz = carregarRaiz();
        // Verifica se a chave já existe na árvore
        if (pesquisar(novoRegistro.chave) != null) {
            System.out.println("Erro: Chave " + novoRegistro.chave + " já existe na árvore.");
            return; // Sai sem inserir
        }

        if (raiz == null) {
            // Cria uma nova página como raiz com um UUID aleatório
            String novaChave = "P" + UUID.randomUUID().toString();
            Pagina novaPagina = new Pagina(m, novaChave);
            novaPagina.adicionarRegistro(novoRegistro);
            novaPagina.salvar();
            salvarRaiz(novaChave);
        } else {
            // Encontra a folha apropriada para inserir
            Pagina folha = encontrarFolha(raiz, novoRegistro);
            inserirNaPagina(folha, novoRegistro);
        }
        System.out.println("---- ATUALIZADO ----");
        System.out.println("---- Inserindo o " + novoRegistro.chave + " ----");
        imprimirArvorePersonalizado();
    }



    private void inserirNaPagina(Pagina pagina, Registro novoRegistro) throws IOException {
        if (!pagina.estaCheia()) {//testa se nao esta cheia
            // Adiciona diretamente na página
            pagina.adicionarRegistro(novoRegistro);
            pagina.salvar();
        } else {
            // Divisão da página
            dividirPagina(pagina, novoRegistro, null);
        }
    }

    private Pagina encontrarPai(String chaveFilha) throws IOException {
        Pagina atual = carregarRaiz();
        if (atual == null) return null;

        while (atual != null) {
            for (Registro reg : atual.getRegistros()) {
                if (chaveFilha.equals(reg.chaveEsq) || chaveFilha.equals(reg.chaveDir)) {
                    return atual;
                }
            }
            // Determina o próximo caminho na árvore
            String proxPaginaChave = null;
            for (Registro reg : atual.getRegistros()) {
                if (chaveFilha.compareTo(reg.chave) < 0) {
                    proxPaginaChave = reg.chaveEsq;
                    break;
                }
                proxPaginaChave = reg.chaveDir;
            }
            if (proxPaginaChave == null || proxPaginaChave.equals("null")) return null;
            atual = Pagina.carregar(proxPaginaChave);
        }
        return null;
    }

    private void dividirPagina(Pagina pagina, Registro novoRegistro, Pagina pai) throws IOException {
        List<Registro> todosRegistros = new ArrayList<>(pagina.getRegistros());
        todosRegistros.add(novoRegistro);
        todosRegistros.sort(Comparator.comparing(r -> Integer.parseInt(r.chave)));

        int meio = todosRegistros.size() / 2;
        Registro registroDoMeio = todosRegistros.get(meio -1);

        // Cria nova página à direita
        String novaChaveDireita = "P" + UUID.randomUUID().toString();
        Pagina novaPaginaDireita = new Pagina(m, novaChaveDireita);
        novaPaginaDireita.getRegistros().addAll(todosRegistros.subList(meio, todosRegistros.size()));
        novaPaginaDireita.salvar();

        // Atualiza a página atual para conter apenas os registros à esquerda
        pagina.getRegistros().clear();
        pagina.getRegistros().addAll(todosRegistros.subList(0, meio -1));
        pagina.salvar();

        Registro registroPai = new Registro(
                registroDoMeio.chave, registroDoMeio.nome, registroDoMeio.outrosCampos,
                pagina.getChave(), novaChaveDireita
        );

        pai = encontrarPai(pagina.getChave());

        if (pai == null) {
            // Criar uma nova raiz
            Pagina novaRaiz = new Pagina(m, "P" + UUID.randomUUID().toString());
            novaRaiz.adicionarRegistro(registroPai);
            novaRaiz.salvar();
            salvarRaiz(novaRaiz.getChave());
        } else {
            //charm insereNaPagina.... para tentar corrigir erro
            inserirNaPagina(pai, registroPai);
            pai.salvar();
            //pai.adicionarRegistro(registroPai);
            /*if (pai.estaCheia()) {
                // Dividir o pai se necessário
                dividirPagina(pai, registroPai, encontrarPai(pai.getChave()));
            } else {
                pai.salvar();
            }*/
        }
        int minElementos = (m / 2) - 1;
        if (pagina.getRegistros().size() < minElementos || novaPaginaDireita.getRegistros().size() < minElementos) {
            throw new IllegalStateException("Página não atende ao requisito de elementos mínimos após a divisão.");
        }
    }

    public void imprimirArvore() throws IOException {
        Pagina raiz = carregarRaiz();
        if (raiz == null) {
            System.out.println("A árvore está vazia.");
            return;
        }
        imprimirPagina(raiz, 0);
    }

    private void imprimirPagina(Pagina pagina, int nivel) throws IOException {
        if (pagina == null) return;

        // Imprime os registros da página atual
        System.out.println("Nível " + nivel + ": " + pagina.getRegistros());

        // Recorre para as páginas filhas
        for (Registro reg : pagina.getRegistros()) {
            if (!reg.chaveEsq.equals("null")) {
                Pagina filhoEsq = Pagina.carregar(reg.chaveEsq);
                imprimirPagina(filhoEsq, nivel + 1);
            }
            if (!reg.chaveDir.equals("null")) {
                Pagina filhoDir = Pagina.carregar(reg.chaveDir);
                imprimirPagina(filhoDir, nivel + 1);
            }
        }
    }

    public Registro buscar(String chaveBusca) throws IOException {
        Pagina atual = carregarRaiz();
        int chaveIntBusca = Integer.parseInt(chaveBusca); // Converte a chave de busca para Integer

        while (atual != null) {
            // Busca na página atual
            for (Registro reg : atual.getRegistros()) {
                int chaveIntAtual = Integer.parseInt(reg.chave); // Converte a chave do registro para Integer

                if (chaveIntBusca == chaveIntAtual) {
                    // Imprime as chaves da página onde o registro foi encontrado
                    System.out.println("Registro encontrado na página com as chaves:");
                    for (Registro registro : atual.getRegistros()) {
                        System.out.print(registro.chave + " ");
                    }
                    System.out.println(); // Nova linha para melhorar a saída
                    return reg;
                }
            }

            // Determinar a próxima página
            String proxPaginaChave = null;
            for (Registro reg : atual.getRegistros()) {
                int chaveIntAtual = Integer.parseInt(reg.chave); // Converte a chave do registro para Integer

                if (chaveIntBusca < chaveIntAtual) {
                    proxPaginaChave = reg.chaveEsq;
                    break;
                }
                proxPaginaChave = reg.chaveDir;
            }

            if (proxPaginaChave == null || proxPaginaChave.equals("null")) {
                return null; // Registro não encontrado
            }

            atual = Pagina.carregar(proxPaginaChave);
        }
        return null; // Registro não encontrado
    }


    public void imprimirArvorePersonalizado() throws IOException {
        Pagina raiz = carregarRaiz();
        if (raiz == null) {
            System.out.println("A árvore está vazia.");
            return;
        }

        Queue<Pagina> fila = new LinkedList<>();
        fila.add(raiz);

        int nivel = 0;
        while (!fila.isEmpty()) {
            int tamanhoNivel = fila.size();
            System.out.print("Nível " + nivel + ": ");

            for (int i = 0; i < tamanhoNivel; i++) {
                Pagina atual = fila.poll();
                System.out.print("[ ");
                for (Registro reg : atual.getRegistros()) {
                    System.out.print(reg.chave + " ");
                }
                System.out.print("] ");

                // Adiciona os filhos na fila
                Set<String> chavesVisitadas = new HashSet<>();

                for (Registro reg : atual.getRegistros()) {
                    // Verifica e adiciona o filho esquerdo
                    if (!reg.chaveEsq.equals("null") && !chavesVisitadas.contains(reg.chaveEsq)) {
                        Pagina filhoEsquerda = Pagina.carregar(reg.chaveEsq);
                        fila.add(filhoEsquerda);
                        chavesVisitadas.add(reg.chaveEsq); // Marca como visitada
                    }

                    // Verifica e adiciona o filho direito
                    if (!reg.chaveDir.equals("null") && !chavesVisitadas.contains(reg.chaveDir)) {
                        Pagina filhoDireita = Pagina.carregar(reg.chaveDir);
                        fila.add(filhoDireita);
                        chavesVisitadas.add(reg.chaveDir); // Marca como visitada
                    }
                }
            }
            System.out.println();
            nivel++;
        }
    }
    public boolean remover(String chave) throws IOException {
        Pagina raiz = carregarRaiz();//ok
        if (raiz == null) {
            System.out.println("Árvore vazia.");
            return false;
        }

        boolean[] diminuiu = {false};
        raiz = removerRecursivo(raiz, chave, diminuiu);

        // Ajustar a raiz se necessário
        if (raiz.getRegistros().isEmpty() && !raiz.ehFolha()) {
            Pagina novaRaiz = Pagina.carregar(raiz.getRegistros().get(0).chaveEsq);
            salvarRaiz(novaRaiz.getChave());
        } else if (raiz.getRegistros().isEmpty()) {
            // Se a raiz ficar completamente vazia
            new File(arquivoRaiz).delete();
            raiz = null;
        }
        return true;
    }

    private Pagina removerRecursivo(Pagina pagina, String chave, boolean[] diminuiu) throws IOException {
        int i;
        Registro regPai = encontrarRegistroPai(pagina.getChave());
        for (i = 0; i < pagina.getRegistros().size(); i++) {
            Registro reg = pagina.getRegistros().get(i);


            int num1 = Integer.parseInt(chave);
            int num2 = Integer.parseInt(reg.chave);

            if (num1 < num2) {
                break; // Chave está na subárvore esquerda
            } else if (chave.equals(reg.chave)) {
                // Caso 1: Remoção na folha
                boolean teste = pagina.ehFolha();
                if (pagina.ehFolha()) {
                    pagina.getRegistros().remove(i);
                    diminuiu[0] = pagina.getRegistros().size() < (m / 2) - 1;
                    pagina.salvar();
                    return pagina;
                }

                // Caso 2: Remoção em uma página interna
                Registro substituto;
                Pagina filhoEsquerdo = Pagina.carregar(reg.chaveEsq);
                Pagina filhoDireito = Pagina.carregar(reg.chaveDir);


                if (filhoEsquerdo.getRegistros().size() >= (m / 2)-1) {
                    substituto = encontrarMaiorRegistro(filhoEsquerdo);
                    reg.chave = substituto.chave;
                    reg.nome = substituto.nome;
                    reg.outrosCampos = substituto.outrosCampos;
                    removerRecursivo(filhoEsquerdo, substituto.chave, diminuiu);
                } else if (filhoDireito.getRegistros().size() >= (m / 2)-1) {
                    substituto = encontrarMenorRegistro(filhoDireito);
                    reg.chave = substituto.chave;
                    reg.nome = substituto.nome;
                    reg.outrosCampos = substituto.outrosCampos;
                    removerRecursivo(filhoDireito, substituto.chave, diminuiu);
                } else {
                    // Fusão de filhos
                    fundirFilhos(pagina, i, filhoEsquerdo, filhoDireito);
                    removerRecursivo(filhoEsquerdo, reg.chave, diminuiu);
                }
                pagina.salvar();
                return pagina;
            }
        }

        // Caso 3: Chave não encontrada nesta página, descer recursivamente
        if (pagina.ehFolha()) {
            System.out.println("Chave " + chave + " não encontrada.");
            diminuiu[0] = false;
            return pagina;
        }

        String proxPaginaChave = (i == 0) ? pagina.getRegistros().get(i).chaveEsq
                : pagina.getRegistros().get(i - 1).chaveDir;

        Pagina proxPagina = Pagina.carregar(proxPaginaChave);
        removerRecursivo(proxPagina, chave, diminuiu);
        if(!(proxPagina.getRegistros().size() < (m / 2) - 1)){
            diminuiu[0] = false;
        }else{
            diminuiu[0] = true;
        }
        // Verifica se precisa balancear a página após a remoção
        if (diminuiu[0]) {
            balancearFilho(pagina, proxPagina, i);
        }

        pagina.salvar();
        return pagina;
    }

    private void fundirFilhos(Pagina pai, int indice, Pagina filhoEsquerdo, Pagina filhoDireito) throws IOException {
        // Remove o registro do pai correspondente à fusão
        Registro registroDoPai = pai.getRegistros().remove(indice);

        // Junta os registros do filho direito no filho esquerdo
        filhoEsquerdo.getRegistros().add(registroDoPai);
        filhoEsquerdo.getRegistros().addAll(filhoDireito.getRegistros());

        // Ajusta os ponteiros para manter a consistência
        if (!filhoEsquerdo.ehFolha()) {
            Registro ultimoRegistroEsq = filhoEsquerdo.getRegistros().get(filhoEsquerdo.getRegistros().size() - 1);
            Registro primeiroRegistroDir = filhoDireito.getRegistros().get(0);

            registroDoPai.chaveEsq = ultimoRegistroEsq.chaveDir;
            registroDoPai.chaveDir = primeiroRegistroDir.chaveEsq;

            ultimoRegistroEsq.chaveDir = registroDoPai.chaveDir; // Ajusta a conexão
        } else {
            registroDoPai.chaveEsq = "null";
            registroDoPai.chaveDir = "null";
        }

        // Atualiza as referências no pai
        if (indice > 0) {
            pai.getRegistros().get(indice - 1).chaveDir = filhoEsquerdo.getChave();
        } else if (!pai.getRegistros().isEmpty()) {
            pai.getRegistros().get(0).chaveEsq = filhoEsquerdo.getChave();
        }

        // Salva as alterações
        filhoEsquerdo.salvar();

        // Remove o arquivo da página do filho direito
        String chaveRemovida = filhoDireito.getChave();
        new File(chaveRemovida + ".txt").delete();

        // Atualiza o pai após ajustar as referências
        if (!ajustarReferencias(pai, chaveRemovida)) {
            pai.salvar();
        }
    }


    private boolean ajustarReferencias(Pagina pai, String chaveRemovida) throws IOException {
        boolean alterado = false;

        for (Registro reg : pai.getRegistros()) {
            // Verifica e ajusta chaveEsq
            if (reg.chaveEsq.equals(chaveRemovida)) {
                reg.chaveEsq = "null";
                alterado = true;
            }

            // Verifica e ajusta chaveDir
            if (reg.chaveDir.equals(chaveRemovida)) {
                reg.chaveDir = "null";
                alterado = true;
            }
        }

        // Salva a página pai apenas se houve alteração
        if (alterado) {
            pai.salvar();
        }

        return alterado; // Retorna true se houve alguma alteração
    }

    private void ajustarAntecessorEPredecessor(Pagina filhoEsquerdo, Pagina filhoDireito, Registro registroDoPai) {
        // Último registro do filho esquerdo
        Registro ultimoRegistroEsq = filhoEsquerdo.getRegistros().get(filhoEsquerdo.getRegistros().size() - 1);

        // Atualizar ponteiro do último registro da esquerda
        ultimoRegistroEsq.chaveDir = registroDoPai.chaveDir;

        // Atualizar ponteiros do registro do pai fundido
        registroDoPai.chaveEsq = ultimoRegistroEsq.chaveDir;
        registroDoPai.chaveDir = "null";
    }

    private void balancearFilho(Pagina pai, Pagina filho, int indice) throws IOException {
        Pagina irmaoEsquerdo = (indice > 0) ? Pagina.carregar(pai.getRegistros().get(indice - 1).chaveEsq) : null;
        Pagina irmaoDireito = (indice < pai.getRegistros().size())
                ? Pagina.carregar(pai.getRegistros().get(indice).chaveDir)
                : null;

        if (irmaoEsquerdo != null && irmaoEsquerdo.getRegistros().size() > (m / 2) - 1) {
            redistribuirEsquerda(pai, filho, irmaoEsquerdo, indice);
        } else if (irmaoDireito != null && irmaoDireito.getRegistros().size() > (m / 2) - 1) {
            redistribuirDireita(pai, filho, irmaoDireito, indice);
        } else if (irmaoEsquerdo != null) {
            fundirFilhos(pai, indice - 1, irmaoEsquerdo, filho);
        } else if (irmaoDireito != null) {
            fundirFilhos(pai, indice, filho, irmaoDireito);
        }
    }

    private Registro encontrarMaiorRegistro(Pagina pagina) throws IOException {
        while (!pagina.ehFolha()) {
            pagina = Pagina.carregar(pagina.getRegistros().get(pagina.getRegistros().size() - 1).chaveDir);
        }
        return pagina.getRegistros().get(pagina.getRegistros().size() - 1);
    }

    private Registro encontrarMenorRegistro(Pagina pagina) throws IOException {
        while (!pagina.ehFolha()) {
            pagina = Pagina.carregar(pagina.getRegistros().get(0).chaveEsq);
        }
        return pagina.getRegistros().get(0);
    }

    private void redistribuirEsquerda(Pagina pai, Pagina filho, Pagina irmaoEsquerdo, int indicePai) throws IOException {
        // Pega o registro do pai que será movido para o filho
        Registro registroDoPai = pai.getRegistros().get(indicePai - 1);

        // Pega o maior registro do irmão esquerdo
        Registro registroDoIrmao = irmaoEsquerdo.getRegistros().remove(irmaoEsquerdo.getRegistros().size() - 1);

        // Ajusta os ponteiros
        registroDoPai.chaveEsq = registroDoIrmao.chaveDir;
        registroDoIrmao.chaveDir = null;
        if (filho.ehFolha()) {
            registroDoPai.chaveDir = "null";
            registroDoPai.chaveEsq = "null";
        }
        // Insere o registro do pai no início do filho
        filho.getRegistros().add(0, registroDoPai);

        // Atualiza o pai com o registro do irmão
        pai.getRegistros().set(indicePai - 1, registroDoIrmao);

        // Salvar alterações
        irmaoEsquerdo.salvar();
        filho.salvar();
        pai.salvar();
    }

    private void redistribuirDireita(Pagina pai, Pagina filho, Pagina irmaoDireito, int indicePai) throws IOException {
        // Pega o registro do pai que será movido para o filho
        Registro registroDoPai = pai.getRegistros().get(indicePai);

        // Pega o menor registro do irmão direito
        Registro registroDoIrmao = irmaoDireito.getRegistros().remove(0);

        // Ajusta os ponteiros
        registroDoPai.chaveDir = registroDoIrmao.chaveEsq;
        registroDoIrmao.chaveEsq = null;

        if (filho.ehFolha()) {
            registroDoPai.chaveDir = "null";
            registroDoPai.chaveEsq = "null";
        }
        // Insere o registro do pai no final do filho
        filho.getRegistros().add(registroDoPai);

        // Atualiza o pai com o registro do irmão
        pai.getRegistros().set(indicePai, registroDoIrmao);

        // Salvar alterações
        irmaoDireito.salvar();
        filho.salvar();
        pai.salvar();
    }

    public void verificarConsistencia() throws IOException {
        Set<String> chavesExistentes = new HashSet<>();
        Queue<Pagina> fila = new LinkedList<>();
        fila.add(carregarRaiz());

        while (!fila.isEmpty()) {
            Pagina pagina = fila.poll();
            chavesExistentes.add(pagina.getChave());

            for (Registro reg : pagina.getRegistros()) {
                if (!reg.chaveEsq.equals("null")) {
                    fila.add(Pagina.carregar(reg.chaveEsq));
                    chavesExistentes.add(reg.chaveEsq);
                }
                if (!reg.chaveDir.equals("null")) {
                    fila.add(Pagina.carregar(reg.chaveDir));
                    chavesExistentes.add(reg.chaveDir);
                }
            }
        }

        // Checar arquivos existentes
        File diretorio = new File(".");
        for (File arquivo : diretorio.listFiles()) {
            if (arquivo.getName().endsWith(".txt")) {
                String chave = arquivo.getName().replace(".txt", "");
                if (!chavesExistentes.contains(chave)) {
                    System.out.println("Página órfã detectada: " + chave);
                }
            }
        }
    }

    private Registro encontrarRegistroPai(String chaveFilha) throws IOException {
        Pagina atual = carregarRaiz();
        if (atual == null) return null;

        while (atual != null) {
            for (Registro reg : atual.getRegistros()) {
                if (chaveFilha.equals(reg.chaveEsq) || chaveFilha.equals(reg.chaveDir)) {
                    return reg; // Retorna o registro que aponta para a página filha
                }
            }

            // Determinar a próxima página para descer na árvore
            String proxPaginaChave = null;
            for (Registro reg : atual.getRegistros()) {
                if (chaveFilha.compareTo(reg.chave) < 0) {
                    proxPaginaChave = reg.chaveEsq;
                    break;
                }
                proxPaginaChave = reg.chaveDir;
            }

            if (proxPaginaChave == null || proxPaginaChave.equals("null")) return null;
            atual = Pagina.carregar(proxPaginaChave);
        }
        return null; // Pai não encontrado
    }

}