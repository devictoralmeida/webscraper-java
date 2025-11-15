package br.com.devictoralmeida.webscraper.java.exception;

public class GlobalExceptionConstants {
    public static final String MENSAGEM_VERIFICAR_CAMPOS = "Erro na requisição, verifique os dados enviados!";
    public static final String MENSAGEM_VALOR_INVALIDO = "Valor inválido para o tipo %s: %s. Valores aceitos são: %s";
    public static final String MENSAGEM_PARAMETRO_OBRIGATORIO_NAO_INFORMADO = "Parâmetro obrigatório não informado: ";
    public static final String MENSAGEM_FORMATO_DATA_INVALIDO = "Formato de data inválido: ";
    public static final String MENSAGEM_FORMATO_ESPERADO_DATA = "O formato esperado é dd/MM/yyyy HH:mm.";
    public static final String MENSAGEM_USAR_FORMATOS_APROPRIADOS = "Use os formatos apropriados para data e hora.";
    public static final String MENSAGEM_TIPO_PARAMETRO_INVALIDO = "Tipo de parâmetro inválido: ";
    public static final String MENSAGEM_PARAMETROS_CONSULTA_INVALIDOS = "Parâmetros de consulta inválidos";
    public static final String MENSAGEM_SEM_AUTORIZACAO = "É necessário estar autenticado para acessar este recurso!";
    public static final String MENSAGEM_PROIBIDO = "Você não tem permissão para acessar este recurso!";
    public static final String MENSAGEM_RECURSO_NAO_ENCONTRADO = "O recurso solicitado não foi encontrado!";


    private GlobalExceptionConstants() {
    }
}
