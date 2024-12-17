



 private void fundirFilhos(Pagina pai, int indice, Pagina filhoEsquerdo, Pagina filhoDireito) throws IOException {
        Registro registroDoPai = pai.getRegistros().remove(indice);

        if (filhoEsquerdo.ehFolha()) {
            registroDoPai.chaveDir = "null";
            registroDoPai.chaveEsq = "null";
        }else{
            // Corrige os ponteiros para manter a conexão com os filhos fundidos
            Registro ultimoRegistroEsq = filhoEsquerdo.getRegistros().get(filhoEsquerdo.getRegistros().size() - 1);
            Registro primeiroRegistroDir = filhoDireito.getRegistros().get(0);

            registroDoPai.chaveEsq = ultimoRegistroEsq.chaveDir;
            registroDoPai.chaveDir = primeiroRegistroDir.chaveEsq;
        }


        // Junta os registros do filho direito no filho esquerdo
        filhoEsquerdo.getRegistros().add(registroDoPai);
        filhoEsquerdo.getRegistros().addAll(filhoDireito.getRegistros());

        // Ajusta os apontamentos do antecessor e do predecessor
        ajustarAntecessorEPredecessor(filhoEsquerdo, filhoDireito, registroDoPai);

        String chaveRemovida = filhoDireito.getChave();
        // Remove o arquivo da página do filho direito
        new File(filhoDireito.getChave() + ".txt").delete();
        if (!ajustarReferencias(pai, chaveRemovida)) {
            pai.salvar();
        }
        filhoEsquerdo.salvar();
    }


        private Pagina encontrarFolha(Pagina pagina, Registro novoRegistro) throws IOException {
        while (pagina.getRegistros().size() > 0 && !pagina.getRegistros().get(0).chaveEsq.equals("null")) {
            String proxPaginaChave = null;
            for (Registro reg : pagina.getRegistros()) {
                if (Integer.parseInt(novoRegistro.chave) < Integer.parseInt(reg.chave)) {
                    proxPaginaChave = reg.chaveEsq;
                    break;
                }
                proxPaginaChave = reg.chaveDir;
            }
            if (proxPaginaChave == null || proxPaginaChave.equals("null")) {
                return pagina;
            }
            pagina = Pagina.carregar(proxPaginaChave);
        }
        return pagina;
    }