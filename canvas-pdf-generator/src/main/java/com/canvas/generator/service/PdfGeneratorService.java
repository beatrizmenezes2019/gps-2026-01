package com.canvas.generator.service;

import com.canvas.generator.model.CanvasData;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Gera o PDF do Project Model Canvas a partir dos dados fornecidos.
     *
     * @param data Dados do canvas coletados pelo assistente de IA
     * @return Array de bytes do PDF gerado
     */
    public byte[] generateCanvasPdf(CanvasData data) throws Exception {

        // 1. Renderiza o template Thymeleaf para HTML
        Context context = new Context(Locale.forLanguageTag("pt-BR"));
        context.setVariable("canvas", data);
        String htmlContent = templateEngine.process("canvas", context);

        // 2. Converte o HTML para PDF usando openhtmltopdf
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
    }
}
