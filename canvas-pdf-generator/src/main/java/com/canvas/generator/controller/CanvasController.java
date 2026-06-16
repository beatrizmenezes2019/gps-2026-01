package com.canvas.generator.controller;

import com.canvas.generator.model.CanvasData;
import com.canvas.generator.service.PdfGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/canvas")
@CrossOrigin(origins = "*")
public class CanvasController {

    private static final Logger log = LoggerFactory.getLogger(CanvasController.class);

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Value("${canvas.storage.path:/app/pdf-storage}")
    private String storagePath;

    @Value("${canvas.public.url:http://host.docker.internal:8080}")
    private String publicUrl;

    /**
     * Gera o PDF do canvas, salva em disco e retorna a URL de download.
     * O n8n envia essa URL como mensagem de texto no WhatsApp.
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateCanvas(@RequestBody CanvasData canvasData) {
        log.info("Gerando PDF do canvas para o projeto: {}", canvasData.getNomeProjeto());

        try {
            byte[] pdf = pdfGeneratorService.generateCanvasPdf(canvasData);

            // Gera nome único para o arquivo
            String fileName = "canvas_" + sanitize(canvasData.getCodigoProjeto())
                    + "_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";

            // Salva o arquivo em disco
            Path dir = Paths.get(storagePath);
            Files.createDirectories(dir);
            Files.write(dir.resolve(fileName), pdf);

            String downloadUrl = publicUrl + "/api/canvas/download/" + fileName;
            log.info("PDF salvo: {} ({} bytes)", fileName, pdf.length);

            return ResponseEntity.ok(Map.of(
                    "downloadUrl", downloadUrl,
                    "fileName", fileName
            ));

        } catch (Exception e) {
            log.error("Erro ao gerar PDF do canvas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Serve o PDF gerado para download via URL.
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String fileName) {
        // Evita path traversal
        if (fileName.contains("..") || fileName.contains("/")) {
            return ResponseEntity.badRequest().build();
        }

        Path filePath = Paths.get(storagePath).resolve(fileName);
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            log.warn("Arquivo não encontrado: {}", fileName);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .body(resource);
    }

    private String sanitize(String input) {
        if (input == null) return "projeto";
        return input.replaceAll("[^a-zA-Z0-9_\\-]", "_").toLowerCase();
    }
}
