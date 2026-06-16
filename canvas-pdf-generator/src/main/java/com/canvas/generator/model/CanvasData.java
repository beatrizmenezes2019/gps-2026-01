package com.canvas.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Representa todos os campos do Project Model Canvas.
 * Esse objeto é recebido via POST JSON do n8n/assistente de IA.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasData {

    /** Código curto do projeto. Ex: "MOE" */
    private String codigoProjeto;

    /** Nome completo do projeto. Ex: "Mural de Oportunidades de Estágio" */
    private String nomeProjeto;

    /** Lista de justificativas para o projeto */
    private List<String> justificativas;

    /** Descrição dos objetivos do projeto */
    private String objetivos;

    /** Lista de benefícios esperados */
    private List<String> beneficios;

    /** Descrição completa do produto */
    private String produto;

    /** Lista de requisitos funcionais/não-funcionais */
    private List<Requisito> requisitos;

    /** Partes interessadas no projeto */
    private List<String> stakeholders;

    /** Membros da equipe */
    private List<String> equipe;

    /** Grupos/entregas do projeto (WBS de alto nível) */
    private List<String> grupoDeEntregas;

    /** Restrições do projeto */
    private List<String> restricoes;

    /** Premissas assumidas */
    private List<String> premissas;

    /** Riscos identificados */
    private List<String> riscos;

    /** Itens da linha do tempo / cronograma */
    private List<String> linhaDoTempo;

    /** Métricas de sucesso do projeto */
    private List<String> metricasDeSucesso;

    // -------------------------------------------------------------------------
    // Classe interna para requisitos (título em negrito + descrição)
    // -------------------------------------------------------------------------

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Requisito {
        /** Título do requisito (ex: "Gestão de Perfis e Acesso") */
        private String titulo;

        /** Descrição detalhada do requisito */
        private String descricao;

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    // -------------------------------------------------------------------------
    // Getters e Setters
    // -------------------------------------------------------------------------

    public String getCodigoProjeto() { return codigoProjeto; }
    public void setCodigoProjeto(String codigoProjeto) { this.codigoProjeto = codigoProjeto; }

    public String getNomeProjeto() { return nomeProjeto; }
    public void setNomeProjeto(String nomeProjeto) { this.nomeProjeto = nomeProjeto; }

    public List<String> getJustificativas() { return justificativas; }
    public void setJustificativas(List<String> justificativas) { this.justificativas = justificativas; }

    public String getObjetivos() { return objetivos; }
    public void setObjetivos(String objetivos) { this.objetivos = objetivos; }

    public List<String> getBeneficios() { return beneficios; }
    public void setBeneficios(List<String> beneficios) { this.beneficios = beneficios; }

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public List<Requisito> getRequisitos() { return requisitos; }
    public void setRequisitos(List<Requisito> requisitos) { this.requisitos = requisitos; }

    public List<String> getStakeholders() { return stakeholders; }
    public void setStakeholders(List<String> stakeholders) { this.stakeholders = stakeholders; }

    public List<String> getEquipe() { return equipe; }
    public void setEquipe(List<String> equipe) { this.equipe = equipe; }

    public List<String> getGrupoDeEntregas() { return grupoDeEntregas; }
    public void setGrupoDeEntregas(List<String> grupoDeEntregas) { this.grupoDeEntregas = grupoDeEntregas; }

    public List<String> getRestricoes() { return restricoes; }
    public void setRestricoes(List<String> restricoes) { this.restricoes = restricoes; }

    public List<String> getPremissas() { return premissas; }
    public void setPremissas(List<String> premissas) { this.premissas = premissas; }

    public List<String> getRiscos() { return riscos; }
    public void setRiscos(List<String> riscos) { this.riscos = riscos; }

    public List<String> getLinhaDoTempo() { return linhaDoTempo; }
    public void setLinhaDoTempo(List<String> linhaDoTempo) { this.linhaDoTempo = linhaDoTempo; }

    public List<String> getMetricasDeSucesso() { return metricasDeSucesso; }
    public void setMetricasDeSucesso(List<String> metricasDeSucesso) { this.metricasDeSucesso = metricasDeSucesso; }
}
